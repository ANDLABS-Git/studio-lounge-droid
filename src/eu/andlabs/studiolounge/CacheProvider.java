/*
 * Copyright (C) 2012, 2013 by it's authors. Some rights reserved.
 *
 * Licensed under the Apache License, Versio
import eu.andlabs.studiolounge.util.Utils;
n 2.0 (the "License");
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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class CacheProvider extends ContentProvider {

    private static final String TAG = "Lounge";

    static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, "lounge.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE chat (_id INT PRIMARY KEY, sender TEXT, time INT, msg TEXT);");
            
            db.execSQL("CREATE TABLE games (_id INT PRIMARY KEY, name TEXT, pkgId TEXT, installed INT);");
            
            db.execSQL("CREATE TABLE matches (_id INT PRIMARY KEY, guid TEXT, game TEXT, activePlayer TEXT);");

            db.execSQL("CREATE TABLE participation (_id INT PRIMARY KEY, matchId TEXT, playerId TEXT, role TEXT, activeTurn INT);");
            
            db.execSQL("CREATE TABLE players (_id INT PRIMARY KEY, playerId TEXT, status TEXT);");
            
            db.execSQL("CREATE TABLE msges (_id INT PRIMARY KEY, matchId TEXT, sender TEXT, msg TEXT);");
    
            Log.d(TAG, "lounge DB CREATED");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    }

    private UriMatcher uriMatcher;
    private static final int HOST = 0;
    private static final int CHAT = 1;
    private static final int GAMES = 2;
    private static final int MATCHES = 3;
    private static final int PLAYERS = 4;
    private static final int MSGES = 5;
    private static final int JOIN = 6;

    private DatabaseHelper db;



    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(0);
        uriMatcher.addURI("foo.lounge", "chat", CHAT);
        uriMatcher.addURI("foo.lounge", "games", GAMES);
        uriMatcher.addURI("foo.lounge", "host/games", HOST);
        uriMatcher.addURI("foo.lounge", "games/*/matches", MATCHES);
        uriMatcher.addURI("foo.lounge", "players", PLAYERS);
        uriMatcher.addURI("foo.lounge", "matches/*/players", JOIN);
        uriMatcher.addURI("foo.lounge", "matches/*/msges", MSGES);
        db = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriResult = uriMatcher.match(uri);
        switch (uriResult) {
        case CHAT:
            db.getWritableDatabase().insert("chat", null, values);
            break;
        case GAMES:
            db.getWritableDatabase().insert("games", null, values);
            break;
        case MATCHES:
            ContentValues match = new ContentValues();
            match.put("guid", values.getAsString("guid"));
            match.put("game", uri.getPathSegments().get(1));
            db.getWritableDatabase().insert("matches", null, match);
            ContentValues participation = new ContentValues();
            participation.put("matchId", values.getAsString("guid"));
            participation.put("playerId", values.getAsString("host"));
            participation.put("role", "host");
            db.getWritableDatabase().insert("participation", null, participation);
            ContentValues host = new ContentValues();
            host.put("playerId", values.getAsString("playerId"));
            db.getWritableDatabase().insert("players", null, host);
            break;
        case PLAYERS:
            db.getWritableDatabase().insert("players", null, values);
            break;
        case MSGES:
            ContentValues next = new ContentValues();
            next.put("activePlayer", values.getAsString("next"));
            db.getWritableDatabase().update("matches", next, "guid="+values.getAsString("match"), null);
            values.remove("next");
            db.getWritableDatabase().insert("msges", null, values);
            break;
        case JOIN:
            
            values.put("matchId", uri.getPathSegments().get(1));
            values.put("role", "join");
            db.getWritableDatabase().insert("participation", null, values);
            break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result = null;
        switch (uriMatcher.match(uri)) {
        case CHAT:
            result = db.getReadableDatabase().query("chat", null, null, null, null, null, null);
            break;
        case HOST:
            result = db.getReadableDatabase().query("games", null, null, null, null, null, "name ASC");
            break;
        case GAMES:
            result = db.getReadableDatabase().rawQuery(
                    "SELECT _id, name, pkgId, SUM(involved), COUNT(guid) FROM ( " +
                        "SELECT games._id, games.name, games.pkgId, matches.guid, " +
                            " SUM(players.playerId='Anyname') AS involved FROM participation" + 
                            " JOIN games ON matches.game=games.pkgId" +
                            " JOIN players ON participation.playerId=players.playerId" +
                            " JOIN matches ON participation.matchId=matches.guid" +
                        " GROUP BY matches.guid" +
                    ") GROUP BY name, involved" +
                    " ORDER BY involved DESC, name DESC", null);
            break;
        case MATCHES:
            result = db.getReadableDatabase().rawQuery(
                        "SELECT matches._id, matches.guid, " +
                        "SUM(players.playerId='Anyname') AS involved FROM participation" + 
                            " JOIN players ON participation.playerId=players.playerId" +
                            " JOIN matches ON participation.matchId=matches.guid" +
                        " WHERE matches.game='" + uri.getPathSegments().get(1) + "'" +
                        " GROUP BY matches.guid" +
                        " HAVING " + (uri.getQueryParameter("playerId") != null ?
                        " involved=1" : "involved=0"), null);
            break;
        }
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
