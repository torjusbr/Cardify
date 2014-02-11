package fr.eurecom.messaging;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import fr.eurecom.cardify.Game;
import fr.eurecom.cardify.Lobby;
import fr.eurecom.util.Card;
import fr.eurecom.util.CardDeck;

public class Client implements Handler.Callback {

	private Activity activity;
	private Set<InetAddress> receivers;
	private boolean isHost;
	private int peersInitialized;
	private MessageListener receivingThread;
	private Handler handler;
	private final ExecutorService pool;
	
	public Client(Activity activity) {
		this.activity = activity;
		handler = new Handler(this);
		try {
			receivingThread = new MessageListener(handler);
			receivingThread.start();
		} catch (IOException e) {
		}

		pool = Executors.newCachedThreadPool();
		
		receivers = new HashSet<InetAddress>();
		this.isHost = false;
		this.peersInitialized = 0;
	}
	
	public void handleThreadMessage(GameMessage gameMessage, int what) {
		Message completeMessage = handler.obtainMessage(what, gameMessage);
		completeMessage.sendToTarget();
	}
	
	public void disconnect(){
		receivingThread.stopThread();
	}
	
	
	public void changeToHost(){
		this.isHost = true;
	}
	
	public boolean isHost(){
		return this.isHost;
	}
	
	public void addReceiver(InetAddress receiver) {
		receivers.add(receiver);
	}
	
	public void registerAtHost(String deviceList) {
		sendMessage(Action.REGISTER, deviceList);
	}
	
	public Set<InetAddress> getReceivers(){
		return receivers;
	}
	
	public void broadcastStartGame() {
		sendMessage(Action.GAME_STARTED, "");
	}
	
	public void publishDrawFromDeckToStack(Card card, String deviceName) {
		sendMessage(Action.DREW_FROM_DECK_TO_STACK, card.toString(), deviceName);
	}
	
	public void publishTakeCardFromPublicZone(Card card, String deviceName) {
		sendMessage(Action.REMOVED_CARD_FROM_PUBLIC_ZONE, card.toString(), deviceName);
	}

	public void publishPutCardInPublicZone(Card card, float x, float y, String deviceName) {
		sendMessage(Action.ADDED_CARD_TO_PUBLIC_ZONE, card.toStringWithPosition(x, y), deviceName);
	}
	
	public void publishCardPositionUpdate(Card card, float x, float y, String deviceName) {
		sendMessage(Action.MOVED_CARD_IN_PUBLIC_ZONE, card.toStringWithPosition(x, y), deviceName);
	}
	
	public void publishTurnCardInPublicZone(Card card, String deviceName) {
		sendMessage(Action.TURNED_CARD_IN_PUBLIC_ZONE, card.toString(), deviceName);
	}
	
	public void publishAddCardToDeck(Card card, String deviceName) {
		sendMessage(Action.ADDED_CARD_TO_DECK, card.toString(), deviceName);
	}
	
	public void publishDisconnect() {
		sendMessageFromUIThread(Action.DISCONNECT, "");
	}
	
	public void pushInitialCards(CardDeck deck, int n) {
		for (InetAddress receiver : receivers){
			String cards = "";
			for (int i = 0; i < n; i++){
				if (deck.peek() == null) break;
				cards += deck.pop().toString() + ";";
			}
			GameMessage message = new GameMessage(Action.INITIAL_CARDS, cards);
			
			sendMessage(message, receiver);
		} 
	}
	
	public void pushNewCards(CardDeck deck, int n) {
		for (InetAddress receiver : receivers){
			String cards = "";
			for (int i = 0; i < n; i++){
				if (deck.peek() == null) break;
				cards += deck.pop().toString() + ";";
			}
			GameMessage message = new GameMessage(Action.NEW_CARDS, cards);
			
			sendMessage(message, receiver);
		}
		this.pushRemainingDeck(deck);
	}
	
	public void publishGameInitialized() {
		sendMessage(Action.GAME_INITIALIZED, "");
	}
	
	public void pushRemainingDeck(CardDeck deck) {
		StringBuilder cardStringBuilder = new StringBuilder();
		List<Card> cards = deck.getCards();
		for (int i = 0; i < cards.size(); i++) {
			cardStringBuilder.append(cards.get(i).toString());
			cardStringBuilder.append(";");
		}
		for (InetAddress receiver : receivers) {
			GameMessage message = new GameMessage(Action.REMAINING_DECK, cardStringBuilder.toString());
			sendMessage(message, receiver);
		}
	}
	
