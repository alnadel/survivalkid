package com.survivalkid.game.singleton;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class GameContext {
	/** The instance. */
	private static GameContext instance;
	
	/** the context */
	Context context;	

	/** The time when the game started. */
	public long initialTime;
	/** The current on-screen difficulty. */
	public int currentDifficulty;
	/** The current score. */
	public int score;
	
	/** Get the singleton. */
	public static GameContext getSingleton() {
		if (instance == null) {
			instance = new GameContext();		
		}
		return instance;
	}
	
	/**
	 * Initialize the singleton
	 */
	public void initSingleton() {
		instance.initialTime=System.currentTimeMillis();
		currentDifficulty = 0;
		score = 0;
	}

	/**
	 * Call at the beginning to initialize the context
	 * @param _context the game context
	 */
	public void setContext(Context _context) {
		context = _context;
	}
	public Context getContext() {
		return context;
	}
	/**
	 * @return the display
	 */
	public Display getDisplay() {
		return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}
	
}