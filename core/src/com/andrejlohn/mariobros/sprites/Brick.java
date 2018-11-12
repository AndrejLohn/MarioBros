package com.andrejlohn.mariobros.sprites;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.scenes.Hud;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/**
 * This class represents an interactive brick in the game world. When the player character jumps
 * against a brick, the brick is destroyed.
 *
 * @version %I%, %G%
 * @see     InteractiveTileObject
 */
public class Brick extends InteractiveTileObject {

    /**
     * Creates the brick.
     *
     * @param world     the game world
     * @param map       the tiled map
     * @param bounds    this bricks bounding box
     */
    public Brick(World world, TiledMap map, Rectangle bounds) {
        super(world, map, bounds);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    /**
     * Reacts to a hit by the player characters head.
     * On hit this brick is destroyed.
     */
    @Override
    public void onHeadHit() {
        Gdx.app.log("Brick", "Collision");
        setCategoryFilter(MarioBros.DESTROYED_BIT);
        getCell().setTile(null);
        Hud.addScore(200);
        MarioBros.manager.get("audio/sounds/smb_breakblock.wav", Sound.class).play();
    }
}