package fr.eurecom.cardify;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Debug;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.eurecom.messaging.Client;
import fr.eurecom.util.CardDeck;
import fr.eurecom.util.CardPlayerHand;
import fr.eurecom.util.CardSolitaireHand;
import fr.eurecom.util.CardSortingRule;

public class Game extends Activity {

	private CardDeck deck;
	private CardPlayerHand playerHand;
	private CardSolitaireHand solitaireHand;
	private Client client;
	private TextView messageStream;
	private WifiP2pDevice device;
	private ProgressDialog progressDialog;
	private int cardsPerPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		//Keeps screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		progressDialog = new ProgressDialog(this);
		
		if((Boolean) getIntent().getExtras().get("isSolitaire")) {
			initSolitaire();
		} else {
			String[] receiverAddresses = getIntent().getExtras().get("receivers").toString().split(",");
			Boolean isHost = getIntent().getExtras().getBoolean("isHost");
			
			if (!isHost) {
				showProgressDialog("Loading...", "Waiting for all players");
			} else {
				cardsPerPlayer = getIntent().getExtras().getInt("cardsPerPlayer");
				Log.e("Game", "cardsPerPlayer is: " + cardsPerPlayer);
			}
			
			messageStream = (TextView) findViewById(R.id.messageStream);
			messageStream.setMovementMethod(new ScrollingMovementMethod());
			// If client, we receive cards from host at a later stage
			deck = null;
			playerHand = new CardPlayerHand(this);
			
			// Set up client
			device = new WifiP2pDevice();
			Log.d("Game", "Device name is ");
			this.client = new Client(this);
			if (isHost) {
				client.changeToHost();
			} 
			
			for (String inetAddr : receiverAddresses){
				try {
					client.addReceiver(InetAddress.getByName(inetAddr.substring(1)));
				} catch (UnknownHostException e) {
					Log.e("Game:onCreate", e.getMessage());
				} catch (StringIndexOutOfBoundsException e) {
					Log.e("Game:onCreate", e.getMessage());
				}
			}

			if (client.isHost()){
				initGame();
			} else {
				Log.e("Game", "Notifying game is initialized");
				client.publishGameInitialized();
			}
		}
	}
	
	private void showProgressDialog(String title, String message) {
		this.progressDialog.setTitle(title);
		this.progressDialog.setMessage(message);
		this.progressDialog.setCancelable(false);
		this.progressDialog.show();
	}
	
	public void dismissProgressDialog() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	public Point getDisplaySize(){
		Display display = getWindowManager().getDefaultDisplay();
		Point displaySize = new Point();
		display.getSize(displaySize);
		return displaySize;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	// Set up game if the this.client is host
	private void initGame(){
		//int numPlayers = client.getReceivers().size() + 1;
		this.deck = new CardDeck(this);
		deck.setOwner(playerHand);
		deck.shuffle();
		
		playerHand.dealCards(deck.draw(cardsPerPlayer));
		
		addView(deck);
		
		client.pushInitialCards(deck, cardsPerPlayer);
		client.pushRemainingDeck(deck);
	}
	
	private void initSolitaire() {
		this.deck = new CardDeck(this);
		deck.shuffle();
		
		this.solitaireHand = new CardSolitaireHand(this);
		solitaireHand.dealCards(deck.draw(1));
		deck.setOwner(solitaireHand);
		
		addView(deck);
	}
	
	public void addView(View v){
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.rootGameLayout);
		layout.addView(v);
	}
	
	public void removeView(View v){
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.rootGameLayout);
		// Must recycle image resource!
		layout.removeView(v);
	}
	
	@Override
	public void onBackPressed() {
		//TODO: Special dialog for host?
		
		long t = Runtime.getRuntime().totalMemory() / (1024*1024);
		long f = Runtime.getRuntime().freeMemory() / (1024*1024);
		long l = Runtime.getRuntime().maxMemory() / (1024*1024);
		long n = Debug.getNativeHeapAllocatedSize() / (1024*1024);
		System.out.println(String.format("TOTAL: %d MB -- FREE: %d MB -- MAX: %d MB -- NATIVE: %d MB", t, f, l, n));
		
		new AlertDialog.Builder(this)
			.setTitle("Are you sure you want to exit?")
			.setMessage("This game will be abandoned")
			.setNegativeButton(android.R.string.no, null)
			.setPositiveButton(android.R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Game.super.onBackPressed();
					
				}
			}).create().show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.NO_SUIT_ACE_HIGH:
				playerHand.sortCards(CardSortingRule.NO_SUIT_ACE_HIGH);
				return true;
			case R.id.NO_SUIT_ACE_LOW:
				playerHand.sortCards(CardSortingRule.NO_SUIT_ACE_LOW);
				return true;
			case R.id.S_D_H_C_ACE_HIGH:
				playerHand.sortCards(CardSortingRule.S_H_D_C_ACE_HIGH);
				return true;
			case R.id.S_D_H_C_ACE_LOW:
				playerHand.sortCards(CardSortingRule.S_H_D_C_ACE_LOW);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
		
	public Client getClient() {
		return this.client;
	}
	
	public CardPlayerHand getPlayerHand() {
		return this.playerHand;
	}
	
	public void printMessage(String message) {
		messageStream.setText(message+messageStream.getText());
	}
	
	public CardDeck getDeck() {
		return deck;
	}
	
	public void setDeck(CardDeck deck) {
		this.deck = deck;
	}
}