	public void pushShuffledDeck(CardDeck deck) {
		StringBuilder cardStringBuilder = new StringBuilder();
		List<Card> cards = deck.getCards();
		for (int i = 0; i < cards.size(); i++) {
			cardStringBuilder.append(cards.get(i).toString());
			cardStringBuilder.append(";");
		}
		for (InetAddress receiver : receivers) {
			GameMessage message = new GameMessage(Action.SHUFFLE_DECK,cardStringBuilder.toString());
			sendMessage(message, receiver);
		}
	}
	
	private void pushDeviceName(String name, InetAddress target) {
		sendSingleMessage(Action.DEVICE_NAME, name, target);
	}
	
	
	
	private void broadcastChange(GameMessage message){
		for (InetAddress receiver : receivers){
			if (!receiver.equals(message.getOriginatorAddr())){
				sendMessage(message, receiver);
			}
		}
	}
	
	public void handleGameMessage(GameMessage message) {
		if(message.what == Action.GAME_INITIALIZED) {
			handleGameInitialized(message);
		} else if (this.activity instanceof Lobby) {
			handlePreGameMessage(message);
		} else if (this.activity instanceof Game){
			handleInGameMessage(message);
		}
	}
	
	private void handlePreGameMessage(GameMessage message){
		switch (message.what){
		case GAME_STARTED:
			handleGameStarted(message);
			return;
		case REGISTER:
			handleRegister(message);
			return;
		case DISCONNECT:
			handlePreGameDisconnect(message);
			return;
		case DEVICE_NAME:
			handleDeviceName(message);
			return;
		default:
			return;
		}
	}
	
	private void handleRegister(GameMessage message) {
		pushDeviceName(((Lobby) activity).getCurrentTargetDeviceName(), message.getOriginatorAddr());
		((Lobby) activity).setHostDeviceName(message.about.split(";"));
		
		addReceiver(message.getOriginatorAddr());
		((Lobby) activity).dismissProgressDialog();
	}
	
	private void handleDeviceName(GameMessage message) {
		((Lobby) activity).setThisDeviceName(message.about);
	}
	
	private void handleGameStarted(GameMessage message) {
		// Start new game and set local game variable in this interpreter to game instance
		((Lobby) activity).startGameActivity(receivers);
	}
	
	private void handleGameInitialized(GameMessage message) {
		if (isHost) {
			peersInitialized++;
			if (peersInitialized == receivers.size()) {
				((Lobby) activity).startGameActivity(receivers, true);				
			}
		} 
	}
	
	public void broadcastGameInitialized() {
		sendMessage(Action.GAME_INITIALIZED, "");
	}
	
