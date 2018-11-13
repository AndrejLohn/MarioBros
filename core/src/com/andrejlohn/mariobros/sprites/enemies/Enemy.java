package com.andrejlohn.mariobros.sprites.enemies;

import com.andrejlohn.mariobros.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Enemy extends Sprite {

    protected World world;
    protected PlayScreen screen;
    public Body b2Body;
    public Vector2 velocity;


    public Enemy(PlayScreen screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(-1, -2);
        b2Body.setActive(false);
    }

    protected abstract void defineEnemy();

    public abstract void hitOnHead();

    public abstract void update(float dt);

    public void reverseVelocity(boolean x, boolean y) {
        if(x) {
            velocity.x = -velocity.x;
        }
        if(y) {
            velocity.y = -velocity.y;
        }
    }
}