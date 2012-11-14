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
package eu.andlabs.studiolounge.chat;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import eu.andlabs.studiolounge.LoungeActivity;
import eu.andlabs.studiolounge.R;
import eu.andlabs.studiolounge.R.id;
import eu.andlabs.studiolounge.R.layout;
import eu.andlabs.studiolounge.gcp.Lounge.ChatListener;
import eu.andlabs.studiolounge.gcp.Lounge.ChatMessage;

public class ChatFragment extends Fragment implements ChatListener,
        OnClickListener {
    ArrayList<ChatMessage> mConversation = new ArrayList<ChatMessage>();
    private EditText mChatEditText;

    static ChatFragment newInstance(int num) {
        ChatFragment f = new ChatFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("THIS", "on create chat fragment");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        Log.i("Lounge", "ChatFragment on START");
        ((LoungeActivity) getActivity()).getLounge().register(this);
        mConversation.clear();
        super.onStart();
    }

    @Override
    public View onCreateView(final LayoutInflater infl, ViewGroup p, Bundle b) {
        Log.d("THIS", "on create view");
        final View chat = infl.inflate(R.layout.chat, p, false);
        mChatEditText = ((EditText) chat.findViewById(R.id.msg_field));
        ((ImageButton) chat.findViewById(R.id.btn_msgSend))
                .setOnClickListener(this);
        ((ListView) chat.findViewById(R.id.list)).setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mConversation.size();
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = infl.inflate(R.layout.chat_list_entry, null);
                ChatMessage msg = mConversation.get(position);
                ((TextView) view.findViewById(R.id.sender)).setText(msg.player);
                ((TextView) view.findViewById(R.id.msg_text)).setText(msg.text);
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
        // http://code.google.com/p/android/issues/detail?id=2516
        mChatEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onClick(null);
                    return true;
                }
                return false;
            }
        });
        return chat;
    }

    @Override
    public void onChatMessageRecieved(ChatMessage msg) {
        Log.d("Lounge", "CHAT " + msg);
        mConversation.add(msg);
        ((BaseAdapter) ((ListView) getView().findViewById(R.id.list))
                .getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        ChatMessage msg = new ChatMessage();
        msg.text = mChatEditText.getText().toString();
        ((LoungeActivity) getActivity()).getLounge().sendChatMessage(msg);
        mChatEditText.requestFocusFromTouch();
        mChatEditText.setText("");
        onChatMessageRecieved(msg);
    }

    @Override
    public void onStop() {
        ((LoungeActivity) getActivity()).getLounge().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d("THIS", "on destroy view chat fragment");
        super.onDestroyView();
    }
}
