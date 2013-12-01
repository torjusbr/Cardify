package fr.eurecom.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Point;
import android.view.View;
import fr.eurecom.cardify.Game;

public class CardPlayerHand {

	public List<Card> cardStack;
	public List<Card> cardHeap;
	public List<Card> cardPublic;
	public Game game;
	
	public CardPlayerHand(Game game){
		this.game = game;
		cardStack = new LinkedList<Card>();
		cardHeap = new LinkedList<Card>();
		cardPublic = new LinkedList<Card>();
	}
	
	public void dealInitialCards(List<Card> cards){
		cardStack = cards;
		Collections.sort(cardStack, new CardComparator(CardSortingRule.S_H_D_C_ACE_HIGH));
		stackCards();
		for (Card c : cardStack){
			game.addView(c);
			c.setOwner(this);
		}
	}
	
	public void addToStack(Card card){
		removeFromStackAndHeap(card);
		int pos = 0;
		for (Card c : cardStack){
			if (c.getX() >= card.getX()) break;
			pos++;
		}
		cardStack.add(pos, card);
		stackCards();
	}
	
	public void addToHeap(Card card){
		removeFromStackAndHeap(card);
		cardHeap.add(card);
		stackCards();
	}
	
	public void addToPublic(Card card){
		
	}
	
	public void stackCards(){
		if (cardStack.isEmpty()) return;
		Point displaySize = game.getDisplaySize();
		int pixelsBetweenCards = displaySize.x/cardStack.size() - (Card.width-displaySize.x/cardStack.size())/cardStack.size();
		int y = displaySize.y-Card.height;
		int x = 0;
		
		int i = 0;
		for (Card card : cardStack){
			x = (i++)*pixelsBetweenCards;
			card.setX(x);
			card.setY(y);
			queueBringToFront(card);
		}
		applyBringToFront(cardStack.get(0));
	}
	
	public boolean inStackZone(float x, float y){
		Point displaySize = game.getDisplaySize();
		if (x < 0 || x > displaySize.x) return false;
		if (y < displaySize.y - 1.75*Card.height || y > displaySize.y) return false;
		return true;
	}
	
	public boolean inPublicZone(float x, float y){
		Point displaySize = game.getDisplaySize();
		if (x < displaySize.x*0.5 || x > displaySize.x) return false;
		if (y < 0 || y > displaySize.y*0.5) return false;
		return true;
	}
	
	public boolean inHeapZone(float x, float y){
		Point displaySize = game.getDisplaySize();
		if(x > displaySize.x*0.5 || x < 0) return false;
		if(y < 0 || y > displaySize.y - Card.height) return false;
		return true;
	}

	public void moveCard(Card card){
		if (inStackZone(card.getX(), card.getY())){
			System.out.println("In stack zone!");
			addToStack(card);
		} else if(inHeapZone(card.getX(), card.getY())){
			System.out.println("In heap zone");
			addToHeap(card);
		} else if(inPublicZone(card.getX(), card.getY())) {
			System.out.println("In public zone");
		} else {
			System.out.println("Outside stack and heap");
		}
	}
	
	public void removeFromStackAndHeap(Card card){
		cardHeap.remove(card);
		cardStack.remove(card);
	}
	
	private void queueBringToFront(View v){
		v.bringToFront();
	}
	
	private void applyBringToFront(View view){
		view.requestLayout();
		view.invalidate();
	}
	
}
