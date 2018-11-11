package com.andrejlohn.mariobros;

import com.andrejlohn.mariobros.screens.PlayScreen;
import com.badlogic.gdx.Game;
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

	public SpriteBatch batch;

    /**
     * Creates the Game. Sets up the SpriteBatch and the PlayScreen.
     *
     * @see SpriteBatch
     * @see PlayScreen
     */
    @Override
	public void create () {
		batch = new SpriteBatch();
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
     * @see SpriteBatch#dispose()
     */
    @Override
	public void dispose () {
		batch.dispose();
	}
}
