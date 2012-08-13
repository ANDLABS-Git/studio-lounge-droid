package eu.andlabs.studiolounge;

public interface FragmentListner {

	
	public void onPlayerJoined(String player);
	
	public void onPlayerLeft(String player);
	
	public void onChatMessageRecieved(String msg);
	
	
}
