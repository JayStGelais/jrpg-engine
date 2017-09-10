package com.github.jaystgelais.jrpg.map.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.jaystgelais.jrpg.map.animation.SpriteSet;

public final class TreasureChestSpriteSet extends SpriteSet {
    private final TextureRegion closedImage;
    private final TextureRegion openImage;
    private final Animation<TextureRegion> openingAnimation;

    public TreasureChestSpriteSet(final TextureRegion closedImage, final TextureRegion openImage,
                                  final Animation<TextureRegion> openingAnimation) {
        this.closedImage = closedImage;
        this.openImage = openImage;
        this.openingAnimation = openingAnimation;
    }

    public TextureRegion getClosedImage() {
        return closedImage;
    }

    public TextureRegion getOpenImage() {
        return openImage;
    }

    public Animation<TextureRegion> getOpeningAnimation() {
        return openingAnimation;
    }
}
