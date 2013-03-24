package eu.andlabs.studiolounge;

import java.util.Comparator;

import eu.andlabs.studiolounge.dao.LobbyListElement;

public class LobbyComperator  implements Comparator<LobbyListElement>{

	@Override
	public int compare(LobbyListElement first, LobbyListElement second) {
		
		if(first.getType().value()<second.getType().value()){
			return 0;
		}
		
		if(first.getType().value()<second.getType().value()){
			return 1;
		}else{
			return -1;
		}
		
	}

}
