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
	private int imageResource;
	private Point anchorPoint = new Point();
	private Point screenSize;
	private CardPlayerHand playerHand;
	
	public Card(Context context) {
		super(context);
	}
	
	public Card(Context context, char suit, int face) {
		super(context);
		this.suit = suit;
		this.face = face;
		this.playerHand = null;
		
		String resourceString = "drawable/"+suit+face;
		imageResource = getImageResource(context, resourceString);
		
		this.setImageResource(imageResource);
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
	
	private static int getImageResource(Context context, String string) {		
		return context.getResources().getIdentifier(string, null, context.getPackageName());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final int action = event.getAction();
		
		switch(action) {
			case MotionEvent.ACTION_DOWN:
				
				Log.e("TOUCH","ACTION_DOWN");
				
				//Calculate the anchor point (x,y) in card image
				anchorPoint.x = (int) (event.getRawX() - v.getX());
				anchorPoint.y = (int) (event.getRawY() - v.getY());
				
				//TODO: Add better dragging graphics
				v.setAlpha((float)0.5);
				v.bringToFront();
				playerHand.removeFromStackAndHeap(this);
		    	break;
		    	
			case MotionEvent.ACTION_MOVE:
				
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                
                float posX = x-anchorPoint.x;
                float posY = y-anchorPoint.y;
                
                if(posX < 0 || (posX+width) > screenSize.x) {
                	if(posY > 0 && (posY+height) < screenSize.y) v.setY(y-(anchorPoint.y));
                } else if(posY < 0 || (posY+height) > screenSize.y) {
                	if(posX > 0 && (posX+width) < screenSize.x) v.setX(x-(anchorPoint.x));
                } else {
                	v.setX(x-(anchorPoint.x));
                	v.setY(y-(anchorPoint.y));
                }
                
				break;
				
			case MotionEvent.ACTION_UP:
				Log.e("TOUCH","ACTION_UP");
				Log.e("DROP_POSITION","("+v.getX()+","+v.getY()+")");
				/*
				 * 
				 * CHRISTIAN, SE P� DETTE!
				//ScaleAnimation animation = new ScaleAnimation(v.getX(), v.getX()+50, v.getY(), v.getY()+50);
				//TranslateAnimation animation = new TranslateAnimation(0, 100, 0, 100);
				//animation.setDuration(1000);
				//startAnimation(animation);
				
				if(v.getX() < Card.width/2) {
					if(v.getY() < Card.height/2) {
						TranslateAnimation animation = new TranslateAnimation(0, Math.abs(v.getX()), 0, Math.abs(v.getY()));
						animation.setDuration(500);
						startAnimation(animation);
						v.setX(0);
						v.setY(0);
					}
				}
				*/
				
				
				v.setAlpha(1);
				playerHand.moveCard(this);
				
				break;

			default:
				Log.e("TOUCH", "OTHER TOUCH EVENT NO."+action);
				break;
		}
		return true;
	}

	public char getSuit() {
		return suit;
	}

	public int getFace() {
		return face;
	}
}

