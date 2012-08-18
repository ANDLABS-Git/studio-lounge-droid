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


import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import eu.andlabs.studiolounge.gcp.Lounge.ChatListener;
import eu.andlabs.studiolounge.gcp.Lounge.LobbyListener;


public class LobbyFragment extends Fragment implements LobbyListener {
    int mNum;
    private ListView mListView;
    private ArrayList<String> mPlayers;

    /**
     * Create a new instance of CountingFragment, providing "num" as an
     * argument.
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
        mPlayers = new ArrayList<String>();
        ((LoungeMainActivity)getActivity()).mLounge.register(this);
    }

    
    @Override
    public void onPlayerJoined(String player) {
        Toast.makeText(getActivity(), player + " joined", 3000).show();
        mPlayers.add(player);
        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }
    
    @Override
    public void onPlayerLeft(String player) {
        Toast.makeText(getActivity(), player + " left", 3000).show();
    }

    /**
     * The Fragment's UI is just a simple text view showing its instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View lobby = inflater.inflate(R.layout.lobby, container, false);
        mListView = (ListView) lobby.findViewById(R.id.playerList);
        
        mListView.setAdapter(new BaseAdapter() {
            
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            @Override
            public int getCount() { return mPlayers.size(); }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = inflater.inflate(R.layout.lobby_list_entry, null);
                ((TextView) view.findViewById(R.id.playername))
                        .setText(mPlayers.get(position));
                return view;
            }
            
            @Override
            public long getItemId(int position) { return 0; }
            
            @Override
            public Object getItem(int position) { return null; }
        });
        return lobby;
    }
}
