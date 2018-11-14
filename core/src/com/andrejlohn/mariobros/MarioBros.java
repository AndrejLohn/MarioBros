package com.andrejlohn.mariobros;

import com.andrejlohn.mariobros.screens.PlayScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * This represents the game main class. It runs the came loop of updating and rendering the game
 * world to the device screen. This class extends the libGDX Game class.
 *
 * @version %I%, %G%
 * @see Game
 */
public class MarioBros extends Game {

    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;
    public static final float PPM = 100;

    // Collision bits
    public static final short GROUND_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short ITEM_BIT = 256;

	public SpriteBatch batch;

	/* WARNING Using AssetManager in a static way can cause issues, especially on Android.
	Instead you may want to pass around AssetManager to those classes that need it.
	We will use it in the static context to save time for now.
	DIRECT COPY FROM THE TUTORIAL
	 */
	public static AssetManager manager;

    /**
     * Creates the Game. Sets up the SpriteBatch and the PlayScreen.
     *
     * @see SpriteBatch
     * @see PlayScreen
     */
    @Override
	public void create () {
		batch = new SpriteBatch();
        manager = new AssetManager();
        manager.load("audio/music/01_main_theme_overworld.mp3", Music.class);
        manager.load("audio/sounds/smb_coin.wav", Sound.class);
        manager.load("audio/sounds/smb_bump.wav", Sound.class);
        manager.load("audio/sounds/smb_breakblock.wav", Sound.class);
        manager.finishLoading();

		setScreen(new PlayScreen(this));
	}

    /**
     * Renders the game to the device screen.
     *
     * @see Game#render()
     */
    @Override
	public void render () {
		super.render();
	}

    /**
     * Disposes all game elements not subject to the garbage collection. Prevents memory leak.
     *
     * @see Game#dispose()
     * @see SpriteBatch#dispose()
     * @see AssetManager#dispose()
     */
    @Override
	public void dispose () {
        super.dispose();
		batch.dispose();
		manager.dispose();
	}
}
