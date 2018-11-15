package com.andrejlohn.mariobros.sprites.enemies;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

public class Turtle extends Enemy {

    public enum State { WALKING, SHELL}
    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private TextureRegion shell;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getTextureAtlas()
                .findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(screen.getTextureAtlas()
                .findRegion("turtle"), 16, 0, 16, 24));
        //TODO animate the shell
        shell = new TextureRegion(screen.getTextureAtlas()
                        .findRegion("turtle"), 64, 0, 16, 24);

        walkAnimation = new Animation<TextureRegion>(0.2f, frames);

        currentState = previousState = State.WALKING;

        setBounds(getX(), getY(), 16 / MarioBros.PPM, 24 / MarioBros.PPM);
    }

    public TextureRegion getFrame(float dt) {
        TextureRegion region;

        switch(currentState) {
            case SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if(velocity.x > 0 && !region.isFlipX()) {
            region.flip(true, false);
        }

        if(velocity.x < 0 && region.isFlipX()) {
            region.flip(true, false);
        }

        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

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

    @Override
    public void hitOnHead() {
        if(currentState != State.SHELL) {
            currentState = State.SHELL;
            velocity.x = 0;
        }
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if(currentState == State.SHELL && stateTime > 5) {
            currentState = State.WALKING;
            velocity.x = 1;
        }

        setPosition(
                b2Body.getPosition().x - getWidth() / 2,
                b2Body.getPosition().y - 8 / MarioBros.PPM);
        b2Body.setLinearVelocity(velocity);
    }
}
