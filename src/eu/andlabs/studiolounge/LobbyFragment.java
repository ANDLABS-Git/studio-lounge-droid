/*
 * Copyright (C) 2012 http://andlabs.eu
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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import eu.andlabs.studiolounge.gcp.Lounge.LobbyListener;


public class LobbyFragment extends Fragment implements LobbyListener {
    private ArrayList<String> mPlayers = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((LoungeMainActivity)getActivity()).mLounge.register(this);
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View lobby = inflater.inflate(R.layout.lobby, container, false);
        ((ListView)lobby.findViewById(R.id.list)).setAdapter(new BaseAdapter() {
            
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
    
    @Override
    public void onPlayerJoined(String player) {
        Toast.makeText(getActivity(), player + " joined", 3000).show();
        mPlayers.add(player);
        ((BaseAdapter) ((ListView) getView().findViewById(R.id.list))
                .getAdapter()).notifyDataSetChanged();
    }
    
    @Override
    public void onPlayerLeft(String player) {
        Toast.makeText(getActivity(), player + " left", 3000).show();
    }
}
