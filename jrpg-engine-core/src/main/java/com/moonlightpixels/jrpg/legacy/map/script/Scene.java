package com.moonlightpixels.jrpg.legacy.map.script;

import com.moonlightpixels.jrpg.legacy.graphics.GraphicsService;
import com.moonlightpixels.jrpg.legacy.graphics.Renderable;
import com.moonlightpixels.jrpg.legacy.input.InputHandler;
import com.moonlightpixels.jrpg.legacy.input.InputService;
import com.moonlightpixels.jrpg.legacy.state.Updatable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class Scene implements Updatable, Renderable, InputHandler {
    private final Queue<Command> script;
    private Command currentCommand;

    public Scene(final Command... commands) {
        this(Arrays.asList(commands));
    }

    public Scene(final List<Command> commands) {
        script = new LinkedList<>();
        script.addAll(commands);
        nextCommand();
    }

    public boolean isComplete() {
        return script.isEmpty() && (currentCommand == null || currentCommand.isComplete());
    }

    @Override
    public void update(final long elapsedTime) {
        if (currentCommand != null) {
            if (currentCommand.isComplete()) {
                nextCommand();
            } else {
                currentCommand.update(elapsedTime);
            }
        }
    }

    @Override
    public void render(final GraphicsService graphicsService) {
        if (currentCommand != null) {
            currentCommand.render(graphicsService);
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void handleInput(final InputService inputService) {
        if (currentCommand != null) {
            currentCommand.handleInput(inputService);
        }
    }

    private void nextCommand() {
        if (currentCommand != null) {
            currentCommand.dispose();
        }
        currentCommand = script.poll();
        if (currentCommand != null) {
            currentCommand.start();
        }
    }
}
