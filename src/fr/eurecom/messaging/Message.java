package fr.eurecom.messaging;

import java.net.InetAddress;

public class Message {
	
	public final Action what;
	public final String about;
	private InetAddress originatorAddr;
	private String originatorName; 
	
	public Message(Action what, String about) {
		this.what = what;
		this.about = about;
		this.originatorAddr = null;
		this.originatorName = null;
	}
	
	public Message(Action what, String about, String originatorName){
		this(what, about);
		this.originatorName = originatorName;
	}
	
	public void setOriginatorAddr(InetAddress originator) {
		this.originatorAddr = originator;
	}
	
	public InetAddress getOriginatorAddr(){
		return this.originatorAddr;
	}
	
	public void setOriginatorName(String originator) {
		this.originatorName = originator;
	}
	
	public String getOriginatorName(){
		return this.originatorName;
	}
	
}
