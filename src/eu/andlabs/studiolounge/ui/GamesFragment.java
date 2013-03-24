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

import eu.andlabs.studiolounge.R;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ExpandableListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GamesFragment extends ExpandableListFragment implements LoaderCallbacks<Cursor> {

    private SparseIntArray listPositions = new SparseIntArray();
    private static final String TAG = "Lounge";
    private static final int GAMES = 0;

    @Override
    public View onCreateView(final LayoutInflater lI, ViewGroup p, Bundle b) {
        return lI.inflate(R.layout.fragment_games, p, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setListAdapter(new CursorTreeAdapter(null, getActivity()) {

            @Override
            protected View newGroupView(Context ctx, Cursor c, boolean e, ViewGroup p) {
                return getLayoutInflater(null).inflate(R.layout.view_game_list_entry, p, false);
            }
            
            @Override
            protected void bindGroupView(View v, Context ctx, Cursor game, boolean isExpanded) {
                ((GameView) v).populate(game);
            }
            
            @Override
            protected Cursor getChildrenCursor(Cursor games) {
                int gameId = games.getInt(games.getColumnIndex(ContactsContract.Groups._ID));
                listPositions.put(gameId, games.getPosition());
//                getLoaderManager().initLoader(gameId, null, GamesFragment.this);
                return null;
            }
            
            @Override
            protected View newChildView(Context ctx, Cursor c, boolean l, ViewGroup p) {
                 View v= getLayoutInflater(null).inflate(R.layout.view_match_list_entry_2players, p, false);
                
                return v;
            }
            
            @Override
            protected void bindChildView(View v, Context ctx, Cursor gameInst, boolean isLastChild) {
                ((GameView) v).populate(gameInst);
            }
            
        });
//        getLoaderManager().initLoader(GAMES, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle b) {
        Log.d(TAG, "onCreateLoader for GAMES " + id);
        if (id == GAMES) {
            Uri uri = Uri.parse("content://foo.lounge/games");
            return new CursorLoader(getActivity(), uri, null, null, null, null);
        } else { // game instances
            Uri uri = Uri.parse("content://foo.lounge/games/"+id+"/instances");
            return new CursorLoader(getActivity(), uri, null, null, null, null);
        }
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished for GAMES " + loader.getId());
        if (loader.getId() == 0) {
            ((CursorTreeAdapter) getExpandableListAdapter()).setGroupCursor(data);
        } else { // game instances
            if (!data.isClosed()) {
                ((CursorTreeAdapter) getExpandableListAdapter())
                        .setChildrenCursor(listPositions.get(loader.getId()), data);
            }
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished() is about to be closed.
        Log.d(TAG, "onLoaderReset for GAMES " + loader.getId());
    }


    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
    }





    static class GameView extends RelativeLayout {

        TextView playerLbl1;
        TextView playerLbl2;
        
        View player1Beacon;
        View player2Beacon;

        public GameView(Context ctx, AttributeSet attrs) {
            super(ctx, attrs);
        }

        @Override
        protected void onFinishInflate() {
        	playerLbl1 = (TextView) findViewById(R.id.playerLbl1);
        	playerLbl2 = (TextView) findViewById(R.id.playerLbl2);
        	player1Beacon=findViewById(R.id.playerBeacon1);
        	player2Beacon=findViewById(R.id.playerBeacon2);
            super.onFinishInflate();
        }
        
        void populate(Cursor game) {
        	String[] players = game.getString(1).split("%%");
        	
        	switch (players.length) {
//			case 4:
//			case 3:
			case 2: playerLbl2.setText(players[1]);
			case 1: playerLbl1.setText(players[1]);
				
				break;

			default:
				break;
			}
        	playerLbl1.setText(game.getString(1));
        }
    }

    static class GameInstanceView extends GameView {
        
        public GameInstanceView(Context ctx, AttributeSet attrs) {
            super(ctx, attrs);
        }

        TextView players;

        @Override
        protected void onFinishInflate() {
            players = (TextView) findViewById(R.id.players);
            super.onFinishInflate();
        }
        
        void populate(Cursor game) {
            players.setText(game.getString(1));
        }
    }
}
