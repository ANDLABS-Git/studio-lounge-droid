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

import junit.framework.Assert;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import android.util.Log;
import eu.andlabs.studiolounge.gcp.CacheProvider;

public class CrashProviderTest_LJ extends ProviderTestCase2<CacheProvider> {

    private static final String TAG = "TEST";
    private MockContentResolver mRes;

    public CrashProviderTest_LJ() {
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
        worms.put("installed",false);
        worms.put("pkg","de.worms");
        
        molecool.put("name", "Molecool");
        molecool.put("installed",true);
        molecool.put("pkg","de.mole");
        
        tumblPanda.put("name", "Panda");
        tumblPanda.put("installed",true)
        tumblPanda.put("pkg","de.panda");
        
        gravityWins.put("name", "Gravity Wins");
        gravityWins.put("pkg", "de.gravity");
        gravityWins.put("installed", true);
        
        mRes = getMockContentResolver();
    }
    
    
    
    // #####   S C E N A R I O   #####
    
    
    private void testHostDrawer(){
    	  mRes.insert(Uri.parse("content://com.lounge/games"), worms); // 1
          mRes.insert(Uri.parse("content://com.lounge/games"), molecool); // 2
          mRes.insert(Uri.parse("content://com.lounge/games"), tumblPanda); // 3
          mRes.insert(Uri.parse("content://com.lounge/games"), gravityWins); // 4
          
          Cursor loungeGames=mRes.query("content://com.lounge/games", projection, selection, selectionArgs, sortOrder);
          assertEquals("four games in list", 4,loungeGames.getCount());
          loungeGames.moveToLast();
          assertEquals("worms", loungeGames.getString(1));
          assertEquals("de.worms", loungeGames.getString(2));
          assertEquals(0, loungeGames.getShort(3)); //worms is not installed
          loungeGames.moveToFirst();
          assertEquals(1, loungeGames.getShort(3)); //molecule is installed
        	  
    }
    
