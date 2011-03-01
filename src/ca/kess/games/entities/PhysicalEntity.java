package ca.kess.games.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import ca.kess.games.World;
import ca.kess.games.interfaces.Renderable;
import ca.kess.games.physics.AABB;
import ca.kess.games.physics.ForceSet;
import ca.kess.games.physics.ForceType;

/**
 * A physical entity is an entity that can move around in the world - it now has position
 * (stored through the internal AABB), velocity and mass.
 * @author mdkess
 *
 */
public class PhysicalEntity extends GameEntity implements Renderable {
	private float mass;
	private Vector2f position;
	private Vector2f velocity;
	private Vector2f maximumVelocity;
	private ForceSet forces;
	private AABB aabb;
	private Color color;
	private float brakingForceMultiplier = 1.0f;
	private float airBrakingForce = 0.0f;
	private float airResistance = 1.0f;
	private float restitution = 1.0f;   //A float in [0, 1]. How bouncy the object is.
	
	/**** STATE VARIABLES ****/
	private Vector2f _prevVelocity;   //Velocity the last frame
	private Vector2f _prevPosition;   //Position the last frame
	private Vector2f _netForce;       //The net force applied to the object.

	/**
	 * This method updates various state variables, so that they don't have to be
	 * calculated multiple times per frame.
	 */
	private void updateState() {
		_netForce = getForces().getNetForce();
		_prevVelocity = new Vector2f(velocity);
		_prevPosition = new Vector2f(position);
	}
	
	
	public PhysicalEntity(AABB aabb, Vector2f position, float mass, World world) {
		super(world);
		velocity = new Vector2f();
		this.aabb = aabb;
		this.mass = mass;
		forces = new ForceSet();
		this.position = position;
		color = (Color) Color.red;
		maximumVelocity = new Vector2f(256,256);
	}
	
	//Return whether this collides with the given position/aabb pair
	public static boolean checkCollision(Vector2f p1, AABB aabb1, Vector2f p2, AABB aabb2) {
		return p1.x                     < p2.x + aabb2.getWidth()  &&
		       p1.x + aabb1.getWidth()  > p2.x                     &&
		       p1.y                     < p2.y + aabb2.getHeight() &&
		       p1.y + aabb1.getHeight() > p2.y;
	}
	
	public float getBrakingForceMultiplier() {
		return brakingForceMultiplier;
	}
	
	public void setBrakingForceMultiplier(float brakingForceMultiplier) {
		this.brakingForceMultiplier = brakingForceMultiplier;
	}
	
	public float getAirBrakingForce() {
		return airBrakingForce;
	}
	
	public void setAirBrakingForce(float airBrakingForce) {
		this.airBrakingForce = airBrakingForce;
	}
	
	public float getMass() {
		return mass;
	}

	public void setMaximumVelocity(Vector2f maximumVelocity) {
		this.maximumVelocity = new Vector2f(maximumVelocity);
	}
	public Vector2f getPosition() {
		return new Vector2f(position);
	}
	public Vector2f getPreviousPosition() {
		return new Vector2f(_prevPosition);
	}
	
	public void setPosition(Vector2f position) {
		this.position = new Vector2f(position);
	}
	
	public Vector2f getVelocity() {
		return new Vector2f(velocity);
	}
	
	public void setVelocity(Vector2f velocity) {
		this.velocity = new Vector2f(velocity);
	}

	public ForceSet getForces() {
		return forces;
	}

	public AABB getAabb() {
		return aabb;
	}
	
	public void addForce(ForceType type, Vector2f force) {
		forces.applyForce(type, force);
	}
	
	public void removeForce(ForceType type) {
		forces.removeForce(type);
	}

	public void updateForces() {
		
	}
	
