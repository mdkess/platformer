package ca.kess.games.states;

import java.io.FileNotFoundException;
import java.io.ObjectInputStream.GetField;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import ca.kess.games.Map;
import ca.kess.games.World;
import ca.kess.games.camera.BasicCamera;
import ca.kess.games.camera.FollowCamera;
import ca.kess.games.entities.ActorEntity;
import ca.kess.games.entities.DirectionType;
import ca.kess.games.entities.ShurrikenEntity;
import ca.kess.games.physics.AABB;
import ca.kess.games.physics.ForceType;

public class GameState extends BasicGameState {

	private int id = -1;
	private World world;
	private BasicCamera camera;
	private ActorEntity entity;
	public GameState(int id) throws FileNotFoundException, SlickException {
		this.id = id;
		world = new World(new Map("level.txt"));
		entity = new ActorEntity(new AABB(16, 16), new Vector2f(100,70), 50.0f, world);
		entity.addForce(ForceType.GRAVITY, new Vector2f(0.0f, 30000.0f));
		//entity.setDebugMode(true);
		camera = new FollowCamera(entity);
		world.addEntity(entity);
	}

	@Override
	public int getID() {
		return id;
	}

	private Animation shurrikenAnimation;
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		world.getMap().setTiledMap(new TiledMap("res/map1.tmx"));
		SpriteSheet spriteSheet = new SpriteSheet("res/spritesheet.png", 16, 16);
		Image shurrikenImages[] = new Image[] {
				spriteSheet.getSprite(1, 0),
				spriteSheet.getSprite(2, 0),
				spriteSheet.getSprite(3, 0),
				spriteSheet.getSprite(4, 0),
				spriteSheet.getSprite(5, 0),
		};
		shurrikenAnimation = new Animation(shurrikenImages, 100);
		Image walkRight[] = new Image[] {
			spriteSheet.getSprite(0, 2),
			spriteSheet.getSprite(1, 2),
			spriteSheet.getSprite(2, 2),
			spriteSheet.getSprite(3, 2),
			spriteSheet.getSprite(4, 2),
			spriteSheet.getSprite(5, 2),
		};
		
		Animation walkRightAnimation = new Animation(walkRight, 200);
		Image walkLeft[] = new Image[] {
			spriteSheet.getSprite(0, 2).getFlippedCopy(true, false),
			spriteSheet.getSprite(1, 2).getFlippedCopy(true, false),
			spriteSheet.getSprite(2, 2).getFlippedCopy(true, false),
			spriteSheet.getSprite(3, 2).getFlippedCopy(true, false),
			spriteSheet.getSprite(4, 2).getFlippedCopy(true, false),
			spriteSheet.getSprite(5, 2).getFlippedCopy(true, false),
		};
		Animation walkLeftAnimation = new Animation(walkLeft, 200);
		
		Image grabRight[] = new Image[] {
			spriteSheet.getSprite(4, 6),
			spriteSheet.getSprite(5, 6),
			spriteSheet.getSprite(6, 6),
//			spriteSheet.getSprite(7, 6),
//			spriteSheet.getSprite(8, 6),	
		};
		Animation grabRightAnimation = new Animation(grabRight, 200);
		
		Image grabLeft[] = new Image[] {
			spriteSheet.getSprite(4, 6).getFlippedCopy(true, false),
			spriteSheet.getSprite(5, 6).getFlippedCopy(true, false),
			spriteSheet.getSprite(6, 6).getFlippedCopy(true, false),
//			spriteSheet.getSprite(7, 6).getFlippedCopy(true, false),
//			spriteSheet.getSprite(8, 6).getFlippedCopy(true, false),	
		};
		Animation grabLeftAnimation = new Animation(grabLeft, 200);
		
		entity.addAnimation("WALK_RIGHT", walkRightAnimation);
		entity.addAnimation("WALK_LEFT", walkLeftAnimation);
		entity.addAnimation("GRAB_RIGHT", grabRightAnimation);
		entity.addAnimation("GRAB_LEFT", grabLeftAnimation);
		
