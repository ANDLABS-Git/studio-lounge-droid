package eu.andlabs.studiolounge;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LobbyListAdpater extends BaseAdapter implements LobbyListner{

	
	private List<String> playerlist;
	private Context context;

	public LobbyListAdpater(Context context){
		this.context=context;
	}
	
	public void setPlayerList(List<String> playerlist){
		this.playerlist=playerlist;
		
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return playerlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        View v= convertView;
			if (convertView == null)
	            v = inflater.inflate(R.layout.lobby_list_entry, null);
			
			((TextView)v.findViewById(R.id.playername)).setText(playerlist.get(position));
		return v;
	}

	@Override
	public void onPlayerJoinedLobby(String playername) {
		playerlist.add(playername);
		notifyDataSetChanged();
		
	}

	@Override
	public void onPlayerLeftLobby(String playername) {
		// TODO Auto-generated method stub
		
	}

}
