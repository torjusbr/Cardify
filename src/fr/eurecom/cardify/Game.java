package fr.eurecom.cardify;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.widget.RelativeLayout;
import fr.eurecom.util.Card;
import fr.eurecom.util.CardComparator;
import fr.eurecom.util.CardDeck;
import fr.eurecom.util.CardSortingRule;

public class Game extends Activity {

	private List<Card> playerCards;
	private Point screenSize;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		Display display = getWindowManager().getDefaultDisplay();
		screenSize = new Point();
		display.getSize(screenSize);

		
		testCards();
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
		drawCards();
	}
	
	private void drawCards() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.rootGameLayout);
		
		final int cardSpace = screenSize.x/playerCards.size() - (Card.cardWidth-screenSize.x/playerCards.size())/playerCards.size();
		
		int yPos = screenSize.y-Card.cardHeight;
		for(int i = 0; i < playerCards.size(); i++) {
			int xPos = i*cardSpace;
			Card c = playerCards.get(i);
			c.setX(xPos);
			c.setY(yPos);
			layout.addView(c);
		}
	}
	
}