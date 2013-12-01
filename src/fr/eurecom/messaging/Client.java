package fr.eurecom.messaging;

import android.net.wifi.p2p.WifiP2pInfo;
import fr.eurecom.cardify.Game;
import fr.eurecom.util.Card;

public class Client {

	private String id;
	private WifiP2pInfo info;
	private Game game;
	
	public Client(WifiP2pInfo info) {
		this.info = info;
		this.game = null;
	}
	
	public void publishTakeCardFromPublicZone(Card card){
		sendMessage(Action.REMOVED_CARD_FROM_PUBLIC_ZONE, card.toString());
	}
	
	public void publishPutCardInPublicZone(Card card){
		sendMessage(Action.ADDED_CARD_TO_PUBLIC_ZONE, card.toString());
	}
	
	
	private void sendMessage(Action action, String subject) {
		Message message = new Message(action, subject);
		Sender.send(message, info.groupOwnerAddress);
	}
	
	
	public void handleMessage(Message message){
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
	
	private void handleNewCardInPublicZone(Message message) {
		char suit = message.getSubject().charAt(0);
		int face = Integer.parseInt(message.getSubject().substring(1));
		//TODO:
		/*
		 * Make receiver method in game instance to handle such events
		 */
		return;
	}
	
	private void handleGameStarted(Message message){
		//TODO:
		/*
		 * Start new game and set local game variable in this interpreter to game instance
		 */
	}
}
