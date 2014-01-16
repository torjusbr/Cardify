package fr.eurecom.messaging;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.util.Log;

public class MessageListener extends Thread {

	private final ServerSocket socket;
	private final ExecutorService pool;
	private Client client;
	
	private volatile boolean running;
	
	

	public MessageListener(Client client)
			throws IOException {
		Log.d("NewReceiver", "Constructor");
		this.client = client;
		this.socket = new ServerSocket(Config.PORT);
		
		//Kanskje bruke newFixedThreadPool i stedet
		pool = Executors.newCachedThreadPool();
		Log.d("NewReceiver", "Listening Thread started");
		this.running = true;
	}

	@Override
	public void run() {
		try {
			Log.e("MessageListener", "Thread running");
			while (this.running && !isInterrupted()) {
				Socket tempSocket = socket.accept();
				Log.d("NewReceiver", "running ? " + this.running);
				pool.execute(new MessageReceiver(tempSocket, client));
			}
			Log.e("MessageListener", "Thread stopping listening");
			socket.close();
			getThreadGroup().interrupt();
			shutdownAndAwaitTermination(pool);
		} catch (IOException ex) {
			pool.shutdown();
		}
	}
	
	public void stopThread() {
		this.running = false;
		try {
			this.socket.close();
		} catch (IOException e) {
			Log.e("MessageListener:stopListening", e.getMessage());
		}
	}
	

	private void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(5, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
}
