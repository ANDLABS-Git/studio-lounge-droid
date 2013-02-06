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
package eu.andlabs.studiolounge;

import android.content.ContentProvider;
import android.content.ContentUris;
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
            db.execSQL("CREATE TABLE players (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " name TEXT" +
                        ");");
            db.execSQL("CREATE TABLE chat (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " player VARCHAR," +
                        " time BIGINT," +
                        " msg TEXT" +
                    ");");
            db.execSQL("CREATE TABLE games (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " name VARCHAR," +
                    " pkgId VARCHAR," +
                    " installed INTEGER" +
                    ");");
            Log.d(TAG, "lounge DB CREATED");
            db.execSQL("INSERT INTO players VALUES (1, 'Anyname');");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    }

    private UriMatcher uriMatcher;
    private static final int CHAT = 1;
    private static final int GAMES = 2;

    private DatabaseHelper db;



    @Override
    public boolean onCreate() {
        db = new DatabaseHelper(getContext());
        uriMatcher = new UriMatcher(0);
        uriMatcher.addURI("foo.lounge", "chat", CHAT);
        uriMatcher.addURI("foo.lounge", "games", GAMES);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
        case CHAT:
            db.getWritableDatabase().insert("chat", null, values);
            break;
        case GAMES:
            db.getWritableDatabase().insert("games", null, values);
            break;
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
        case CHAT:
            return db.getReadableDatabase().query("chat", null, null, null, null, null, null);
        case GAMES:
            return db.getReadableDatabase().query("games", null, null, null, null, null, "name");
        }
        return null;
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
