package com.github.jaystgelais.jrpg.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import java.util.Optional;

public final class SelectItem<T extends Widget> {
    private final T display;
    private final Action action;
    private final Action onCursorAction;

    public SelectItem(final T display, final Action action, final Action onCursorAction) {
        this.display = display;
        this.action = action;
        this.onCursorAction = onCursorAction;
    }

    public SelectItem(final T display, final Action action) {
        this(display, action, null);
    }

    public T getDisplay() {
        return display;
    }

    public Action getAction() {
        return action;
    }

    public Optional<Action> getOnCursorAction() {
        return Optional.ofNullable(onCursorAction);
    }

    public interface Action {
        void perform();
    }
}