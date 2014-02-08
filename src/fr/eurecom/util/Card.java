package fr.eurecom.util;


public class Card {
	
	private char suit;
	private int face;
	private boolean turned;
	
	public Card(char suit, int face, boolean turned) {
		this.suit = suit;
		this.face = face;
		this.turned = turned;
	}
	
	public char getSuit() {
		return suit;
	}

	public int getFace() {
		return face;
	}
	
	public String toString() {
		return this.turned ? "1"+this.suit+this.face : "0"+this.suit+this.face;
	}
	
	public String toStringWithPosition(float x, float y) {
		return String.format("%s@%s,%s", this.toString(), x, y);
	}
	
	public void turn() {
		this.turned = !this.turned;
	}
	
	public void setTurned(boolean turned) {
		this.turned = turned;
	}
	
	public boolean getTurned() {
		return this.turned;
	}

}

