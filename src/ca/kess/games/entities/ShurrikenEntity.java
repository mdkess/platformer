package ca.kess.games.entities;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import ca.kess.games.World;
import ca.kess.games.interfaces.Animatable;
import ca.kess.games.physics.AABB;

/**
 * The shirriken sticks to walls and the ground.
 * @author mdkess
 *
 */
public class ShurrikenEntity extends PhysicalEntity implements Animatable {
	private Animation currentAnimation;
	private Map<String, Animation> animations;
	
	public ShurrikenEntity(AABB aabb, Vector2f position, float mass, World world) {
		super(aabb, position, mass, world);
		animations = new HashMap<String, Animation>();
		currentAnimation = null;
	}
	
	@Override
	public void onCollisionX() {
		setAlive(false);
	}
	@Override
	public void onCollisionY() {
		setAlive(false);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if(currentAnimation != null) {
			currentAnimation.update((long) (dt * 1000.0f));
		}
	}


	@Override
	public void addAnimation(String name, Animation animation) {
		animations.put(name, animation);
	}

	@Override
	public void setCurrentAnimation(String name) {
		currentAnimation = animations.get(name);
	}
	
	@Override
	public void render(Graphics g) {
		if(currentAnimation != null) {
			currentAnimation.draw((int)getPosition().x, (int)getPosition().y, getAabb().getWidth(), getAabb().getHeight());
		} else {
			super.render(g);
		}
	}

}
