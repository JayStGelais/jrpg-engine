package com.github.jaystgelais.jrpg.menu;

public final class OpenMenuAction implements MenuAction {
    private final MenuDefinition menuDefinition;

    public OpenMenuAction(final MenuDefinition menuDefinition) {
        this.menuDefinition = menuDefinition;
    }

    @Override
    public void perform(final MenuMode mode) {
        mode.openMenu(menuDefinition.getMenu(mode.getGame().getGraphicsService()));
    }
}