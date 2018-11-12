package com.andrejlohn.mariobros.sprites;

import com.andrejlohn.mariobros.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Enemy extends Sprite {

    protected World world;
    protected PlayScreen screen;
    public Body b2Body;


    public Enemy(PlayScreen screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        defineEnemy();
    }

    protected abstract void defineEnemy();
}
