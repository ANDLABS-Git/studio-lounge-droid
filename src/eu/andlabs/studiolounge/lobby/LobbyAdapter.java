package eu.andlabs.studiolounge.lobby;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import eu.andlabs.studiolounge.LoungeActivity;
import eu.andlabs.studiolounge.Player;
import eu.andlabs.studiolounge.R;

public class LobbyAdapter extends BaseAdapter {

	private ArrayList<Player> mPlayers = new ArrayList<Player>();
	private Context mContext;
	private String mOwnID;

	public LobbyAdapter(Context context) {
		this.mContext = context;
		this.mOwnID = LoginManager.getInstance(this.mContext).getUserId();
	}

	public List<Player> getPlayerList() {
		return this.mPlayers;
	}

	@Override
	public int getCount() {
		return this.mPlayers.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final LayoutInflater inflater = LayoutInflater.from(this.mContext);

		if (view == null) {
			view = inflater.inflate(R.layout.lobby_list_entry, null);
		}

		final TextView playerLabel = (TextView) view
				.findViewById(R.id.playername);
		final Player player = mPlayers.get(position);
		playerLabel.setText(player.getShortPlayername());
		
		final View join = view.findViewById(R.id.join_btn_area);
		if (player.getHostedGame() != null) {
			((TextView) view.findViewById(R.id.gamename)).setText(player
					.getHostedGameName());
			join.setVisibility(View.VISIBLE);

			if (player.getPlayername().equalsIgnoreCase(mOwnID)) {
				join.setAlpha(0.5f);
				join.setEnabled(false);
			}

			join.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (Utils.isGameInstalled(LobbyAdapter.this.mContext, player.getHostedGamePackage())) {
						((LoungeActivity) LobbyAdapter.this.mContext)
								.getLounge().joinGame(player.getPlayername(),
										player.getHostedGame());

						// TODO: needs to be triggered when the min of players
						// is reached, not by default (postponed to GCP 0.4)
						Utils.launchGameApp(LobbyAdapter.this.mContext,
								player.getHostedGame());
					} else {
						Utils.openPlay(LobbyAdapter.this.mContext, player.getHostedGamePackage());
					}
				}
			});
			
			final ImageView icon = (ImageView) view.findViewById(R.id.icon);
			Drawable drawable = Utils.getGameIcon(this.mContext, player.getHostedGamePackage());
			if(drawable == null) {
				drawable = this.mContext.getResources().getDrawable(R.drawable.ic_play);
			}
			icon.setImageDrawable(drawable);
		} else {
			// Remove the join stuff
		}
		return view;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

}
