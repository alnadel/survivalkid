package com.survivalkid.game.data;

import static com.survivalkid.game.util.MoveUtil.HAS_MULTITOUCH;
import static com.survivalkid.game.util.MoveUtil.SCREEN_HEIGHT;
import static com.survivalkid.game.util.MoveUtil.SCREEN_WIDTH;
import static com.survivalkid.game.util.MoveUtil.btn_left;
import static com.survivalkid.game.util.MoveUtil.btn_right;
import static com.survivalkid.game.util.MoveUtil.btn_up;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import com.survivalkid.game.core.ActionButton;
import com.survivalkid.game.core.enums.SpriteEnum;
import com.survivalkid.game.util.MoveUtil;
import com.survivalkid.game.util.PrefsUtil;

/**
 * Initialize the position of the button
 * 
 * @author Thomas
 */
public class ButtonPosition {

	private static final String TAG = ButtonPosition.class.getSimpleName();
	
	private enum TypeButton {
		LEFT, RIGHT, UP, BAG, NONE;
	}
	
	private static final String KEY_POSITION = "survival-kid-buttonPosition";
	private static final String SEPARATOR = ",";
	
	private Point leftButton;
	private Point rightButton;
	private Point upButton;
	private Point bagButton;
	
	TypeButton buttonSelect = TypeButton.NONE;
	
	public ButtonPosition() {
		// do nothing when creating
	}
	
	public void init() {
		String buttonPosition = PrefsUtil.getPrefs().getString(KEY_POSITION, null);
		if (buttonPosition == null) {
			initDefaultPosition();
		}
		else {
			initStorePosition(buttonPosition);
		}		
	}
	
	public void resetPosition() {
		PrefsUtil.removePref(KEY_POSITION);
		MoveUtil.initializePositionButton();
		Log.d(TAG, "Button position has been reseted");
	}

	public void initStorePosition(String buttonPosition) {
		String[] listePosition = buttonPosition.split(SEPARATOR);
		if (listePosition.length != 8) {
			// problem in the store datas
			initDefaultPosition();
		}
		else {
			try {
				// load the save configuration
				leftButton = new Point(Integer.parseInt(listePosition[0]), Integer.parseInt(listePosition[1]));
				rightButton = new Point(Integer.parseInt(listePosition[2]), Integer.parseInt(listePosition[3]));
				upButton = new Point(Integer.parseInt(listePosition[4]), Integer.parseInt(listePosition[5]));
				bagButton = new Point(Integer.parseInt(listePosition[6]), Integer.parseInt(listePosition[7]));
			}
			catch(Exception e) {
				Log.e(TAG, "Erreur lors du rechargement de la position des boutons : " + buttonPosition, e);
				initDefaultPosition();
			}
		}
	}
	
	public void initDefaultPosition() {
		// initialize default position
		if (HAS_MULTITOUCH) {
			int leftX = (int) (SCREEN_WIDTH*0.03);
			int widthUp = btn_up.getWidth();
			leftButton = new Point(leftX, SCREEN_HEIGHT - btn_left.getHeight());
			rightButton = new Point(leftX + btn_left.getWidth()*2, SCREEN_HEIGHT - btn_right.getHeight());
			upButton = new Point(SCREEN_WIDTH - widthUp - widthUp/2, SCREEN_HEIGHT - btn_up.getHeight());
			
			Bitmap bagImg = SpriteEnum.BAG_SLOT.getBitmap();
			bagButton = new Point(SCREEN_WIDTH - widthUp - widthUp/3 - (int)(bagImg.getWidth()), SCREEN_HEIGHT - bagImg.getHeight());
		}
		else {
			// not multitouch, the button are superposed so the player can jump and move un the same time
			int heightHo = btn_left.getHeight();
			int widthUp = btn_up.getWidth();
			int widthHo = btn_left.getWidth();
			int leftX = (int) (0.98f*SCREEN_WIDTH - widthHo*3);
			int posY = SCREEN_HEIGHT - (int)(heightHo*1.3f);
			
			btn_left.setMarginVertical(heightHo);
			btn_right.setMarginVertical(heightHo);
			btn_up.setMarginHorizontal(widthUp + btn_left.getMarginHorizontal());
			btn_up.setMarginVertical(heightHo);
			leftButton = new Point(leftX, posY);
			rightButton = new Point(leftX + widthHo*2, posY);
			upButton = new Point(leftX + widthHo + btn_left.getMarginHorizontal() - widthUp/2,
					SCREEN_HEIGHT - (int)(2.2f*heightHo) - btn_up.getHeight());
			
			Bitmap bagImg = SpriteEnum.BAG_SLOT.getBitmap();
			bagButton = new Point(SCREEN_WIDTH - widthUp - widthUp/2 - bagImg.getWidth()*2, SCREEN_HEIGHT - bagImg.getHeight());
		}		
	}
	
