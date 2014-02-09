package fr.eurecom.messaging;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

public class Sender extends AsyncTask<Object, Void, Void> {
	
	public void send(GameMessage message, InetAddress receiver) {
	    if (android.os.Build.VERSION.SDK_INT > 9) {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy);
	    }

	    String messageToSend = serialize(message);
	    
	    if (messageToSend.length() == 0) return;
		Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect((new InetSocketAddress(receiver, Config.PORT)), Config.TIMEOUT_INTERVAL);

			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(messageToSend.getBytes());
			outputStream.close();
			
		} catch (IOException e) {
			Log.e("Sender:send", e.getMessage());
		} 
		
		finally {
		    if (socket != null) {
		        if (socket.isConnected()) {
		            try {
		                socket.close();
		            } catch (IOException e) {
		               Log.e("Sender", e.getMessage());
		            }
		        }
		    }
		}
	}
	
	private String serialize(GameMessage message) {
		JSONObject json = new JSONObject();
		try {
			json.put("what", message.what.ordinal());
			json.put("about", message.about);
			String name;
			if (message.getOriginatorName() == null || message.getOriginatorName().length() == 0) name = "0";
			else name = message.getOriginatorName();
			json.put("name", name);
		} catch (JSONException e) {
			Log.e("Sender", "JSONError");
			e.printStackTrace();
		}
		return json.toString();
	}

	@Override
	protected Void doInBackground(Object... objects) {
		// Objects[0] is gameMessage, objects[1] is destination address
		GameMessage message = null;
		InetAddress receiver = null;
		message = (GameMessage) objects[0];
		Log.e("Sender", "Message");
		receiver = (InetAddress) objects[1];
		Log.e("Sender", "Receiver");
		if (message != null && receiver != null) {
			send(message, receiver);
		}
		return null;
	}
	
}
