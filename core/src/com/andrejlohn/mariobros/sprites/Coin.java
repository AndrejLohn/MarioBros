package com.andrejlohn.mariobros.sprites;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.scenes.Hud;
import com.andrejlohn.mariobros.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;

/**
 * This class represents an interactive coin block in the game world. When the player character
 * jumps against a coin block, the brick is blanked.
 *
 * @version %I%, %G%
 * @see     InteractiveTileObject
 */
public class Coin extends InteractiveTileObject {

    private final int BLANK_COIN = 28;

    private static TiledMapTileSet tileSet;

    /**
     * Creates the coin.
     *
     * @param screen    the play screen
     * @param bounds    this coins bounding box
     * @see             PlayScreen
     * @see             Rectangle
     */
    public Coin(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }

    /**
     * Reacts to a hit by the player characters head.
     * On hit this coin is blanked and a sound is played.
     */
    @Override
    public void onHeadHit() {
        Gdx.app.log("Coin", "Collision");
        if(getCell().getTile().getId() == BLANK_COIN){
            MarioBros.manager.get("audio/sounds/smb_bump.wav", Sound.class).play();
        } else {
            MarioBros.manager.get("audio/sounds/smb_coin.wav", Sound.class).play();
            Hud.addScore(1000);
            getCell().setTile(tileSet.getTile(BLANK_COIN));
        }
    }
}
