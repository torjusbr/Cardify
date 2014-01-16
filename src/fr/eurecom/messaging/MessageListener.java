package fr.eurecom.messaging;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.util.Log;

public class MessageListener extends Thread {

	private final ServerSocket socket;
	private final ExecutorService pool;
	private Handler handler;
	
	private volatile boolean running;
	
	public MessageListener(Handler handler)
			throws IOException {
		Log.d("NewReceiver", "Constructor");
		this.handler = handler;
		this.socket = new ServerSocket(Config.PORT);
		
		pool = Executors.newFixedThreadPool(2);
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
				pool.execute(new MessageReceiver(tempSocket, handler));
			}
			Log.e("MessageListener", "Thread stopping listening");
			socket.close();
			getThreadGroup().interrupt();
			shutdownAndAwaitTermination(pool);
		} catch (IOException ex) {
			Log.e("MessageListener", "Thread stopping listening");
			getThreadGroup().interrupt();
			shutdownAndAwaitTermination(pool);
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
			if (!pool.awaitTermination(50, TimeUnit.MILLISECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(50, TimeUnit.MILLISECONDS))
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
