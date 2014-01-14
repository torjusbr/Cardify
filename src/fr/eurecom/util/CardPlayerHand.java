package fr.eurecom.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import fr.eurecom.cardify.Game;

public class CardPlayerHand {

	public List<CardView> cardStack;
	public List<CardView> cardPublic;
	public Game game;
	private Point displaySize;
	
	public CardPlayerHand(Game game){
		this.game = game;
		cardStack = new LinkedList<CardView>();
		cardPublic = new LinkedList<CardView>();
		displaySize = game.getDisplaySize();
	}
	
	public void dealCards(List<Card> cards){
		for (Card card : cards){
			CardView view = addCardGraphics(card);
			cardStack.add(view);
		}
		Collections.sort(cardStack, new CardComparator(CardSortingRule.S_H_D_C_ACE_HIGH));
		stackCards();
	}
	
	public void drawFromDeck(Card card) {
		CardView view = addCardGraphics(card);
		cardPublic.add(view);
		
		this.printMessage("You drew", "from the deck", card, false);
		game.getClient().publishDrawFromDeck(card);
	}
	
	protected void addToDeck(CardView view) {
		game.getDeck().addCard(view.getCard());
		game.getDeck().setColorFilter(null);
		
		if (!cardPublic.remove(view)) {
			cardStack.remove(view);
			view.getCard().setTurned(true);
			stackCards();
		}
		
		this.printMessage("You added", "to the deck", view.getCard(), !view.getCard().getTurned());
		game.getClient().publishAddCardToDeck(view.getCard());
		
		removeCardGraphics(view);
	}
	
	private void addToStack(CardView view) {
		cardStack.remove(view);
		int pos = 0;
		for (CardView v : cardStack) {
			if (v.getX() >= view.getX()) break;
			pos++;
		}
		cardStack.add(pos, view);
		stackCards();
	}
	
	public void addToPublic(CardView view){
		if (cardStack.remove(view)){
			cardPublic.add(view);
			game.getClient().publishPutCardInPublicZone(view.getCard());
			this.printMessage("You played", "", view.getCard(), !view.getCard().getTurned());
			stackCards();
		}
	}
	
	public void removeFromPublic(CardView view) {
		if (cardPublic.remove(view)) {
			addToStack(view);
			game.getClient().publishTakeCardFromPublicZone(view.getCard());
			
			this.printMessage("You took", "from the table", view.getCard(), !view.getCard().getTurned());
		}
	}
	
	public void blindAddToPublic(char suit, int face, boolean turned) {
		Log.e("CardPlayerHand:blindAddToPublic", ""+suit+face);
		Card card = new Card(suit, face, turned);
		CardView view = addCardGraphics(card);
		view.setOwner(this);
		cardPublic.add(view);
		animateCardIntoView(view);
		
		this.printMessage("Opponent played", "", card, !card.getTurned());
	}
	
	public void blindRemoveFromPublic(char suit, int face){
		CardView view = getCardViewFromPublic(suit, face);
		if (cardPublic.remove(view)) {
			this.printMessage("Opponent took", "from the table", view.getCard(), view.getCard().getTurned());
			removeCardGraphics(view);
		}
	}
	
	public void blindAddToDeck(char suit, int face, boolean turned) {
		//TODO: Opponent added card to deck animation
		
		CardView view = getCardViewFromPublic(suit, face);
		
		if (view == null) { //from opponents stack, no alterations in graphics
			Card card = new Card(suit, face, true);
			game.getDeck().addCard(card);
			this.printMessage("Opponent added", "from his stack to the deck", card, !card.getTurned());
		} else { //from public area
			cardPublic.remove(view);
			game.getDeck().addCard(view.getCard());
			this.printMessage("Opponent added", "from the table to the deck", view.getCard(), !view.getCard().getTurned());
			removeCardGraphics(view);
		}
	}
	
	public void blindDrawFromDeck() {
		//TODO: Draw from deck animation
		
		Card card = game.getDeck().drawFromDeck();
		CardView view = addCardGraphics(card);
		cardPublic.add(view);
		
		this.printMessage("Opponent drew", "from the deck", null, false);
	}
	
	public void blindTurnInPublic(char suit, int face) {
		CardView view = getCardViewFromPublic(suit, face);
		if (view != null) {
			view.getCard().turn();
			String turned = view.getCard().getTurned() ? "face down" : "face up";
			view.updateGraphics();
			
			this.printMessage("Opponent turned", turned, view.getCard(), true);
		}
	}
	
	public void blindDealCards(String[] cardStrings){
		this.dealCards(getCardListFromStrings(cardStrings));
	}
	
