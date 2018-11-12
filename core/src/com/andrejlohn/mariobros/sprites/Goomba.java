package com.andrejlohn.mariobros.sprites;

import com.andrejlohn.mariobros.MarioBros;
import com.andrejlohn.mariobros.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;

public class Goomba extends Enemy {

    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;

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
    }

    public void update(float dt) {
        stateTime += dt;
        setPosition(
                b2Body.getPosition().x - getWidth() / 2,
                b2Body.getPosition().y - getHeight() / 2);
        setRegion(walkAnimation.getKeyFrame(stateTime, true));
    }

    @Override
    protected void defineEnemy() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
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
        b2Body.createFixture(fDef);
    }
}
