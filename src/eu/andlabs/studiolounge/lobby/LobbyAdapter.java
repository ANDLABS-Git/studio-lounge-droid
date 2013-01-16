package eu.andlabs.studiolounge.lobby;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import eu.andlabs.studiolounge.LoungeActivity;
import eu.andlabs.studiolounge.Constants;
import eu.andlabs.studiolounge.Player;
import eu.andlabs.studiolounge.R;

public class LobbyAdapter extends BaseAdapter {

    private ArrayList<Player> mPlayers;
    private Context mContext;
    private String mOwnID;

    public LobbyAdapter(Context context, ArrayList<Player> mPlayers) {
        this.mContext = context;
        this.mPlayers = mPlayers;
        this.mOwnID = LoginManager.getInstance(this.mContext).getUserId().getPlayername();
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
        final ImageView icon = (ImageView) view.findViewById(R.id.icon);
        final TextView minmax = ((TextView) view.findViewById(R.id.minmax));
        final TextView gameLabel = ((TextView) view.findViewById(R.id.gamename));
        
        if (player.getHostedGame() != null) {
            gameLabel.setText(player.getHostedGameName());
            minmax.setVisibility(View.VISIBLE);
            minmax.setText(player.joined+"/"+player.max);
            join.setVisibility(View.VISIBLE);
            if (player.getPlayername().equalsIgnoreCase(mOwnID)) {
                // Using alpha animation as a workaround since setAlpha is not supported in API level < 11.
                join.setEnabled(false);
                AlphaAnimation animation = new AlphaAnimation(1, 0.5f);
                animation.setDuration(0);
                animation.setFillAfter(true);
                join.startAnimation(animation);
            } else {
                join.setEnabled(true);
                AlphaAnimation animation = new AlphaAnimation(0.5f, 1);
                animation.setDuration(0);
                animation.setFillAfter(true);
                join.startAnimation(animation);
            }

            join.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (Utils.isGameInstalled(mContext, player.getHostedGamePkg())) {
                        ((LoungeActivity) mContext).getLounge()
                            .joinGame(player.getHostedGame());

                        // TODO: needs to be triggered when the min of players
                        // is reached, not by default (postponed to GCP 0.4)
                        Utils.launchGameApp(LobbyAdapter.this.mContext,
                                player.getHostedGamePkg(),
                                Constants.GUEST_FLAG,player.getShortPlayername(),LoginManager.getInstance(mContext).getUserId().getShortPlayername());
                    } else {
                        Utils.openPlay(LobbyAdapter.this.mContext,
                                player.getHostedGamePkg());
                    }
                }
            });

            icon.setVisibility(View.VISIBLE);
            Drawable drawable = Utils.getGameIcon(this.mContext,
                    player.getHostedGamePkg());
            if (drawable == null) {
                drawable = this.mContext.getResources().getDrawable(
                        R.drawable.ic_play);
            }
            icon.setImageDrawable(drawable);
        } else {
            join.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            minmax.setVisibility(View.GONE);
            gameLabel.setText("Waiting in Lobby..");
            view.findViewById(R.id.icon).setVisibility(View.INVISIBLE);
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
