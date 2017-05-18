package com.github.jaystgelais.jrpg.tween;

public final class IntegerTween extends AbstractTween<Integer> {

    public IntegerTween(final Integer start, final Integer end, final long totalTweenTimeMs) {
        this(new ConstantVelocityTweenFunction(), start, end, totalTweenTimeMs);
    }

    public IntegerTween(final TweenFunction tweenFunction, final Integer start,
                        final Integer end, final long totalTweenTimeMs) {
        super(tweenFunction, start, end, totalTweenTimeMs);
    }

    @Override
    protected Integer getValue(final float percentComplete, final Integer start, final Integer end) {
        return start + Math.round(percentComplete * (end - start));
    }
}