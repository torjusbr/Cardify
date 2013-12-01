package fr.eurecom.util;

import java.util.ArrayList;
import java.util.Comparator;

public class CardComparator implements Comparator<Card> {
	
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
	public int compare(Card a, Card b) {
		switch (sorting) {
			case S_H_D_C_ACE_HIGH:
				if(a.getSuit() == b.getSuit()) {
					return a.getFace()-b.getFace();
				} else {
					return suitArray.indexOf(b.getSuit())-suitArray.indexOf(a.getSuit());
				}
			default:
				return 0;
		}
	}
}
