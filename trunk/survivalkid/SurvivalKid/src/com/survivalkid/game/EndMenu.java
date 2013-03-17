package com.survivalkid.game;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.survivalkid.EndActivity;
import com.survivalkid.R;
import com.survivalkid.game.core.Constants.PersonageConstants;
import com.survivalkid.game.singleton.GameContext;
import com.survivalkid.game.util.BitmapUtil;

public class EndMenu extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = EndMenu.class.getSimpleName();

	private static final DateFormat formatter = new SimpleDateFormat("mm:ss,SSS");

	/**
	 * Whether this menu is open because the game is over (true) or just paused
	 * (false).
	 */
	private boolean endMode;
	/** The selected character. (yugo or yuna)*/
	private int selectedCharacter;

	private EndActivity activity;
	private Bitmap panel;
	private Bitmap buttonRestart;
	private Rect buttonRestartRect;
	private Bitmap buttonMenu;
	private Rect buttonMenuRect;
	private Bitmap buttonLeave;
	private Rect buttonLeaveRect;
	private Bitmap textSurvivalTime;
	private Rect textSurvivalTimeRect;
	private Bitmap textGamePaused;
	private Rect textGamePausedRect;
	private Bitmap persoArtwork;
	private Rect persoArtworkRect;

	private Paint paint;
	private String survivalTime;
	private Rect survivalTimeRect;

	public EndMenu(Context _context, boolean _endMode, int _selectedCharacter) {
		super(_context);

		endMode = _endMode;
		selectedCharacter = _selectedCharacter;

		// make the GamePanel focusable so it can handle events
		setFocusable(true);

		activity = (EndActivity) _context;
		setWillNotDraw(false);

		Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/MELODBO.TTF");
		paint = new Paint();
		paint.setTextSize(50);
		paint.setTypeface(tf);
		paint.setColor(Color.WHITE);

		panel = BitmapUtil.createBitmap(R.drawable.panel_back);
		buttonRestart = BitmapUtil.createBitmap(R.drawable.button_restart);
		buttonMenu = BitmapUtil.createBitmap(R.drawable.button_menu);
		buttonLeave = BitmapUtil.createBitmap(R.drawable.button_leave);
		textSurvivalTime = BitmapUtil.createBitmap(R.drawable.text_survival_time);
		textGamePaused = BitmapUtil.createBitmap(R.drawable.text_game_paused);
		if (selectedCharacter == PersonageConstants.PERSO_YUGO) {
			persoArtwork = BitmapUtil.createBitmap(R.drawable.yugo_artwork_1);
		} else {
			persoArtwork = BitmapUtil.createBitmap(R.drawable.yuna_artwork_1);
		}

		buttonRestartRect = buildRect(buttonRestart,
				panel.getWidth() - buttonRestart.getWidth() - buttonRestart.getWidth() / 5,
				buttonRestart.getHeight() / 3);
		buttonMenuRect = buildRect(buttonMenu, panel.getWidth() - buttonMenu.getWidth() - buttonMenu.getWidth() / 5,
				buttonRestartRect.top + buttonRestart.getHeight() + panel.getHeight() / 7);
		buttonLeaveRect = buildRect(buttonLeave,
				panel.getWidth() - buttonLeave.getWidth() - buttonLeave.getWidth() / 5,
				buttonMenuRect.top + buttonMenu.getHeight() + panel.getHeight() / 7);
		textSurvivalTimeRect = buildRect(textSurvivalTime, panel.getWidth() / 2 - textSurvivalTime.getWidth() / 2,
				panel.getWidth() / 15);
		textGamePausedRect = buildRect(textGamePaused, panel.getWidth() / 2 - textGamePaused.getWidth() / 2, panel.getWidth() / 20);
		persoArtworkRect = buildRect(persoArtwork, persoArtwork.getWidth()/10, panel.getWidth() / 20);

		Date date = new Date(GameContext.getSingleton().gameDuration);
		survivalTime = formatter.format(date);
		survivalTimeRect = new Rect(panel.getWidth() / 2 - textSurvivalTime.getWidth() / 2 + 10,
				textSurvivalTimeRect.bottom + panel.getWidth() / 10, 0, 0);

		Log.d(TAG, "End menu displayed !");
		create();
	}

	private Rect buildRect(Bitmap bitmap, int left, int top) {
		return new Rect(left, top, left + bitmap.getWidth(), top + bitmap.getHeight());
	}

	/**
	 * Start the game.
	 * 
	 * @param context
	 */
	public void create() {

	}

	/**
	 * Restart the game
	 * 
	 * @return true if the game restart
	 */
	public void restart() {

	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawBitmap(panel, 0, 0, null);
		canvas.drawBitmap(buttonRestart, buttonRestartRect.left, buttonRestartRect.top, null);
		canvas.drawBitmap(buttonMenu, buttonMenuRect.left, buttonMenuRect.top, null);
		canvas.drawBitmap(buttonLeave, buttonLeaveRect.left, buttonLeaveRect.top, null);

		if (endMode) {
			canvas.drawBitmap(textSurvivalTime, textSurvivalTimeRect.left, textSurvivalTimeRect.top, null);
		} else {
			canvas.drawBitmap(textGamePaused, textGamePausedRect.left, textGamePausedRect.top, null);
		}
		
		canvas.drawBitmap(persoArtwork,persoArtworkRect.left, persoArtworkRect.top, null);

		canvas.drawText(survivalTime, survivalTimeRect.left, survivalTimeRect.top, paint);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (buttonLeaveRect.contains((int) event.getX(), (int) event.getY())) {
			activity.returnResult("leave");
		} else if (buttonMenuRect.contains((int) event.getX(), (int) event.getY())) {
			activity.returnResult("menu");
		} else if (buttonRestartRect.contains((int) event.getX(), (int) event.getY())) {
			if (endMode) {
				activity.returnResult("restart");
			} else {
				activity.returnResult(null);
			}
		}

		return true;
	}

	// ------------------------------------------------------------------------
	// Surface managing
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		setWillNotDraw(true);
	}

	/** Action when clicking on the BACK button. */
	public void unpause() {
		setWillNotDraw(false);
	}

	/** Action when clicking on the BACK button. */
	public void stop() {
		setWillNotDraw(true);
		// ((Activity) getContext()).finish();
	}

	/**
	 * @return the panel
	 */
	public Bitmap getPanel() {
		return panel;
	}

	/**
	 * @param panel
	 *            the panel to set
	 */
	public void setPanel(Bitmap panel) {
		this.panel = panel;
	}

}
