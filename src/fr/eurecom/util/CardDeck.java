package fr.eurecom.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class CardDeck extends ImageView implements OnTouchListener {
	private static char[] suits = {'s', 'h', 'd', 'c'};
	private static int[] faces = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
	
	private List<Card> cards;
	private Point anchorPoint = new Point();
	private Point screenSize;
	private CardPlayerHand playerHand;
	
	private long lastDown;
	
	public CardDeck(Context context){
		super(context);
		
		//TODO: Fix layout params based on resolution or make different resolutions of deck.png
		
		this.cards = new LinkedList<Card>();
		
		for (char suit : suits){
			for (int face : faces){
				cards.add(new Card(context, suit, face, false));
			}
		}
		
		this.setOnTouchListener(this);
		this.setImageResource(context.getResources().getIdentifier("drawable/deck", null, context.getPackageName()));
		
		
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		screenSize = new Point();
		display.getSize(screenSize);
	}
	
	public void setOwner(CardPlayerHand owner) {
		this.playerHand = owner;
	}
	
	public Card pop(){
		if (cards.isEmpty()) return null;
		return cards.remove(0);
	}

	public Card peak(){
		if (cards.isEmpty()) return null;
		return cards.get(0);
	}
	
	public List<Card> draw(int n){
		List<Card> temp = new LinkedList<Card>();
		for (int i = 0; i < n; i++){
			if (cards.isEmpty()) break;
			temp.add(this.pop());
		}
		return temp;
	}
	
	public void shuffle(){
		Collections.shuffle(this.cards);
	}
	
	private Card drawFromDeck() {
		if (cards.size() == 1) { //Drawing the last card of the deck
			this.setAlpha((float)0.1);
			this.setOnTouchListener(null);
		}
		Card c = pop();
		//TODO: Set pos
		c.setX(this.getX() + this.getWidth());
		c.setY(this.getY());
		return c;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final int action = event.getAction();
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if(lastDown != 0 && (System.currentTimeMillis() - lastDown) <= 200) {
				playerHand.drawFromDeck(drawFromDeck());
				return true;
			}
			lastDown = System.currentTimeMillis();
			
			anchorPoint.x = (int) (event.getRawX() - v.getX());
			anchorPoint.y = (int) (event.getRawY() - v.getY());
			
			v.setAlpha((float)0.5);
			v.bringToFront();
			
			return true;
			
		case MotionEvent.ACTION_MOVE:
			int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            
            float posX = x-anchorPoint.x;
            float posY = y-anchorPoint.y;
            
            if(posX < 0 || (posX+getWidth()) > screenSize.x) {
            	if(posY > 0 && (posY+getHeight()) < screenSize.y - Card.height) {
            		v.setY(y-(anchorPoint.y));
            	}
            } else if(posY < 0 || (posY+getHeight()) > screenSize.y - Card.height) {
            	if(posX > 0 && (posX+getWidth()) < screenSize.x) {
            		v.setX(x-(anchorPoint.x));
            	}
            } else {
            	v.setX(x-(anchorPoint.x));
            	v.setY(y-(anchorPoint.y));
            }
            
            return true;
		case MotionEvent.ACTION_UP:
			v.setAlpha(1);
			return true;
		default:
			return false;
		}
	}
}
