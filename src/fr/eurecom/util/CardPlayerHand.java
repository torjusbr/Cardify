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

	public List<Card> cardStack;
	public List<Card> cardPublic;
	public Game game;
	private Point displaySize;
	
	//TODO: handle add to deck from stack (not restacking)
	
	
	public CardPlayerHand(Game game){
		this.game = game;
		cardStack = new LinkedList<Card>();
		cardPublic = new LinkedList<Card>();
		displaySize = game.getDisplaySize();
	}
	
	public void dealCards(List<Card> cards){
		for (Card c : cards){
			game.addView(c);
			c.setOwner(this);
		}
		cardStack.addAll(cards);
		Collections.sort(cardStack, new CardComparator(CardSortingRule.S_H_D_C_ACE_HIGH));
		stackCards();
	}
	
	public void drawFromDeck(Card card) {
		game.addView(card);
		card.setOwner(this);
		cardPublic.add(card);
		
		this.printMessage("You drew", "from the deck", card, false);
		game.getClient().publishDrawFromDeck(card);
	}
	
	protected void addToDeck(Card card) {
		game.removeView(card);
		card.setOwner(null);
		
		game.getDeck().addCard(card);
		game.getDeck().setColorFilter(null);
		
		if (!cardPublic.remove(card)) {
			cardStack.remove(card);
			card.setTurned(true);
			stackCards();
		}
		
		this.printMessage("You added", "to the deck", card, !card.getTurned());
		game.getClient().publishAddCardToDeck(card);
	}
	
	
	public void addToStack(Card card){
		cardStack.remove(card);
		int pos = 0;
		for (Card c : cardStack){
			if (c.getX() >= card.getX()) break;
			pos++;
		}
		cardStack.add(pos, card);
		stackCards();
	}
	
	public void addToPublic(Card card){
		if (cardStack.remove(card)){
			cardPublic.add(card);
			game.getClient().publishPutCardInPublicZone(card);
			this.printMessage("You played", "", card, !card.getTurned());
		}
		stackCards();
	}
	
	public void removeFromPublic(Card card) {
		if (cardPublic.remove(card)) {
			cardStack.add(card);
			game.getClient().publishTakeCardFromPublicZone(card);
			
			this.printMessage("You took", "from the table", card, !card.getTurned());
		}
	}
	
	public void blindAddToPublic(char suit, int face, boolean turned) {
		Log.e("CardPlayerHand:blindAddToPublic", ""+suit+face);
		Card card = new Card(this.game, suit, face, turned);
		card.setOwner(this);
		cardPublic.add(card);
		animateCardIntoView(card);
		
		this.printMessage("Opponent played", "", card, !card.getTurned());
	}
	
	public void blindRemoveFromPublic(char suit, int face){
		Card cardToRemove = null;
		for (Card card : cardPublic){
			if (card.getSuit() == suit && card.getFace() == face){
				cardToRemove = card;
				break;
			}
		}
		if (cardPublic.remove(cardToRemove)){
			game.removeView(cardToRemove);
		}
		
		this.printMessage("Opponent took", "from the table", cardToRemove, cardToRemove.getTurned());
	}
	
	public void blindAddToDeck(char suit, int face, boolean turned) {
		Card card = null;
		String endOfMessage = "";
		
		for (Card c : cardPublic) {
			if (c.getSuit() == suit && c.getFace() == face){
				card = c;
				cardPublic.remove(c);
				game.removeView(c);
				endOfMessage = "to the deck";
				break;
			}
		}
		if (card == null) { //from opponents stack
			card = new Card(this.game, suit, face, true);
			card.setOwner(this);
			endOfMessage = "from his stack to the deck";
		}
		
		game.getDeck().addCard(card);
		
		//TODO: Opponent added card to deck animation
		this.printMessage("Opponent added", endOfMessage, card, !card.getTurned());
	}
	
	public void blindDrawFromDeck() {
		Card card = game.getDeck().drawFromDeck();
		card.setOwner(this);
		cardPublic.add(card);
		
		game.addView(card);
		
		
		this.printMessage("Opponent drew", "from the deck", null, false);
	}
	
	public void blindTurnInPublic(char suit, int face) {
		Card cardToTurn = null;
		for (Card card: cardPublic) {
			if (card.getSuit() == suit && card.getFace() == face) {
				cardToTurn = card;
				break;
			}
		}
		if (cardToTurn != null) {
			cardToTurn.setTurned(!cardToTurn.getTurned());
			String turned = cardToTurn.getTurned() ? "down" : "up";
			
			this.printMessage("Opponent turned", turned, cardToTurn, true);
		}
	}
	
	private void animateCardIntoView(Card card) {
		card.setX(displaySize.x/2 - Card.width/2);
		card.setY(0 - Card.height);
		
		int yTranslation = (displaySize.y/2 - Card.height/2);
		
		game.addView(card);
		card.animate().translationY(yTranslation).setDuration(1000).setInterpolator(new AccelerateDecelerateInterpolator());
	}
	
	public void blindDealCards(String[] cardStrings){
		List<Card> cards = new LinkedList<Card>();
		for (String str : cardStrings){
			boolean turned = str.charAt(0) == '1' ? true : false;
			char suit = str.charAt(1);
			int face = Integer.parseInt(str.substring(2));
			cards.add(new Card(this.game, suit, face, turned));
		}
		this.dealCards(cards);
	}
	
	public void blindAddDeck(String[] cardStrings) {
		List<Card> cards = new LinkedList<Card>();
		for (String str : cardStrings) {
			boolean turned = str.charAt(0) == '1' ? true : false;
			char suit = str.charAt(1);
			int face = Integer.parseInt(str.substring(2));
			cards.add(new Card(this.game, suit, face, turned));
		}
		game.setDeck(new CardDeck(game.getApplicationContext(),cards));
		game.getDeck().setOwner(this);
		game.addView(game.getDeck());
	}
	
	public void stackCards(){
		if (cardStack.isEmpty()) return;
		
		int maximumStackWidth = displaySize.x - 10; //5px free on each side of stack
		double pixelsBetweenCards = cardStack.size() != 1 ? Math.min(Card.width/2, (maximumStackWidth - Card.width)/(cardStack.size() - 1)) : 0;
		int y = displaySize.y - Card.height;
		int x = 0;
		
		if (pixelsBetweenCards <= Card.width/2) {
			x = (int) Math.round(((displaySize.x - ((cardStack.size() - 1)*pixelsBetweenCards + Card.width))/2));
		} else {
			x = 5;
		}
		
		for (Card card : cardStack) {
			card.setX(x);
			card.setY(y);
			x += Math.round(pixelsBetweenCards);
			queueBringToFront(card);
		}
		applyBringToFront(cardStack.get(0));
	}
	
	public boolean inStackZone(float x, float y) {
		if (x < 0 || x > displaySize.x) return false;
		if (y < displaySize.y - 1.75*Card.height || y > displaySize.y) return false;
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
	
	public void takeCard(Card card) {
		//TODO: Delete if unused
	}
	
	public void dropCard(Card card){
		if(inStackZone(card.getX(), card.getY())) {
			removeFromPublic(card);
			addToStack(card);
		} else if (inCardDeck(card.getX(), card.getY())) {
			addToDeck(card);
		} else {
			addToPublic(card);
		}
	}
	
	public void moveCard(Card card) {
		if (inStackZone(card.getX(), card.getY())) {
			//TODO: Add color filter to stack zone??
			game.getDeck().toggleHighlight(false);
		} else if (inCardDeck(card.getX(), card.getY())) {
			game.getDeck().toggleHighlight(true);
		} else {
			game.getDeck().toggleHighlight(false);
		}
		
	}
	
	public void turnCard(Card card) {
		if(!inStackZone(card.getX(), card.getY())) {
			game.getClient().publishTurnCardInPublicZone(card);
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
	
}
