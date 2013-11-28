package fr.eurecom.cardify;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import fr.eurecom.util.Card;
import fr.eurecom.util.CardComparator;
import fr.eurecom.util.CardDeck;
import fr.eurecom.util.CardPlayerHand;
import fr.eurecom.util.CardSortingRule;

public class Game extends Activity {

	private List<Card> playerCards;
	private Point displaySize;
	private CardDeck deck;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		this.displaySize = getDisplaySize();
		
		testCards();
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
	
	private void testCards() {
		CardDeck deck = new CardDeck(this);
		deck.shuffle();
		playerCards = deck.draw(13);
		Collections.sort(playerCards, new CardComparator(CardSortingRule.S_H_D_C_ACE_HIGH));
		//drawCards();
		
		CardPlayerHand cards = new CardPlayerHand(this);
		cards.dealInitialCards(playerCards);
		
	}	
	
	public void addView(View v){
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.rootGameLayout);
		layout.addView(v);
	}
	
}