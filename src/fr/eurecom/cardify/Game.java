package fr.eurecom.cardify;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import fr.eurecom.util.CardDeck;
import fr.eurecom.util.CardPlayerHand;

public class Game extends Activity {

	private CardDeck deck;
	private CardPlayerHand playerHand;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		initGame();
	}
	
	public Point getDisplaySize(){
		Display display = getWindowManager().getDefaultDisplay();
		Point displaySize = new Point();
		display.getSize(displaySize);
		return displaySize;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	private void initGame(){
		this.deck = new CardDeck(this);
		deck.shuffle();
		
		playerHand = new CardPlayerHand(this);
		playerHand.dealInitialCards(deck.draw(13));
	}
	
	public void addView(View v){
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.rootGameLayout);
		layout.addView(v);
	}
	
}