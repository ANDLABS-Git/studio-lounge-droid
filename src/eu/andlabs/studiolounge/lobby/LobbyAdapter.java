package eu.andlabs.studiolounge.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import eu.andlabs.studiolounge.LoungeActivity;
import eu.andlabs.studiolounge.R;
import android.content.Context;
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

    public LobbyAdapter(LobbyFragment lobbyFragment) {
        this.lobbyFragment = lobbyFragment;
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
        final LayoutInflater inflater = (LayoutInflater) lobbyFragment
                .getContext()

                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            v = inflater.inflate(R.layout.lobby_list_entry, null);
        }

        final TextView playerLabel = (TextView) v.findViewById(R.id.playername);
        final Player player = mPlayers.get(position);
        playerLabel.setText(player.getPlayername());
        Button b = (Button) v.findViewById(R.id.joinbtn);
        LinearLayout join = (LinearLayout) v.findViewById(R.id.join_btn_area);
        if (player.getHostedGame() != null) {
            b.setText(player.getHostedGameName());
            join.setVisibility(View.VISIBLE);
            join.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((LoungeActivity) lobbyFragment.getContext()).getLounge()
                            .joinGame(player.getPlayername(),
                                    player.getHostedGame());
                    lobbyFragment.launchGameApp(player.getHostedGame());
                }
            });
        } else {
            b.setVisibility(View.GONE);
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
