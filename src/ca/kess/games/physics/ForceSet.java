package ca.kess.games.physics;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.geom.Vector2f;

public class ForceSet {
	private Map<ForceType, BasicForce> forceMap;
	
	public ForceSet() {
		forceMap = new HashMap<ForceType, BasicForce>();
	}
	
	public void applyForce(ForceType name, Vector2f force) {
		forceMap.put(name, new BasicForce(force));
	}
	
	public void removeForce(ForceType name) {
		forceMap.remove(name);
	}
	
	public Vector2f getNetForce() {
		Vector2f ret = new Vector2f();
		for(BasicForce v: forceMap.values()) {
			ret.set(ret.x + v.getMagnitude().x, ret.y + v.getMagnitude().y);
		}
		return ret;
	}
	
	public Vector2f getForce(ForceType name) {
		return forceMap.get(name).getMagnitude();
	}
	public boolean hasForce(ForceType name) {
		return forceMap.containsKey(name);
	}
	
}
