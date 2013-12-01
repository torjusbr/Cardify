package fr.eurecom.messaging;

public class Message {
	
	private Action action;
	private String subject;
	
	public Message(Action action, String subject) {
		this.action = action;
		this.subject = subject;
	}
	
	public Action getAction() {
		return action;
	}

	public String getSubject() {
		return subject;
	}
	
}
