package ca.kess.games;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import ca.kess.games.interfaces.Renderable;
import ca.kess.games.physics.AABB;
import ca.kess.games.triggers.AreaParticleEmitter;
import ca.kess.games.triggers.AreaTrigger;
import ca.kess.games.triggers.TimerTrigger;
import ca.kess.games.triggers.Trigger;
import ca.kess.games.triggers.Triggerable;
public class Map implements Renderable {
	private static final String DEFAULT_FRICTION = "1.0";
	private TiledMap tiledMap = null;
	private java.util.Map<String, AreaTrigger> areaTriggers = new HashMap<String, AreaTrigger>();
	private java.util.Map<String, TimerTrigger> timerTriggers = new HashMap<String, TimerTrigger>();
	private java.util.Map<String, AreaParticleEmitter> areaParticleEmitters = new HashMap<String, AreaParticleEmitter>();
	
	public java.util.Map<String, AreaTrigger> getAreaTriggers() {
		return areaTriggers;
	}
	public Map(String path) throws SlickException {
		
	}
	
	public void setTiledMap(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
		areaTriggers = new HashMap<String, AreaTrigger>();
		//System.out.println(tiledMap.getObjectGroupCount());
		
		//Maybe a bit of a hack. Within Tiled, hierarchy is stored as strings. So we have
		//to untangle that.
		java.util.Map<Trigger, String> objectHierarchy = new HashMap<Trigger, String>();
		java.util.Map<String, Triggerable> nameMap = new HashMap<String, Triggerable>();
		for(int layer = 0;;++layer) {
			if(tiledMap.getObjectCount(layer) == -1) {
				break;
			}
			for(int object = 0;;++object) {
				String objectType = tiledMap.getObjectType(layer, object);
				if(objectType == null) {
					break;
				}
				Trigger trigger = null;
				if("AreaTrigger".equals(objectType)) {
					AreaTrigger areaTrigger = new AreaTrigger(
						tiledMap.getObjectName(layer, object),
						new Vector2f(tiledMap.getObjectX(layer, object), tiledMap.getObjectY(layer, object)),
						new AABB(tiledMap.getObjectWidth(layer, object), tiledMap.getObjectHeight(layer, object)));
					areaTriggers.put(areaTrigger.getName(), areaTrigger);
					System.out.println("AreaTrigger: " + areaTrigger.getName());
					trigger = areaTrigger;
					nameMap.put(areaTrigger.getName(), areaTrigger);
				} else if("TimerTrigger".equals(objectType)) {
					TimerTrigger timerTrigger = new TimerTrigger(
							tiledMap.getObjectName(layer, object),
							new Vector2f(tiledMap.getObjectX(layer, object), tiledMap.getObjectY(layer, object)),
							new AABB(tiledMap.getObjectWidth(layer, object), tiledMap.getObjectHeight(layer, object)),
							Float.valueOf(tiledMap.getObjectProperty(layer, object, "duration", "0")));
					timerTriggers.put(timerTrigger.getName(), timerTrigger);
					trigger = timerTrigger;
					nameMap.put(timerTrigger.getName(), timerTrigger);
				} else if("AreaParticleEmitter".equals(objectType)) {
					AreaParticleEmitter areaParticleEmitter = new AreaParticleEmitter(
							tiledMap.getObjectName(layer, object),
							new Vector2f(tiledMap.getObjectX(layer, object), tiledMap.getObjectY(layer, object)),
							new AABB(tiledMap.getObjectWidth(layer, object), tiledMap.getObjectHeight(layer, object)));
					areaParticleEmitters.put(areaParticleEmitter.getName(), areaParticleEmitter);
					nameMap.put(areaParticleEmitter.getName(), areaParticleEmitter);
					//Particle emitter is a leaf - no children
				}
				
				if(trigger != null) {
					objectHierarchy.put(trigger, tiledMap.getObjectProperty(layer, object, "children", ""));
				}
			}
		}
		//Now we have to build the hierarchy
		for(Entry<Trigger, String> e : objectHierarchy.entrySet()) {
			String[] children = e.getValue().split(" ");
			for(String child : children) {
				if(nameMap.get(child) == null) {
					System.out.println("Warning - couldn't find entity named " + child);
					continue;
				}
				System.out.println("Adding child " + child + " to " + e.getKey().getName());
				e.getKey().addChild(nameMap.get(child));
				
			}
		}
	}
	
	public int getWidth() {
		return tiledMap == null ? 0 : tiledMap.getWidth();
	}
	public int getHeight() {
		return tiledMap == null ? 0 : tiledMap.getHeight();
	}
	
