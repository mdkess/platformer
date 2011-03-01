package ca.kess.games;
import java.io.FileNotFoundException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import ca.kess.games.states.GameState;


public class Game extends StateBasedGame {
	private static final int GAME_STATE_ID = 1;
	public Game(String title) throws FileNotFoundException, SlickException {
		super(title);
		addState(new GameState(GAME_STATE_ID));
		enterState(GAME_STATE_ID);
	}

	public static void main(String[] args) throws SlickException, FileNotFoundException {
		AppGameContainer app = new AppGameContainer(new Game("ninjas"));
		
		app.setDisplayMode(800, 600, false);
		app.setShowFPS(false);
		app.start();
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		getState(GAME_STATE_ID).init(container, this);
	}

}
