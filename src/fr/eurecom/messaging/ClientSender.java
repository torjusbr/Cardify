package fr.eurecom.messaging;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONObject;

import android.os.StrictMode;
import android.util.Log;

public class ClientSender {
	
	public static void send(String message, String host, int port) {
		Log.d("ClientSender", "send()");
		Log.d("ClientSender", "host: " + host);
		Log.d("ClientSender", "port: " + port);
		
		//Hack
	    if (android.os.Build.VERSION.SDK_INT > 9) {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy);
	    }
	    
		Socket socket = new Socket();
		
		try {
			socket.bind(null);
			socket.connect((new InetSocketAddress(host, port)), 500);

			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(message.getBytes());
			outputStream.close();
			Log.d("ClientSender", "sendt?");
		} catch (FileNotFoundException e) {
			// catch logic
			Log.d("ClientSender", "WTF? Feilmelding\n" + e.getStackTrace());
		} catch (IOException e) {
			// catch logic
			Log.d("ClientSender", "WTF? Feilmelding\n" + e.getStackTrace());
		} catch (Exception e) {
			Log.d("ClientSender", "WTF? Feilmelding\n" + e.getStackTrace());
			e.printStackTrace();
		}
		
		finally {
		    if (socket != null) {
		        if (socket.isConnected()) {
		            try {
		                socket.close();
		            } catch (IOException e) {
		                //catch logic
		            }
		        }
		    }
		}
	}
	
	public static void send(JSONObject json, String host, int port) {
		Log.d("ClientSender", "send()");
		Log.d("ClientSender", "host: " + host);
		Log.d("ClientSender", "port: " + port);
		
		//Hack
	    if (android.os.Build.VERSION.SDK_INT > 9) {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy);
	    }
	    
		Socket socket = new Socket();
		
		try {
			socket.bind(null);
			socket.connect((new InetSocketAddress(host, port)), 500);

			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(json.toString().getBytes());
			outputStream.close();
			Log.d("ClientSender", "sendt?");
		} catch (FileNotFoundException e) {
			// catch logic
			Log.d("ClientSender", "WTF? Feilmelding\n" + e.getStackTrace());
		} catch (IOException e) {
			// catch logic
			Log.d("ClientSender", "WTF? Feilmelding\n" + e.getStackTrace());
		} catch (Exception e) {
			Log.d("ClientSender", "WTF? Feilmelding\n" + e.getStackTrace());
			e.printStackTrace();
		}
		
		finally {
		    if (socket != null) {
		        if (socket.isConnected()) {
		            try {
		                socket.close();
		            } catch (IOException e) {
		                //catch logic
		            }
		        }
		    }
		}
	}
}
