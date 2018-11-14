package com.andrejlohn.mariobros.sprites.tileobjects;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.screens.PlayScreen;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * This class is the base class for all interactive game world objects (i.e. bricks and coins).
 */
public abstract class InteractiveTileObject {

    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected PlayScreen screen;
    protected MapObject object;

    /**
     * Creates the interactive game world object.
     *
     * @param screen    the play screen
     * @param object    the map object
     * @see             PlayScreen
     * @see             Rectangle
     */
    public InteractiveTileObject(PlayScreen screen, MapObject object) {
        this.object = object;
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = ((RectangleMapObject) object).getRectangle();

        BodyDef bDef = new BodyDef();
        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.position.set(
                (bounds.getX() + bounds.getWidth() / 2) / MarioBros.PPM,
                (bounds.getY() + bounds.getHeight() / 2) / MarioBros.PPM);

        body = world.createBody(bDef);

        shape.setAsBox(
                (bounds.getWidth() / 2) / MarioBros.PPM,
                (bounds.getHeight() / 2) / MarioBros.PPM);
        fDef.shape = shape;
        fixture = body.createFixture(fDef);
    }

    /**
     * Offers the functionality to react to a collision with the player characters head.
     */
    public abstract void onHeadHit();

    /**
     * Sets the collision filter based on a given filter bit.
     *
     * @param filterBit the filter bit
     * @see             Filter
     */
    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();

        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    /**
     * Gets this game world objects cell in the tiled map.
     *
     * @return  the objects cell
     */
    public TiledMapTileLayer.Cell getCell() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell(
                (int) (body.getPosition().x * MarioBros.PPM / 16),
                (int) (body.getPosition().y * MarioBros.PPM / 16));
    }
}
