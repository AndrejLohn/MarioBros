package com.andrejlohn.mariobros.screens;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.scenes.Hud;
import com.andrejlohn.mariobros.sprites.Mario;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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

    private MarioBros game;
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

    // Character
    private Mario player;


    /**
     * Creates the PlayScreen for a running MarioBros game. Sets up the game camera, viewport, HUD
     * and game map.
     *
     * @param game  the MarioBros game
     * @see         FitViewport
     * @see         Hud
     * @see         OrthogonalTiledMapRenderer
     * @see         TmxMapLoader#load(String)
     * @see         OrthographicCamera#position
     */
    public PlayScreen(MarioBros game) {
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(
                MarioBros.V_WIDTH / MarioBros.PPM,
                MarioBros.V_HEIGHT / MarioBros.PPM,
                gameCam);

        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1_simple.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        gameCam.position.set(
                gamePort.getWorldWidth() / 2,
                gamePort.getWorldHeight() / 2,
                0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        BodyDef bDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fDef = new FixtureDef();
        Body body;

        // Create ground  fixtures and bodies. The index of mapLayers().get() depends on the layers
        // position in the .tmx file starting at 0.
        for(MapObject object:
                map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bDef.type = BodyDef.BodyType.StaticBody;
            bDef.position.set(
                    (rect.getX() + rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bDef);

            shape.setAsBox(
                    (rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getHeight() / 2) / MarioBros.PPM);
            fDef.shape = shape;
            body.createFixture(fDef);
        }

        // Create pipe bodies/fixtures
        for(MapObject object:
                map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bDef.type = BodyDef.BodyType.StaticBody;
            bDef.position.set(
                    (rect.getX() + rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bDef);

            shape.setAsBox(
                    (rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getHeight() / 2) / MarioBros.PPM);
            fDef.shape = shape;
            body.createFixture(fDef);
        }

        // Create brick bodies/fixtures
        for(MapObject object:
                map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bDef.type = BodyDef.BodyType.StaticBody;
            bDef.position.set(
                    (rect.getX() + rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bDef);

            shape.setAsBox(
                    (rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getHeight() / 2) / MarioBros.PPM);
            fDef.shape = shape;
            body.createFixture(fDef);
        }

        // Create coin bodies/fixtures
        for(MapObject object:
                map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bDef.type = BodyDef.BodyType.StaticBody;
            bDef.position.set(
                    (rect.getX() + rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bDef);

            shape.setAsBox(
                    (rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getHeight() / 2) / MarioBros.PPM);
            fDef.shape = shape;
            body.createFixture(fDef);
        }

        player = new Mario(world);
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
            player.b2Body.applyLinearImpulse(
                    new Vector2(0, 4f),
                    player.b2Body.getWorldCenter(),
                    true);
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
     * @see         OrthographicCamera#update()
     * @see         OrthogonalTiledMapRenderer#setView(OrthographicCamera)
     */
    public void update(float dt) {
        handleInput(dt);

        world.step(1/60f, 6, 2);
        gameCam.position.x = player.b2Body.getPosition().x;

        gameCam.update();
        renderer.setView(gameCam);
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

        b2dr.render(world, gameCam.combined);

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
