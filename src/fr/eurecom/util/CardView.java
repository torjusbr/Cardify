package fr.eurecom.util;

import fr.eurecom.cardify.Game;
import android.content.Context;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class CardView extends ImageView implements OnTouchListener {
	
	private long lastDown;
	private Context context;
	private Point anchorPoint = new Point();
	private CardPlayerHand playerHand;
	private Card card;
	
	public CardView(Context context) {
		super(context);
	}
	
	public CardView(Context context, CardPlayerHand playerHand) {
		super(context);
		this.context = context;
		this.playerHand = playerHand;
		this.setOnTouchListener(new GhostTouchListener());
	}
	
	public CardView(Context context, Card card, CardPlayerHand playerHand) {
		super(context);
		this.context = context;
		this.card = card;
		this.playerHand = playerHand;
		
		this.setOnTouchListener(this);
		this.updateGraphics();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		final int action = event.getAction();
		
		switch(action) {
			case MotionEvent.ACTION_DOWN:
				if(lastDown != 0 && (System.currentTimeMillis() - lastDown) <= 200) {
					card.setTurned(card.getTurned() ? false : true);
					updateGraphics();
					playerHand.turnCard(this);
				}
				lastDown = System.currentTimeMillis();
				
				anchorPoint.x = (int) (event.getRawX() - v.getX());
				anchorPoint.y = (int) (event.getRawY() - v.getY());
				
				playerHand.liftCard(this);
				
		    	return true;
		    	
			case MotionEvent.ACTION_MOVE:
				
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                
                float posX = x-anchorPoint.x;
                float posY = y-anchorPoint.y;
                
                if(posX < 0 || (posX+getWidth()) > Game.screenSize.x) {
                	if(posY > 0 && (posY+getHeight()) < Game.screenSize.y) {
                		v.setY(y-(anchorPoint.y));
                	}
                } else if(posY < 0 || (posY+getHeight()) > Game.screenSize.y) {
                	if(posX > 0 && (posX+getWidth()) < Game.screenSize.x) {
                		v.setX(x-(anchorPoint.x));
                	}
                } else {
                	v.setX(x-(anchorPoint.x));
                	v.setY(y-(anchorPoint.y));
                }
                
                playerHand.moveCard(this);
                playerHand.broadcastPositionUpdate(this);
                return true;
				
			case MotionEvent.ACTION_UP:
				
				playerHand.dropCard(this);
				return true;

			default:
				return false;
		}
	}
	
	public void setOwner(CardPlayerHand owner) {
		this.playerHand = owner;
	}
	
	public Card getCard() {
		return this.card;
	}
	
	public void setCard(Card card) {
		this.card = card;
	}
	
	private String getResourceString() {
		return card.getTurned() ? "drawable/back_blue" : "drawable/"+card.getSuit()+card.getFace();
	}
	
	private static int getImageResource(Context context, String string) {
		return context.getResources().getIdentifier(string, null, context.getPackageName());
	}
	
	public void updateGraphics() {
		System.out.println("UPDATE GRAPHICS: "+getResourceString()+" -- Card is "+card.getTurned());
		this.setImageResource(getImageResource(context, getResourceString()));
	}
	
	private void setGhostGraphics() {
		this.setImageResource(getImageResource(context, "drawable/back_blue"));
	}
	
	protected void ghostDown() {
		setGhostGraphics();
		playerHand.liftCard(this);
	}
	
	protected void ghostCancel() {
		this.setImageResource(android.R.color.transparent);
	}
	
	protected void ghostMove() {
		playerHand.moveCard(this);
	}
	
	protected void ghostUp() {
		playerHand.dropGhost(this);
	}	
	
	protected void ghostDeckDropped() {
		playerHand.dropDeck();
	}
	
	

	public class GhostTouchListener implements OnTouchListener {
		private Point anchorPoint = new Point();
		private int startX;
		private int startY;
		private boolean moveDeck;
		private boolean timerStarted;
		private CardView view;
		private CardDeck deck;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int action = event.getAction();
			
			switch (action) {
				case MotionEvent.ACTION_DOWN:
					view = (CardView)v;
					timer.start();
					timerStarted = true;
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					anchorPoint.x = (int) (event.getRawX() - v.getX());
					anchorPoint.y = (int) (event.getRawY() - v.getY());
					if (!view.playerHand.game.getDeck().getCards().isEmpty())
						view.ghostDown();
					return true;
				case MotionEvent.ACTION_MOVE:
					int x = (int) event.getRawX();
		            int y = (int) event.getRawY();
		            
		            float posX = x-anchorPoint.x;
		            float posY = y-anchorPoint.y;
		            
		            if (moveDeck) {
		            	float width = view.playerHand.game.getDeck().getWidth();
		            	float height = view.playerHand.game.getDeck().getHeight();
		            	if(posX < 0 || (posX+width) > Game.screenSize.x) {
		            		if(posY > 0 && (posY+height) < Game.screenSize.y - view.getHeight()) {
		            			deck.setY(y-(anchorPoint.y));
		            			v.setY(y-(anchorPoint.y));
		            		}
		            	} else if(posY < 0 || (posY+height) > Game.screenSize.y - view.getHeight()) {
		            		if(posX > 0 && (posX+width) < Game.screenSize.x) {
		            			deck.setX(x-(anchorPoint.x));
		            			v.setX(x-(anchorPoint.x));
		            		}
		            	} else {
		            		deck.setX(x-(anchorPoint.x));
		            		deck.setY(y-(anchorPoint.y));
		            		v.setX(x-(anchorPoint.x));
		            		v.setY(y-(anchorPoint.y));
		            	}
		            	
		            } else {
		            	if (!timerStarted || willCancel(startX, startY, x, y)) {
			            	timerStarted = false;
			            	timer.cancel();
				            
			            	if (!view.playerHand.game.getDeck().getCards().isEmpty()) {
			            		if(posX < 0 || (posX+getWidth()) > Game.screenSize.x) {
			            			if(posY > 0 && (posY+getHeight()) < Game.screenSize.y) {
			            				v.setY(y-(anchorPoint.y));
			            			}
			            		} else if(posY < 0 || (posY+getHeight()) > Game.screenSize.y) {
			            			if(posX > 0 && (posX+getWidth()) < Game.screenSize.x) {
			            				v.setX(x-(anchorPoint.x));
			            			}
			            		} else {
			            			v.setX(x-(anchorPoint.x));
			            			v.setY(y-(anchorPoint.y));
			            		}

			            		view.ghostMove();
			            	}
		            	}
		            }
					return true;
				case MotionEvent.ACTION_UP:
					if (moveDeck) {
						reset();
					} else if(!view.playerHand.game.getDeck().toggleEmpty()) {
						((CardView) v).ghostUp();
					}
					return true;
				default:
					return true;
			}
		}
		
		private boolean willCancel(int x1, int y1, int x2, int y2) {
			return Math.abs(x2-x1) > 10 || Math.abs(y2-y1) > 10;
		}
		
		private void reset() {
			moveDeck = false;
			timerStarted = false;
			if (!view.playerHand.game.getDeck().getCards().isEmpty())
				deck.setAlpha(1f);
			ghostDeckDropped();
		}
		
		private void startDeckMove() {
			view.ghostCancel();
			moveDeck = true;
			deck = view.playerHand.game.getDeck(); 
			deck.setAlpha(0.5f);
		}
		
		CountDownTimer timer = new CountDownTimer(1000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				
			}
			
			@Override
			public void onFinish() {
				startDeckMove();
			}
		};
		
	}
}
