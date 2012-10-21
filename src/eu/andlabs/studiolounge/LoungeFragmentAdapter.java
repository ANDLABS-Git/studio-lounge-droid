package eu.andlabs.studiolounge;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.Log;

public class LoungeFragmentAdapter extends FragmentStatePagerAdapter {

	private static final int NUM_PAGES = 4;
	private static final int LOBBY = 0;
	private static final int CHAT = 1;
	private static final int STATS = 2;
	private static final int ABOUT = 3;
	private LobbyFragment mLobbyFragment;
	private ChatFragment mChatFragment;
	private StatisticFragment mStatisticFragment;
	private AboutFragment mAboutFragment;

	public LoungeFragmentAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		Log.i("ADAPTER","FRAGMENT: "+position);
		switch (position) {
		case LOBBY:
			if (mLobbyFragment == null) {
				mLobbyFragment = new LobbyFragment();
			}
			return mLobbyFragment;

		case CHAT:

			if (mChatFragment == null) {
				mChatFragment = new ChatFragment();
				
			}
			return mChatFragment;
		case STATS:

			if (mStatisticFragment == null) {
				mStatisticFragment = new StatisticFragment();
			
			}
			return mStatisticFragment;

		case ABOUT:
			if (mAboutFragment == null) {
				mAboutFragment = new AboutFragment();
			}

			return mAboutFragment;

		default:
			Log.i("ADAPTER", "NO Fragment for position");
			return null;
		}

			
		}
		
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return NUM_PAGES;
	}

}