		entity.setFacingDirection(DirectionType.LEFT);
		entity.setCurrentAnimation("WALK_LEFT");
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setColor(Color.gray);
		
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		g.pushTransform();
		camera.render(container, game, world, g);
		g.popTransform();
		drawFPS(g, container);
		drawPosition(g);
	}
	
	private void drawFPS(Graphics g, GameContainer container) {
		g.setColor(Color.black);
		g.drawString("FPS: " + container.getFPS(), 22, 22);
		g.setColor(Color.white);
		g.drawString("FPS: " + container.getFPS(), 20, 20);
	}
	
	private void drawPosition(Graphics g) {
		g.setColor(Color.black);
		int x = (int) entity.getPosition().x;
		int y = (int) entity.getPosition().y;
		int tw = (int) world.getMap().getTileWidth();
		int th = (int) world.getMap().getTileHeight();
		int tx = (int) (x / tw);
		int ty = (int) (y / th);
		/*
		g.setColor(Color.black);
		g.drawString("Pos:  (" + x + " " + y + ")",   22, 42);
		g.drawString("Tile: [" + tx + " " + ty + "]", 22, 62);
		
		g.setColor(Color.white);
		g.drawString("Pos:  (" + x + " " + y + ")",   20, 40);
		g.drawString("Tile: [" + tx + " " + ty + "]", 20, 60);
		*/
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		while(delta > 5) {
			camera.update(5.0f/1000.0f);  // 1/200th of a second
			world.update(5.0f/1000.0f);
			delta -= 5;
		}
		camera.update(delta);
		world.update(((float)delta)/1000.0f);
		
		
	}
	
	@Override
	public void keyPressed(int key, char c) {
		float forceX = 50000.0f;
		float forceY = 50000.0f;
		if('a' == c) {
			if(!entity.isGrabbing()) {
				entity.addForce(ForceType.LEFT, new Vector2f(-forceX, 0.0f));
				entity.setCurrentAnimation("WALK_LEFT");
				entity.setFacingDirection(DirectionType.LEFT);
			}
		} else if('s' == c) {
			if(!entity.isGrabbing()) {
				entity.addForce(ForceType.DOWN, new Vector2f(0.0f, forceY));
			}
		} else if('d' == c) {
			if(!entity.isGrabbing()) {
				entity.addForce(ForceType.RIGHT, new Vector2f(forceX, 0.0f));
				entity.setCurrentAnimation("WALK_RIGHT");
				entity.setFacingDirection(DirectionType.RIGHT);
			}
		} else if('w' == c) {
			if(!entity.isGrabbing()) {
				entity.addForce(ForceType.UP, new Vector2f(0.0f, -forceY));
			}
		} else if(' ' == c) {
			if(!entity.isGrabbing()) {
				ShurrikenEntity shurriken = new ShurrikenEntity(new AABB(16,16), new Vector2f(entity.getPosition()), 1.0f, world);
				if(entity.getFacingDirection() == DirectionType.LEFT) {
					shurriken.setVelocity(new Vector2f(-500000.0f, 0.0f));
				} else {
					shurriken.setVelocity(new Vector2f(500000.0f, 0.0f));
				}
				shurriken.addAnimation("SHURRIKEN", shurrikenAnimation);
				shurriken.setCurrentAnimation("SHURRIKEN");
				shurriken.addForce(ForceType.GRAVITY, new Vector2f(0,500.0f));
				//shurriken.setDebugMode(true);
				world.addEntity(shurriken);
			}
		}
		/*
		float forceX = 50000.0f; 
		float forceJump = -8000000.0f;
		if('a' == c) {
			entity.addForce(ForceType.MOVE_LEFT, new Vector2f(-forceX, 0.0f));
		} else if('d' == c) {
			entity.addForce(ForceType.MOVE_RIGHT, new Vector2f( forceX, 0.0f));
		}
		else if(' ' == c) {
			if(entity.canJump()) {
				entity.jump(new Vector2f(0.0f, forceJump));
			} else if(entity.isGrabbing()) {
				entity.setGrabbing(false);
				entity.setVelocity(new Vector2f(0.0f,0.0f));
				entity.jump(new Vector2f(0.0f, forceJump));
			} else if(entity.isOnWallLeft()) {
				entity.jump(new Vector2f(forceJump, forceJump));
			} else if(entity.isOnWallRight()) {
				entity.jump(new Vector2f(-forceJump, forceJump)); 
			} else if(!entity.doubleJumped()) {
				entity.doubleJump(new Vector2f(0.0f, forceJump));
			}
		
		} else if('s' == c) {
			entity.setGrabbing(false);
		}*/
	}
	@Override
	public void keyReleased(int key, char c) {
		if('a' == c) {
			entity.removeForce(ForceType.LEFT);
		} else if('s' == c) {
			entity.removeForce(ForceType.DOWN);
		} else if('d' == c) {
			entity.removeForce(ForceType.RIGHT);
		} else if('w' == c) {
			entity.removeForce(ForceType.UP);
		} else if(' ' == c) {
			entity.setGrabbing(false, false);
		}
	}

}
