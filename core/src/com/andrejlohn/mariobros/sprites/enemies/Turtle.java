package com.andrejlohn.mariobros.sprites.enemies;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.screens.PlayScreen;
import com.andrejlohn.mariobros.sprites.Mario;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

public class Turtle extends Enemy {

    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;

    public enum State { WALKING, STANDING_SHELL, MOVING_SHELL, DEAD }

    public State currentState;
    public State previousState;
    private float stateTime;

    private Animation<TextureRegion> walkAnimation;
    private TextureRegion shell;
    private Array<TextureRegion> frames;

    private float deadRotationDegrees;
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

        deadRotationDegrees = 0;

        setBounds(getX(), getY(), 16 / MarioBros.PPM, 24 / MarioBros.PPM);
    }

    public TextureRegion getFrame(float dt) {
        TextureRegion region;

        switch(currentState) {
            case STANDING_SHELL:
            case MOVING_SHELL:
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

    public void kick(int speed) {
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public State getCurrentState() {
        return currentState;
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
        fDef.restitution = 1.5f;
        fDef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2Body.createFixture(fDef).setUserData(this);
    }

    @Override
    public void hitOnHead(Mario mario) {
        if(currentState != State.STANDING_SHELL) {
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        } else {
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if(currentState == State.STANDING_SHELL && stateTime > 5) {
            currentState = State.WALKING;
            velocity.x = 1;
        }

        setPosition(
                b2Body.getPosition().x - getWidth() / 2,
                b2Body.getPosition().y - 8 / MarioBros.PPM);

        if(currentState == State.DEAD) {
            deadRotationDegrees += 3;
            rotate(deadRotationDegrees);
            if(stateTime > 5 && !destroyed) {
                world.destroyBody(b2Body);
                destroyed = true;
            }
        } else {
            b2Body.setLinearVelocity(velocity);
        }
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle) {
            if(((Turtle) enemy).currentState == State.MOVING_SHELL &&
                    currentState != State.MOVING_SHELL) {
                killed();
            } else if(currentState == State.MOVING_SHELL &&
                    ((Turtle) enemy).currentState == State.WALKING) {
                return;
            } else {
                reverseVelocity(true, false);
            }
        } else if(currentState != State.MOVING_SHELL) {
            reverseVelocity(true, false);
        }
    }

    public void draw(Batch batch) {
        if(!destroyed) {
            super.draw(batch);
        }
    }

    public void killed() {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioBros.NOTHING_BIT;

        for(Fixture fixture: b2Body.getFixtureList()) {
            fixture.setFilterData(filter);
        }

        b2Body.applyLinearImpulse(new Vector2(0, 5f), b2Body.getWorldCenter(), true);


    }
}
