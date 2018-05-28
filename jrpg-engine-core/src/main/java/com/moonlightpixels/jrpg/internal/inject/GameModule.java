package com.moonlightpixels.jrpg.internal.inject;

import com.badlogic.gdx.ai.fsm.State;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.moonlightpixels.jrpg.combat.internal.CombatState;
import com.moonlightpixels.jrpg.combat.internal.DefaultCombatState;
import com.moonlightpixels.jrpg.config.JRPGConfiguration;
import com.moonlightpixels.jrpg.frontend.internal.DefaultFrontEndState;
import com.moonlightpixels.jrpg.frontend.internal.FrontEndState;
import com.moonlightpixels.jrpg.internal.DefaultJRPG;
import com.moonlightpixels.jrpg.internal.JRPG;
import com.moonlightpixels.jrpg.map.JRPGMap;
import com.moonlightpixels.jrpg.map.JRPGMapFactory;
import com.moonlightpixels.jrpg.map.internal.DefaultJRPGMap;
import com.moonlightpixels.jrpg.map.internal.DefaultMapState;
import com.moonlightpixels.jrpg.map.internal.MapState;
import com.moonlightpixels.jrpg.ui.UserInterface;
import com.moonlightpixels.jrpg.ui.internal.DefaultUserInterface;

import javax.inject.Named;

public final class GameModule extends AbstractModule {
    public static final String INITIAL_STATE = "initial";
    private final JRPGConfiguration configuration;

    GameModule(final JRPGConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bind(JRPG.class).to(DefaultJRPG.class).asEagerSingleton();
        bind(FrontEndState.class).to(DefaultFrontEndState.class).asEagerSingleton();
        bind(MapState.class).to(DefaultMapState.class).asEagerSingleton();
        bind(CombatState.class).to(DefaultCombatState.class).asEagerSingleton();
        bind(UserInterface.class).to(DefaultUserInterface.class).asEagerSingleton();

        install(
            new FactoryModuleBuilder()
                .implement(JRPGMap.class, DefaultJRPGMap.class)
                .build(JRPGMapFactory.class)
        );
    }

    @Provides
    @Named(INITIAL_STATE)
    public State<JRPG> provideInitialState(final FrontEndState frontEndState) {
        return frontEndState;
    }
}
