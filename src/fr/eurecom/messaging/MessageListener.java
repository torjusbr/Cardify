package fr.eurecom.messaging;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

public class MessageListener extends Thread {

	private final ServerSocket socket;
	private final ExecutorService pool;
	private Handler handler;
	
	private volatile boolean running;
	
	public MessageListener(Handler handler) throws IOException {
		this.handler = handler;
		this.socket = new ServerSocket(Config.PORT);
		
		pool = Executors.newCachedThreadPool();
		this.running = true;
	}

	@Override
	public void run() {
		try {
			while (this.running && !isInterrupted()) {
				Socket tempSocket = socket.accept();
				pool.execute(new MessageReceiver(tempSocket, handler));
			}
			socket.close();
			getThreadGroup().interrupt();
			shutdownAndAwaitTermination(pool);
		} catch (IOException ex) {
			getThreadGroup().interrupt();
			shutdownAndAwaitTermination(pool);
		}
	}
	
	
	
	public void stopThread() {
		this.running = false;
		interrupt();
		try {
			this.socket.close();
			socket.close();
			
		} catch (IOException e) {
		} catch (NullPointerException e) {
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
