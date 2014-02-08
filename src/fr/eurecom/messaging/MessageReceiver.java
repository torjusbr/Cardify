package fr.eurecom.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MessageReceiver implements Runnable {
	private final Socket sender;
	private JSONObject json;
	private Client client;

	public MessageReceiver(Socket sender, Client client) throws IOException {
		this.sender = sender;
		this.client = client;
		Log.e("SocketHandler", "Received message, started new thread");
	}

	public void run() {
		try {
			Log.e("SocketHandler", "New thread running");
			BufferedReader in = new BufferedReader(new InputStreamReader(sender.getInputStream()));

			json = new JSONObject(in.readLine());

			Log.d("Tekst fra host", "Inputstreamen er: " + json.toString());

			receiveMessage(json, sender.getInetAddress());
			
			sender.close();
		} catch (Exception e) {
			
		}
	}
	
	private void receiveMessage(JSONObject json, InetAddress sender){
		Log.e("SocketHandler", "ReceiveMessage: " + json.toString());
		try {
			Action action = Action.values()[json.getInt("what")];
			String subject = json.getString("about");
			GameMessage gameMessage = new GameMessage(action, subject);
			gameMessage.setOriginatorAddr(sender);
			
			client.handleThreadMessage(gameMessage, Config.GAME_MESSAGE_INT);
		} catch (JSONException e){
			Log.e("ClientInterpreter:receiveMessage", e.getMessage());
		}
	}
}