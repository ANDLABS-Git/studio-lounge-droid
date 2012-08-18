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

public class ChatFragment extends Fragment implements ChatListener {
    ArrayList<String> mConversation;
    ListView mListView;
    int mNum;

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static ChatFragment newInstance(int num) {
        ChatFragment f = new ChatFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConversation = new ArrayList<String>();
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        ((LoungeMainActivity)getActivity()).mLounge.register(this);
    }

    @Override
    public void onChatMessageRecieved(String msg) {
        Toast.makeText(getActivity(), msg, 3000).show();
        mConversation.add(msg);
        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View chat = inflater.inflate(R.layout.chat, container, false);
        mListView = (ListView) chat.findViewById(R.id.chatlist);
        
        mListView.setAdapter(new BaseAdapter() {
            
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            @Override
            public int getCount() { return mConversation.size(); }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = inflater.inflate(R.layout.lobby_list_entry, null);
                String[] msg = mConversation.get(position).split(">");
                ((TextView)view.findViewById(R.id.sender)).setText(msg[0]);
                ((TextView)view.findViewById(R.id.msg_text)).setText(msg[1]);
                return view;
            }
            
            @Override
            public long getItemId(int position) { return 0; }
            
            @Override
            public Object getItem(int position) { return null; }
        });
        return chat;
    }
}
