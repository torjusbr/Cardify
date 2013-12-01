package fr.eurecom.messaging;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;
import fr.eurecom.cardify.Game;
import fr.eurecom.util.Card;

public class Client {

	private Game game;
	private Set<InetAddress> receivers;
	
	public Client(WifiP2pInfo info) {
		this.game = null;
		// Start listening to messages
		// TODO: WHAT HAPPENS IF I WANT TO STOP LISTENING?
		new Receiver(this).execute();
		receivers = new HashSet<InetAddress>();
	}
	
	public void addReceiver(InetAddress receiver){
		receivers.add(receiver);
		for (InetAddress r : receivers) {
			Log.e("Receiver", r.toString());
		}
	}
	
	public void registerAtHost(){
		sendMessage(Action.REGISTER, "");
	}
	
	public void publishTakeCardFromPublicZone(Card card){
		sendMessage(Action.REMOVED_CARD_FROM_PUBLIC_ZONE, card.toString());
	}
	
	public void publishPutCardInPublicZone(Card card){
		sendMessage(Action.ADDED_CARD_TO_PUBLIC_ZONE, card.toString());
	}
	
	
	private void sendMessage(Action action, String subject) {
		Message message = new Message(action, subject);
		for (InetAddress receiver : receivers){
			Sender.send(message, receiver);
		}
		//Sender.send(message, info.groupOwnerAddress);
	}
	
	
	public void handleMessage(Message message){
		if (this.game == null) {
			switch (message.getAction()){
			case GAME_STARTED:
				handleGameStarted(message);
				return;
			case REGISTER:
				handleRegister(message);
				return;
			default:
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
	
	private void handleRegister(Message message){
		addReceiver(message.getSender());
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
