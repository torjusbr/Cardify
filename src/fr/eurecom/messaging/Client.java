package fr.eurecom.messaging;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.util.Log;
import fr.eurecom.cardify.Game;
import fr.eurecom.cardify.Lobby;
import fr.eurecom.util.Card;
import fr.eurecom.util.CardDeck;

public class Client {

	private Activity activity;
	private Set<InetAddress> receivers;
	private Receiver receiver;
	private Sender sender;
	private boolean isHost;
	
	public Client(Activity activity) {
		this.activity = activity;
		this.receiver = new Receiver(this);
		receiver.execute();
		//receiver.run();
		receivers = new HashSet<InetAddress>();
		this.sender = new Sender();
		this.isHost = false;
	}
	
	public void disconnect(){
		receiver.stopListening();
	}
	
	public void changeToHost(){
		this.isHost = true;
	}
	
	public boolean isHost(){
		return this.isHost;
	}
	
	public void addReceiver(InetAddress receiver) {
		receivers.add(receiver);
		for (InetAddress r : receivers) {
			Log.e("Receiver", r.toString());
		}
	}
	
	public void registerAtHost() {
		sendMessage(Action.REGISTER, "");
	}
	
	public Set<InetAddress> getReceivers(){
		return receivers;
	}
	
	public void broadcastStartGame() {
		sendMessage(Action.GAME_STARTED, "");
	}
	
	public void publishTakeCardFromPublicZone(Card card) {
		sendMessage(Action.REMOVED_CARD_FROM_PUBLIC_ZONE, card.toString());
	}
	
	public void publishPutCardInPublicZone(Card card) {
		sendMessage(Action.ADDED_CARD_TO_PUBLIC_ZONE, card.toString());
	}
	
	public void publishTurnCardInPublicZone(Card card) {
		sendMessage(Action.TURNED_CARD_IN_PUBLIC_ZONE, card.toString());
	}
	
	public void pushInitialCards(CardDeck deck, int n) {
		for (InetAddress receiver : receivers){
			String cards = "";
			for (int i = 0; i < n; i++){
				if (deck.peak() == null) break;
				cards += deck.pop().toString() + ";";
			}
			Message message = new Message(Action.INITIAL_CARDS, cards);
			this.sender.send(message, receiver);
		}
	}
	
	
	private void sendMessage(Action what, String about) {
		Message message = new Message(what, about);
		for (InetAddress receiver : receivers){
			this.sender.send(message, receiver);
		}
	}
	
	private void broadcastChange(Message message){
		for (InetAddress receiver : receivers){
			if (!receiver.equals(message.getOriginatorAddr())){
				this.sender.send(message, receiver);
			}
		}
	}
	
	
	public void handleMessage(Message message) {
		if (this.activity instanceof Lobby) {
			handlePreGameMessage(message);
		} else if (this.activity instanceof Game){
			handleInGameMessage(message);
		}
	}
	
	private void handlePreGameMessage(Message message){
		switch (message.what){
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
	
	private void handleRegister(Message message) {
		addReceiver(message.getOriginatorAddr());
		((Lobby) activity).dismissProgressDialog();
	}
	
	private void handleGameStarted(Message message) {
		//TODO:
		/*
		 * Start new game and set local game variable in this interpreter to game instance
		 */
		((Lobby) activity).startGameActivity(receivers);
	}
	
	private void handleInGameMessage(Message message){
		switch (message.what) {
		case ADDED_CARD_TO_PUBLIC_ZONE:
			handleAddedCardToPublicZone(message);
			return;
		case REMOVED_CARD_FROM_PUBLIC_ZONE:
			handleRemovedCardFromPublicZone(message);
			return;
		case TURNED_CARD_IN_PUBLIC_ZONE:
			handleTurnedCardInPublicZone(message);
			return;
		case ILLEGAL_ACTION:
			return;
		case INITIAL_CARDS:
			handleInitialCards(message);
			return;
		default:
			return;
		}
	}
	
	private void handleAddedCardToPublicZone(Message message) {
		Log.e("Client:handleAddedCardToPublicZone", "RUN: " + message.about);
		boolean turned = message.about.charAt(0) == '1' ? true : false;
		char suit = message.about.charAt(1);
		int face = Integer.parseInt(message.about.substring(2));
		((Game) activity).getPlayerHand().blindAddToPublic(suit, face, turned);
		if (isHost) { 
			broadcastChange(message);
		}
	}
	
	private void handleRemovedCardFromPublicZone(Message message) {
		Log.e("Client:handleAddedCardToPublicZone", "RUN: " + message.about);
		char suit = message.about.charAt(1);
		int face = Integer.parseInt(message.about.substring(2));
		((Game) activity).getPlayerHand().blindRemoveFromPublic(suit, face);
		if (isHost) {
			broadcastChange(message);
		}
	}
	
	private void handleTurnedCardInPublicZone(Message message) {
		Log.e("Client:handleAddedCardToPublicZone", "RUN: " + message.about);
		char suit = message.about.charAt(1);
		int face = Integer.parseInt(message.about.substring(2));
		((Game) activity).getPlayerHand().blindTurnInPublic(suit, face);
		if (isHost) {
			broadcastChange(message);
		}
	}
	
	private void handleInitialCards(Message message) {
		Log.e("Client:handleInitialCards", "Cards: " + message.about);
		String[] cards = message.about.split(";");
		((Game) activity).getPlayerHand().blindDealCards(cards);
	}
}
