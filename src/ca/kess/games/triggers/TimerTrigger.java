package ca.kess.games.triggers;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import ca.kess.games.interfaces.Renderable;
import ca.kess.games.physics.AABB;

/**
 * A trigger which fires when the timer goes off
 * @author mdkess
 *
 */
public class TimerTrigger implements Trigger, Triggerable, Renderable {
	
	private String name;
	private float duration;
	private float timeLeft;
	private boolean triggered;
	private List<Triggerable> children;
	private boolean activated;
	private Vector2f position;
	private AABB aabb;
	public TimerTrigger(String name, Vector2f position, AABB aabb, float duration) {
		this.name = name;
		this.duration = duration;
		timeLeft = duration;
		activated = false;
		triggered = false;
		children = new ArrayList<Triggerable>();
		this.aabb = aabb;
		this.position = position;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public float getDuration() {
		return duration;
	}

	@Override
	public void addChild(Triggerable child) {
		children.add(child);
		
	}

	@Override
	public void activate() {
		for(Triggerable child : children) {
			child.trigger(this);
		}
	}

	@Override
	public void deactivate() {
		
	}

	@Override
	public void update(float dt) {
		if(!triggered) {
			return;
		}
		timeLeft -= dt;
		if(timeLeft < 0) {
			timeLeft = 0;
			triggered = false;
			if(!activated) {
				activated = true;
				activate();
			}
		}
		
	}

	@Override
	public void reset() {
		timeLeft = duration;
		activated = false;
		triggered = false;
	}

	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		g.setColor(new Color(1.0f, 1.0f, 0.0f, 0.2f));
		g.fillRect((int)position.x, (int)position.y, aabb.getWidth(), aabb.getHeight());
		g.setColor(Color.black);
		g.drawString(getName() + ": " + String.format("%.2f", timeLeft), (int)position.x+2, (int)position.y+2);
		g.setColor(Color.white);
		g.drawString(getName() + ": " + String.format("%.2f", timeLeft), (int)position.x, (int)position.y);
		
		//Draw sexy health box
		g.setColor(Color.white);
		g.drawRect((int)position.x, (int)position.y+20, 30, 8);
		g.setColor(Color.green);
		g.fillRect((int)position.x+1, (int)position.y+21, 29*(timeLeft/duration), 7);
	}

	@Override
	public void trigger(Trigger trigger) {
		triggered = true;
	}

}
