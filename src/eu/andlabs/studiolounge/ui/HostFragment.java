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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import eu.andlabs.studiolounge.R;
import eu.andlabs.studiolounge.ui.GamesFragment.GameView;

public class HostFragment extends ListFragment 
    implements OnClickListener, LoaderCallbacks<Cursor> {
    
    private ImageView pulseBeacon;
    private ImageView staticBeacon;

    @Override
    public View onCreateView(final LayoutInflater layout, ViewGroup p, Bundle b) {
        return layout.inflate(R.layout.fragment_host, p, false);
    }

    @Override
    public void onViewCreated(View layout, Bundle savedInstanceState) {
        super.onViewCreated(layout, savedInstanceState);
        
        layout.findViewById(R.id.btn_host).setOnClickListener(this);
        layout.findViewById(R.id.btn_practise).setOnClickListener(this);
        
        pulseBeacon = (ImageView) layout.findViewById(R.id.ic_lobby_host_pulse);
        staticBeacon = (ImageView) layout.findViewById(R.id.ic_lobby_host_static_pulse);
        
        setListAdapter(new CursorAdapter(getActivity(), null, true) {
            
            @Override
            public View newView(Context ctx, Cursor msgs, ViewGroup parent) {
                return getLayoutInflater(null).inflate(R.layout.view_game_list_entry, null);
            }
            
            @Override
            public void bindView(View listItem, Context ctx, Cursor msges) {
                final GameView msg = (GameView) listItem;
                msg.name.setText(msges.getString(1));
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
        Uri uri = Uri.parse("content://foo.lounge/games");
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor msges) {
        ((CursorAdapter)getListAdapter()).swapCursor(msges);
    }
    
    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }
    
    @Override
    public void onClick(View v) {
        long id = getListView().getSelectedItemId();
        if (id != 0) { // haz package
            if (v.getId() == R.id.btn_host) {
                animateHostMode();
            } else if (v.getId() == R.id.btn_practise) {
            }
        } else {
            Toast.makeText(getActivity(), "Please select a game", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void animateHostMode() {
        pulseBeacon.setVisibility(View.VISIBLE);
        staticBeacon.setVisibility(View.INVISIBLE);

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.pulse);
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

    private void stopAnimatingHostMode() {
        // if (scaleDown != null) {
        // scaleDown.cancel();
        // }
        pulseBeacon.setVisibility(View.INVISIBLE);
        staticBeacon.setVisibility(View.VISIBLE);
    }


}
