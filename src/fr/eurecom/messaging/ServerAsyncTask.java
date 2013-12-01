package fr.eurecom.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ServerAsyncTask extends AsyncTask<String, Void, String> {
	private static final String TAG = "FileServerAsyncTask";
	private Context context;
	private TextView statusText;
	private boolean listening;
	private Object callback;
	
	
	public ServerAsyncTask(Context context, View statusText) {
		super();
		this.context = context;
		this.statusText = (TextView) statusText;
		listening = true;
	}

	@Override
	protected String doInBackground(String... params) {
		Log.d(ServerAsyncTask.TAG, "doInBackground()!");
		try {
			
			/**
			 * Create a server socket and wait for client connections. This call
			 * blocks until a connection is accepted from a client
			 */
			ServerSocket serverSocket = new ServerSocket(6969);
			Log.d(ServerAsyncTask.TAG, "Server: Socket opened!!");
			while (listening) {
				Socket client = serverSocket.accept();
				Log.d(ServerAsyncTask.TAG, "Server: connection done with client!!");
				Log.d(ServerAsyncTask.TAG, "Client IP address: " + client.getInetAddress());
	//			InputStream inputstream = client.getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
	//			InputStreamReader isr = new InputStreamReader(inputstream); 
				Log.d("Text fra klient", "Inputstreamen er: " + in.readLine());
				
			}
		} catch (IOException e) {
			
			return null;
		} catch (Exception e) {
			Log.d(ServerAsyncTask.TAG, "An error occured");
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
//		statusText.append(result);
	}
}
