package com.moonlightpixels.jrpg.internal

import com.moonlightpixels.jrpg.combat.CombatState
import com.moonlightpixels.jrpg.frontend.internal.FrontEndState
import com.moonlightpixels.jrpg.input.InputSystem
import com.moonlightpixels.jrpg.internal.plugin.PluginSystem
import com.moonlightpixels.jrpg.map.internal.MapState
import spock.lang.Specification

class JRPGSpec extends Specification {
    FrontEndState frontEndState
    MapState mapState
    CombatState combatState
    DefaultJRPG jrpg
    InputSystem inputSystem
    PluginSystem pluginSystem

    void setup() {
        frontEndState = Mock(FrontEndState)
        mapState = Mock(MapState)
        combatState = Mock(CombatState)
        inputSystem = Mock(InputSystem)
        pluginSystem = new PluginSystem()
    }

    void 'Calls to init trigger enter on initial state'() {
        setup:
        jrpg = new DefaultJRPG(frontEndState, mapState, combatState, frontEndState, inputSystem, pluginSystem)

        when:
        jrpg.init()

        then:
        1 * frontEndState.enter(jrpg)
    }

    void 'Calls to update are passed to active state'() {
        setup:
        jrpg = new DefaultJRPG(frontEndState, mapState, combatState, frontEndState, inputSystem, pluginSystem)

        when:
        jrpg.update()

        then:
        1 * frontEndState.update(jrpg)
    }

    void 'toLocation() Changes state to MapState'() {
        setup:
        jrpg = new DefaultJRPG(frontEndState, mapState, combatState, frontEndState, inputSystem, pluginSystem)

        when:
        jrpg.toMap()

        then:
        1 * mapState.enter(jrpg)
    }

    void 'toBattle() Changes state to CombatState'() {
        setup:
        jrpg = new DefaultJRPG(frontEndState, mapState, combatState, frontEndState, inputSystem, pluginSystem)

        when:
        jrpg.toBattle()

        then:
        1 * combatState.enter(jrpg)
    }

    void 'toMainMenu() Changes state to FrontEndState'() {
        setup:
        jrpg = new DefaultJRPG(frontEndState, mapState, combatState, mapState, inputSystem, pluginSystem)

        when:
        jrpg.toMainMenu()

        then:
        1 * frontEndState.enter(jrpg)
    }

    void 'exitBattle() returns to previous state'() {
        setup:
        jrpg = new DefaultJRPG(frontEndState, mapState, combatState, mapState, inputSystem, pluginSystem)

        when:
        jrpg.toBattle()
        jrpg.exitBattle()

        then:
        1 * mapState.enter(jrpg)
        1 * combatState.exit(jrpg)
    }

    void 'exitBattle() throws IllegalStateException if not in combatState'() {
        setup:
        jrpg = new DefaultJRPG(frontEndState, mapState, combatState, mapState, inputSystem, pluginSystem)

        when:
        jrpg.exitBattle()

        then:
        thrown IllegalStateException
    }

    void 'exitBattle() throws IllegalStateException if there is no previous state to return to'() {
        setup:
        jrpg = new DefaultJRPG(frontEndState, mapState, combatState, combatState, inputSystem, pluginSystem)

        when:
        jrpg.exitBattle()

        then:
        thrown IllegalStateException
    }
}
