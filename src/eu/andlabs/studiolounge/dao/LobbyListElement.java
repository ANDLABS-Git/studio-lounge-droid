package eu.andlabs.studiolounge.dao;

import java.util.ArrayList;
import java.util.List;

public class LobbyListElement {
	
	private ElementType type;
	private String Title;
	private String pgkName;
	private List<GameMatch> gameMatches;
	private boolean involved;
	private boolean localPlayerOnTurn;
	
	
	public LobbyListElement() {
		gameMatches=new ArrayList<GameMatch>();
	}
	
	public ElementType getType() {
		return type;
	}



	public void setType(ElementType type) {
		this.type = type;
	}



	public String getTitle() {
		return Title;
	}



	public void setTitle(String title) {
		Title = title;
	}



	public String getPgkName() {
		return pgkName;
	}



	public void setPgkName(String pgkName) {
		this.pgkName = pgkName;
	}



	public List<GameMatch> getGameMatches() {
		return gameMatches;
	}



	public void setGameMatches(List<GameMatch> gameMatches) {
		this.gameMatches = gameMatches;
	}



	public boolean isInvolved() {
		return involved;
	}



	public void setInvolved(boolean involved) {
		this.involved = involved;
	}



	public enum ElementType{
		 JOINED_GAME(0), SEPERATOR(1), OPEN_GAME(2);
		
		private int type;
		
		ElementType(int type){
			this.setType(type);
		}

		public int value() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
	}

}
