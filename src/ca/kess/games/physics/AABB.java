package ca.kess.games.physics;

import org.newdawn.slick.geom.Vector2f;

public class AABB {
	
	private final Vector2f dimensions;

	public AABB(int width, int height) {
		dimensions = new Vector2f(width, height);
	}
	
	public AABB(Vector2f dimensions) {
		this.dimensions = new Vector2f(dimensions.getX(), dimensions.getY());
	}
	
	public Vector2f getDimensions() {
		return new Vector2f(dimensions);
	}
	
	public float getWidth() {
		return dimensions.getX();
	}
	public float getHeight() {
		return dimensions.getY();
	}
	
}
