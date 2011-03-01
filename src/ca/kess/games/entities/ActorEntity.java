package ca.kess.games.entities;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import ca.kess.games.Trail;
import ca.kess.games.World;
import ca.kess.games.interfaces.Animatable;
import ca.kess.games.physics.AABB;
import ca.kess.games.physics.ForceType;

public class ActorEntity extends PhysicalEntity implements Animatable {
	private float jumpDuration;
	private boolean doubleJumped;
	private boolean jumping;
	private boolean grabbing;
	private DirectionType direction;
	private float timeBetweenGrabs = 0.0f; //You can only grab every 0.5 seconds
	private float doubleJumpDuration = 0.0f;
	private Trail trail;
	private Animation currentAnimation;
	private Map<String, Animation> animations;
	
	public ActorEntity(AABB aabb, Vector2f position, float mass, World world) {
		super(aabb, position, mass, world);
		jumpDuration = 0.0f;
		jumping = false;
		doubleJumped = false;
		trail = new Trail(120);
		animations = new HashMap<String, Animation>();
		grabbing = false;
	}
	public void setFacingDirection(DirectionType direction) {
		this.direction = direction;
	}
	public DirectionType getFacingDirection() {
		return direction;
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
	public void update(float dt) {
		super.update(dt);
		trail.addPoint(new Vector2f((int)(getPosition().getX() + 0.5f * getAabb().getWidth()),
				                    (int)(getPosition().getY() + 0.5f * getAabb().getHeight())));
	}
	public boolean canGrab() {
		return timeBetweenGrabs <= 0.0f;
	}
	
	public void setGrabbing(boolean grabbing, boolean left) {
		this.grabbing = grabbing;
		if(grabbing) {
			jumping = false;
			setVelocity(new Vector2f(0.0f, 0.0f)); //reset velocity;
			if(left) {
				setCurrentAnimation("GRAB_LEFT");
			} else {
				setCurrentAnimation("GRAB_RIGHT");
			}
		} else {
			timeBetweenGrabs = 0.5f;
			if(direction == DirectionType.LEFT){ 
				setCurrentAnimation("WALK_LEFT");
			} else {
				setCurrentAnimation("WALK_RIGHT");
			}
		}
	}
	public boolean isGrabbing() {
		return grabbing;
	}
	public void jump(Vector2f force) {
		jumping = true;
		jumpDuration = 0.01f;	//100th of a second
		setVelocityY(0.0f);
		getForces().applyForce(ForceType.JUMP, force);
	}
	public void doubleJump(Vector2f force) {
		doubleJumped = true;
		doubleJumpDuration = 0.01f;
		setVelocityY(0.0f);
		getForces().applyForce(ForceType.DOUBLE_JUMP, force);
		
	}
	public boolean doubleJumped() {
		return doubleJumped;
	}
	public void stopJump() {
		jumping = false;
		getForces().removeForce(ForceType.JUMP);
	}

	public boolean isOnWallLeft() {
		Vector2f position = getPosition();
		return getWorld().getMap().isSolidReal(position.getX() - 1.0f, position.getY());
	}
	public boolean isOnWallRight() {
		Vector2f position = getPosition();
		AABB aabb = getAabb();
		return getWorld().getMap().isSolidReal(position.getX() + 1.0f + aabb.getWidth(), position.getY());
	}
	
	public boolean canJump() {
		Vector2f position = getPosition();
		AABB aabb = getAabb();
		boolean leftTileSolid  = getWorld().getMap().isSolidReal(position.getX(),                   position.getY() + aabb.getHeight() + 1.0f);
		boolean rightTileSolid = getWorld().getMap().isSolidReal(position.getX() + aabb.getWidth(), position.getY() + aabb.getHeight() + 1.0f);
		return leftTileSolid || rightTileSolid;
	}
	
	@Override
	public void updateVelocity(float dt) {
		timeBetweenGrabs -= dt;
		if(grabbing) {
			return;	//no need to update velocity if we are grabbing the edge.
		}
		if(currentAnimation != null) {
			currentAnimation.update((long) Math.ceil(dt * 1000.0f));
		}
		super.updateVelocity(dt);
		
		jumpDuration -= dt;
		doubleJumpDuration -= dt;
		
		if(jumping && jumpDuration < 0.0f) {
			removeForce(ForceType.JUMP);
		}
		if(doubleJumped && doubleJumpDuration < 0.0f) {
			removeForce(ForceType.DOUBLE_JUMP);

		}
		
		if(canJump()) {
			stopJump();
			doubleJumped = false;
		}
		/*
		if(doubleJumpDuration < 0.0f) {
			removeForce(ForceType.DOUBLE_JUMP);
		}
		doubleJumpDuration -= dt;
		if(canJump() || isOnWallLeft() || isOnWallRight()) {
			doubleJumped = false;
		}
		
		if(jumping) {
			if(jumpDuration < 0.0f) {
				jumping = false;
				getForces().removeForce(ForceType.JUMP);
			}
			jumpDuration -= dt;
		}*/
	}
	@Override
	public void render(Graphics g) {
		trail.render(g);
		if(isDebug()) {
			//Render force in blue, velocity in green
			Vector2f netForce = getForces().getNetForce();
			netForce = netForce.normalise();
			g.setColor(Color.blue);
			Vector2f position = getPosition();
			AABB aabb = getAabb();
			int x = (int) (position.x + aabb.getWidth()/2.0f);
			int y = (int) (position.y + aabb.getHeight()/2.0f);
			int length = 50;
			g.drawLine(x, y, x + netForce.x*length, y + netForce.y*length);
			
			g.setColor(Color.green);
			Vector2f velocity = getVelocity();
			velocity = velocity.normalise();
			g.drawLine(x, y, x + velocity.x*length, y + velocity.y*length);
		}
		if(currentAnimation != null) {
			currentAnimation.draw((int)getPosition().x, (int)getPosition().y, getAabb().getWidth(), getAabb().getHeight());
		} else {
			super.render(g);
		}
	}
}
