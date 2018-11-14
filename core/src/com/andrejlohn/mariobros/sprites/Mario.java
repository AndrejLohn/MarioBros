package com.andrejlohn.mariobros.sprites;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.screens.PlayScreen;
import com.badlogic.gdx.audio.Sound;
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

    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING };
    public State currentState;
    public State previousState;

    public World world;
    public Body b2Body;

    private TextureRegion marioStand;
    private TextureRegion marioJump;
    private Animation<TextureRegion> marioRun;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;

    private float stateTimer;
    private boolean runningRight;
    private boolean isBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;

    /**
     * Creates the player character within the game world. Sets up the move animations.
     *
     * @param screen    the play screen
     * @see             #defineMario()
     * @see             PlayScreen
     * @see             World
     * @see             TextureRegion
     * @see             com.badlogic.gdx.graphics.g2d.TextureAtlas#findRegion(String)
     * @see             Sprite#setBounds(float, float, float, float)
     * @see             Sprite#setRegion(Texture)
     */
    public Mario(PlayScreen screen) {
        this.world = screen.getWorld();

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        // Set up the run animations
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i=1; i<4; i++){
            frames.add(new TextureRegion(screen.getTextureAtlas()
                    .findRegion("little_mario"), i * 16, 0, 16, 16));
        }
        marioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for(int i=1; i<4; i++){
            frames.add(new TextureRegion(screen.getTextureAtlas()
                    .findRegion("big_mario"), i * 16, 0, 16, 32));
        }
        bigMarioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        // Set up the grow animation
        frames.add(new TextureRegion(screen.getTextureAtlas()
                .findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getTextureAtlas()
                .findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getTextureAtlas()
                .findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getTextureAtlas()
                .findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();

        // Set up the jump animations
        marioJump = new TextureRegion(screen.getTextureAtlas()
                .findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getTextureAtlas()
                .findRegion("big_mario"), 80, 0, 16, 32);

        // Set the stand textures
        marioStand = new TextureRegion(screen.getTextureAtlas()
                .findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getTextureAtlas()
                .findRegion("big_mario"), 0, 0, 16, 32);

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
        if(isBig) {
            setPosition(
                    b2Body.getPosition().x - getWidth() / 2,
                    b2Body.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
        } else {
            setPosition(
                    b2Body.getPosition().x - getWidth() / 2,
                    b2Body.getPosition().y - getHeight() / 2);
        }

        setRegion(getFrame(dt));

        if(timeToDefineBigMario) {
            defineBigMario();
        }

        if(timeToRedefineMario) {
            redefineMario();
        }
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
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = isBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = isBig ?
                        bigMarioRun.getKeyFrame(stateTimer, true) :
                        marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = isBig ? bigMarioStand : marioStand;
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
        if(runGrowAnimation) {
            return State.GROWING;
        }
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
     * Grows the player character to its large size. Enlarges the bounding box.
     */
    public void grow() {
        runGrowAnimation = true;
        isBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
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
        bDef.position.set((16 * 7 + 8) / MarioBros.PPM, 32 / MarioBros.PPM);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.COIN_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fDef.shape = shape;
        b2Body.createFixture(fDef).setUserData(this);

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
        fDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;
        b2Body.createFixture(fDef).setUserData(this);
    }

    public void defineBigMario() {
        Vector2 currentPosition = b2Body.getPosition();
        world.destroyBody(b2Body);

        BodyDef bDef = new BodyDef();
        bDef.position.set(currentPosition.add(0, 10 / MarioBros.PPM));
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.COIN_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fDef.shape = shape;
        b2Body.createFixture(fDef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        b2Body.createFixture(fDef).setUserData(this);

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
        fDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;
        b2Body.createFixture(fDef).setUserData(this);

        timeToDefineBigMario = false;
    }


    /**
     * Gets the player characters size.
     *
     * @return  <code>true</code> if the character is big
     *          <code>false</code> else
     */
    public boolean isBig() {
        return isBig;
    }

    /**
     * Handles enemy hits on the player character.
     */
    public void hit() {
        if(isBig) {
            isBig = false;
            timeToRedefineMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() / 2);
            MarioBros.manager.get("audio/sounds/smb_pipe.wav", Sound.class).play();
        } else {
            MarioBros.manager.get("audio/music/smb_mariodie.wav", Sound.class).play();
        }
    }

    /**
     * Turns the player character from big to small.
     */
    public void redefineMario() {
        Vector2 position = b2Body.getPosition();
        world.destroyBody(b2Body);

        BodyDef bDef = new BodyDef();
        bDef.position.set(position.x, position.y);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.COIN_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fDef.shape = shape;
        b2Body.createFixture(fDef).setUserData(this);

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
        fDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;
        b2Body.createFixture(fDef).setUserData(this);

        timeToRedefineMario = false;
    }
}
