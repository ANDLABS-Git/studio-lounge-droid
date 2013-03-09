package eu.andlabs.studiolounge.ui;

import java.util.ArrayList;
import java.util.List;

import eu.andlabs.studiolounge.dao.GameMatch;
import eu.andlabs.studiolounge.dao.LobbyListElement;
import eu.andlabs.studiolounge.dao.LobbyListElement.ElementType;
import eu.andlabs.studiolounge.dao.Player;

public class TestData {
	
	public static List<LobbyListElement> getMockData(){
		
		
		
		List<LobbyListElement> list = new ArrayList<LobbyListElement>();
		Player pa=new Player();
		pa.setDisplayName("Tommo");
		
		Player pb=new Player();
		pb.setDisplayName("Ninja");
		
		Player pc=new Player();
		pc.setDisplayName("Nico");
		
		LobbyListElement game1=new LobbyListElement();
		game1.setTitle("Mol3cool");
		game1.setType(ElementType.JOINED_GAME);
		List<GameMatch> matchesGame1= new ArrayList<GameMatch>();
		
		
		GameMatch ma= new GameMatch();
		List<Player> pl1=new ArrayList<Player>();
		pl1.add(pa);
		ma.setMaxPlayers(2);
		ma.setPlayers(pl1);
		
		GameMatch ma2= new GameMatch();
		List<Player> pl2=new ArrayList<Player>();
		pl2.add(pb);
		pl2.add(pa);
		ma2.setMaxPlayers(2);
		ma2.setPlayers(pl2);
		
		matchesGame1.add(ma);
		matchesGame1.add(ma2);
		game1.setGameMatches(matchesGame1);
		
		
		LobbyListElement game2=new LobbyListElement();
		game2.setTitle("Gravity Losses");
		game2.setType(ElementType.JOINED_GAME);
		List<GameMatch> matchesGame2= new ArrayList<GameMatch>();
		
		
		GameMatch ma3= new GameMatch();
		List<Player> pl3=new ArrayList<Player>();
		pl3.add(pa);
		ma3.setMaxPlayers(2);
		ma3.setPlayers(pl3);
		matchesGame2.add(ma3);
		
		matchesGame1.add(ma3);
		game2.setGameMatches(matchesGame2);
		
		
		LobbyListElement seperator = new LobbyListElement();
		seperator.setType(ElementType.SEPERATOR);
		
		
		LobbyListElement openGame1 = new LobbyListElement();
		openGame1.setTitle("Mol3Cool");
		openGame1.setType(ElementType.OPEN_GAME);
		GameMatch mb1=new GameMatch();
		
		mb1.setMaxPlayers(4);
		List<Player> pl4=new ArrayList<Player>();
		pl4.add(pb);
		mb1.setPlayers(pl4);
		List<GameMatch> matchesOpen= new ArrayList<GameMatch>();
		matchesOpen.add(mb1);
		openGame1.setGameMatches(matchesOpen);
		
		list.add(game1);
		list.add(game2);
		list.add(seperator);
		list.add(openGame1);
		
		return list;
		
	}

}
