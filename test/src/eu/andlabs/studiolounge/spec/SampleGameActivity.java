package eu.andlabs.studiolounge.spec;

import eu.andlabs.studiolounge.Lounge.GameMsgListener;
import android.app.Activity;
import android.os.Bundle;

public class SampleGameActivity extends Activity implements GameMsgListener{
	
	SampleLounge mLounge;
	private boolean gameEndedNormally;
	private String matchID;

	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	matchID =getIntent().getStringExtra("MATCH_ID");
		
		if(matchID!=null){ // reopen old existing Game
			Bundle lastGameState =mLounge.getLastMove(matchID);
			Bundle lastMove= mLounge.getLastMove(matchID);
			
			initGame(lastGameState);
			onMessageReceived(lastMove);// use the same Method as the Lounge Libary would use
										// when this game would be running already.
			
		}else{
			// Just init a normal game like always
			//...
			
		}
		
		
		//When Level Loading/Initialization is finished
		mLounge.registerListener(this);
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if(gameEndedNormally){
			//close Activity
		}else{
			Bundle actualGameState= new Bundle();
			actualGameState.putInt("GAMEPROGRESS", 231);
			mLounge.pauseGame(actualGameState, matchID); 
			//saves the gameState so it can be restored and notifies
			//the Server/other Players that this player is inActive.
		}
	}
	

	private void initGame(Bundle lastGameState) {
		// init Game to the last state that was stored in the Bundle
		
	}


	@Override
	public void onMessageReceived(Bundle msg) {
		// handle regular game messages
		
	}
}
