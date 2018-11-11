package com.andrejlohn.mariobros.sprites;

import com.andrejlohn.mariobros.MarioBros;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * This represents the player character in the game.
 * The class extends the libGDX Sprite class.
 *
 * @version %I%, %G%
 * @see     Sprite
 */
public class Mario extends Sprite {

    public World world;
    public Body b2Body;

    /**
     * Creates the player character within the game world.
     *
     * @param world the game world
     * @see         World
     * @see         #defineMario()
     */
    public Mario(World world) {
        this.world = world;
        defineMario();
    }

    /**
     * Defines the body and fixture of the player character.
     * For now the character will just be a circle.
     *
     * @see BodyDef#position
     * @see BodyDef#type
     * @see com.badlogic.gdx.physics.box2d.BodyDef.BodyType#DynamicBody
     * @see World#createBody(BodyDef)
     * @see FixtureDef#shape
     * @see CircleShape#setRadius(float)
     * @see Body#createFixture(FixtureDef)
     */
    public void defineMario() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / MarioBros.PPM);

        fDef.shape = shape;
        b2Body.createFixture(fDef);
    }
}
