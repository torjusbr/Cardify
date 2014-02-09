package fr.eurecom.util;

import fr.eurecom.cardify.Game;

public class CardSolitaireHand extends CardPlayerHand {
	
	public CardSolitaireHand(Game game) {
		super(game);
	}
	
	@Override
	public void turnCard(CardView view) {
		//Avoid publishing when turning card
	}
	
	@Override
	protected void addToDeckFromStack(CardView view) {
		game.getDeck().addCard(view.getCard());
		game.getDeck().toggleHighlight(false);
		stackCards();
		removeCardGraphics(view);
	}
	
	@Override
	protected void addToDeckFromPublic(CardView view) {
		game.getDeck().addCard(view.getCard());
		game.getDeck().toggleHighlight(false);
		removeCardGraphics(view);
	}
	
	@Override
	protected void addToPublicFromDeck(CardView view) {
		cardPublic.add(view);
	}
	
	@Override
	protected void addToPublicFromStack(CardView view) {
		cardPublic.add(view);
		stackCards();
	}
	
	@Override
	protected void addToStackFromDeck(CardView view) {
		cardStack.add(view);
		stackCards();
	}
	
	@Override
	protected void addToStackFromPublic(CardView view) {
		cardStack.add(view);
		stackCards();
	}
	
	@Override
	protected void broadcastPositionUpdate(CardView view) {
		return;
	}
	
	@Override
	public void redeal(int numberOfCards) {
		reDealCards(game.getDeck().draw(numberOfCards));
	}
	
	@Override
	public void shuffle() {
		for (CardView v : cardPublic) {
			game.getDeck().addCard(v.getCard());
			removeCardGraphics(v);
		}
		game.getDeck().shuffle();
	}
	
}
