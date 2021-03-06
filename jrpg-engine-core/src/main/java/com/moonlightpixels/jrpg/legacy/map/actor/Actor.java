package com.moonlightpixels.jrpg.legacy.map.actor;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.moonlightpixels.jrpg.legacy.Game;
import com.moonlightpixels.jrpg.legacy.graphics.GraphicsService;
import com.moonlightpixels.jrpg.legacy.input.InputHandler;
import com.moonlightpixels.jrpg.legacy.input.InputService;
import com.moonlightpixels.jrpg.legacy.map.Entity;
import com.moonlightpixels.jrpg.legacy.map.MapMode;
import com.moonlightpixels.jrpg.legacy.map.animation.Door;
import com.moonlightpixels.jrpg.legacy.state.State;
import com.moonlightpixels.jrpg.legacy.state.StateAdapter;
import com.moonlightpixels.jrpg.legacy.state.StateMachine;
import com.moonlightpixels.jrpg.legacy.tween.Tween;
import com.moonlightpixels.jrpg.legacy.util.TiledUtil;
import com.moonlightpixels.jrpg.legacy.map.GameMap;
import com.moonlightpixels.jrpg.legacy.map.TileCoordinate;
import com.moonlightpixels.jrpg.legacy.map.script.SceneFactory;
import com.moonlightpixels.jrpg.legacy.tween.IntegerTween;
import com.moonlightpixels.jrpg.legacy.util.TimeUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Actor implements Entity, InputHandler {
    public static final String STATE_WALKING = "walking";
    public static final String STATE_STANDING = "standing";
    public static final String STATE_INSPECTING = "inspecting";
    public static final String STATE_WAITING = "waiting";
    public static final String STATE_PARAM_DIRECTION = "direction";
    public static final String STATE_PARAM_WAIT_TIME_MS = "waitTimeMs";

    private final GameMap map;
    private final ActorSpriteSet spriteSet;
    private final StateMachine stateMachine;
    private final Controller controller;
    private final SceneController sceneController;
    private final int height;
    private final int width;
    private Direction facing;
    private TileCoordinate location;
    private TileCoordinate destination;
    private long timeToTravelTileMs;
    private int positionX;
    private int positionY;
    private boolean isHero;
    private boolean isControlledByScene = false;
    private SceneFactory interaction;

    public Actor(final GameMap map, final ActorSpriteSet spriteSet, final Controller controller,
                 final TileCoordinate location, final long timeToTravelTileMs) {
        this.map = map;
        this.spriteSet = spriteSet;
        this.controller = controller;
        this.facing = Direction.DOWN;
        this.location = location;
        this.destination = location;
        this.timeToTravelTileMs = timeToTravelTileMs;
        positionX = map.getAbsoluteX(location);
        positionY = map.getAbsoluteY(location);
        height = spriteSet.getSpriteHeight();
        width = spriteSet.getSpriteWidth();
        sceneController =  new SceneController();
        stateMachine = initStateMachine();
        controller.setActor(this);
    }

    public Actor(final GameMap map, final ActorSpriteSet spriteSet, final Controller controller,
                 final TileCoordinate location) {
        this(map, spriteSet, controller, location, WalkSpeeds.NORMAL);
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public boolean isHero() {
        return isHero;
    }

    public void setIsHero(final boolean isHero) {
        this.isHero = isHero;
    }

    public Direction getFacing() {
        return facing;
    }

    public void setFacing(final Direction direction) {
        this.facing = direction;
    }

    public GameMap getMap() {
        return map;
    }

    private TileCoordinate getAdjacentTileCoordinate(final Direction direction) {
        TileCoordinate target = null;
        switch (direction) {
            case UP:
                target = location.getAbove();
                break;
            case DOWN:
                target = location.getBelow();
                break;
            case LEFT:
                target = location.getLeft();
                break;
            case RIGHT:
                target = location.getRight();
                break;
            default:
        }

        return target;
    }

    public TileCoordinate getLocation() {
        return location;
    }

    public void setLocation(final TileCoordinate location) {
        this.location = location;
    }

    public boolean isOccupying(final TileCoordinate coordinate) {
        return coordinate.equals(location) || coordinate.equals(destination);
    }

    public void setInteraction(final SceneFactory interaction) {
        this.interaction = interaction;
    }

    @Override
    public void interactWith() {
        if (interaction != null) {
            ((MapMode) Game.getInstance().getActiveMode()).startScene(interaction.createScene());
        }
    }

    @Override
    public void render(final GraphicsService graphicsService) {
        stateMachine.render(graphicsService);
    }

    @Override
    public void dispose() {

    }

    private void processNextAction() {
        Action action = getActiveController().nextAction();
        if (action != null) {
            stateMachine.change(action.getActorState(), action.getParameters());
        } else {
            stateMachine.change(STATE_STANDING);
        }
    }

    private StateMachine initStateMachine() {
        Set<State> states = new HashSet<>();
        states.add(createStandingState());
        states.add(createWalkingState());
        states.add(createInspectingState());

        return new StateMachine(states, STATE_STANDING);
    }

    private State createInspectingState() {
        return new StateAdapter() {

            @Override
            public String getKey() {
                return STATE_INSPECTING;
            }

            @Override
            public void onEnter(final Map<String, Object> params) {
                Entity targetEntity = map.getEntity(getAdjacentTileCoordinate(facing));
                if (targetEntity != null) {
                    targetEntity.interactWith();
                } else {
                    map.getDoors().stream()
                            .filter(door -> door.isInteractableWithAt(getAdjacentTileCoordinate(facing)))
                            .forEach(Door::interactWith);
                    map.fireOnInspectTrigger(getAdjacentTileCoordinate(facing));
                }
            }

            @Override
            public void update(final long elapsedTime) {
                stateMachine.change(STATE_STANDING);
            }

            @Override
            public void render(final GraphicsService graphicsService) {
                final TextureRegion standingImage = spriteSet.getStandingImage(facing);
                drawSprite(graphicsService, standingImage);
            }
        };
    }

    private State createWalkingState() {
        final Actor actor = this;

        return new StateAdapter() {
            private Animation<TextureRegion> walkingAnimation;
            private long currentTimeToTravelTileMs = timeToTravelTileMs;
            private long timeInAnimation = 0L;
            private Tween<Integer> positionXTween;
            private Tween<Integer> positionYTween;

            @Override
            public String getKey() {
                return STATE_WALKING;
            }

            @Override
            public void onEnter(final Map<String, Object> params) {
                Direction direction = (Direction) params.getOrDefault(STATE_PARAM_DIRECTION, facing);
                setFacing(direction);

                if (currentTimeToTravelTileMs != timeToTravelTileMs) {
                    currentTimeToTravelTileMs = timeToTravelTileMs;
                    timeInAnimation = 0L;
                }

                TileCoordinate target = getAdjacentTileCoordinate(direction);
                if (isOpen(target)) {
                    walkingAnimation = spriteSet.getWalkingAnimation(facing, timeToTravelTileMs);
                    destination = target;
                    positionXTween = new IntegerTween(
                            map.getAbsoluteX(location),
                            map.getAbsoluteX(destination),
                            timeToTravelTileMs
                    );
                    positionYTween = new IntegerTween(
                            map.getAbsoluteY(location),
                            map.getAbsoluteY(destination),
                            timeToTravelTileMs
                    );
                } else {
                    stateMachine.change(STATE_STANDING);
                }
            }

            @Override
            public void onExit() {

            }

            @Override
            public void update(final long elapsedTime) {
                timeInAnimation += elapsedTime;
                walkingAnimation = spriteSet.getWalkingAnimation(facing, timeToTravelTileMs);
                final long animationTimeMs = TimeUtil.convertFloatSecondsToLongMs(
                        walkingAnimation.getAnimationDuration()
                );
                timeInAnimation = timeInAnimation % animationTimeMs;

                positionXTween.update(elapsedTime);
                positionYTween.update(elapsedTime);

                if (positionXTween.isComplete() && positionYTween.isComplete()) {
                    map.fireOnExitTrigger(location, actor);
                    location = destination;
                    destination = null;
                    map.fireOnEnterTrigger(location, actor);

                    processNextAction();
                } else {
                    positionX = positionXTween.getValue();
                    positionY = positionYTween.getValue();
                }
            }

            @Override
            public void render(final GraphicsService graphicsService) {
                final TextureRegion walkingFrame = walkingAnimation.getKeyFrame(
                        TimeUtil.convertMsToFloatSeconds(timeInAnimation)
                );
                drawSprite(graphicsService, walkingFrame);
            }
        };
    }

    private State createStandingState() {
        return new StateAdapter() {
            @Override
            public String getKey() {
                return STATE_STANDING;
            }

            @Override
            public void onEnter(final Map<String, Object> params) {
                positionX = map.getAbsoluteX(location);
                positionY = map.getAbsoluteY(location);
                if (params.containsKey(STATE_PARAM_DIRECTION)) {
                    facing = (Direction) params.get(STATE_PARAM_DIRECTION);
                }
            }

            @Override
            public void update(final long elapsedTime) {
                getActiveController().update(elapsedTime);
                processNextAction();
            }

            @Override
            public void render(final GraphicsService graphicsService) {
                final TextureRegion standingImage = spriteSet.getStandingImage(facing);
                drawSprite(graphicsService, standingImage);
            }
        };
    }

    private State createWaitingState() {
        return new StateAdapter() {
            private long timeRemainingMs;

            @Override
            public void update(final long elapsedTime) {
                timeRemainingMs -= elapsedTime;
                if (timeRemainingMs < 0L) {
                    processNextAction();
                }
            }

            @Override
            public void render(final GraphicsService graphicsService) {
                final TextureRegion standingImage = spriteSet.getStandingImage(facing);
                drawSprite(graphicsService, standingImage);
            }

            @Override
            public void onEnter(final Map<String, Object> params) {
                timeRemainingMs = (long) params.getOrDefault(STATE_PARAM_WAIT_TIME_MS, 0L);
            }

            @Override
            public String getKey() {
                return STATE_WAITING;
            }
        };
    }

    private void drawSprite(final GraphicsService graphicsService, final TextureRegion sprite) {
        ActorRenderFrame frame = new ActorRenderFrame(
                sprite,
                Math.round(positionX - (sprite.getRegionWidth() / 2.0f)),
                positionY
        )
                .translate(getEclipseTranslation());
        graphicsService.drawSprite(frame.getSprite(), frame.getPositionX(), frame.getPositionY());
    }

    private PartialEclipseTranslation getEclipseTranslation() {
        Integer eclipsedHeight = TiledUtil.getCellPropertyFromTopMostTile(
                map.getTiledMap(),
                (destination != null) ? destination : location,
                GameMap.TILE_PROP_ECLIPSED_HEIGHT,
                Integer.class
        );

        return new PartialEclipseTranslation((eclipsedHeight != null) ? eclipsedHeight : 0);
    }

    private boolean isOpen(final TileCoordinate targetCoordinate) {
        // check that move is inbounds for map
        if (targetCoordinate.getX() < 0
                || targetCoordinate.getY() < 0
                || targetCoordinate.getX() >= map.getMapWidthInTiles()
                || targetCoordinate.getY() >= map.getMapHeightInTiles()) {
            return false;
        }

        return !map.isCollision(this, targetCoordinate);
    }

    private int weightedAverage(final int start, final int finish, final float percentComplete) {
        return Math.round(start + ((finish - start) * percentComplete));
    }

    @Override
    public void handleInput(final InputService inputService) {
        stateMachine.handleInput(inputService);
    }

    @Override
    public void update(final long elapsedTime) {
        getActiveController().update(elapsedTime);
        stateMachine.update(elapsedTime);
    }

    public void startScene() {
        isControlledByScene = true;
    }

    public void endScene() {
        isControlledByScene = false;
    }

    private Controller getActiveController() {
        return (isControlledByScene) ? sceneController : controller;
    }

    public SceneController getSceneController() {
        return sceneController;
    }
}
