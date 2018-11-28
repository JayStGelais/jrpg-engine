package com.moonlightpixels.jrpg.combat.stats;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Function;

public interface StatHolder {
    /**
     * Get holders base value for a given stat.
     *
     * @param key Key identiying requested stat
     * @return stat's base value
     */
    int getBaseValue(Stat.Key key);

    /**
     * Get all Stat Addition modifiers for a given stat. Negative modifiers represnt penalties.
     *
     * @param stat Key identiying requested stat
     * @return List of additions to this stat.
     */
    List<StatAdditon> getStatAdditions(Stat.Key stat);

    /**
     * Get all Stat Multipliers modifiers for a given stat.
     *
     * @param stat Key identiying requested stat
     * @return List of multipliers to this stat.
     */
    List<StatMutiplier> getStatMultipliers(Stat.Key stat);

    /**
     * Get all modifiers for a given stat, ordered in the correct order to be applid. Ordering defaults to additions
     * first, follwed by multipleirs. Override this method to implement a different ordering.
     *
     * @param stat identiying requested stat
     * @return List of modifiers to this stat.
     */
    default List<Function<Integer, Integer>> getStatModifiers(Stat.Key stat) {
        return Lists.newLinkedList(Iterables.concat(getStatAdditions(stat), getStatMultipliers(stat)));
    }
}
