package com.moonlightpixels.jrpg.legacy.graphics;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.moonlightpixels.jrpg.legacy.ui.text.FontDefinition;
import com.moonlightpixels.jrpg.legacy.ui.text.FontSet;

public interface GraphicsService extends Disposable {
    LwjglApplicationConfiguration getConfiguration();

    AssetManager getAssetManager();

    void renderStart();

    void renderEnd();

    void init();

    void clearScreen();

    void resize(int width, int height);

    void drawSprite(Texture texture, float posX, float posY);

    void drawSprite(TextureRegion textureRegion, float posX, float posY);

    void drawSprite(Pixmap pixmap, float posX, float posY);

    void drawBackground(Texture background);

    SpriteBatch getSpriteBatch();

    OrthographicCamera getCamera();

    int getCameraOffsetX();

    int getCameraOffsetY();

    TiledMapRenderer getTileMapRenderer(TiledMap tiledMap);

    FontSet getFontSet();

    FontDefinition getTextFont();

    void setTextFont(FontDefinition textFont);

    void setNumberFont(FontDefinition numberFont);

    int getResolutionWidth();

    void setResolutionWidth(int resolutionWidth);

    int getResolutionHeight();

    void setResolutionHeight(int resolutionHeight);

    int getdisplayXfromMapX(int mapX);

    int getdisplayYfromMapY(int mapY);

    int getPhysicalXFromResolutionX(int resolutionX);

    int getPhysicalYFromResolutionY(int resolutionY);
}
