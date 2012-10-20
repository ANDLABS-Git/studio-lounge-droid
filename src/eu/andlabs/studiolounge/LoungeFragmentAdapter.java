package eu.andlabs.studiolounge;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

public class LoungeFragmentAdapter extends FragmentStatePagerAdapter {

	private static final int NUM_PAGES = 3;
	private static final int LOBBY = 0;
	private static final int CHAT = 1;
	private LobbyFragment mLobbyFragment;
	private ChatFragment mChatFragment;
	private StatisticFragment mStatisticFragment;

	public LoungeFragmentAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		if(position==LOBBY){
			if(mLobbyFragment==null){
				mLobbyFragment=new LobbyFragment();
			}
			return mLobbyFragment;
		}else if(position==CHAT){
			if(mChatFragment==null){
			mChatFragment=new ChatFragment();
			return mChatFragment;
		}
			}
		if(mStatisticFragment==null){
			mStatisticFragment=new StatisticFragment();
		}
		
		return mStatisticFragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return NUM_PAGES;
	}
	
	

}
