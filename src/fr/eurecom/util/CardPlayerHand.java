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
			
			game.printMessage("You played "+card.getSuit()+card.getFace()+"\n");
		}
		stackCards();
	}
	
	public void removeFromPublic(Card card) {
		if (cardPublic.remove(card)) {
			cardStack.add(card);
			game.getClient().publishTakeCardFromPublicZone(card);
			
			game.printMessage("You took "+card.getSuit()+card.getFace()+" from the table\n");
		}
	}
	
	public void blindAddToPublic(char suit, int face, boolean turned) {
		Log.e("CardPlayerHand:blindAddToPublic", ""+suit+face);
		Card card = new Card(this.game, suit, face, turned);
		card.setOwner(this);
		cardPublic.add(card);
		animateCardIntoView(card);
		
		game.printMessage("PLAYER played "+suit+face+"\n");
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
		
		game.printMessage("PLAYER took "+suit+face+" from the table\n");
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
			cardToTurn.setTurned();
			String turned = cardToTurn.getTurned() ? "down" : "up";
			
			game.printMessage("PLAYER turned "+suit+face+" face "+turned+"\n");
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
	
	public boolean inStackZone(float x, float y){
		if (x < 0 || x > displaySize.x) return false;
		if (y < displaySize.y - 1.75*Card.height || y > displaySize.y) return false;
		return true;
	}
	
	public void takeCard(Card card) {
		
	}
	
	public void dropCard(Card card){
		if(inStackZone(card.getX(), card.getY())) {
			removeFromPublic(card);
			addToStack(card);
		} else {
			addToPublic(card);
		}
	}
	
	public void moveCard(Card card) {
		
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
	
}
