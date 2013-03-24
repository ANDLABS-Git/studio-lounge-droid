package eu.andlabs.studiolounge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import eu.andlabs.studiolounge.dao.GameMatch;
import eu.andlabs.studiolounge.dao.LobbyListElement;
import eu.andlabs.studiolounge.dao.LobbyListElement.ElementType;
import eu.andlabs.studiolounge.dao.Player;

public class DataManager {

	private String localPlayer;
	private List<LobbyListElement> lobbydata;

	public void openMatch(String matchID, String playername, String packageName) {

		for (LobbyListElement entry : lobbydata) { //L
			if (entry.getPgkName().equalsIgnoreCase(packageName)) {
				GameMatch newGame = new GameMatch();
				Player host = new Player();
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
						Player joiningPlayer = new Player();
						joiningPlayer.setDisplayName(playername);
						match.getPlayers().add(joiningPlayer);
						
						
						if(joiningPlayer.equals(localPlayer)){
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
}
