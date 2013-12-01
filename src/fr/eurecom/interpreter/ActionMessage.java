package fr.eurecom.interpreter;

public class ActionMessage {
	
	private String sender;
	private Action action;
	private String subject;
	
	public ActionMessage(String sender, Action action, String subject) {
		this.sender = sender;
		this.action = action;
		this.subject = subject;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	
}
