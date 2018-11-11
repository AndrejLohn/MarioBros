package com.andrejlohn.mariobros;

import com.andrejlohn.mariobros.Screens.PlayScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MarioBros extends Game {
	public SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
