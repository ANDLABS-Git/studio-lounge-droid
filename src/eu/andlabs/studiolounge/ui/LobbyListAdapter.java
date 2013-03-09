package eu.andlabs.studiolounge.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import eu.andlabs.studiolounge.R;
import eu.andlabs.studiolounge.dao.GameMatch;
import eu.andlabs.studiolounge.dao.LobbyListElement;
import eu.andlabs.studiolounge.dao.Player;

public class LobbyListAdapter extends BaseExpandableListAdapter {

	private static final int TYPE_JOINEDGAME = 0;
	private static final int TYPE_OPENGAME = 1;
	private static final int TYPE_SEPARATOR = 2;

	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private List<LobbyListElement> content;
	private Context context;
	private LayoutInflater mInflater;

	public LobbyListAdapter(Context context) {
		this.context=context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setContent( List<LobbyListElement> content){
		this.content=content;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		int type = getChildType(groupPosition, childPosition);
		System.out.println("getChildView " + groupPosition + " " + convertView
				+ " type = " + type);
		if (convertView == null || convertView.getId() != type) { // When it is
																	// a new
																	// view or
																	// not
																	// recycleable
																	// because
																	// its a
																	// different
																	// view type

			switch (type) {
			case TYPE_JOINEDGAME:
				convertView = mInflater
						.inflate(R.layout.lobby_match_list_entry_2players,
								parent, false);
				fillJoinedGameView(groupPosition, childPosition, convertView);

				break;
			case TYPE_SEPARATOR:
				convertView = mInflater.inflate(R.layout.lobby_seperator, null);
				break;

			case TYPE_OPENGAME:
				convertView = mInflater.inflate(R.layout.lobby_open_game_entry,
						null);
				break;
			}

		} else {

			switch (type) {
			case TYPE_JOINEDGAME:
				fillJoinedGameView(groupPosition, childPosition, convertView);
				break;

			case TYPE_SEPARATOR:
				break;
				
			case TYPE_OPENGAME:
				fillOpenGameView(groupPosition, childPosition, convertView);
				break;
			}
		}
		convertView.setId(type);
		return convertView;

	}

	private void fillOpenGameView(int groupPosition, int childPosition,
			View convertView) {
		TextView hostname = (TextView) convertView
				.findViewById(R.id.hostname);
		TextView playercount = (TextView) convertView
				.findViewById(R.id.playercount);
		GameMatch match = content.get(groupPosition).getGameMatches().get(childPosition);
		hostname.setText(match.getPlayers().get(0).getDisplayName());
		playercount.setText(match.getPlayers().size()+"/"+match.getMaxPlayers());
	}

	private void fillJoinedGameView(int groupPosition, int childPosition,
			View convertView) {
		TextView player1Label = (TextView) convertView
				.findViewById(R.id.playerLbl1);
		TextView player2Label = (TextView) convertView
				.findViewById(R.id.playerLbl2);
		
		View player1Beacon=convertView.findViewById(R.id.playerBeacon1);
		View player2Beacon=convertView.findViewById(R.id.playerBeacon2);
		
		player1Label.setText(content.get(groupPosition).getGameMatches()
				.get(childPosition).getPlayers().get(0).getDisplayName());
		player1Beacon.setBackgroundColor(Color.GREEN);
		if(content.get(groupPosition).getGameMatches()
				.get(childPosition).getPlayers().size()==2){
		Player player2 = content.get(groupPosition).getGameMatches()
				.get(childPosition).getPlayers().get(1);

			player2Label.setText(player2.getDisplayName());
			player2Beacon.setBackgroundColor(Color.GREEN);
		}else{
			player2Beacon.setBackgroundColor(Color.WHITE);
			player2Label.setText("Open");
		}
		
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return content.get(groupPosition).getGameMatches().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return content.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		int type = getGroupType(groupPosition);
		System.out.println("getChildView " + groupPosition + " " + convertView
				+ " type = " + type);
		if (convertView == null || convertView.getId() != type) { // When it is
																	// a new
																	// view or
																	// not
																	// recycleable
																	// because
																	// its a
																	// different
																	// view type

			switch (type) {
			case TYPE_JOINEDGAME:
				convertView = mInflater
						.inflate(R.layout.lobby_gamelist_entry,
								parent, false);
				createGameView(groupPosition, convertView);

				break;
			case TYPE_SEPARATOR:
				convertView = mInflater.inflate(R.layout.lobby_seperator, null);
				break;

			case TYPE_OPENGAME:
				convertView = mInflater.inflate(R.layout.lobby_gamelist_entry,
						null);
				createGameView(groupPosition, convertView);
				break;
			}

		} else {

			switch (type) {
			case TYPE_JOINEDGAME:
				createGameView(groupPosition, convertView);
				break;

			case TYPE_SEPARATOR:
				break;
				
			case TYPE_OPENGAME:
				createGameView(groupPosition, convertView);
				break;
			}
		}
		convertView.setId(type);
		return convertView;
		
	
	

	}

	private void createGameView(int groupPosition, View convertView) {
		((TextView) convertView.findViewById(R.id.gamename)).setText(content
				.get(groupPosition).getTitle());
		((TextView) convertView.findViewById(R.id.count)).setText(content
				.get(groupPosition).getGameMatches().size()
				+ "");
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public int getChildType(int groupPosition, int childPosition) {
		return content.get(groupPosition).getType().value();
	}

	@Override
	public int getGroupType(int groupPosition) {
		
		return content.get(groupPosition).getType().value();
	}

	@Override
	public int getChildTypeCount() {
		// TODO Auto-generated method stub
		return 3;
	}
	
	@Override
	public int getGroupTypeCount() {
		// TODO Auto-generated method stub
		return 3;
	}
}
