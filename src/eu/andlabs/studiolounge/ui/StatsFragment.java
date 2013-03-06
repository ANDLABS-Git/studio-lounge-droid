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

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.andlabs.studiolounge.R;

public class StatsFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private TextView mPlayerOnl;
    private TextView mOpenGames;
    private TextView mMsgesSend;

    @Override
    public View onCreateView(final LayoutInflater lI, ViewGroup p, Bundle b) {
        return lI.inflate(R.layout.fragment_stats, p, false);
    }
    
    @Override
    public void onViewCreated(View layout, Bundle savedInstanceState) {
        super.onViewCreated(layout, savedInstanceState);
        
        mPlayerOnl = (TextView) layout.findViewById(R.id.player);
        mOpenGames = (TextView) layout.findViewById(R.id.games);
        mMsgesSend = (TextView) layout.findViewById(R.id.msges);
        
//        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri uri = Uri.parse("content://foo.lounge/stats");
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> l, Cursor stats) {
        stats.moveToFirst();
        mPlayerOnl.setText(stats.getString(1));
        mOpenGames.setText(stats.getString(1));
        mMsgesSend.setText(stats.getString(1));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
        
    }


}
