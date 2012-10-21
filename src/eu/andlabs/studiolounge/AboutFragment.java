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

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import eu.andlabs.studiolounge.gcp.Lounge;
import eu.andlabs.studiolounge.gcp.Lounge.ChatListener;
import eu.andlabs.studiolounge.gcp.Lounge.ChatMessage;

public class AboutFragment extends Fragment implements ChatListener, OnClickListener {
    ArrayList<ChatMessage> mConversation = new ArrayList<ChatMessage>();
    private EditText mChatEditText;

    static AboutFragment newInstance(int num) {
        AboutFragment f = new AboutFragment();
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
        ((LoungeActivity)getActivity()).mLounge.register(this);
        super.onStart();
    }

    @Override
    public View onCreateView(final LayoutInflater infl, ViewGroup p, Bundle b) {
        Log.d("THIS", "on create view");
        final View stats = infl.inflate(R.layout.fragment_about, p, false);
       
        return stats;
    }

    @Override
    public void onChatMessageRecieved(ChatMessage msg) {
      
    }

    @Override
    public void onClick(View v) {
        ChatMessage msg = new ChatMessage();
        msg.text = mChatEditText.getText().toString();
        ((LoungeActivity)getActivity()).mLounge.sendChatMessage(msg);
        mChatEditText.requestFocusFromTouch();
        mChatEditText.setText("");
        onChatMessageRecieved(msg);
    }

    @Override
    public void onStop() {
        ((LoungeActivity)getActivity()).mLounge.unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d("THIS", "on destroy view chat fragment");
        super.onDestroyView();
    }
}
