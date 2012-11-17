/*
 * Copyright (C) 2012 ANDLABS. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.andlabs.studiolounge.lobby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import eu.andlabs.studiolounge.LoungeActivity;
import eu.andlabs.studiolounge.LoungeConstants;
import eu.andlabs.studiolounge.Player;
import eu.andlabs.studiolounge.R;
import eu.andlabs.studiolounge.gcp.GCPService;
import eu.andlabs.studiolounge.gcp.Lounge;
import eu.andlabs.studiolounge.gcp.Lounge.LobbyListener;

public class LobbyFragment extends Fragment implements LobbyListener,
        OnClickListener, LoungeConstants {

    private ListView lobbyList;
    private ImageView pulseBeacon;
    private ImageView staticBeacon;
    // private AnimatorSet scaleDown;
    private ListView mHostList;
    private HostGameAdapter mAdapter;
    private LobbyAdapter lobbyAdapter;
    private Lounge mLounge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("Lounge", "LobbyFragment on CREATE");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        this.mLounge = ((LoungeActivity) getActivity()).getLounge();

    }

    @Override
    public void onStart() {
        Log.i("Lounge", "LobbyFragment on START");
        this.mLounge.register(this);
        lobbyAdapter.getPlayerList().clear();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onStart();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View lobby = inflater.inflate(R.layout.lobby, container, false);
        pulseBeacon = (ImageView) lobby.findViewById(R.id.ic_lobby_host_pulse);
        staticBeacon = (ImageView) lobby
                .findViewById(R.id.ic_lobby_host_static_pulse);

        this.mHostList = (ListView) lobby.findViewById(R.id.installed_games);
        this.mAdapter = new HostGameAdapter(getActivity());
        this.mHostList.setAdapter(mAdapter);
        this.mHostList.setOnItemClickListener(mAdapter);

        lobby.findViewById(R.id.btn_host).setOnClickListener(this);
        lobby.findViewById(R.id.btn_practise).setOnClickListener(this);
        lobbyAdapter = new LobbyAdapter(getContext());
        ((ListView) lobby.findViewById(R.id.list)).setAdapter(lobbyAdapter);
        lobbyList = (ListView) lobby.findViewById(R.id.list);
        return lobby;
    }

    @Override
    public void onPlayerLoggedIn(String player) {
        // Toast.makeText(getActivity(), player + " joined", 3000).show();
        for (Player p : lobbyAdapter.getPlayerList()) {
            if (p.getPlayername().equalsIgnoreCase(player)) {
                return;
            }
        }
        lobbyAdapter.getPlayerList().add(new Player(player));
        lobbyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayerLeft(String player) {
        Toast.makeText(getActivity(), player + " left", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onNewHostedGame(String player, String hostedGame) {
        for (Player p : lobbyAdapter.getPlayerList()) {
            if (p.getPlayername().equals(player)) {
                p.setHostedGame(hostedGame);
                lobbyAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPlayerJoined(String playerName, String game) {
        final Player player = new Player(playerName);
        player.setHostedGame(game);

        if (!player.getPlayername().equals(GCPService.mName)) {
            Log.i("debug", " player joined" + playerName + " .. " + game);
            Utils.launchGameApp(getContext(), player.getHostedGamePackage(),
                    LoungeConstants.HOST_FLAG);
        }

    }

    @Override
    public void onStop() {
        this.mLounge.unregister(this);
        super.onStop();
    }

    private void stopAnimatingHostMode() {
        // if (scaleDown != null) {
        // scaleDown.cancel();
        // }
        pulseBeacon.setVisibility(View.INVISIBLE);
        staticBeacon.setVisibility(View.VISIBLE);
    }

    private void animateHostMode() {
        pulseBeacon.setVisibility(View.VISIBLE);
        staticBeacon.setVisibility(View.INVISIBLE);

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                getActivity(), R.anim.pulse);
        hyperspaceJumpAnimation.setRepeatMode(Animation.INFINITE);
        hyperspaceJumpAnimation.setRepeatCount(1000);
        pulseBeacon.startAnimation(hyperspaceJumpAnimation);

        // final ObjectAnimator alphaAnimation =
        // ObjectAnimator.ofFloat(pulseBeacon, "alpha", 0);
        //
        // final ObjectAnimator scaleXAnimation =
        // ObjectAnimator.ofFloat(pulseBeacon, "scaleX", 1);
        //
        // final ObjectAnimator scaleYAnimation =
        // ObjectAnimator.ofFloat(pulseBeacon, "scaleY", 1);
        //
        // long duration= 300;
        // alphaAnimation.setDuration(duration);
        //
        // scaleXAnimation.setDuration(duration);
        //
        // scaleYAnimation.setDuration(duration);
        //
        // scaleYAnimation.setRepeatMode(ObjectAnimator.INFINITE);
        // scaleXAnimation.setRepeatMode(ObjectAnimator.INFINITE);
        // alphaAnimation.setRepeatMode(ObjectAnimator.INFINITE);
        //
        // scaleDown = new AnimatorSet();
        //
        //
        // scaleDown.play(alphaAnimation).with(scaleXAnimation).with(scaleYAnimation);
        // scaleDown.start();
    }

    @Override
    public void onClick(View v) {
        final String launchString = this.mAdapter.getSelectedItemPackage();
        if (launchString != null) { // haz package
            if (v.getId() == R.id.btn_host) {
                this.mLounge.hostGame(launchString);
                animateHostMode();
            }
            if (v.getId() == R.id.btn_practise) {
                final Intent intent = getActivity()
                        .getPackageManager()
                        .getLaunchIntentForPackage(
                                launchString.split(PACKAGE_APPNAME_SEPERATOR)[0]);
                startActivity(intent);
            }
        } else {
            Toast.makeText(getActivity(), "Please select a game",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public Context getContext() {
        // TODO Auto-generated method stub
        return getActivity();
    }

}