	/**
	 * Save the positions in the preferences
	 */
	public void savePosition() {
		StringBuilder sb = new StringBuilder().append(leftButton.x).append(SEPARATOR).append(leftButton.y).append(SEPARATOR);
		sb.append(rightButton.x).append(SEPARATOR).append(rightButton.y).append(SEPARATOR);
		sb.append(upButton.x).append(SEPARATOR).append(upButton.y).append(SEPARATOR);
		sb.append(bagButton.x).append(SEPARATOR).append(bagButton.y);
		PrefsUtil.setPrefs(String.class, KEY_POSITION, sb.toString());
	}
	
	/**
	 * Manage an event of changing button position
	 * 
	 * @param event MotionEvent
	 * @return true if a button has been moved, false otherwise
	 */
	public boolean manageEventChangePosition(MotionEvent event) {
		int action =  event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		if (actionCode == MotionEvent.ACTION_DOWN || actionCode == MotionEvent.ACTION_POINTER_DOWN) {
			int pid = action >> MoveUtil.ACTION_POINTER_INDEX_SHIFT;
			int x = (int) event.getX(pid);
			int y = (int) event.getY(pid);
			if (MoveUtil.btn_left.isOnButton(x, y)) {
				buttonSelect = TypeButton.LEFT;
			}
			else if (MoveUtil.btn_right.isOnButton(x, y)) {
				buttonSelect = TypeButton.RIGHT;
			}
			else if (MoveUtil.btn_up.isOnButton(x, y)) {
				buttonSelect = TypeButton.UP;
			}
			else {
				buttonSelect = TypeButton.NONE;
			}
		}
		else if (actionCode == MotionEvent.ACTION_UP || actionCode == MotionEvent.ACTION_POINTER_UP) {
			buttonSelect = TypeButton.NONE;
		}
		else { // move
			int pid = action >> MoveUtil.ACTION_POINTER_INDEX_SHIFT;
			int x = (int) event.getX(pid);
			int y = (int) event.getY(pid);
			switch(buttonSelect) {
				case LEFT:manageMoveButton(leftButton, btn_left, x, y);break;
				case RIGHT:manageMoveButton(rightButton, btn_right, x, y);break;
				case UP:manageMoveButton(upButton, btn_up, x, y);break;
				case BAG:Log.d(TAG, "TODO : bah not manage");
				default:return false;
			}
			return true;
		}
		return false;
	}
	
	private void manageMoveButton(Point point, ActionButton actionButton, int x, int y) {
		point.x = x - actionButton.getWidth()/2;
		point.y = y - actionButton.getHeight()/2;
		actionButton.setPosition(point);
	}

	// GETTER et SETTER
	public Point getLeftButton() {
		return leftButton;
	}

	public void setLeftButton(Point leftButton) {
		this.leftButton = leftButton;
	}

	public Point getRightButton() {
		return rightButton;
	}

	public void setRightButton(Point rightButton) {
		this.rightButton = rightButton;
	}

	public Point getUpButton() {
		return upButton;
	}

	public void setUpButton(Point upButton) {
		this.upButton = upButton;
	}

	public Point getBag() {
		return bagButton;
	}

	public void setBag(Point bag) {
		this.bagButton = bag;
	}
	
}