	public void blindAddDeck(String[] cardStrings) {
		game.setDeck(new CardDeck(game.getApplicationContext(),getCardListFromStrings(cardStrings)));
		game.getDeck().setOwner(this);
		game.addView(game.getDeck());
	}
	
	private CardView getCardViewFromPublic(char suit, int face) {
		CardView view = null;
		for (CardView v : cardPublic) {
			if (v.getCard().getSuit() == suit && v.getCard().getFace() == face) {
				view = v;
				break;
			}
		}
		return view;
	}
	
	private List<Card> getCardListFromStrings(String[] strings) {
		List<Card> cards = new LinkedList<Card>();
		for (String str : strings) {
			boolean turned = str.charAt(0) == '1' ? true : false;
			char suit = str.charAt(1);
			int face = Integer.parseInt(str.substring(2));
			cards.add(new Card(suit, face, turned));
		}
		return cards;
	}
	
	public void stackCards(){
		if (cardStack.isEmpty()) return;
		
		int height = cardStack.get(0).getHeight();
		int width = cardStack.get(0).getWidth();
		int maximumStackWidth = displaySize.x - 10; //5px free on each side of stack
		double pixelsBetweenCards = cardStack.size() != 1 ? Math.min(width/2, (maximumStackWidth - width)/(cardStack.size() - 1)) : 0;
		int y = displaySize.y - height;
		int x = 0;
		
		if (pixelsBetweenCards <= width/2) {
			x = (int) Math.round(((displaySize.x - ((cardStack.size() - 1)*pixelsBetweenCards + width))/2));
		} else {
			x = 5;
		}
		
		for (CardView view : cardStack) {
			view.setX(x);
			view.setY(y);
			x += Math.round(pixelsBetweenCards);
			queueBringToFront(view);
		}
		applyBringToFront(cardStack.get(0));
	}
	
	public boolean inStackZone(float x, float y) {
		int height = cardStack.get(0).getHeight();
		if (x < 0 || x > displaySize.x) return false;
		if (y < displaySize.y - 1.75*height || y > displaySize.y) return false;
		return true;
	}
	
	protected boolean inCardDeck(float x, float y) {
		CardDeck d = game.getDeck();
		float w = d.getWidth()/2;
		float h = d.getHeight()/2;
		
		return 	(x < d.getX() + w) && 
				(x > d.getX() - w) &&
				(y < d.getY() + h) &&
				(y > d.getY() - h);
	}
	
	public void takeCard(CardView view) {
		//TODO: Delete if unused
	}
	
	public void dropCard(CardView view){
		if(inStackZone(view.getX(), view.getY())) {
			removeFromPublic(view);
		} else if (inCardDeck(view.getX(), view.getY())) {
			addToDeck(view);
		} else {
			addToPublic(view);
		}
	}
	
	public void moveCard(CardView view) {
		//TODO: Possibly add color filter to stack zone
		if (inCardDeck(view.getX(), view.getY())) {
			game.getDeck().toggleHighlight(true);
		} else {
			game.getDeck().toggleHighlight(false);
		}
		
	}
	
	public void turnCard(CardView view) {
		if(!inStackZone(view.getX(), view.getY())) {
			game.getClient().publishTurnCardInPublicZone(view.getCard());
		} 
	}
	
	private void queueBringToFront(View v){
		v.bringToFront();
	}
	
	private void applyBringToFront(View view){
		view.requestLayout();
		view.invalidate();
	}
	
	public void sortCards(CardSortingRule sorting) {
		Collections.sort(cardStack, new CardComparator(sorting));
		stackCards();
	}
	
	private void printMessage(String start, String end, Card c, boolean showValue) {
		String value = showValue ? ""+c.getSuit()+c.getFace() : "a card";
		String formatted = String.format("%s %s %s\n", start, value, end);
		game.printMessage(formatted);
	}
	
	protected CardView addCardGraphics(Card card) {
		CardView cView = new CardView(this.game, card);
		cView.setOwner(this);
		game.addView(cView);
		return cView;
	}
	
	protected void removeCardGraphics(CardView view) {
		game.removeView(view);
		view = null;
		System.gc();
	}
	
	private void animateCardIntoView(CardView view) {
		view.setX(displaySize.x/2 - view.getWidth()/2);
		view.setY(0 - view.getHeight());
		
		int yTranslation = (displaySize.y/2 - view.getHeight()/2);
		
		view.animate().translationY(yTranslation).setDuration(1000).setInterpolator(new AccelerateDecelerateInterpolator());
	}
	
}