    private void testGameLobbyApi() {
        // crash test players
        mRes.insert(Uri.parse("content://com.lounge/players"), anyName); // 1
        mRes.insert(Uri.parse("content://com.lounge/players"), ananDa); // 2
        mRes.insert(Uri.parse("content://com.lounge/players"), lukas); // 3
        
        // crash test games
        mRes.insert(Uri.parse("content://com.lounge/games"), worms); // 1
        mRes.insert(Uri.parse("content://com.lounge/games"), molecool); // 2
        mRes.insert(Uri.parse("content://com.lounge/games"), tumblPanda); // 3
        mRes.insert(Uri.parse("content://com.lounge/games"), gravityWins); // 4
        
        int matchID_1=1;
        int matchID_2=2;
        int matchID_3=3;
        int matchID_4=4;
        int matchID_5=5;
        
        
//        Upper List => 4 Use cases     
//			
//        
//        
//        
//        
        // test scenario running games of this user
        mRes.insert(Uri.parse("content://com.lounge/games/de.panda/matches/"), anyName, matchID_5); //host Mol3cool
        mRes.insert(Uri.parse("content://com.lounge/games/de.panda/matches/+"matchID_1+"/players"), ananDa); // join
        mRes.update("content://com.lounge/games/de.panda/matches/+"matchID_1+"/","activePlayer=anyName", null, null);
        
        mRes.insert(Uri.parse("content://com.lounge/games/de.panda/matches/"), lukas, matchID_4); //host Mol3cool
        mRes.insert(Uri.parse("content://com.lounge/games/de.panda/matches/+"matchID_4+"/players"), anyName); // join
        //no update on activePlayer yet for this game which means its not running yet.
        
        
        // Question:
        // Should the running games be also under content://com.lounge/games/de.panda/matches/ 
        // and have a running flag?
        
        
        
        
        
        
        
        
        // test scenario hosted games by others 
        // 2 x Molecule games with each 1 player joined
        // 1 x worms game with 2 player joined
        // 1 x gravity game with no joined players
        
        mRes.insert(Uri.parse("content://com.lounge/games/de.mole/matches"), anyName, matchID_1); // host Mol3cool
        mRes.insert(Uri.parse("content://com.lounge/games/de.mole/matches/+"matchID_1+"/players"), ananDa); // join
        
        mRes.insert(Uri.parse("content://com.lounge/games/de.mole/matches"), ananDa, matchID_2); // host Mol3cool
        mRes.insert(Uri.parse("content://com.lounge/games/de.mole/matches/"+matchID_2+"/players"), lukas); // join
        Cursor games = mRes.query(Uri.parse("content://com.lounge/games?player=anyName"), null, "username=", lukas, null);
        assertEquals("There should be 3 games, 2 games in the upper list and one game in the lower",3, games.getCount()); 
        games.moveToFirst();
        assertEquals("de.panda", games.getString(1));
        assertEquals("Panda", games.getString(2));
        assertEquals(1, games.getShort(3)); //selected player is involved
        
        
        
        Cursor in_pandaMatches=mRes.query("content://com.lounge/games/de.panda/matches?player=anyName", projection, selection, selectionArgs, sortOrder)
        assertEquals("two children for panda in the upper list", 2,in_pandaMatches.getCount())
        in_pandaMatches.moveToFirst();
        assertEquals(matchID_5, in_pandaMatches.getString(1)); 
        assertEquals("anyName",in_pandaMatches.getString(2)); //host
        assertEquals("ananDa", in_pandaMatches.getString(3)); // Involved Players
        assertEquals("anyName", in_pandaMatches.getString(4)); //active player
        
        in_pandaMatches.moveToNext();
        assertEquals(matchID_4, in_pandaMatches.getString(1)); 
        assertEquals("lukas",in_pandaMatches.getString(2)); //host
        assertEquals("anyName", in_pandaMatches.getString(3)); // Involved Players
        assertEquals("", in_pandaMatches.getString(4)); //active player => no active player = not running
       
        games.moveToNext(); //move to next game -> Molecule
        assertEquals("de.mole", games.getString(1));
        assertEquals("Molecool", games.getString(2));
        assertEquals(1, games.getShort(3)); //selected player is involved
        Cursor in_MoleMatches=mRes.query("content://com.lounge/games/de.mole/matches?player=anyName", projection, selection, selectionArgs, sortOrder)
        assertEquals("one child for mole in the upper list", 1,in_MoleMatches.getCount())
        
        
        games.moveToNext();
        assertEquals(0, games.getShort(3)); //selected player is NOT involved
        Cursor out_MoleMatches=mRes.query("content://com.lounge/games/de.mole/matches?notplayer=anyName", projection, selection, selectionArgs, sortOrder)
        assertEquals("one child for mole in the lower list", 1,out_MoleMatches.getCount())
        out_MoleMatches.moveToFirst();
        assertEquals(matchID_4, out_MoleMatches.getString(1)); 
        assertEquals("ananDa",out_MoleMatches.getString(2)); //host
        assertEquals("lukas", out_MoleMatches.getString(3)); // Involved Players
        mRes.update("content://com.lounge/games/de.panda/matches/+"matchID_1+"/","activePlayer=lukas", null, null);
        Cursor out_MoleMatches=mRes.query("content://com.lounge/games/de.mole/matches?notplayer=anyName", projection, selection, selectionArgs, sortOrder)
        assertEquals("No child for mole in the lower list", 0,out_MoleMatches.getCount())
        
        mRes.update("content://com.lounge/games/de.panda/matches/+"matchID_1+"/","activePlayer=ananDa", null, null);
        //If the player(anyname) is none of the involved players and the active player is set which means 
        //the game started, this match should not be displayed on the screen anymore.
        
        
        mRes.insert(Uri.parse("content://com.lounge/games/de.worms/matches"), ananDa, matchID_3); // host worms
        mRes.insert(Uri.parse("content://com.lounge/games/de.worms/matches/"+matchID_3+"/players"), lukas); // join
        mRes.insert(Uri.parse("content://com.lounge/games/de.worms/matches/"+matchID_3+"/players"), anyName); // join
        
        
        
        mRes.insert(Uri.parse("content://com.lounge/games/de.gravity/matches"), lukas, matchID_4); // host gravity, no joins
        
    }


    public void testLobbyQuery() {
//        
        insertTestDummies();
        
        // GET A LIST OF GAMES FOR Ananda'S LOBBY
        Cursor games = mRes.query(Uri.parse("content://com.lounge/games?player=lukas"), null, "username=", lukas, null);
        //With break entry for list seperator
        
        assertEquals("There should be 4 games, 1xpanda(running) 2xmole(hosted) 1xworms(hosted) 1xgravity(hosted)", 4, games.getCount());
        
        // FIRST TWO GAMES WHERE Ananda IS INVOLVED
        games.moveToFirst();
        assertEquals("Panda should be first because its in the running section <- Ananda is opponent", "Panda", games.getString(1));
        assertEquals("Panda should show one game instances to expand", 1, games.getInt(2));
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
//}
