package eu.andlabs.studiolounge;

public interface LobbyListner {

	
	public void onPlayerJoinedLobby(String playername);
	
	public void onPlayerLeftLobby(String playername);
}
