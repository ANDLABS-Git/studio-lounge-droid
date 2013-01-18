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
package eu.andlabs.studiolounge.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import android.util.Log;
import eu.andlabs.studiolounge.gcp.CacheProvider;

public class CrashProviderTest extends ProviderTestCase2<CacheProvider> {

    private static final String TAG = "TEST";
    private MockContentResolver mRes;

    public CrashProviderTest() {
        super(CacheProvider.class, "com.lounge");
        Log.d(TAG, "HI");
    }

    // CRASH TEST DUMMIES 
    ContentValues anyName = new ContentValues();
    ContentValues ananDa = new ContentValues();
    ContentValues lukas = new ContentValues();
    ContentValues worms = new ContentValues();
    ContentValues molecool = new ContentValues();
    ContentValues tumblPanda = new ContentValues();
    ContentValues gravityWins = new ContentValues();
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        anyName.put("name", "Anyname");
        ananDa.put("name", "Ananda");
        lukas.put("name", "Lukas");
        worms.put("name", "Worms");
        molecool.put("name", "Mol3cool");
        tumblPanda.put("name", "Humble Pan Da");
        gravityWins.put("name", "Gravity Wins");
        mRes = getMockContentResolver();
    }
    
    
    
    // #####   S C E N A R I O   #####
    
    private void insertTestDummies() {
        // crash test players
        mRes.insert(Uri.parse("content://com.lounge/players"), anyName); // 1
        mRes.insert(Uri.parse("content://com.lounge/players"), ananDa); // 2
        mRes.insert(Uri.parse("content://com.lounge/players"), lukas); // 3
        
        // crash test games
        mRes.insert(Uri.parse("content://com.lounge/games"), worms); // 1
        mRes.insert(Uri.parse("content://com.lounge/games"), molecool); // 2
        mRes.insert(Uri.parse("content://com.lounge/games"), tumblPanda); // 3
        mRes.insert(Uri.parse("content://com.lounge/games"), gravityWins); // 4
        
        // test scenario gaming
        mRes.insert(Uri.parse("content://com.lounge/games/2/instances"), anyName); // host Mol3cool
        mRes.insert(Uri.parse("content://com.lounge/games/2/instances/1/players"), ananDa); // join
        
        mRes.insert(Uri.parse("content://com.lounge/games/2/instances"), lukas); // host Mol3cool
        mRes.insert(Uri.parse("content://com.lounge/games/2/instances/1/players"), anyName); // join
        
        mRes.insert(Uri.parse("content://com.lounge/games/3/instances"), ananDa); // host Panda
        mRes.insert(Uri.parse("content://com.lounge/games/3/instances/1/players"), anyName); // join
        mRes.insert(Uri.parse("content://com.lounge/games/3/instances/1/players"), lukas); // join
        
        mRes.insert(Uri.parse("content://com.lounge/games/3/instances"), lukas); // host Panda
        mRes.insert(Uri.parse("content://com.lounge/games/3/instances/2/players"), ananDa); // join
        
        mRes.insert(Uri.parse("content://com.lounge/games/4/instances"), lukas); // host Gravity
        mRes.insert(Uri.parse("content://com.lounge/games/4/instances/2/players"), anyName); // join
    }


    public void testLobbyQuery() {
        
        insertTestDummies();
        
        // GET A LIST OF GAMES FOR Ananda'S LOBBY
        Cursor games = mRes.query(Uri.parse("content://com.lounge/games?player=Ananda"), null, null, null, null);
        assertEquals("There should be 4 games <- Mol3cool x2 and no Worms", 4, games.getCount());
        
        // FIRST TWO GAMES WHERE Ananda IS INVOLVED
        games.moveToFirst();
        assertEquals("TumblePanda should be first <- Ananda hosted one and joined one", "TumblePanda", games.getString(1));
        assertEquals("TumblePanda should show two game instances to expand", 1, games.getInt(2));
        assertEquals("TumblePanda should have a content uri for its instances",  // EXPAND
                "content://com.lounge/games/3/instances?player=Ananda", games.getString(3));
        // expand TumblePanda
        Cursor panda_games = mRes.query(Uri.parse(games.getString(3)), null, null, null, null);
        assertEquals("TumblePanda should have two instances for Ananda", 2, panda_games.getCount());
        panda_games.moveToFirst();
        assertEquals("Ananda's instance should have three players", "Ananda, Anyname, Lukas", panda_games.getString(1));
        panda_games.moveToNext();
        assertEquals("Lukas's instance should have two players", "Lukas, Ananda", panda_games.getString(1));
        
        games.moveToNext();
        assertEquals("Mol3cool should be second <- Ananda joined one", "Mol3cool", games.getString(1));
        assertEquals("Mol3cool should show 1 game instance to expand", 1, games.getInt(2));
        assertEquals("Mol3cool should have a content uri for its instances",  // EXPAND
                "content://com.lounge/games/2/instances?player=Ananda", games.getString(3));
        Cursor molecool_games = mRes.query(Uri.parse(games.getString(3)), null, null, null, null);
        assertEquals("Mol3cool should have 1 instance for Ananda", 1, molecool_games.getCount());
        molecool_games.moveToFirst();
        assertEquals("Anyname's instance should have two players", "Anyname, Ananda", molecool_games.getString(1)); 

        // THEN TWO GAMES WITH INSTANCES BY OTHER PLAYERS
        games.moveToNext();
        assertEquals("Gravity should be third <- Someone else hosted one instance", "Gravity Wins", games.getString(1));
        assertEquals("Gravity should show one game instance to expand", 1, games.getInt(2));
        assertEquals("Gravity should have a content uri for its instances",  // EXPAND
                "content://com.lounge/games/4/instances", games.getString(3));
        Cursor gravity_games = mRes.query(Uri.parse(games.getString(3)), null, null, null, null);
        assertEquals("Gravity should have one instance", 1, gravity_games.getCount());
        gravity_games.moveToFirst();
        assertEquals("Lukas's instance should have two players", "Lukas, Ananda", panda_games.getString(1));

        games.moveToNext();
        assertEquals("Mol3cool should be again <- this time without Ananda", "Mol3cool", games.getString(1));
        assertEquals("Mol3cool should show one game instance to expand", 1, games.getInt(2));
        assertEquals("Mol3cool should have a content uri for its instances",  // EXPAND
                "content://com.lounge/games/2/instances", games.getString(3));
        molecool_games = mRes.query(Uri.parse(games.getString(3)), null, null, null, null);
        assertEquals("Mol3cool should have one instance", 1, molecool_games.getCount());
        molecool_games.moveToFirst();
        assertEquals("Lukas's instance should have two players", "Lukas, Anyname", panda_games.getString(1));
    }
}
