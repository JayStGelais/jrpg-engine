package com.moonlightpixels.jrpg.internal

import com.moonlightpixels.jrpg.combat.internal.CombatState
import com.moonlightpixels.jrpg.frontend.internal.FrontEndState
import com.moonlightpixels.jrpg.map.internal.MapState
import spock.lang.Specification

class JRPGSpec extends Specification {
    FrontEndState frontEndState
    MapState mapState
    CombatState combatState
    JRPG jrpg

    void setup() {
        frontEndState = Mock(FrontEndState)
        mapState = Mock(MapState)
        combatState = Mock(CombatState)
    }

    void 'Calls to update are passed to active state'() {
        setup:
        jrpg = new JRPG(frontEndState, mapState, combatState, frontEndState)

        when:
        jrpg.update()

        then:
        1 * frontEndState.update(jrpg)
    }

    void 'toLocation() Changes state to MapState'() {
        setup:
        jrpg = new JRPG(frontEndState, mapState, combatState, frontEndState)

        when:
        jrpg.toLocation()

        then:
        1 * mapState.enter(jrpg)
    }

    void 'toBattle() Changes state to CombatState'() {
        setup:
        jrpg = new JRPG(frontEndState, mapState, combatState, frontEndState)

        when:
        jrpg.toBattle()

        then:
        1 * combatState.enter(jrpg)
    }

    void 'toMainMenu() Changes state to FrontEndState'() {
        setup:
        jrpg = new JRPG(frontEndState, mapState, combatState, mapState)

        when:
        jrpg.toMainMenu()

        then:
        1 * frontEndState.enter(jrpg)
    }

    void 'exitBattle() returns to previous state'() {
        setup:
        jrpg = new JRPG(frontEndState, mapState, combatState, mapState)

        when:
        jrpg.toBattle()
        jrpg.exitBattle()

        then:
        1 * mapState.enter(jrpg)
        1 * combatState.exit(jrpg)
    }

    void 'exitBattle() throws IllegalStateException if not in combatState'() {
        setup:
        jrpg = new JRPG(frontEndState, mapState, combatState, mapState)

        when:
        jrpg.exitBattle()

        then:
        thrown IllegalStateException
    }

    void 'exitBattle() throws IllegalStateException if there is no previous state to return to'() {
        setup:
        jrpg = new JRPG(frontEndState, mapState, combatState, combatState)

        when:
        jrpg.exitBattle()

        then:
        thrown IllegalStateException
    }
}