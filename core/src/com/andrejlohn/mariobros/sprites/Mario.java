package com.andrejlohn.mariobros.sprites;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.screens.PlayScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private TextureRegion marioStand;

    /**
     * Creates the player character within the game world.
     *
     * @param world the game world
     * @see         #defineMario()
     * @see         World
     * @see         TextureRegion
     * @see         com.badlogic.gdx.graphics.g2d.TextureAtlas#findRegion(String)
     * @see         Sprite#setBounds(float, float, float, float)
     * @see         Sprite#setRegion(Texture)
     */
    public Mario(World world, PlayScreen screen) {
        super(screen.getTextureAtlas().findRegion("little_mario"));
        this.world = world;
        defineMario();
        marioStand = new TextureRegion(getTexture(), 1, 11, 16, 16);
        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setRegion(marioStand);
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
        shape.setRadius(7.5f / MarioBros.PPM);

        fDef.shape = shape;
        b2Body.createFixture(fDef);
    }

    /**
     * Updates the player character based on the time passed since the ast update.
     *
     * @param dt    the time since the last update
     * @see         Sprite#setPosition(float, float)
     */
    public void update(float dt) {
        setPosition(
                b2Body.getPosition().x - getWidth() / 2,
                b2Body.getPosition().y - getHeight() / 2);
    }
}
