package ca.kess.games.entities;

import java.util.HashMap;
import java.util.Map;

import ca.kess.games.World;

public class GameEntity {
	private final int id;
	private static int nextID = 0;
	private final World world;
	private boolean debug;  //To be used on a per entity basis for showing debug information
	private boolean alive;	//Whether the entity should be updated.
	private Map<String, String> entityProperties;
	
	public GameEntity(World world) {
		id = ++nextID;
		this.world = world;
		this.debug = false;
		this.alive = true;
		entityProperties = new HashMap<String, String>();
	}

	/**
	 * Performs an update for this object.
	 * @param dt The time difference, in milliseconds.
	 */
	public void update(float dt) {
		
	}
	

	/**** FINAL METHODS ****/
	public final boolean isDebug() {
		return debug;
	}
	public final void setDebugMode(boolean debug) {
		this.debug = debug;
	}
	public final int id() {
		return id;
	}
	public final World getWorld() {
		return world;
	}
	public final boolean isAlive() {
		return alive;
	}
	public final void setAlive(boolean alive) {
		this.alive = alive;
	}
	public final void setProperty(String key, String value) {
		entityProperties.put(key, value);
	}
	public final String getProperty(String key) {
		return entityProperties.get(key);
	}
	public final void removeProperty(String key) {
		entityProperties.remove(key);
	}
}