	private void handleInGameMessage(GameMessage message){
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
		case MOVED_CARD_IN_PUBLIC_ZONE:
			handleMovedCardInPublicZone(message);
			return;
		case ADDED_CARD_TO_DECK:
			handleAddCardToDeck(message);
			return;
		case DREW_FROM_DECK_TO_STACK:
			handleDrewFromDeckToStack(message);
			return;
		case ILLEGAL_ACTION:
			return;
		case INITIAL_CARDS:
			handleInitialCards(message);
			return;
		case NEW_CARDS:
			handleNewCards(message);
			return;
		case SHUFFLE_DECK:
			handleShuffleDeck(message);
			return;
		case REMAINING_DECK:
			handleRemainingDeck(message);
			return;
		case DISCONNECT:
			handleInGameDisconnect(message);
			return;
		default:
			return;
		}
	}
	
	private void handleInGameDisconnect(GameMessage message) {
		((Game) activity).exitGame();
	}
	
	private void handlePreGameDisconnect(GameMessage message) {
		if (isHost) {
			receivers.remove(message.getOriginatorAddr());
			if (receivers.size() > 0) {
				return;
			} 
		}
		((Lobby) activity).resetLobby();
	}

	private void handleDrewFromDeckToStack(GameMessage message) {
		((Game) activity).getPlayerHand().blindDrawFromDeckToStack(message.getOriginatorName());
		if (isHost) {
			broadcastChange(message);
		}
	}
	

	private void handleAddedCardToPublicZone(GameMessage message) {
		// 0s10@0.231,0.423 --> 0s10 and 0.231,0.423
		String[] token = message.about.split("@");
		
		boolean turned = token[0].charAt(0) == '1' ? true : false;
		char suit = token[0].charAt(1);
		int face = Integer.parseInt(token[0].substring(2));
		
		String[] location = token[1].split(",");
		float x = Float.parseFloat(location[0]);
		float y = Float.parseFloat(location[1]);
		
		((Game) activity).getPlayerHand().blindAddToPublic(suit, face, turned, x, y, message.getOriginatorName());
		if (isHost) { 
			broadcastChange(message);
		}
	}
	
	private void handleRemovedCardFromPublicZone(GameMessage message) {
		char suit = message.about.charAt(1);
		int face = Integer.parseInt(message.about.substring(2));
		((Game) activity).getPlayerHand().blindRemoveFromPublic(suit, face, message.getOriginatorName());
		if (isHost) {
			broadcastChange(message);
		}
	}
	
	private void handleShuffleDeck(GameMessage message) {
		String[] cards = message.about.length() == 0 ? null : message.about.split(";");
		((Game) activity).getPlayerHand().blindShuffleDeck(cards);
	}
	
	private void handleTurnedCardInPublicZone(GameMessage message) {
		char suit = message.about.charAt(1);
		int face = Integer.parseInt(message.about.substring(2));
		((Game) activity).getPlayerHand().blindTurnInPublic(suit, face, message.getOriginatorName());
		if (isHost) {
			broadcastChange(message);
		}
	}
	
	private void handleMovedCardInPublicZone(GameMessage message) {
		// 0s10@0.231,0.423 --> 0s10 and 0.231,0.423
		String[] token = message.about.split("@");

		char suit = token[0].charAt(1);
		int face = Integer.parseInt(token[0].substring(2));
		
		String[] location = token[1].split(",");
		float x = Float.parseFloat(location[0]);
		float y = Float.parseFloat(location[1]);
		
		((Game) activity).getPlayerHand().blindMoveInPublic(suit, face, x, y);
		if (isHost) { 
			broadcastChange(message);
		}
	}
	
	private void handleInitialCards(GameMessage message) {
		if (message.about.length() > 0) {
			String[] cards = message.about.split(";");
			((Game) activity).getPlayerHand().blindDealCards(cards);
		}
	}
	
	private void handleNewCards(GameMessage message) {
		if (message.about.length() > 0) {
			String[] cards = message.about.split(";");
			((Game) activity).getPlayerHand().blindNewCards(cards);
		}
	}
	
	private void handleRemainingDeck(GameMessage message) {
		String[] cards = message.about.length() == 0 ? null : message.about.split(";");
		((Game) activity).getPlayerHand().blindAddDeck(cards);
		((Game) activity).dismissProgressDialog();
	}
	
	private void handleAddCardToDeck(GameMessage message) {
		char suit = message.about.charAt(1);
		int face = Integer.parseInt(message.about.substring(2));
		boolean turned = message.about.charAt(0) == '1' ? true : false;
		((Game) activity).getPlayerHand().blindAddToDeck(suit, face, turned, message.getOriginatorName());
		if (isHost) { 
			broadcastChange(message);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == Config.GAME_MESSAGE_INT) {
			GameMessage gameMessage = (GameMessage) msg.obj;
			handleGameMessage(gameMessage);
			return true;
		}
		return false;
	}
	
	
	private void sendMessage(GameMessage message, InetAddress receiver) {
		pool.execute(new Sender(message, receiver));
	}
	
	private void sendMessageFromUIThread(Action what, String about) {
		GameMessage message = new GameMessage(what, about);
		for (InetAddress receiver : receivers){
			sendMessage(message, receiver);
		}
	}
	
	private void sendMessage(Action what, String about, String deviceName) {
		GameMessage message = new GameMessage(what, about, deviceName);
		for (InetAddress receiver : receivers){
			sendMessage(message, receiver);
		}
	}
	private void sendSingleMessage(Action what, String about, InetAddress receiver) {
		GameMessage message = new GameMessage(what, about);
		sendMessage(message, receiver);
	}

	private void sendMessage(Action what, String about) {
		GameMessage message = new GameMessage(what, about);
		for (InetAddress receiver : receivers){
			sendMessage(message, receiver);
		}
	}
}
