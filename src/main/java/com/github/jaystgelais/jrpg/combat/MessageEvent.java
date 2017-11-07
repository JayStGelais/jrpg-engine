package com.github.jaystgelais.jrpg.combat;

public final class MessageEvent extends CombatEvent {
    private final String message;

    protected MessageEvent(final String message) {
        super(TICKS_REMAINING_IMMEDIATE);
        this.message = message;
    }

    @Override
    public void start(final Battle battle) {
        battle.queueMessage(message);
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void update(final long elapsedTime) {

    }
}