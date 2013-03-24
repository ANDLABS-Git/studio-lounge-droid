package eu.andlabs.studiolounge.dao;

import android.os.Bundle;

public class MatchMove {

	
	private Bundle customMsg;
	private String sendingPlayer;
	private String nextPlayer;
	public Bundle getCustomMsg() {
		return customMsg;
	}
	public void setCustomMsg(Bundle customMsg) {
		this.customMsg = customMsg;
	}
	public String getSendingPlayer() {
		return sendingPlayer;
	}
	public void setSendingPlayer(String sendingPlayer) {
		this.sendingPlayer = sendingPlayer;
	}
	public String getNextPlayer() {
		return nextPlayer;
	}
	public void setNextPlayer(String nextPlayer) {
		this.nextPlayer = nextPlayer;
	}
	
	
	
}
