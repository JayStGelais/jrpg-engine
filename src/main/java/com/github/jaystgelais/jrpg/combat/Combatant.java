package com.github.jaystgelais.jrpg.combat;

import com.github.jaystgelais.jrpg.combat.stats.StatHolder;

public interface Combatant extends StatHolder {
    void applyDamage(final int damage);
}
