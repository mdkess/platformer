package ca.kess.games.triggers;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import ca.kess.games.entities.PhysicalEntity;
import ca.kess.games.interfaces.Renderable;
import ca.kess.games.physics.AABB;

public class AreaTrigger implements Trigger, Triggerable, Renderable {
	private AABB aabb;
	private boolean activated;
	private Vector2f position;
	private String name;
	private List<Triggerable> children = new ArrayList<Triggerable>();
	
	public AreaTrigger(String name, Vector2f position, AABB aabb) {
		this.aabb = aabb;
		activated = false;
		this.position = position;
		this.name = name;
	}
	
	//Return whether the entity collides with the trigger
	public boolean checkCollision(Vector2f position, AABB aabb) {
		return (PhysicalEntity.checkCollision(position, aabb, this.position, this.aabb));
	}
	public Vector2f getPosition() {
		return position;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void addChild(Triggerable child) {
		children.add(child);
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub
		activated = true;
		
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(float dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void onEnter(PhysicalEntity entity) {
		System.out.println("onEnter(" + name  + ")");
		for(Triggerable t : children) {
			t.trigger(this);
		}
	}
	public void onUpdate(PhysicalEntity entity) {
		System.out.println("onUpdate(" + name + ")");
	}
	public void onExit(PhysicalEntity entity) {
		System.out.println("onExit(" + name + ")");
	}
	
	
	@Override
	public void render(Graphics g) {
		g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.2f));
		g.fillRect((int)position.x, (int)position.y, aabb.getWidth(), aabb.getHeight());
		//Draw a line to each child
		
		g.setColor(Color.black);
		g.drawString(getName(), (int)position.x+2, (int)position.y+2);
		g.setColor(Color.white);
		g.drawString(getName(), (int)position.x, (int)position.y);
		
	}

	@Override
	public void trigger(Trigger trigger) {
	}

}
