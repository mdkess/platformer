package ca.kess.games;


import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2i;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Tile {
	private enum TileType {
		SQUARE,
		BOTTOM_LEFT_TRIANGLE,
		BOTTOM_RIGHT_TRIANGLE,
		TOP_LEFT_TRIANGLE,
		TOP_RIGHT_TRIANGLE
	};
	
	private float charge = 0.0f;
	private static Color[] colors;
	private static TileType[] types;
	private static float[] friction;
	private static Image[] images;
	
	public float getCharge() {
		return charge;
	}
	
	public void setCharge(float charge) {
		this.charge = charge;
	}
	
	public void update(float dt) {
		charge -= 0.1f*dt;
		if(charge < 0.0f) {
			charge = 0.0f;
		}
	}
	
	static {
		colors = new Color[7];
		friction = new float[7];
		types = new TileType[7];
		
		types[0] = TileType.SQUARE;
		colors[0] = Color.white;
		friction[0] = 0.0f;
		
		types[1] = TileType.SQUARE;
		colors[1] = Color.green;
		friction[1] = 1.0f;
		
		types[2] = TileType.SQUARE;
		colors[2] = (Color) Color.blue;
		friction[2] = 0.1f;
		
		
		types[3] = TileType.BOTTOM_LEFT_TRIANGLE;
		colors[3] = (Color) Color.green;
		friction[3] = 0.0f;
		
		types[4] = TileType.BOTTOM_RIGHT_TRIANGLE;
		colors[4] = (Color) Color.green;
		friction[4] = 0.0f;
		
		types[5] = TileType.TOP_LEFT_TRIANGLE;
		colors[5] = (Color) Color.green;
		friction[5] = 0.0f;
		
		types[6] = TileType.TOP_RIGHT_TRIANGLE;
		colors[6] = (Color) Color.green;
		friction[6] = 0.0f;
	}
	

	
	private int type;
	public Tile(int type) {
		this.type = type;
	}
	public int getType() {
		return type;
	}
	
	public float getCoefficientOfFriction() {
		return friction[type];
	}
	
	public boolean isSolid() {
		return type != 0;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Color getColor() {
		return Tile.colors[type];
	}
	
	public void render(Graphics g, int x1, int y1, int x2, int y2) {
		g.setColor(colors[type]);
		g.fillRect(x1, y1, x2-x1, y2-y1);
		/*
		float k = 1.0f;
		if(!isSolid()) {
			k = 1.0f - charge;
		}
		glColor3f(k*colors[type].getRed(), k*colors[type].getGreen(), k*colors[type].getBlue());
		if(Tile.types[type] == TileType.SQUARE) {
			renderSquare(x1, y1, x2, y2);
		} else if(Tile.types[type] == TileType.BOTTOM_LEFT_TRIANGLE) {
			renderBottomLeftTriangle(x1, y1, x2, y2);
		} else if(Tile.types[type] == TileType.BOTTOM_RIGHT_TRIANGLE) {
			renderBottomRightTriangle(x1, y1, x2, y2);
		} else if(Tile.types[type] == TileType.TOP_LEFT_TRIANGLE) {
			renderTopLeftTriangle(x1, y1, x2, y2);
		} else if(Tile.types[type] == TileType.TOP_RIGHT_TRIANGLE) {
			renderTopRightTriangle(x1, y1, x2, y2);
		}*/
	}
	private void renderTopRightTriangle(int x1, int y1, int x2, int y2) {
		glBegin(GL_TRIANGLES);
		glVertex2i(x2,y2);
		glVertex2i(x1,y2);
		glVertex2i(x2,y1);
		glEnd();
		glColor3f(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue());
		glBegin(GL_TRIANGLES);
		glVertex2i(x1,y1);
		glVertex2i(x2,y1);
		glVertex2i(x1,y2);
		glEnd();
	}
	private void renderTopLeftTriangle(int x1, int y1, int x2, int y2) {
		glBegin(GL_TRIANGLES);
		glVertex2i(x1,y1);
		glVertex2i(x2,y2);
		glVertex2i(x1,y2);
		glEnd();
		glColor3f(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue());
		glBegin(GL_TRIANGLES);
		glVertex2i(x1,y1);
		glVertex2i(x2,y1);
		glVertex2i(x2,y2);
		glEnd();
	}
	private void renderBottomRightTriangle(int x1, int y1, int x2, int y2) {
		glBegin(GL_TRIANGLES);
		glVertex2i(x1,y1);
		glVertex2i(x2,y1);
		glVertex2i(x2,y2);
		glEnd();
		glColor3f(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue());
		glBegin(GL_TRIANGLES);
		glVertex2i(x1,y1);
		glVertex2i(x2,y2);
		glVertex2i(x1,y2);
		glEnd();		
	}
	private void renderBottomLeftTriangle(int x1, int y1, int x2, int y2) {
		glBegin(GL_TRIANGLES);
		glVertex2i(x1,y1);
		glVertex2i(x2,y1);
		glVertex2i(x1,y2);
		glEnd();
		glColor3f(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue());
		glBegin(GL_TRIANGLES);
		glVertex2i(x2,y1);
		glVertex2i(x2,y2);
		glVertex2i(x1,y2);
		glEnd();
		
	}
	private void renderSquare(int x1, int y1, int x2, int y2) {
		glBegin(GL_QUADS);
		glVertex2i(x1,y1);
		glVertex2i(x2,y1);
		glVertex2i(x2,y2);
		glVertex2i(x1,y2);
		glEnd();
	}
}
