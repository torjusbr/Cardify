package fr.eurecom.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

import android.util.Log;

public class NewReceiver implements Runnable {
	
	private boolean listening;
	private Client client;
	private ServerSocket socket;
	private JSONObject json;
	private Socket sender;
	private Thread runningThread;
	
	public NewReceiver(Client client) {
		Log.d("NewReceiver", "Constructor");
		this.listening = true;
		this.client = client;
		this.runningThread = null;
	}

	@Override
	public void run() {
		synchronized(this){
            this.runningThread = Thread.currentThread();
        }
		openSocket();
		
		while (isListening()) {
			try {
				sender = socket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(sender.getInputStream()));
				json = new JSONObject(in.readLine());
				sender.close();
			} catch (Exception e) {}
		}

	}
	
	private synchronized boolean isListening() {
		return this.listening;
	}
	
	public synchronized void stop(){
        this.listening = false;
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
	
	private void openSocket() {
		try  {
			socket = new ServerSocket(Config.PORT);
		} catch (Exception e) {
			throw new RuntimeException("Cannot open port 8080", e);
		}
	}

}