	private float calculateFriction(Vector2f netForce) {
		//Take the maximum u_k of the two feet
		boolean leftTileBottomSolid  = getWorld().getMap().isSolidReal(position.getX(),                   position.getY() + aabb.getHeight() + 1.0f);
		boolean rightTileBottomSolid = getWorld().getMap().isSolidReal(position.getX() + aabb.getWidth(), position.getY() + aabb.getHeight() + 1.0f);
		
		if(leftTileBottomSolid || rightTileBottomSolid) {
			//setColor((Color)Color.RED);
			float leftFriction  = getWorld().getMap().getFrictionReal(position.getX(),                   position.getY() - 1.0f);
			float rightFriction = getWorld().getMap().getFrictionReal(position.getX() + aabb.getWidth(), position.getY() - 1.0f);
			float u_k = Math.max(leftFriction, rightFriction);
			float friction = netForce.getY() * u_k * 1.0f;

			if(friction < 0.0f) {
				//System.out.println("Warning - negative friction. Friction removed: " + friction);
				friction = 0.0f;
			}
			//Add breaking force if velocity is 0.
			if(Math.abs(netForce.getX()) < 0.1f) {
				friction *= getBrakingForceMultiplier();
			}
			return friction;
		} else {
			//We're airborne
			if(Math.abs(netForce.getX()) < 0.1f) {
				return airBrakingForce;
			}
		}
		//setColor((Color)Color.BLUE);
		return 0.0f;
	}
	
	public Vector2f calculateBuoyancy(Vector2f gravity) {
		//TODO THIS IS HAX
		float bouyancy  = -getWorld().getMap().getBouyancyReal(position.getX() + aabb.getWidth()/2, position.getY() + aabb.getHeight()/2)/3.0f;
		bouyancy  += -getWorld().getMap().getBouyancyReal(position.getX() + aabb.getWidth()/2, position.getY() + aabb.getHeight())/3.0f;
		bouyancy  += -getWorld().getMap().getBouyancyReal(position.getX() + aabb.getWidth()/2, position.getY())/3.0f;
		if(velocity.y > 0.0) {
			bouyancy *= 2;
		}
		return new Vector2f(gravity.x*bouyancy, gravity.y*bouyancy);
		
	}
	public void setVelocityX(float value) {
		velocity.x = (value);
	}
	public void setVelocityY(float value) {
		velocity.y = (value);
	}
	public float getVelocityX() {
		return velocity.getX();
	}
	public float getVelocityY() {
		return velocity.getY();
	}
	public void updateVelocity(float dt) {
		Vector2f netForce = _netForce;
		float friction = calculateFriction(netForce);
		Vector2f bouyancy = calculateBuoyancy(forces.getForce(ForceType.GRAVITY));

		netForce = netForce.add(bouyancy);
		//friction = 0.0f;
		//a = F/m
		Vector2f acceleration = new Vector2f(netForce.getX()/mass, netForce.getY()/mass);
		velocity.set(velocity.getX() + acceleration.getX()*dt, velocity.getY() + acceleration.getY()*dt);
		//Now add friction to the velocity 
		float frictionDeltaVelocity = (friction/mass)*dt;
		if(velocity.getX() > 0.0f) {
			velocity.x = (velocity.getX() - frictionDeltaVelocity);
			if(velocity.getX() < 0.0f) {
				velocity.x = (0.0f);
			}
		} else if(velocity.getX() < 0.0f) {
			velocity.x = (velocity.getX() + frictionDeltaVelocity);
			if(velocity.getX() > 0.0f) {
				velocity.x = (0.0f);
			}
		}
		if(velocity.getX() > maximumVelocity.getX()) {
			velocity.x = (maximumVelocity.getX());
		} else if(velocity.getX() < -maximumVelocity.getX()) {
			velocity.x = (-maximumVelocity.getX());
		} else if(velocity.getY() > maximumVelocity.getY()) {
			velocity.y = (maximumVelocity.getY());
		} else if(velocity.getY() < -maximumVelocity.getY()) {
			velocity.y = (-maximumVelocity.getY());
		}
	}

	public void update(float dt) {
		if(isDebug()) {
			System.out.println("v:" + velocity);
			System.out.println("p:" + position);
			System.out.println("f:" + _netForce);
			System.out.println();
		}
		updateState();
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void onCollisionX() {
		velocity.x = (0.0f);
	}
	
	public void onCollisionY() {
		velocity.y = (0.0f);
	}
	
	@Override
	public void render(Graphics g) {
		//We give this a default render function for debugging purposes.
		g.setColor(color);
		g.drawRect((int)position.x, (int)position.y, aabb.getWidth(), aabb.getHeight());
	}

	public void setAirResistance(float airResistance) {
		this.airResistance = airResistance;
	}

	public float getAirResistance() {
		return airResistance;
	}
}