	public boolean isSolid(int x, int y) {
		if(x < 0 || x > getWidth()-1 || y < 0 || y > getHeight() -1) {
			return true;
		}
		int tileId = tiledMap.getTileId(x, y, 0);
		return tiledMap.getTileProperty(tileId, "solid", "false").equals("true");
	}
	public float getFriction(int x, int y) {
		if(x < 0 || x > getWidth()-1 || y < 0 || y > getHeight() -1) {
			return 0.0f;
		}
		int tileId = tiledMap.getTileId(x, y, 0);
		return new Float(tiledMap.getTileProperty(tileId, "friction", DEFAULT_FRICTION)).floatValue();
	}
	public float getBouyancy(int x, int y) {
		if(x < 0 || x > getWidth()-1 || y < 0 || y > getHeight() -1) {
			return 0.0f;
		}
		int tileId = tiledMap.getTileId(x, y, 0);
		return new Float(tiledMap.getTileProperty(tileId, "bouyancy", "0")).floatValue();
	}

	public boolean isSolidReal(float x, float y) {
		return isSolid((int)x / tiledMap.getTileWidth(), (int)y / tiledMap.getTileHeight());
	}
	public float getFrictionReal(float x, float y) {
		return getFriction((int)x / tiledMap.getTileWidth(), (int)y / tiledMap.getTileHeight());
	}
	public float getBouyancyReal(float x, float y) {
		return getBouyancy((int)x / tiledMap.getTileWidth(), (int)y / tiledMap.getTileHeight());
	}
	
	public int getTileWidth() {
		return tiledMap.getTileWidth();
	}
	public int getTileHeight() {
		return tiledMap.getTileHeight();
	}
	
//
//	public Tile getTileReal(float x, float y) {
//		return getTile((int)x / tiledMap.getTileWidth(), (int)y / tiledMap.getTileHeight());
//	}
//	
	/**
	 * A tile is grabbable in the following formation or the mirror
	 * +--+--+
	 * |  |  |
	 * +--+--+
	 * |  |xx|
	 * +--+--+
	 * |  |     <- bottom tile so that you don't get forced to grab
	 * +--+        low tiles
	 */
	public boolean isGrabbableRight(int x, int y) {
		return isSolid(x    , y    ) &&
		      !isSolid(x - 1, y - 1) &&
		      !isSolid(x    , y - 1) &&
		      !isSolid(x - 1, y    ) &&
		      !isSolid(x - 1, y + 1);
	}
	public boolean isGrabbableLeft(int x, int y) {
		return isSolid(x    , y    ) &&
		      !isSolid(x + 1, y - 1) &&
		      !isSolid(x    , y - 1) &&
		      !isSolid(x + 1, y    ) &&
		      !isSolid(x + 1, y + 1);
	}
	
	@Override
	public void render(Graphics g) {
		tiledMap.render(0, 0);
		//Draw triggers
		Collection<AreaTrigger> ats = areaTriggers.values();
		for(AreaTrigger at : ats) {
			at.render(g);
		}
		Collection<TimerTrigger> tts = timerTriggers.values();
		for(TimerTrigger at : tts) {
			at.render(g);
		}
		Collection<AreaParticleEmitter> pes = areaParticleEmitters.values();
		for(AreaParticleEmitter at : pes) {
			at.render(g);
		}
		/*
		int w = getTileWidth();
		int h = getTileHeight();
		for(int i=0; i< tiledMap.getWidth(); ++i) {
			for(int j=0; j< tiledMap.getHeight(); ++j) {
				if(isGrabbableLeft(i,j) || isGrabbableRight(i,j)) {
					g.fillRect(i * w + 4, j * h + 4, w - 8, h - 8);
				}
			}
		}*/
		/*
		for(int i=0; i<tiles.length;++i) {
			for(int j=0; j<tiles[i].length;++j) {
				tiles[i][j].render(g, TILE_WIDTH*i, TILE_HEIGHT*j, TILE_WIDTH*i + TILE_WIDTH, TILE_HEIGHT*j + TILE_HEIGHT);
			}
		}*/
		//Now draw a grid
		/*
		glBegin(GL_LINES);
		glColor3f(0.7f, 0.7f, 0.7f);
		for(int x=0;x<tiles.length;++x) {
			glVertex2i(x * TILE_WIDTH, 0);
			glVertex2i(x * TILE_WIDTH, tiles[x].length * TILE_HEIGHT);
		}
		for(int y=0;y<tiles[0].length;++y) {
			glVertex2i(0, y * TILE_HEIGHT);
			glVertex2i(tiles.length * TILE_WIDTH, y*TILE_HEIGHT);
		}
		glEnd();
		*/
	}

	public void update(float dt) {
		Collection<TimerTrigger> tts = timerTriggers.values();
		for(TimerTrigger t : tts) {
			t.update(dt);
		}
		/*
		for(int i=0;i<tiles.length;++i) {
			for(int j=0;j<tiles[i].length;++j) {
				tiles[i][j].update(dt);
			}
		}
		*/
	}
}
