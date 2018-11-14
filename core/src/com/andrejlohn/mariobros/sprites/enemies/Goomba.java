package com.andrejlohn.mariobros.sprites.enemies;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.screens.PlayScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

/**
 * This class represents a Goomba enemy character.
 * Goombas will only move horizontally and change direction when bumping into an object. Jumping on
 * their head will "kill" them. Each other form of contact with the player character will cause
 * character death.
 *
 * @version %I%, %G%
 * @see     Enemy
 */
public class Goomba extends Enemy {

    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;

    /**
     * Creates the Goomba. Sets the Goombas animation and bounding box. Positions the Goomba in the
     * game world according to a given position.
     *
     * @param screen    the play screen
     * @param x         the position x-coordinate
     * @param y         the position y-coordinate
     */
    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for(int i=0; i<2; i++) {
            frames.add(
                    new TextureRegion(
                            screen.getTextureAtlas().findRegion("goomba"),
                            i * 16,
                            0,
                            16,
                            16));
        }
        walkAnimation = new Animation<TextureRegion>(0.4f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);

        setToDestroy = false;
        destroyed = false;
    }

    /**
     * Updates the Gommba with respect to the time passed since the last update. Only not destroyed
     * Goombas will be updated. Stomped Goombas will be destroyed.
     *
     * @param dt    the time passed
     * @see         com.badlogic.gdx.physics.box2d.World#destroyBody(Body)
     * @see         Enemy#setRegion(Texture)
     * @see         Enemy#setPosition(float, float)
     * @see         Body#setLinearVelocity(Vector2)
     */
    public void update(float dt) {
        stateTime += dt;
        if(setToDestroy && !destroyed) {
            world.destroyBody(b2Body);
            destroyed = true;
            setRegion(new TextureRegion(
                    screen.getTextureAtlas().findRegion("goomba"),
                    32,
                    0,
                    16,
                    16));
            stateTime = 0;
        } else if(!destroyed) {
            b2Body.setLinearVelocity(velocity);
            setPosition(
                    b2Body.getPosition().x - getWidth() / 2,
                    b2Body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    /**
     * Defines the Goombas bounding box and sets the bits for collision detection. In addition a
     * head fixture is created to detect stomping.
     *
     * @see BodyDef#position
     * @see BodyDef#type
     * @see com.badlogic.gdx.physics.box2d.World#createBody(BodyDef)
     * @see CircleShape#setRadius(float)
     * @see FixtureDef#filter
     * @see FixtureDef#shape
     * @see FixtureDef#restitution
     * @see Body#createFixture(FixtureDef)
     * @see com.badlogic.gdx.physics.box2d.Fixture#setUserData(Object)
     * @see PolygonShape#set(Vector2[])
     */
    @Override
    protected void defineEnemy() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(getX(), getY());
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fDef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.COIN_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.MARIO_BIT;

        fDef.shape = shape;
        b2Body.createFixture(fDef).setUserData(this);

        // Create the Head
        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-5, 8).scl(1 / MarioBros.PPM);
        vertices[1] = new Vector2(5, 8).scl(1 / MarioBros.PPM);
        vertices[2] = new Vector2(-3, 3).scl(1 / MarioBros.PPM);
        vertices[3] = new Vector2(3, 3).scl(1 / MarioBros.PPM);
        head.set(vertices);

        fDef.shape = head;
        fDef.restitution = 0.5f;
        fDef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2Body.createFixture(fDef).setUserData(this);
    }

    /**
     * Draws the Goomba to the screen. A Goomba stomped at least a second ago will no longer be
     * drawn.
     *
     * @param batch the sprite batch
     * @see         Enemy#draw(Batch)
     */
    public void draw(Batch batch) {
        if(!destroyed || stateTime < 1) {
            super.draw(batch);
        }
    }

    /**
     * Reatchs to the Goomba being stomped. Sets the Goomba to be destroyed in the next update
     * cycle.
     */
    @Override
    public void hitOnHead() {
        setToDestroy = true;
    }
}
