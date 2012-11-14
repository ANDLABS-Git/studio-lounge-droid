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
package eu.andlabs.studiolounge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.animation.AnimatorSet;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import eu.andlabs.studiolounge.gcp.GCPService;
import eu.andlabs.studiolounge.gcp.Lounge.LobbyListener;

public class LobbyFragment extends Fragment implements LobbyListener,
		OnClickListener, LoungeConstants {
	private ArrayList<Player> mPlayers = new ArrayList<Player>();
	private ListView lobbyList;
	private ImageView pulseBeacon;
	private ImageView staticBeacon;
	private AnimatorSet scaleDown;
	private ListView mHostList;
	private HostGameAdapter mAdapter;

	// private LinearLayout gameAtomDroid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("Lounge", "LobbyFragment on CREATE");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

	}

	@Override
	public void onStart() {
		Log.i("Lounge", "LobbyFragment on START");
		((LoungeActivity) getActivity()).getLounge().register(this);
		mPlayers.clear();
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

		((ListView) lobby.findViewById(R.id.list))
				.setAdapter(new BaseAdapter() {

					@Override
					public int getCount() {
						return mPlayers.size();
					}

					@Override
					public View getView(final int position, View view,
							ViewGroup parent) {
						if (view == null)
							view = inflater.inflate(R.layout.lobby_list_entry,
									null);
						final TextView playerLabel = (TextView) view
								.findViewById(R.id.playername);
						final Player player = mPlayers.get(position);
						playerLabel.setText(player.getPlayername());
						Button b = (Button) view.findViewById(R.id.joinbtn);
						LinearLayout join = (LinearLayout) view
								.findViewById(R.id.join_btn_area);
						if (player.getHostedGame() != null) {
							b.setText(player.getHostedGame().split("\\.")[4]);
							join.setVisibility(View.VISIBLE);
							join.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									((LoungeActivity) getActivity()).getLounge()
											.joinGame(player.getPlayername(),
													player.getHostedGame());
									launchGameApp(player.getHostedGame());
								}
							});
						} else {
							b.setVisibility(View.GONE);
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
				});

		lobbyList = (ListView) lobby.findViewById(R.id.list);
		return lobby;
	}

	@Override
	public void onPlayerLoggedIn(String player) {
		// Toast.makeText(getActivity(), player + " joined", 3000).show();
		mPlayers.add(new Player(player));
		((BaseAdapter) lobbyList.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onPlayerLeft(String player) {
		Toast.makeText(getActivity(), player + " left", 3000).show();
	}

	@Override
	public void onNewHostedGame(String player, String hostedGame) {
		for (Player p : mPlayers) {
			if (p.getPlayername().equals(player)) {
				p.setHostedGame(hostedGame);
				((BaseAdapter) lobbyList.getAdapter()).notifyDataSetChanged();
			}
		}

	}

	@Override
	public void onPlayerJoined(String player, String game) {
		if (!player.equals(GCPService.mName))
			launchGameApp(getActivity().getPackageName());
	}

	private void launchGameApp(String pkgName) {
		PackageManager pm = getActivity().getPackageManager();
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.addCategory(CATEGORY);
		List<ResolveInfo> list = pm.queryIntentActivities(i, 0);

		for (ResolveInfo info : list) {
			Intent launch = new Intent(Intent.ACTION_MAIN);
			Log.i("debug", "found package " + info.activityInfo.packageName);
			if (info.activityInfo.packageName.equalsIgnoreCase(pkgName)) {
				Log.i("debug", "Packge Match found");
				launch.setComponent(new ComponentName(
						info.activityInfo.packageName, info.activityInfo.name));
			} else {
				Log.i("debug", "NO Package Match");
				launch = pm.getLaunchIntentForPackage(pkgName);
			}
			startActivity(launch);
		}
	}

	@Override
	public void onStop() {
		((LoungeActivity) getActivity()).getLounge().unregister(this);
		super.onStop();
	}

	private void stopAnimatingHostMode() {
		if (scaleDown != null) {
			scaleDown.cancel();
		}
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
		final ComponentName launchComponent = this.mAdapter
				.getSelectedItemPackage();
		if (launchComponent != null) { // haz package
			if (v.getId() == R.id.btn_host) {
				// TODO: Use interface
				((LoungeActivity) getActivity()).hostGame(launchComponent);
				animateHostMode();
			}
			if (v.getId() == R.id.btn_practise) {
				final Intent intent = new Intent();
				intent.setComponent(launchComponent);
				startActivity(intent);
			}
		} else {
			Toast.makeText(getActivity(), "Please select a game",
					Toast.LENGTH_SHORT).show();
		}
	}
}
