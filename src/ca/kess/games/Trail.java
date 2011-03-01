package ca.kess.games;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import ca.kess.games.interfaces.Renderable;

public class Trail implements Renderable {
	private List<Vector2f> points;
	private int head;
	private boolean wrapped = false;
	
	public Trail(int size) {
		points = new ArrayList<Vector2f>(size);
		for(int i=0;i<size;++i) {
			points.add(new Vector2f(0,0));
		}
		head = 0;
	}
	
	public void addPoint(Vector2f point) {
		points.set(head, point);
		head = (head+1)%points.size();
		if(head == 0) {
			wrapped = true;
		}
	}
	
	@Override
	public void render(Graphics g) {
		if(!wrapped && head == 0)
			return; //no points
		glColor3f(0.7f, 0.7f, 0.7f);
		glBegin(GL_LINE_STRIP);
		if(wrapped) {
			//0 1 2 3 4 5 6
			//      h  
			int i= head;
			for(;;) {
				glVertex2f(points.get(i).getX(), points.get(i).getY());
				i = (i+1)%points.size();
				if(i == head) break;
			}
		} else {
			for(int i=0;i<head;++i) {
				glVertex2f(points.get(i).getX(), points.get(i).getY());
			}
		}
		glEnd();
	}
}
