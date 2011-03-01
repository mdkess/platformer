package ca.kess.games;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import ca.kess.games.entities.ActorEntity;
import ca.kess.games.entities.GameEntity;
import ca.kess.games.entities.PhysicalEntity;
import ca.kess.games.interfaces.Renderable;
import ca.kess.games.physics.AABB;
import ca.kess.games.triggers.AreaTrigger;

/**
 * The world tracks the state of all entities within the game.
 * @author mdkess
 *
 */
public class World implements Renderable {
	private List<GameEntity> entities;
	private Map map;
	public World(Map map) {
		this.map = map;
		entities = new ArrayList<GameEntity>();
	}
	
	public Map getMap() {
		return map;
	}
	
	public void update(float dt) {
		map.update(dt);
		for(GameEntity entity : entities) {
			if(!entity.isAlive()) {
				continue;
			}
			entity.update(dt);
			if(entity instanceof PhysicalEntity) {
				updatePhysicalEntity((PhysicalEntity) entity, dt);
			} else {
				updateGameEntity(entity, dt);
			}
		}
		updateTriggers();
	}
	private void updateTriggers() {
		Collection<AreaTrigger> triggers = map.getAreaTriggers().values();
		for(GameEntity entity : entities) {
			if(entity instanceof PhysicalEntity) {
				PhysicalEntity pe = (PhysicalEntity)entity;
				for(AreaTrigger t : triggers) {
					//There are two cases:
					// 1. Entering
					// 2. Exiting
					boolean prevFrameInTrigger = t.checkCollision(pe.getPreviousPosition(), pe.getAabb());
					boolean curFrameInTrigger = t.checkCollision(pe.getPosition(), pe.getAabb());
					if(!prevFrameInTrigger && curFrameInTrigger) {
						t.onEnter(pe);
					} else if(prevFrameInTrigger && !curFrameInTrigger) {
						t.onExit(pe);
					}
				}
			}
		}
	}
	
	public void addEntity(GameEntity entity) {
		entities.add(entity);
	}
	
	public void deleteEntity(GameEntity entity) {
		entities.remove(entity);
	}

	private void updatePhysicalEntity(PhysicalEntity entity, float dt) {
		entity.updateForces();
		entity.updateVelocity(dt);
		Vector2f position = entity.getPosition();
		
		Vector2f newPosition = new Vector2f(position.x + dt * entity.getVelocity().x,
				                            position.y + dt * entity.getVelocity().y);

		AABB aabb = entity.getAabb();
		//updateTail(position, aabb);
		//Now, resolve collisions.
		float newX =
			resolveCollisionsX(position, new Vector2f(newPosition.getX(), position.getY()), aabb);
		if(newX != newPosition.getX()) {
			entity.onCollisionX();
		}
		newPosition.x = (newX);
		
		float newY = resolveCollisionsY(position, newPosition, aabb);
		if(newY != newPosition.getY()) {
			entity.onCollisionY();
		}
		newPosition.y = (newY);

		if(entity instanceof ActorEntity) {
			//Now, check whether the actor entity can grab based on the difference in position
			//between this frame and the last.
			ActorEntity actor = (ActorEntity) entity;
			if(actor.canGrab()) {
				//TODO: Bug here - if the actor is moving too far, it skips the tile, even
				//though the box does not. So we have to search up until we find a grabbable
				//tile to know which we grab on to.
				if(checkGrabLeft((ActorEntity)entity, position, newPosition, aabb)) {
					actor.setGrabbing(true, true);
					newPosition.x = (((int)newPosition.getX()/ map.getTileWidth())* map.getTileWidth());
					System.out.println();
					//TODO: This only works if the entity is shorter than a tile.
					newPosition.y = (((int)newPosition.getY()/map.getTileHeight())*map.getTileHeight());
					
				} else if(checkGrabRight((ActorEntity)entity, position, newPosition, aabb)) {
					actor.setGrabbing(true, false);
					//TODO: This only works if the entity is shorter and thinner than a tile.
					newPosition.x = (((int)newPosition.getX()/ map.getTileWidth())* map.getTileWidth()
							+  map.getTileWidth() - aabb.getWidth());
					//TODO: This only works if the entity is shorter than a tile.
					newPosition.y = (((int)newPosition.getY()/map.getTileHeight())*map.getTileHeight());
				}
			}
		}
		//And we're done!
		entity.setPosition(newPosition);
	}
	

	
	private boolean checkGrabLeft(ActorEntity entity, Vector2f prevPosition, Vector2f newPosition, AABB aabb) {
		if(entity.getVelocity().getY() <= 0.0f) {
			return false;	//only can grab when falling.
		}
		//left side.
		float x1 = prevPosition.getX();  
		float y1 = prevPosition.getY();
		float x2 = newPosition.getX();
		float y2 = newPosition.getY();
		
		//Make sure the points are in increasing order.
		if(x1 > x2) {
			float tmp = x2;
			x2 = x1;
			x1 = tmp;
		}
		if(y1 > y2) {
			float tmp = y2;
			y2 = y1;
			y1 = tmp;
		}
		x1 -= 1.0f;	//make the box a bit wider
		
		if(((int)x1 /  map.getTileWidth() < (int)x2 /  map.getTileWidth()) &&
		   ((int)y1 / map.getTileHeight() < (int)y2 / map.getTileHeight())) {
			if(map.isGrabbableLeft((int)x1/ map.getTileWidth(), (int)y2/map.getTileHeight())) {
				return true;
			}
		}
		return false;
	}
	private boolean checkGrabRight(ActorEntity entity, Vector2f prevPosition, Vector2f newPosition, AABB aabb) {
		if(entity.getVelocity().getY() <= 0.0f) {
			return false;	//only can grab when falling.
		}
		
		//right side.
		float x1 = prevPosition.getX() + aabb.getWidth()-1.0f;  
		float y1 = prevPosition.getY();
		float x2 = newPosition.getX()  + aabb.getWidth()-1.0f;
		float y2 = newPosition.getY();
		
		//Make sure the points are in increasing order.
		if(x1 > x2) {
			float tmp = x2;
			x2 = x1;
			x1 = tmp;
		}
		if(y1 > y2) {
			float tmp = y2;
			y2 = y1;
			y1 = tmp;
		}
		x2 += 1.0f;	//make the box a bit wider
		if(((int)x1 / map.getTileWidth()  < (int)x2 / map.getTileWidth()) &&
		   ((int)y1 / map.getTileHeight() < (int)y2 / map.getTileHeight())) {
			if(map.isGrabbableRight((int)x2/ map.getTileWidth(), (int)y2/map.getTileHeight())) {
				return true;
			}
		}
		
		return false;
	}
	
