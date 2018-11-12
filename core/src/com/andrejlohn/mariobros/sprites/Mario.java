package com.andrejlohn.mariobros.sprites;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.screens.PlayScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * This represents the player character in the game.
 * The class extends the libGDX Sprite class.
 *
 * @version %I%, %G%
 * @see     Sprite
 */
public class Mario extends Sprite {

    public enum State { FALLING, JUMPING, STANDING, RUNNING };
    public State currentState;
    public State previousState;

    public World world;
    public Body b2Body;

    private TextureRegion marioStand;
    private Animation<TextureRegion> marioRun;
    private Animation<TextureRegion> marioJump;
    private boolean runningRight;
    private float stateTimer;

    /**
     * Creates the player character within the game world. Sets up the move animations.
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

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        // Set up the run animation
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i=1; i<4; i++){
            frames.add(new TextureRegion(getTexture(), 1 + i * 16, 11, 16, 16));
        }
        marioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        // Set up the jump animation
        for(int i=4; i<6; i++){
            frames.add(new TextureRegion(getTexture(), 1 + i * 16, 11, 16, 16));
        }
        marioJump = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        marioStand = new TextureRegion(getTexture(), 1, 11, 16, 16);

        defineMario();
        setBounds(1, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setRegion(marioStand);
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

        setRegion(getFrame(dt));
    }

    /**
     * Gets the current frame of the character animation with respect to the time passed.
     *
     * @param dt    the time passed
     * @return      the current animation frame
     */
    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;

        switch(currentState) {
            case JUMPING:
                region = marioJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioStand;
                break;
        }

        if((b2Body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if((b2Body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    /**
     * Gets the current state the player character is in.
     *
     * @return  the current state
     * @see     Body#getLinearVelocity()
     */
    public State getState() {
        if(b2Body.getLinearVelocity().y > 0
                || (b2Body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
            return State.JUMPING;
        }
        if(b2Body.getLinearVelocity().y < 0) {
            return State.FALLING;
        }
        if(b2Body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        }
        return State.STANDING;
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
        shape.setRadius(6 / MarioBros.PPM);
        fDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fDef.filter.maskBits = MarioBros.DEFAULT_BIT | MarioBros.BRICK_BIT | MarioBros.COIN_BIT;

        fDef.shape = shape;
        b2Body.createFixture(fDef);

        // Additional shape to act as the characters feet. this avoids the issue of a jump animation
        // trigger if the character walks over a connection between game objects.
        EdgeShape feet = new EdgeShape();
        feet.set(
                new Vector2(-2 / MarioBros.PPM, -6 / MarioBros.PPM),
                new Vector2(2 / MarioBros.PPM, -6 / MarioBros.PPM));
        fDef.shape = feet;
        b2Body.createFixture(fDef);

        EdgeShape head = new EdgeShape();
        head.set(
                new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM),
                new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fDef.shape = head;
        fDef.isSensor = true;
        b2Body.createFixture(fDef).setUserData("head");
    }
}
