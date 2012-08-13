/*
 * Copyright (C) 2011 The Android Open Source Project
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


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class LobbyFragment extends Fragment implements FragmentListner{
    int mNum;
	private ListView playerlist;
	private LobbyListAdpater mAdapter;

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static LobbyFragment newInstance(int num) {
        LobbyFragment f = new LobbyFragment();

        

        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        ((LoungeMainActivity)getActivity()).registerLobbyFragment(this);
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lobby, container, false);
        playerlist=(ListView) v.findViewById(R.id.playerList);
        mAdapter= new LobbyListAdpater(getActivity());
        playerlist.setAdapter(mAdapter);
        return v;
    }

	@Override
	public void onPlayerJoined(String player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerLeft(String player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChatMessageRecieved(String msg) {
		// TODO Auto-generated method stub
		
	}
}
