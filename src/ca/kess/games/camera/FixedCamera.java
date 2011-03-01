package ca.kess.games.camera;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import ca.kess.games.World;

/**
 * A fixed camera is a camera with a constant position. This position can be changed,
 * but the position of the camera is independent of entities within the world.
 * 
 * The camera's point will be centered on the screen.
 * 
 * @author mdkess
 *
 */
public class FixedCamera implements BasicCamera {
	private Vector2f position;

	public FixedCamera(Vector2f position) {
		this.position = position;
	}
	public FixedCamera(int x, int y) {
		this(new Vector2f(x,y));
	}
	
	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public Vector2f getPosition() {
		return position;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, World world, Graphics g) {
		//Switch coordinate system to have +y up.
		//g.scale(1.0f, -1.0f);
		g.translate(container.getWidth()/2 - position.x, container.getHeight()/2 - position.y);
		
		world.render(g);
	}

	@Override
	public void update(float dt) {
		// TODO Auto-generated method stub
		
	}
}
