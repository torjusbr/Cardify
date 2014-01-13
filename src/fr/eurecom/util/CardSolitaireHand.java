package fr.eurecom.util;

import java.util.LinkedList;
import java.util.List;

import fr.eurecom.cardify.Game;

public class CardSolitaireHand extends CardPlayerHand {

	public List<CardView> cardHeap;
	
	public CardSolitaireHand(Game game) {
		super(game);
		cardHeap = new LinkedList<CardView>();
	}
	
	@Override
	public void addToPublic(CardView view) {
		if (cardStack.remove(view)) {
			cardHeap.add(view);
			stackCards();
		}
	}
	
	@Override
	public void removeFromPublic(CardView view) {
		if (cardHeap.remove(view)) {
			cardStack.add(view);
			stackCards();
		}
	}
	
	@Override
	public void turnCard(CardView view) {
		//Avoid publishing when turning card
	}
	
	@Override
	protected void addToDeck(CardView view) {
		if (!cardPublic.remove(view)) {
			cardStack.remove(view);
			stackCards();
		}
		
		game.getDeck().addCard(view.getCard());
		game.getDeck().setColorFilter(null);
		
		removeCardGraphics(view);
		
		System.out.println("VIEW:"+view.getCard().getSuit());
	}
	
	@Override
	public void drawFromDeck(Card card) {
		CardView view = addCardGraphics(card);
		cardPublic.add(view);
	}
	
	
}
