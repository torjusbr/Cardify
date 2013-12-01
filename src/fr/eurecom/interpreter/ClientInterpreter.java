package fr.eurecom.interpreter;

import android.content.Context;
import fr.eurecom.cardify.Game;
import fr.eurecom.util.Card;

public class ClientInterpreter {

	private String id;
	private Game game;
	
	public ClientInterpreter(Context context) {
		this.id = "my wifi direct ip address";
		this.game = null;
	}
	
	private void sendActionMessage(Action action, String subject) {
		ActionMessage message = new ActionMessage(this.id, action, subject);
		return;
	}
	
	public void publishTakeCardFromPublicZone(Card card){
		sendActionMessage(Action.REMOVED_CARD_FROM_PUBLIC_ZONE, card.toString());
	}
	
	public void publishPutCardInPublicZone(Card card){
		sendActionMessage(Action.ADDED_CARD_TO_PUBLIC_ZONE, card.toString());
	}
	
	
	public void parseMessage(ActionMessage message){
		
		if (this.game == null) {
			if (message.getAction().equals(Action.GAME_STARTED)){
				handleGameStarted(message);
			} else {
				return;
			}
		}
		
		switch (message.getAction()){
		case ADDED_CARD_TO_PUBLIC_ZONE:
			handleNewCardInPublicZone(message);
			return;
		case REMOVED_CARD_FROM_PUBLIC_ZONE:
			return;
		case ILLEGAL_ACTION:
			return;
		default:
			return;
		}
	}
	
	private void handleNewCardInPublicZone(ActionMessage message) {
		char suit = message.getSubject().charAt(0);
		int face = Integer.parseInt(message.getSubject().substring(1));
		//TODO:
		/*
		 * Make receiver method in game instance to handle such events
		 */
		return;
	}
	
	private void handleGameStarted(ActionMessage message){
		//TODO:
		/*
		 * Start new game and set local game variable in this interpreter to game instance
		 */
	}
	
}
