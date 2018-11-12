package com.andrejlohn.mariobros.tools;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.sprites.Brick;
import com.andrejlohn.mariobros.sprites.Coin;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * This class generates the game world from a given Box2D world and a tiled map.
 *
 * @version %I%, %G%
 */
public class B2WorldCreator {

    /**
     * Creates the game world. Sets up all map objects (ground, pipes, boxes, coins).
     *
     * @param world the Box2D world
     * @param map   the tiled map
     * @see         BodyDef
     * @see         PolygonShape
     * @see         FixtureDef
     * @see         MapObject
     * @see         TiledMap#getLayers()
     * @see         World#createBody(BodyDef)
     * @see         Body#createFixture(FixtureDef)
     */
    public B2WorldCreator(World world, TiledMap map) {
        BodyDef bDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fDef = new FixtureDef();
        Body body;

        // Create ground bodies and fixtures. The index of mapLayers().get() depends on the layers
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

            new Brick(world, map, rect);
        }

        // Create coin bodies/fixtures
        for(MapObject object:
                map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Coin(world, map, rect);
        }
    }
}
