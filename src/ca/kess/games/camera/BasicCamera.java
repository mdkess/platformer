package ca.kess.games.camera;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import ca.kess.games.World;

public interface BasicCamera {
	public void render(GameContainer container, StateBasedGame game, World world, Graphics g);
	public void update(float dt);
	
}
