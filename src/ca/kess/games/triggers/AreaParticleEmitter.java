package ca.kess.games.triggers;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import ca.kess.games.entities.PhysicalEntity;
import ca.kess.games.interfaces.Renderable;
import ca.kess.games.physics.AABB;

public class AreaParticleEmitter implements Triggerable, Renderable {
	private String name;
	private AABB aabb;
	private Vector2f position;
	private boolean triggered = false;
	
	//*** CONTROL VARIABLES ***//
	private int maxParticles;         //maximum number of particles this can own at once
	private int numParticlesToSpawn;  //number of particles to spawn in total. 0 for unlimited
	private Vector2f minWidth;        //minimum size of particle
	private Vector2f maxWidth;        //maximum width of particle
	private float particleMass;       //mass of particle (const for now)
	private Vector2f minVelocity;     //minimum velocity of a new particle
	private Vector2f maxVelocity;     //maximum velocity of a new particle
	private float emitRate;           //Rate of particles to spawned
	
	private int lastSpawn;            //Time since the previous particle was spawned
	private List<PhysicalEntity> particles;
	
	
	public AreaParticleEmitter(String name, Vector2f position, AABB aabb) {
		this.name = name;
		this.position = position;
		this.aabb = aabb;
	}
	
	public String getName() {
		return name;
	}
	public AABB getAabb() {
		return aabb;
	}
	public Vector2f getPosition() {
		return position;
	}

	@Override
	public void trigger(Trigger trigger) {
		System.out.println("Triggered!");
		triggered = true;
	}
	
	@Override
	public void render(Graphics g) {
		g.setColor(new Color(1.0f, 0.0f, 1.0f, 0.2f));
		g.fillRect((int)position.x, (int)position.y, aabb.getWidth(), aabb.getHeight());
		g.setColor(Color.black);
		g.drawString(getName(), (int)position.x+2, (int)position.y+2);
		if(triggered) {
			g.setColor(Color.green);
		} else {
			g.setColor(Color.white);
		}
		g.drawString(getName(), (int)position.x, (int)position.y);
	}

}
