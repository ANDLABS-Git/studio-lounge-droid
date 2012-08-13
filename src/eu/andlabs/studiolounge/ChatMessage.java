package eu.andlabs.studiolounge;

public class ChatMessage {

	private String sender;
	private String message;
	
	
	public ChatMessage(String sender, String message) {
		super();
		this.sender = sender;
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
}
