package com.andrejlohn.mariobros.screens;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.scenes.Hud;
import com.andrejlohn.mariobros.sprites.Enemy;
import com.andrejlohn.mariobros.sprites.Goomba;
import com.andrejlohn.mariobros.sprites.Mario;
import com.andrejlohn.mariobros.tools.B2WorldCreator;
import com.andrejlohn.mariobros.tools.WorldContactListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * This class represents the game screen in a running game. It implements the libGDX Screen
 * interface. It contains the game camera, viewport and HUD. An orthogonal tiled map is used as the
 * game map.
 *
 * @version %I%, %G%
 * @see     Screen
 */
public class PlayScreen implements Screen {

    // Game
    private MarioBros game;
    private TextureAtlas atlas;

    // Play screen
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    // Tiled map
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // Box2D
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    // Character
    private Mario player;

    // Music
    private Music music;


    /**
     * Creates the PlayScreen for a running MarioBros game. Sets up the game camera, viewport, HUD
     * and game map.
     *
     * @param game  the MarioBros game
     * @see         FitViewport
     * @see         Hud
     * @see         B2WorldCreator
     * @see         OrthogonalTiledMapRenderer
     * @see         TmxMapLoader#load(String)
     * @see         OrthographicCamera#position
     */
    public PlayScreen(MarioBros game) {
        this.game = game;
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(
                MarioBros.V_WIDTH / MarioBros.PPM,
                MarioBros.V_HEIGHT / MarioBros.PPM,
                gameCam);

        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        gameCam.position.set(
                gamePort.getWorldWidth() / 2,
                gamePort.getWorldHeight() / 2,
                0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = MarioBros.manager.get("audio/music/01_main_theme_overworld.mp3", Music.class);
        music.setLooping(true);
        music.play();
    }

    /**
     * Handles user input with respect to the time passed since the last update.
     *
     * @param dt    the time passed
     * @see         Input#isTouched()
     * @see         OrthographicCamera#position
     */
    public void handleInput(float dt) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if(player.b2Body.getLinearVelocity().y == 0) {
                player.b2Body.applyLinearImpulse(
                        new Vector2(0, 4f),
                        player.b2Body.getWorldCenter(),
                        true);
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2Body.getLinearVelocity().x <= 2) {
            player.b2Body.applyLinearImpulse(
                    new Vector2(0.1f, 0),
                    player.b2Body.getWorldCenter(),
                    true);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2Body.getLinearVelocity().x >= -2) {
            player.b2Body.applyLinearImpulse(
                    new Vector2(-0.1f, 0),
                    player.b2Body.getWorldCenter(),
                    true);
        }
    }

    /**
     * Updates the play screen based on the time passed since the last update.
     * This represents the update part of the game cycle.
     *
     * @param dt    the time passed
     * @see         World#step(float, int, int)
     * @see         OrthographicCamera#update()
     * @see         OrthogonalTiledMapRenderer#setView(OrthographicCamera)
     * @see         Mario#update(float)
     */
    public void update(float dt) {
        handleInput(dt);

        world.step(1/60f, 6, 2);

        player.update(dt);
        for(Enemy enemy: creator.getGoombas()) {
            enemy.update(dt);
        }
        hud.update(dt);

        gameCam.position.x = player.b2Body.getPosition().x;

        gameCam.update();
        renderer.setView(gameCam);
    }

    /**
     * Gets the texture atlas.
     *
     * @see TextureAtlas
     */
    public TextureAtlas getTextureAtlas() {
        return atlas;
    }

    /**
     * Gets the game world.
     *
     * @return  the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Gets the game map.
     *
     * @return  the map
     */
    public TiledMap getMap() {
        return map;
    }

    @Override
    public void show() {

    }

    /**
     * Renders the game to the device screen based on the time passed since the last rendering.
     * This represents the render part of the game cycle.
     *
     * @param delta the time passed
     * @see         OrthographicCamera#combined
     * @see         com.badlogic.gdx.graphics.g2d.SpriteBatch#setProjectionMatrix(Matrix4)
     * @see         Box2DDebugRenderer#render(World, Matrix4)
     */
    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        // render Box2DDebugLines
        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy: creator.getGoombas()) {
            enemy.draw(game.batch);
        }
        game.batch.end();

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

    /**
     * Disposes als play screen components not subject to the garbage collection. Prevents memory
     * leak.
     *
     * @see Map#dispose()
     * @see OrthogonalTiledMapRenderer#dispose()
     * @see World#dispose()
     * @see Box2DDebugRenderer#dispose()
     * @see Hud#dispose()
     */
    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
