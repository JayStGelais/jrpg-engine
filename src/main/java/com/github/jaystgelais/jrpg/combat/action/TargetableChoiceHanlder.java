package com.github.jaystgelais.jrpg.combat.action;

import com.github.jaystgelais.jrpg.combat.Battle;

public abstract class TargetableChoiceHanlder<T extends Targetable> {
    private final TargetableChoiceProvider<T> provider;

    protected TargetableChoiceHanlder(final TargetableChoiceProvider<T> provider) {
        this.provider = provider;
    }

    public abstract void start(Battle battle);

    protected final void provideChoice(final T choice) {
        provider.setChoice(choice);
    }
}
