package fr.eurecom.util;

import java.util.LinkedList;
import java.util.List;

import fr.eurecom.cardify.Game;

public class CardSolitaireHand extends CardPlayerHand {

	public List<Card> cardHeap;
	
	public CardSolitaireHand(Game game) {
		super(game);
		cardHeap = new LinkedList<Card>();
	}
	
	@Override
	public void addToPublic(Card card) {
		if (cardStack.remove(card)) {
			cardHeap.add(card);
			stackCards();
		}
	}
	
	@Override
	public void removeFromPublic(Card card) {
		if (cardHeap.remove(card)) {
			cardStack.add(card);
		}
	}
	
	
}
