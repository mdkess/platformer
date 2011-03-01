package ca.kess.games.physics;

import org.newdawn.slick.geom.Vector2f;

public class BasicForce {
	private Vector2f magnitude; 
	public BasicForce(Vector2f magnitude) {
		this.magnitude = new Vector2f(magnitude);
	}
	public Vector2f getMagnitude() {
		return new Vector2f(magnitude);
	}
	
	public void setMagnitude(Vector2f magnitude) {
		this.magnitude = new Vector2f(magnitude);
	}
}
