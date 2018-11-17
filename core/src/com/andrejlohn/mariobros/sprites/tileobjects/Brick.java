package com.andrejlohn.mariobros.sprites.tileobjects;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.scenes.Hud;
import com.andrejlohn.mariobros.screens.PlayScreen;
import com.andrejlohn.mariobros.sprites.Mario;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;

/**
 * This class represents an interactive brick in the game world. When the player character jumps
 * against a brick, the brick is destroyed.
 *
 * @version %I%, %G%
 * @see     InteractiveTileObject
 */
public class Brick extends InteractiveTileObject {

    AssetManager manager;

    /**
     * Creates the brick.
     *
     * @param screen    the play screen
     * @param object    the map object
     * @see             PlayScreen
     * @see             Rectangle
     */
    public Brick(PlayScreen screen, MapObject object, AssetManager manager) {
        super(screen, object);
        this.manager = manager;
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    /**
     * Reacts to a hit by the player characters head.
     * On hit this brick is destroyed and a sound is played.
     */
    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig()) {
            Gdx.app.log("Brick", "Collision");
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            manager.get("audio/sounds/smb_breakblock.wav", Sound.class).play();
        } else {
            manager.get("audio/sounds/smb_bump.wav", Sound.class).play();
        }
    }
}