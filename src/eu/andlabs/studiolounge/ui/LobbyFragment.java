/*
 * Copyright (C) 2012, 2013 by it's authors. Some rights reserved.
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
package eu.andlabs.studiolounge.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ExpandableListView;
import eu.andlabs.studiolounge.R;

public class LobbyFragment extends Fragment {

	private SparseIntArray listPositions = new SparseIntArray();
	private ExpandableListView lobbyList;
	private LobbyListAdapter mAdapter;
	private static final String TAG = "Lounge";
	private static final int GAMES = 0;

	@Override
	public View onCreateView(final LayoutInflater lI, ViewGroup p, Bundle b) {
		View v=lI.inflate(R.layout.fragment_lobby, p, false);
		lobbyList=(ExpandableListView) v.findViewById(R.id.list);
		
		ViewTreeObserver vto = lobbyList.getViewTreeObserver();

    vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
                 lobbyList.setIndicatorBounds(lobbyList.getRight()- 40, lobbyList.getWidth());
        }
    });
		mAdapter = new LobbyListAdapter(getActivity());
		mAdapter.setContent(TestData.getMockData());
		lobbyList.setAdapter(mAdapter);
		
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

}
