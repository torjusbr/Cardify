package fr.eurecom.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Message;

public class MessageReceiver implements Runnable {
	private final Socket sender;
	private JSONObject json;
	private Handler handler;

	public MessageReceiver(Socket sender, Handler handler) throws IOException {
		this.sender = sender;
		this.handler = handler;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(sender.getInputStream()));

			json = new JSONObject(in.readLine());

			receiveMessage(json, sender.getInetAddress());
			
			sender.close();
		} catch (Exception e) {
			
		}
	}
	
	private void receiveMessage(JSONObject json, InetAddress sender){
		try {
			Action action = Action.values()[json.getInt("what")];
			String subject = json.getString("about");
			String name = json.getString("name");
			GameMessage gameMessage = new GameMessage(action, subject, name);
			gameMessage.setOriginatorAddr(sender);
			
			handleThreadMessage(gameMessage, Config.GAME_MESSAGE_INT);
		} catch (JSONException e){
		}
	}
	
	public void handleThreadMessage(GameMessage gameMessage, int what) {
		Message completeMessage = handler.obtainMessage(what, gameMessage);
		completeMessage.sendToTarget();
	}
}