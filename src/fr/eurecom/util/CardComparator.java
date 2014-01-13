package fr.eurecom.util;

import java.util.ArrayList;
import java.util.Comparator;

public class CardComparator implements Comparator<CardView> {
	
	private ArrayList<Character> suitArray = new ArrayList<Character>();
	private CardSortingRule sorting;

	public CardComparator(CardSortingRule sorting) {
		this.sorting = sorting;		
		switch (sorting) {
			case S_H_D_C_ACE_HIGH:
			default:
				suitArray.add('s');
				suitArray.add('h');
				suitArray.add('d');
				suitArray.add('c');
				break;
		}
	}
	
	@Override
	public int compare(CardView a, CardView b) {
		switch (sorting) {
			case S_H_D_C_ACE_HIGH:
				if(a.getCard().getSuit() == b.getCard().getSuit()) {
					return a.getCard().getFace() == 1 ? 1 : (b.getCard().getFace() == 1 ? -1 : a.getCard().getFace() - b.getCard().getFace());
				} else {
					return suitArray.indexOf(b.getCard().getSuit())-suitArray.indexOf(a.getCard().getSuit());
				}
			case NO_SUIT_ACE_HIGH:
				return a.getCard().getFace() == 1 ? 1 : (b.getCard().getFace() == 1 ? -1 : a.getCard().getFace() - b.getCard().getFace());
			case S_H_D_C_ACE_LOW:
				if(a.getCard().getSuit() == b.getCard().getSuit()) {
					return a.getCard().getFace() - b.getCard().getFace();
				} else {
					return suitArray.indexOf(b.getCard().getSuit())-suitArray.indexOf(a.getCard().getSuit());
				}
			case NO_SUIT_ACE_LOW:
				return a.getCard().getFace() - b.getCard().getFace();
			default:
				return 0;
		}
	}
}
