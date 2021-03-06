package com.moonlightpixels.jrpg.legacy.map.ai;

import com.moonlightpixels.jrpg.legacy.map.TileCoordinate;
import com.moonlightpixels.jrpg.legacy.map.actor.Action;
import com.moonlightpixels.jrpg.legacy.map.actor.Actor;
import com.moonlightpixels.jrpg.legacy.map.actor.Direction;
import com.moonlightpixels.jrpg.legacy.map.actor.MoveAction;

public final class MoveToGoal implements Goal {
    private final TileCoordinate destination;

    public MoveToGoal(final TileCoordinate destination) {
        this.destination = destination;
    }

    @Override
    public boolean isAchievable(final Actor actor) {
        return nextAction(actor) != null;
    }

    @Override
    public boolean isComplete(final Actor actor) {
        return destination.equals(actor.getLocation());
    }

    @Override
    public Action nextAction(final Actor actor) {
        final PathFinder pathFinder = new PathFinder(actor.getMap());
        final TileCoordinate currentLocation = actor.getLocation();
        final TileCoordinate target = pathFinder.getNextTarget(currentLocation, destination);

        if (target != null) {
            if (target.getX() == currentLocation.getX() && target.getY() < currentLocation.getY()) {
                return new MoveAction(Direction.DOWN);
            }

            if (target.getX() == currentLocation.getX() && target.getY() > currentLocation.getY()) {
                return new MoveAction(Direction.UP);
            }

            if (target.getY() == currentLocation.getY() && target.getX() < currentLocation.getX()) {
                return new MoveAction(Direction.LEFT);
            }

            if (target.getY() == currentLocation.getY() && target.getX() > currentLocation.getX()) {
                return new MoveAction(Direction.RIGHT);
            }
        }

        return null;
    }

    @Override
    public void update(final long elapsedTime) {

    }

    public TileCoordinate getDestination() {
        return destination;
    }
}
