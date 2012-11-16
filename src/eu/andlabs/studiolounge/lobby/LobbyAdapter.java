package eu.andlabs.studiolounge.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import eu.andlabs.studiolounge.LoungeActivity;
import eu.andlabs.studiolounge.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LobbyAdapter extends BaseAdapter {

    private ArrayList<Player> mPlayers = new ArrayList<Player>();
    private LobbyFragment lobbyFragment;
    private String mOwnID;

    public LobbyAdapter(LobbyFragment lobbyFragment) {
        this.lobbyFragment = lobbyFragment;
        mOwnID = LoginManager.getInstance(lobbyFragment.getContext()).getUserId();
    }

    public List<Player> getPlayerList() {
        return mPlayers;
    }

    @Override
    public int getCount() {
        return mPlayers.size();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View v = view;
        final LayoutInflater inflater = (LayoutInflater) lobbyFragment.getContext()

        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            v = inflater.inflate(R.layout.lobby_list_entry, null);
        }
        v.setClickable(false);
        final TextView playerLabel = (TextView) v.findViewById(R.id.playername);
        final Player player = mPlayers.get(position);
        playerLabel.setText(player.getShortPlayername());
        View join = v.findViewById(R.id.join_btn_area);
        // Log.i("Players",
        // player.getShortPlayername() + " - "
        // + player.getHostedGamePackage());
        if (player.getHostedGame() != null) {
            ((TextView) v.findViewById(R.id.gamename)).setText(player.getHostedGamePackage());
            join.setVisibility(View.VISIBLE);
            if (player.getPlayername().equalsIgnoreCase(mOwnID)) {
                Log.i("Players", player.getShortPlayername() + "  Own ID: " + mOwnID);
                join.setAlpha(0.5f);
                join.setEnabled(false);
            }
            Log.i("debug",
                    "show login btn: " + player.getShortPlayername() + " #"
                            + player.getHostedGame());
            join.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((LoungeActivity) lobbyFragment.getContext()).getLounge().joinGame(
                            player.getPlayername(), player.getHostedGame());
                    lobbyFragment.launchGameApp(player.getHostedGamePackage(),200);
                }
            });
        } else {
        }
        return v;
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
