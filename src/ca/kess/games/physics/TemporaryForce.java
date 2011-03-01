package ca.kess.games.physics;

import org.newdawn.slick.geom.Vector2f;

public class TemporaryForce {
	private float duration;
	private Vector2f magnitude;
	
	public TemporaryForce(Vector2f magnitude, float duration) {
		this.magnitude = magnitude;
		this.duration = duration;
	}
	
	public Vector2f getMagnitude(float dt) {
		return magnitude;
	}
	public void setMagnitude(Vector2f magnitude) {
		
	}
	
	
}
