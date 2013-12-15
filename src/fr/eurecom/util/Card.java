package fr.eurecom.util;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;

public class Card extends ImageView implements OnTouchListener {

	public static int height = 208;
	public static int width = 150;
	
	private char suit;
	private int face;
	private boolean turned;
	
	private int imageResource;
	private Point anchorPoint = new Point();
	private Point screenSize;
	private CardPlayerHand playerHand;
	private Context context;
	
	private long lastDown;
	
	
	public Card(Context context) {
		super(context);
	}
	
	public Card(Context context, char suit, int face, boolean turned) {
		super(context);
		this.context = context;
		this.suit = suit;
		this.face = face;
		this.turned = turned;
		this.playerHand = null;
		
		updateImageResource();
		this.setLayoutParams(new LayoutParams(width, height));
		
		this.setOnTouchListener(this);
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		screenSize = new Point();
		display.getSize(screenSize);
	}
	
	public void setOwner(CardPlayerHand owner){
		this.playerHand = owner;
	}
	
	private void updateImageResource() {
		imageResource = getImageResource(context, getImageResource());
		this.setImageResource(imageResource);
	}
	
	private static int getImageResource(Context context, String string) {		
		return context.getResources().getIdentifier(string, null, context.getPackageName());
	}
	
	private String getImageResource() {
		return turned ? "drawable/back_blue" : "drawable/"+suit+face;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final int action = event.getAction();
		
		switch(action) {
			case MotionEvent.ACTION_DOWN:
				if(lastDown != 0 && (System.currentTimeMillis() - lastDown) <= 200) {
					turned = turned ? false : true;
					updateImageResource();
				}
				lastDown = System.currentTimeMillis();
				
				anchorPoint.x = (int) (event.getRawX() - v.getX());
				anchorPoint.y = (int) (event.getRawY() - v.getY());
				
				v.setAlpha((float)0.5);
				v.bringToFront();
				playerHand.takeCard(this);
				
		    	return true;
		    	
			case MotionEvent.ACTION_MOVE:
				
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                
                float posX = x-anchorPoint.x;
                float posY = y-anchorPoint.y;
                
                if(posX < 0 || (posX+width) > screenSize.x) {
                	if(posY > 0 && (posY+height) < screenSize.y) {
                		v.setY(y-(anchorPoint.y));
                		playerHand.moveCard(this);
                	}
                } else if(posY < 0 || (posY+height) > screenSize.y) {
                	if(posX > 0 && (posX+width) < screenSize.x) {
                		v.setX(x-(anchorPoint.x));
                		playerHand.moveCard(this);
                	}
                } else {
                	v.setX(x-(anchorPoint.x));
                	v.setY(y-(anchorPoint.y));
                	playerHand.moveCard(this);
                }
				
                return true;
				
			case MotionEvent.ACTION_UP:
				
				v.setAlpha(1);
				playerHand.dropCard(this);
				return true;

			default:
				Log.e("TOUCH", "OTHER TOUCH EVENT NO."+action);
				return false;
		}
	}

	public char getSuit() {
		return suit;
	}

	public int getFace() {
		return face;
	}
	
	public String toString() {
		return this.turned ? "1"+this.suit+this.face : "0"+this.suit+this.face;
	}

}

