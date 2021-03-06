package com.andrejlohn.mariobros.screens;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.scenes.Hud;
import com.andrejlohn.mariobros.sprites.enemies.Enemy;
import com.andrejlohn.mariobros.sprites.Mario;
import com.andrejlohn.mariobros.sprites.items.Item;
import com.andrejlohn.mariobros.sprites.items.ItemDef;
import com.andrejlohn.mariobros.sprites.items.Mushroom;
import com.andrejlohn.mariobros.tools.B2WorldCreator;
import com.andrejlohn.mariobros.tools.Controller;
import com.andrejlohn.mariobros.tools.WorldContactListener;
import com.badlogic.gdx.Application;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.concurrent.LinkedBlockingQueue;

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

    // Sprites
    private Mario player;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    // Music
    private Music music;

    // Controller
    private Controller controller;


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
        controller = new Controller(game);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        gameCam.position.set(
                gamePort.getWorldWidth() / 2,
                gamePort.getWorldHeight() / 2,
                0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this, game.getManager());

        player = new Mario(this, game.getManager());

        world.setContactListener(new WorldContactListener());

        music = game.getManager().get("audio/music/01_main_theme_overworld.mp3", Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef iDef) {
        itemsToSpawn.add(iDef);
    }

    public void handleSpawningItems() {
        if(!itemsToSpawn.isEmpty()) {
            ItemDef iDef = itemsToSpawn.poll();
            if(iDef.type == Mushroom.class) {
                items.add(new Mushroom(this, iDef.position.x, iDef.position.y));
            }
        }
    }

    /**
     * Handles user input with respect to the time passed since the last update.
     *
     * @param dt    the time passed
     * @see         Input#isTouched()
     * @see         OrthographicCamera#position
     */
    public void handleInput(float dt) {
        if(player.currentState != Mario.State.DEAD) {

            if(controller.isUpPressed()) {

                if(player.b2Body.getLinearVelocity().y == 0) {

                    player.b2Body.applyLinearImpulse(
                            new Vector2(0, 4f),
                            player.b2Body.getWorldCenter(),
                            true);
                }
            }

            if(controller.isRightPressed() &&
                    player.b2Body.getLinearVelocity().x <= 2) {

                player.b2Body.applyLinearImpulse(
                        new Vector2(0.1f, 0),
                        player.b2Body.getWorldCenter(),
                        true);
            }

            if(controller.isLeftPressed() &&
                    player.b2Body.getLinearVelocity().x >= -2) {

                player.b2Body.applyLinearImpulse(
                        new Vector2(-0.1f, 0),
                        player.b2Body.getWorldCenter(),
                        true);
            }
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
        handleSpawningItems();

        world.step(1/60f, 6, 2);

        player.update(dt);
        for(Enemy enemy: creator.getEnemies()) {
            // Activate enemies only when they are at most 2 tiles away from the screen edge
            // (12+2)*16 = 224
            //TODO check if enemy deactivation is necessary
            if(enemy.getX() < player.getX() + 224 / MarioBros.PPM) {
                enemy.b2Body.setActive(true);
            }
            enemy.update(dt);
        }

        for(Item item: items) {
            item.update(dt);
        }

        hud.update(dt);

        if(player.currentState != Mario.State.DEAD) {
            if(player.b2Body.getPosition().x < MarioBros.V_WIDTH / 2 / MarioBros.PPM) {
                gameCam.position.x = MarioBros.V_WIDTH / 2 / MarioBros.PPM;
            } else {
                gameCam.position.x = player.b2Body.getPosition().x;
            }
        }

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

    public MarioBros getGame() {
        return game;
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
        //b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy: creator.getEnemies()) {
            enemy.draw(game.batch);
        }
        for(Item item: items) {
            item.draw(game.batch);
        }
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            controller.draw();
        }

        if(gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
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

    public boolean gameOver() {
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3) {
            return true;
        }
        return false;
    }
}
