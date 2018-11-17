package com.andrejlohn.mariobros.sprites.tileobjects;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.scenes.Hud;
import com.andrejlohn.mariobros.screens.PlayScreen;
import com.andrejlohn.mariobros.sprites.Mario;
import com.andrejlohn.mariobros.sprites.items.ItemDef;
import com.andrejlohn.mariobros.sprites.items.Mushroom;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

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

    private AssetManager manager;

    /**
     * Creates the coin.
     *
     * @param screen    the play screen
     * @param object    the map object
     * @see             PlayScreen
     * @see             Rectangle
     */
    public Coin(PlayScreen screen, MapObject object, AssetManager manager) {
        super(screen, object);
        this.manager = manager;
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }

    /**
     * Reacts to a hit by the player characters head.
     * On hit this coin is blanked and a sound is played.
     */
    @Override
    public void onHeadHit(Mario mario) {
        if (getCell().getTile().getId() == BLANK_COIN) {
            manager.get("audio/sounds/smb_bump.wav", Sound.class).play();
        } else if (object.getProperties().containsKey("mushroom")) {
            screen.spawnItem(
                    new ItemDef(
                            new Vector2(
                                    body.getPosition().x,
                                    body.getPosition().y + 16 / MarioBros.PPM),
                                Mushroom.class));
            manager.get("audio/sounds/smb_powerup_appears.wav", Sound.class).play();
        } else {
            manager.get("audio/sounds/smb_coin.wav", Sound.class).play();
        }
        Hud.addScore(100);
        getCell().setTile(tileSet.getTile(BLANK_COIN));
    }
}
