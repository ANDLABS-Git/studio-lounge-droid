package eu.andlabs.studiolounge.spec;

import eu.andlabs.studiolounge.Lounge.GameMsgListener;
import android.app.Activity;
import android.os.Bundle;

public class SampleGameActivity extends Activity implements GameMsgListener{
	
	SampleLounge mLounge;
	private boolean gameEndedNormally;
	private String matchID;
	private int playersCheckedIn;
	private int maxPlayer;

	

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
			
			//Alternative: After registring as a Listener, the last Message(s) will be 
			// will be forwarded to this Listener
			
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
	
	
	/**
	 * When a player starts/returns to the GameActivity
	 * @param player
	 */
	@Override
	public void onCheckIn(String player){
		playersCheckedIn++;
		
	
	}
	
	/**
	 * When a player closes to the GameActivity
	 * @param player
	 */
	@Override
	public void onCheckOut(String player){
		playersCheckedIn--;
	}
	
	/**
	 * When all players are in the Activity
	 *
	 */
	public void voidOnAllPlayerCheckedIn(){
		
	}
	
	//These Callbacks are for Games that requires all players to be in the gameActivity(real time games)
	//or to let other players know if this player is activly playing or not.
}


