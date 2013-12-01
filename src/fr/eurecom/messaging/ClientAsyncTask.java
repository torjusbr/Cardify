package fr.eurecom.messaging;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ClientAsyncTask extends AsyncTask<String, Void, JSONObject> {
	private static final String TAG = "ClientAsyncTask";
	private Context context;
	private boolean listening;

	public ClientAsyncTask(Context context) {
		this.context = context;
		Log.d("ClientAsyncTask", "Constructor");
		listening = true;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		try {
			ServerSocket clientSocket = new ServerSocket(6969);
			Log.d(ClientAsyncTask.TAG, "Client: Socket opened!!");
			while (listening) {
				Socket host = clientSocket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(host.getInputStream()));
				
				JSONObject json = new JSONObject(in.readLine());
				
				Log.d("Tekst fra host", "Inputstreamen er: " + in.readLine());
				
			}
			
		} catch (IOException e) {
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
}
