package fr.eurecom.messaging;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
			while (this.running && !Thread.currentThread().isInterrupted()) {
				Socket tempSocket = socket.accept();
				if (!Thread.currentThread().isInterrupted()) {
					pool.execute(new MessageReceiver(tempSocket, handler));
				}
			}
			socket.close();
			getThreadGroup().interrupt();
			shutdownThreads(pool);
		} catch (IOException ex) {
			getThreadGroup().interrupt();
			shutdownThreads(pool);
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
	

	private void shutdownThreads(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		pool.shutdownNow(); // Cancel currently executing tasks
	}
}
