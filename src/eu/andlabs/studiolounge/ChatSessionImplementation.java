package eu.andlabs.studiolounge;

import eu.andlabs.studiolounge.GameCommunicationService.ChatSession;

public class ChatSessionImplementation extends ChatSession {

//	public ChatSessionImplementation( String name) {
//		gameCommunicationService.super(name);
//		// TODO Auto-generated constructor stub
//	}

	public ChatSessionImplementation(
			GameCommunicationService gameCommunicationService, String name) {
		gameCommunicationService.super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onJoin(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChat(String msg) {
		// TODO Auto-generated method stub
		
	}

}
