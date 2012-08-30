package eu.andlabs.studiolounge;

public class Player {
	
	private String playername;
	private String hostedGame;

	public Player(String playername) {
		super();
		this.playername = playername;
	}

	public String getPlayername() {
		return playername;
	}

	public void setPlayername(String playername) {
		this.playername = playername;
	}

	public String getHostedGame() {
		return hostedGame;
	}

	public void setHostedGame(String hostedGame) {
		this.hostedGame = hostedGame;
	}

}
