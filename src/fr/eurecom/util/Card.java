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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class Card extends ImageView implements OnTouchListener {

	public static int cardHeight = 208;
	public static int cardWidth = 150;
	
	
	private char suit;
	private int face;
	private int imageResource;
	private Point anchorPoint = new Point();
	private Point screenSize;
	
	public Card(Context context) {
		super(context);
	}
	
	public Card(Context context, char suit, int face) {
		super(context);
		this.suit = suit;
		this.face = face;
		
		String resourceString = "drawable/"+suit+face;
		imageResource = getImageResource(context, resourceString);
		
		this.setImageResource(imageResource);
		this.setLayoutParams(new LayoutParams(cardWidth, cardHeight));
		
		this.setOnTouchListener(this);
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		screenSize = new Point();
		display.getSize(screenSize);
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
		    	break;
		    	
			case MotionEvent.ACTION_MOVE:
				
				Log.e("TOUCH","ACTION_MOVE");
				
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                
                v.setX(x-(anchorPoint.x));
                v.setY(y-(anchorPoint.y));
                
				break;
				
			case MotionEvent.ACTION_UP:
				Log.e("TOUCH","ACTION_UP");
				Log.e("DROP_POSITION","("+v.getX()+","+v.getY()+")");
				
				//ScaleAnimation animation = new ScaleAnimation(v.getX(), v.getX()+50, v.getY(), v.getY()+50);
				//TranslateAnimation animation = new TranslateAnimation(0, 100, 0, 100);
				//animation.setDuration(1000);
				//startAnimation(animation);
				
				if(v.getX() < Card.cardWidth/2) {
					if(v.getY() < Card.cardHeight/2) {
						TranslateAnimation animation = new TranslateAnimation(0, Math.abs(v.getX()), 0, Math.abs(v.getY()));
						animation.setDuration(500);
						startAnimation(animation);
						v.setX(0);
						v.setY(0);
					}
				}
				
				v.setAlpha(1);
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

