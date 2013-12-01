package fr.eurecom.messaging;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ClientAsyncTask extends AsyncTask<String, Void, JSONObject> {

	private Context context;
	private String host;
	private final int port = 8888;

	public ClientAsyncTask(Context context, String host) {
		this.context = context;
		this.host = host;
		Log.d("ClientAsyncTask", "Constructor");
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		Log.d("ClientAsyncTask", "doInBackground");
		Socket socket = new Socket();
		byte buf[] = new byte[1024];

		try {
			/**
			 * Create a client socket with the host, port, and timeout
			 * information.
			 */
			socket.bind(null);
			socket.connect((new InetSocketAddress(host, port)), 500);

			/**
			 * Create a byte stream from a JPEG file and pipe it to the output
			 * stream of the socket. This data will be retrieved by the server
			 * device.
			 */
			OutputStream outputStream = socket.getOutputStream();
			
			outputStream.write("Denne teksten er sendt fra klient til server".getBytes());
			outputStream.close();
		} catch (FileNotFoundException e) {
			// catch logic
		} catch (IOException e) {
			// catch logic
		}

		/**
		 * Clean up any open sockets when done transferring or if an exception
		 * occurred.
		 */
		finally {
			if (socket != null) {
				if (socket.isConnected()) {
					try {
						socket.close();
					} catch (IOException e) {
						// catch logic
					}
				}
			}

			return null;
		}
	}
	
}
