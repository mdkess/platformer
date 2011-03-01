package ca.kess.games.camera;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import ca.kess.games.World;
import ca.kess.games.entities.PhysicalEntity;

/**
 * A following camera is a camera which follows a given entity within the world.
 * It renders based on the position of the entity, offset by a constant amount.
 * 
 * @author mdkess
 *
 */
public class FollowCamera implements BasicCamera {

	private PhysicalEntity target;
	private Vector2f offset;
	
	public FollowCamera(PhysicalEntity target, Vector2f offset) {
		this.target = target;
		this.offset = offset;
	}
	public FollowCamera(PhysicalEntity target) {
		this(target, new Vector2f(0,0));
	}
	public Vector2f getOffset() {
		return offset;
	}
	public void setOffset(Vector2f offset) {
		this.offset = offset;
	}
	public void setTarget(PhysicalEntity target) {
		this.target = target;
	}
	public PhysicalEntity getTarget() {
		return target;
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, World world, Graphics g) {
		float scaleFactor = 2.0f;

		g.translate((int)container.getWidth()/2, (int)container.getHeight()/2);
		g.scale(scaleFactor, scaleFactor);
		g.translate((int)-target.getPosition().x, (int)-target.getPosition().y);
		world.render(g);
	}
	@Override
	public void update(float dt) {
		
	}

}
