package eu.andlabs.studiolounge.dao;

import java.util.List;

public class GameMatch {

	
	private String matchId;
	private List<Player> players;
	private int maxPlayers;
	private boolean localPlayerOnTurn;
	private boolean running;

	
	
	public String getMatchId() {
		return matchId;
	}
	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}
	public List<Player> getPlayers() {
		return players;
	}
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	public int getMaxPlayers() {
		return maxPlayers;
	}
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
	public boolean isLocalPlayerOnTurn() {
		return localPlayerOnTurn;
	}
	public void setLocalPlayerOnTurn(boolean localPlayerOnTurn) {
		this.localPlayerOnTurn = localPlayerOnTurn;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	
	
}