	private float resolveCollisionsX(Vector2f position, Vector2f newPosition, AABB aabb) {
		//Try to move character to new position.
		if(!hasCollisions(new Vector2f(newPosition.getX(), position.getY()), aabb)) {
			//If there are no collisions, we are done.
			return newPosition.getX();
		}
		Vector2f ret = new Vector2f(newPosition);
		int numIters = 0;
		while(hasCollisions(ret, aabb)) {
			++numIters;
			if(numIters > 10000) {
				System.out.println("----");
				System.out.println(ret);
				System.out.println(position);
			}
			if(ret.getX() - position.getX() > 0) {
				//moving right
				int x = (int)(ret.getX() + aabb.getWidth()); //The furthest right point. Move this to the next tile over
				int depthX = x %  map.getTileWidth();
				ret.x = (ret.getX() - depthX);
			} else if(ret.getX() - position.getX() < 0){
				//moving left
				int x = (int)(ret.getX()); //The furthest left point
				int depthX =  map.getTileWidth() - (x %  map.getTileWidth());
				ret.x = (ret.getX() + depthX);
			} else {
				break;
			}
		}
		return ret.getX();
	}
	
	private float resolveCollisionsY(Vector2f position, Vector2f newPosition, AABB aabb) {

		//Try to move character to new position.
		if(!hasCollisions(new Vector2f(position.getX(), newPosition.getY()), aabb)) {
			//If there are no collisions, we are done.
			return newPosition.getY();
		}
		Vector2f ret = new Vector2f(newPosition);
		int numIters = 0;
		while(hasCollisions(ret, aabb)) {
			++numIters;
			if(numIters > 1) {
				System.out.println("oh noes Y " + numIters);
			}
			if(ret.getY() - position.getY() > 0) {
				//moving up
				int y = (int)(ret.getY() + aabb.getHeight()); //The furthest up point. Move this to the next tile over
				int depthY = y %  map.getTileHeight();
				ret.y = (ret.getY() - depthY);
			} else if(ret.getY() - position.getY() < 0){
				//moving down
				int y = (int)(ret.getY()); //The furthest left point
				int depthY =  map.getTileHeight() - (y %  map.getTileHeight());
				ret.y = (ret.getY() + depthY);
			} else {
				break;
			}
		}
		return ret.getY();
	}
	
	public boolean hasCollisions(Vector2f position, AABB aabb) {
		//Check the four corners of the AABB against the map
		int minTileX = ((int)position.getX()) / map.getTileWidth();
		int maxTileX = (int)(position.getX() + aabb.getWidth() - 1.0f) / map.getTileWidth();
		int minTileY = ((int)position.getY()) / map.getTileHeight();
		int maxTileY = (int)(position.getY() + aabb.getHeight() - 1.0f) / map.getTileHeight();

		//System.out.println(minTileX + " " + maxTileX + " " + minTileY + " " + maxTileY);
		for(int x=minTileX; x <= maxTileX; ++x) {
			for(int y=minTileY; y <= maxTileY; ++y) {
				if(map.isSolid(x,y)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void updateGameEntity(GameEntity entity, float dt) {
		
	}

	@Override
	public void render(Graphics g) {
		map.render(g);
		for(GameEntity entity : entities) {
			if(entity instanceof Renderable) {
				((Renderable) entity).render(g);
			}
		}
	}
}
