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
package eu.andlabs.studiolounge.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import eu.andlabs.studiolounge.R;

public class ChatFragment extends ListFragment 
    implements OnClickListener, OnKeyListener, LoaderCallbacks<Cursor> {
    
    private static final String TAG = "Lounge";
    private EditText mText;

    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater layout, ViewGroup p, Bundle b) {
        return layout.inflate(R.layout.chat, p, false);
    }

    @Override
    public void onViewCreated(View layout, Bundle savedInstanceState) {
        super.onViewCreated(layout, savedInstanceState);
        layout.findViewById(R.id.btn_send).setOnClickListener(this);
        mText = (EditText) layout.findViewById(R.id.msg_field);
        mText.setOnKeyListener(this);
        
        setListAdapter(new CursorAdapter(getActivity(), null) {
            
            @Override
            public View newView(Context ctx, Cursor msgs, ViewGroup parent) {
                return getLayoutInflater(null).inflate(R.layout.chat_list_entry, null);
            }
            
            @Override
            public void bindView(View listItem, Context ctx, Cursor msges) {
                final ChatMsgView msg = (ChatMsgView) listItem;
                msg.player.setText(msges.getString(0));
                msg.text.setText(msges.getString(1));
                msg.text.setText(msges.getString(0));
            }
        });
        
        Loader loader = getLoaderManager().getLoader(0);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(0, null, this);
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
        Uri uri = Uri.parse("content://foo.lounge/chat/msges");
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor msges) {
        ((CursorAdapter)getListAdapter()).swapCursor(msges);
    }
    
    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onClick(View button) {
        // SEND mMsg.getText().toString();
        mText.requestFocusFromTouch();
        mText.setText("");
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            onClick(null);
            return true;
        }
        return false;
    }





    static class ChatMsgView extends RelativeLayout {

        private TextView player;
        private TextView text;
        private TextView time;

        public ChatMsgView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        
        @Override
        protected void onFinishInflate() {
            super.onFinishInflate();
            player = (TextView) findViewById(R.id.player);
            text = (TextView) findViewById(R.id.msg_text);
            time = (TextView) findViewById(R.id.timestamp);
        }
    }

    
}
