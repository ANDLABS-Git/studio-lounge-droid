package eu.andlabs.studiolounge.dao;

public class Player {

	private String guid;
	private String displayName;
	private String playerStatus;
	private boolean isOnTurn;
	private int avatar;
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPlayerStatus() {
		return playerStatus;
	}
	public void setPlayerStatus(String playerStatus) {
		this.playerStatus = playerStatus;
	}
	public boolean isOnTurn() {
		return isOnTurn;
	}
	public void setOnTurn(boolean isOnTurn) {
		this.isOnTurn = isOnTurn;
	}

	
}
