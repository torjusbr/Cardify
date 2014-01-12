package fr.eurecom.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class CardDeck extends ImageView implements OnTouchListener {
	private static char[] suits = {'s', 'h', 'd', 'c'};
	private static int[] faces = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
	
	private List<Card> cards;
	
	public CardDeck(Context context){
		super(context);
		
		this.cards = new LinkedList<Card>();
		
		for (char suit : suits){
			for (int face : faces){
				cards.add(new Card(context, suit, face, false));
			}
		}
		
		this.setImageResource(context.getResources().getIdentifier("drawable/deck", null, context.getPackageName()));
		
		//TODO: Fix layout params based on resolution or make different resolutions of deck.png
		//this.setLayoutParams(new LayoutParams(208,150));
	}
	
	public Card pop(){
		if (cards.isEmpty()) return null;
		return cards.remove(0);
	}

	public Card peak(){
		if (cards.isEmpty()) return null;
		return cards.get(0);
	}
	
	public List<Card> draw(int n){
		List<Card> temp = new LinkedList<Card>();
		for (int i = 0; i < n; i++){
			if (cards.isEmpty()) break;
			temp.add(this.pop());
		}
		return temp;
	}
	
	public void shuffle(){
		Collections.shuffle(this.cards);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
