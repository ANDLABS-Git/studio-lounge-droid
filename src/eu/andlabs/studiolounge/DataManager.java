package eu.andlabs.studiolounge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import eu.andlabs.studiolounge.dao.GameMatch;
import eu.andlabs.studiolounge.dao.LobbyListElement;
import eu.andlabs.studiolounge.dao.LobbyListElement.ElementType;
import eu.andlabs.studiolounge.dao.MatchMove;
import eu.andlabs.studiolounge.dao.Player;

public class DataManager {

	private String localPlayer;
	private List<LobbyListElement> lobbydata;
	private Map<String, MatchMove> matchMoves;
	private Map<String, Player> loggedInPlayers;
	
	
	public DataManager(){
		lobbydata=new ArrayList<LobbyListElement>();
		loggedInPlayers= new HashMap<String, Player>();
		matchMoves=new HashMap<String, MatchMove>();
	}

	public void loginPlayer(String displayName, String playerId, int avatar){
		Player newPlayer= new Player();
		newPlayer.setDisplayName(displayName);
		newPlayer.setGuid(playerId);
		newPlayer.setGuid(playerId);
		loggedInPlayers.put(playerId,newPlayer);
	}
	
	public void updatePlayerStatus(String playerId, String pkgName, String matchId){
		Player player = loggedInPlayers.get(playerId);
		player.setPlayerStatus(pkgName);
	}
	
	public void openMatch(String matchID, String playername, String packageName) {

		for (LobbyListElement entry : lobbydata) { // L
			if (entry.getPgkName().equalsIgnoreCase(packageName)) {
				GameMatch newGame = new GameMatch();
				Player host = loggedInPlayers.get(playername);
				host.setDisplayName(playername);
				newGame.getPlayers().add(host);
				newGame.setMatchId(matchID);
				entry.getGameMatches().add(newGame);
				return;
			}

		}
		Log.i("Datamanger", "openMatch(): packageName not found");
		LobbyListElement newEntry = new LobbyListElement();
		newEntry.setPgkName(packageName);

		if (playername.equalsIgnoreCase(localPlayer)) {
			newEntry.setType(ElementType.JOINED_GAME);
		} else {
			newEntry.setType(ElementType.OPEN_GAME);
		}

		GameMatch newGame = new GameMatch();
		Player host = new Player();
		host.setDisplayName(playername);
		newGame.getPlayers().add(host);
		newGame.setMatchId(matchID);
		newEntry.getGameMatches().add(newGame);

		Collections.sort(lobbydata, new LobbyComperator());

	}

	public void joinGame(String matchID, String playername, String packageName) {
		for (LobbyListElement entry : lobbydata) {
			if (entry.getPgkName().equalsIgnoreCase(packageName)) {
				for (GameMatch match : entry.getGameMatches()) {
					if (match.getMatchId().equalsIgnoreCase(matchID)) {
						Player joiningPlayer = loggedInPlayers.get(playername);
						joiningPlayer.setDisplayName(playername);
						match.getPlayers().add(joiningPlayer);

						if (joiningPlayer.equals(localPlayer)) {
							entry.setType(ElementType.JOINED_GAME);
							Collections.sort(lobbydata, new LobbyComperator());
						}
						return;
					}

				}
				Log.i("Datamanger", "joinMatch(): MatchId not found");
			}

		}
		Log.i("Datamanger", "joinMatch(): packageName not found");
	}

	public void updateMatchStatus(String matchID, String pkgName,
			String matchStatus) {
		for (LobbyListElement entry : lobbydata) {
			if (entry.getPgkName().equalsIgnoreCase(pkgName)) {
				for (GameMatch match : entry.getGameMatches()) {
					if (match.getMatchId().equalsIgnoreCase(matchID)) {
						if (matchStatus == "closed") {
							entry.getGameMatches().remove(match);
						}
						return;
					}

				}
				Log.i("Datamanger", "joinMatch(): MatchId not found");
			}

		}
		Log.i("Datamanger", "joinMatch(): packageName not found");
	}

	public void insertMatchMove(String matchID, String sendingPlayer,
			String nextPlayer, Bundle customMsg) {

		MatchMove move = new MatchMove();
		move.setCustomMsg(customMsg);
		move.setSendingPlayer(sendingPlayer);
		move.setNextPlayer(nextPlayer);

		matchMoves.put(matchID, move);
	}

	public MatchMove getMatchMove(String matchId) {
		return matchMoves.get(matchId);
	}
}
