package com.survivalkid.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.survivalkid.R;
import com.survivalkid.game.entity.personage.Personage;
import com.survivalkid.game.manager.CharacterManager;
import com.survivalkid.game.manager.EnemyManager;
import com.survivalkid.game.manager.ItemManager;
import com.survivalkid.game.manager.ObjectManager;
import com.survivalkid.game.singleton.GameContext;
import com.survivalkid.game.thread.MainThread;
import com.survivalkid.game.util.MoveUtil;

public class GameManager extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = GameManager.class.getSimpleName();

	/** The thread corresponding to the game loop. */
	private MainThread thread;
	
	private CharacterManager characterManager;
	private ObjectManager enemyManager;
	private ObjectManager itemManager;


	@SuppressLint("NewApi")
	public GameManager(Context context) {
		super(context);
		
		// initialize of the context singleton
		GameContext.getSingleton().setContext(context);
		
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create the game loop thread
		thread = new MainThread(getHolder(), this);
		
		characterManager = new CharacterManager();
		enemyManager = new EnemyManager();
		itemManager = new ItemManager();

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
		
		
		// TEST ------------------
		Personage yugo = new Personage(BitmapFactory.decodeResource(getResources(), R.drawable.yugo),150,150,
				6,12);
		yugo.addAnimation("run", new int[]{1,2,3,5,6,7,9,10,11,12,13,14}, 20);
		yugo.play("run", true, true);
		
		Personage yuna = new Personage(BitmapFactory.decodeResource(getResources(), R.drawable.yuna),250,150,
				6,12);
		yuna.addAnimation("run", new int[]{1,2,4,6,7,8,9,10,11,12,13,14}, 20);
		yuna.play("run", true, false);
		
		
		Point size = new Point();
		GameContext.getSingleton().getDisplay().getSize(size);
		
		Personage yuna2 = new Personage(BitmapFactory.decodeResource(getResources(), R.drawable.yuna),350,150,
				6,12);
		yuna2.setX(size.x - yuna2.getSprite().getWidth());
		yuna2.addAnimation("oneshot", new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35}, 12);
		yuna2.addAnimation("loop", new int[]{42,43,44,45,46,47,48,49,50}, 15);
		yuna2.play("oneshot", false, true);
		yuna2.play("loop", true, false);
		
		characterManager.addCharacter(yugo);
		characterManager.addCharacter(yuna);
		characterManager.addCharacter(yuna2);
		// END TESTS --------------

		
		
	}
	
	/**
	 * This is the game update method. It iterates through all the objects
	 * and calls their update method if they have one or calls specific
	 * engine's update method.
	 */
	public void update() {
		long gameTime = System.currentTimeMillis();
		enemyManager.update(gameTime);
		itemManager.update(gameTime);
		characterManager.update(gameTime);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (canvas != null) {
			// fills the canvas with black
			canvas.drawColor(Color.BLACK);

			enemyManager.draw(canvas);
			itemManager.draw(canvas);
			characterManager.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		Personage currentChar = characterManager.getCharacterList(0);
		MoveUtil.calculMove(event, currentChar);
		//if (event.getAction() != MotionEvent.ACTION_MOVE)
			dumpEvent(event);
		return true;
	}
	
	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
		"POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_" ).append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
		|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid " ).append(
			action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
			sb.append(")" );
		}
		sb.append("[" );
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#" ).append(i);
			sb.append("(pid " ).append(event.getPointerId(i));
			sb.append(")=" ).append((int) event.getX(i));
			sb.append("," ).append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";" );
		}
		sb.append("]" ).append(MoveUtil.lastEnabledLeft);
		Log.d(TAG, sb.toString());
	}
	
	//------------------------------------------------------------------------
	// Surface managing
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		thread.setRunning(true);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}
	
	/** Action when clicking on the BACK button. */
	public void stop() {
		thread.setRunning(false);
		((Activity) getContext()).finish();
	}
}