package fr.eurecom.messaging;

import java.net.InetAddress;

public class Message {
	
	private Action action;
	private String subject;
	private InetAddress sender;
	
	public Message(Action action, String subject) {
		this.action = action;
		this.subject = subject;
		this.sender = null;
	}
	
	public Action getAction() {
		return action;
	}

	public String getSubject() {
		return subject;
	}
	
	public void setSender(InetAddress sender) {
		this.sender = sender;
	}
	
	public InetAddress getSender(){
		return this.sender;
	}
	
}
