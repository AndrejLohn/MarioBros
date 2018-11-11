package com.andrejlohn.mariobros.screens;

import com.andrejlohn.mariobros.MarioBros;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * This class represents the game screen in a running game. It implements the libGDX Screen
 * interface. It contains the game camera, viewport and HUD.
 *
 * @version %I%, %G%
 * @see     Screen
 */
public class PlayScreen implements Screen {

    private MarioBros game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    /**
     * Creates the PlayScreen for a running MarioBros game. Sets up the game camera, viewport and
     * HUD.
     *
     * @param game  the MarioBros game
     * @see         OrthographicCamera
     * @see         FitViewport
     * @see         Hud
     */
    public PlayScreen(MarioBros game) {
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, gameCam);
        hud = new Hud(game.batch);
    }

    @Override
    public void show() {

    }

    /**
     * Renders the game to the device screen based on the time passed since the last rendering.
     *
     * @param delta the time passed
     * @see         OrthographicCamera#combined
     * @see         com.badlogic.gdx.graphics.g2d.SpriteBatch#setProjectionMatrix(Matrix4)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    /**
     * Resizes the viewport to a given screen size.
     *
     * @param width     the screen width
     * @param height    the screen height
     * @see             FitViewport#update(int, int)
     */
    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
