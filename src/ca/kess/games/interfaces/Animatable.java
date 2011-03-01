package ca.kess.games.interfaces;

import org.newdawn.slick.Animation;

public interface Animatable {
	public void addAnimation(String name, Animation animation);
	public void setCurrentAnimation(String name);
}
