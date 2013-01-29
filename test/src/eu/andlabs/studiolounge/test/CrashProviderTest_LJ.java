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

import eu.andlabs.studiolounge.CacheProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

public class CrashProviderTest_LJ extends ProviderTestCase2<CacheProvider> {

    private static final String TAG = "TEST";
    private MockContentResolver mRes;

    public CrashProviderTest_LJ() {
        super(CacheProvider.class, "com.lounge");
    }

// CRASH TEST DUMMIES
    private Game wrms;
    private Game panda;
    private Game gravty;
    private Game molecul;
    private Player lukas;
    private Player ananDa;
    private Player anyName;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mRes = getMockContentResolver();
        // first gather the troops
        lukas = new Player("Lukas");
        ananDa = new Player("Ananda");
        anyName = new Player("Anyname");
        
        wrms = new Game("Worms", "de.worms", false);
        panda = new Game("Pandararr", "de.panda", true);
        molecul = new Game("Molecoooool", "de.mole", true);
        gravty = new Game("Graffity Wins", "de.gravity", true);
    }





    // ########################
    // ######   Level 1  ######
    // ########################
    
    public void testChat() {
        ContentValues msg = new ContentValues();
        msg.put("player", "Ananda");
        msg.put("text", "Hi, what's up?");
        long now = System.currentTimeMillis();
        msg.put("time", now);
        
        mRes.insert(Uri.parse("content://foo.lounge/chat"), msg); // 1
        mRes.insert(Uri.parse("content://foo.lounge/chat"), msg); // 2
        mRes.insert(Uri.parse("content://foo.lounge/chat"), msg); // 3
        
        Cursor msges = mRes.query(Uri.parse("content://foo.lounge/chat"), null, null, null, null);
        assertEquals("There should be three chat messages", 3, msges.getCount());
        msges.moveToFirst();
        assertEquals("Ananda", msges.getString(1));
        assertEquals("Hi, what's up?", msges.getString(2));
        assertEquals(now, msges.getLong(3));
    }





    // ########################
    // ######   Level 2  ######
    // ########################
    
    public void testStats() {
        ContentValues stat;
        stat = new ContentValues();
        stat.put("total_msges", 3);
        mRes.update(Uri.parse("content://foo.lounge/stats"), stat, null, null);
        stat.put("total_msges", 42);
        mRes.update(Uri.parse("content://foo.lounge/stats"), stat, null, null);

        stat = new ContentValues();
        stat.put("games_played", 5);
        mRes.update(Uri.parse("content://foo.lounge/stats"), stat, null, null);
        stat.put("games_played", 7);
        mRes.update(Uri.parse("content://foo.lounge/stats"), stat, null, null);
        
        stat = new ContentValues();
        stat.put("player_online", 42);
        mRes.update(Uri.parse("content://foo.lounge/stats"), stat, null, null);

        Cursor stats = mRes.query(Uri.parse("content://foo.lounge/stats"), null, null, null, null);
        assertEquals("There should always be only one set of stats", 1, stats.getCount());
        stats.moveToFirst();
        assertEquals("total messages should have been added", 45, stats.getInt(1));
        assertEquals("games played should have been added", 12, stats.getInt(2));
        assertEquals("player should be online", 42, stats.getInt(3));
    }





    // ########################
    // ######   Level 3  ######
    // ########################
    
    class Game {
        String name;
        String pkgId;
        boolean installed;
        
        public Game(String n, String p, boolean i) {
            name = n;
            pkgId = p;
            installed = i;
        }
        
        public ContentValues toContenValues() {
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            cv.put("pkgId", pkgId);
            cv.put("installed", installed);
            return cv;
        }
    }
    
    public void testHostDRAWER() {
          // crash test dummy games
          mRes.insert(Uri.parse("content://com.lounge/games"), wrms.toContenValues());
          mRes.insert(Uri.parse("content://com.lounge/games"), panda.toContenValues());
          mRes.insert(Uri.parse("content://com.lounge/games"), molecul.toContenValues());
          mRes.insert(Uri.parse("content://com.lounge/games"), gravty.toContenValues());
          
          Cursor games = mRes.query(Uri.parse("content://com.lounge/games"), null, null, null, null);
          assertEquals("four games in list", 4,games.getCount());

          games.moveToFirst();
          assertEquals(gravty, games); // sorted by alphabet G?
          assertEquals("Gravity is installed", 1, games.getShort(3));
          
          games.moveToLast();
          assertEquals(wrms, games);
          assertEquals("Worms is not installd", 0, games.getShort(3));
    }
    
    private void assertEquals(Game game, Cursor row) {
        assertEquals(game.name, row.getString(1));
        assertEquals(game.pkgId, row.getString(2));
        // TODO expect icon and promotion graphics...
    }





    // ########################
    // ######   Level 4  ######
    // ########################
    
    class Player {
        String name;
        
        public Player (String n) {
            name = n;
        }
        public ContentValues toContenValues() {
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            return cv;
        }
        public Match hosts(Game game) {
            Match match = new Match();
            match.game = game.pkgId;
            match.host = name;
            match.id = mRes.insert(
                    Uri.parse("content://com.lounge/games/" +game.pkgId+ "/matches"), match.toContenValues()
                    ).getLastPathSegment(); 
            return match;
        }
        public void joins(Match match) {
            match.players = name;
            mRes.insert(Uri.parse("content://com.lounge/games/" +match.game+ "/matches/" +match.id +"/players"), toContenValues());
        }
    }
    
    class Match {
        String id;
        String game;
        String host;
        String players;
        String activePlayer;
        
        public ContentValues toContenValues() {
            ContentValues cv = new ContentValues();
            cv.put("game", game);
            cv.put("host", host);
            cv.put("activePlayer", activePlayer);
            return cv;
        }
        
        public void setActivePlayer(Player player) {
            activePlayer = player.name;
            mRes.update(Uri.parse("content://com.lounge/games/" +game+ "/matches/"+id), toContenValues(), null, null);
        }
    }
    
    private void testGamesLobbyAPI() {
        // crash test dummy games
        mRes.insert(Uri.parse("content://com.lounge/games"), wrms.toContenValues());
        mRes.insert(Uri.parse("content://com.lounge/games"), panda.toContenValues());
        mRes.insert(Uri.parse("content://com.lounge/games"), molecul.toContenValues());
        mRes.insert(Uri.parse("content://com.lounge/games"), gravty.toContenValues());
        // crash test dummy players
        mRes.insert(Uri.parse("content://com.lounge/players"), anyName.toContenValues());
        mRes.insert(Uri.parse("content://com.lounge/players"), ananDa.toContenValues());
        mRes.insert(Uri.parse("content://com.lounge/players"), lukas.toContenValues());
        
        
        // here comes the STORY  (what happened before..)
        Match anynamesPanda = anyName.hosts(panda);
        ananDa.joins(anynamesPanda);
        anynamesPanda.setActivePlayer(anyName);
        
        Match lukasPanda = lukas.hosts(panda);
        anyName.joins(lukasPanda);
        // no activePlayer yet for this game -> means not running yet
        
        Match anynamesMole = anyName.hosts(molecul);
        ananDa.joins(anynamesMole);
        
        Match anandasMole = ananDa.hosts(molecul);
        lukas.joins(anandasMole);
        
        
        // get the fancy special sorted list of games to show in the lobby of Anyname
        Cursor games = mRes.query(Uri.parse("content://com.lounge/games?player=anyName"), null, null, null, null);
        assertEquals("Should be 3 games:  two games in the upper list and one game in the lower", 3, games.getCount()); 
        
        games.moveToFirst();
        assertEquals(panda, games);
        assertEquals("Anyname is involved", 1, games.getShort(3));
        
        Cursor pandaMatches_ForAnyname = mRes.query( // expandable list children 
                Uri.parse("content://com.lounge/games/de.panda/matches?player=anyName"), null, null, null, null);
        assertEquals("two matches for panda in the upper list", 2 ,pandaMatches_ForAnyname.getCount());
        pandaMatches_ForAnyname.moveToFirst();
        assertEquals(anynamesPanda, pandaMatches_ForAnyname);
        pandaMatches_ForAnyname.moveToNext();
        assertEquals(lukasPanda, pandaMatches_ForAnyname);
        
        games.moveToNext();
        assertEquals(molecul, games);
        assertEquals("Anyname is involved", 1, games.getShort(3));
        
        Cursor moleMatches_ForAnyname = mRes.query( // expandable list children
                Uri.parse("content://com.lounge/games/de.mole/matches?player=anyName"), null, null, null, null);
        assertEquals("one Molecule match in the upper list", 1 ,moleMatches_ForAnyname.getCount());
        moleMatches_ForAnyname.moveToFirst();
        assertEquals(anynamesMole, moleMatches_ForAnyname);
        
        games.moveToNext();
        assertEquals(molecul, games);
        assertEquals("Anyname is NOT involved", 0, games.getShort(3));
        
        Cursor moleMatches_WithoutAnyname = mRes.query( // expandable list children
                Uri.parse("content://com.lounge/games/de.mole/matches?notplayer=anyName"), null, null, null, null);
        assertEquals("one child for mole in the lower list", 1, moleMatches_WithoutAnyname.getCount());
        moleMatches_WithoutAnyname.moveToFirst();
        assertEquals(anandasMole, moleMatches_WithoutAnyname);
        
        
        anandasMole.setActivePlayer(lukas);
        // AGAIN get the matches where Anyname is NOT involved
        moleMatches_WithoutAnyname = mRes.query( // expandable list children
                Uri.parse("content://com.lounge/games/de.mole/matches?notplayer=anyName"), null, null, null, null);
        assertEquals("NO more second Molecool in the lower list", 0, moleMatches_WithoutAnyname.getCount()); // because Anyname cannot join anymore
        
        // AGAIN get the fancy special sorted list of games to show in the lobby of Anyname
        games = mRes.query(Uri.parse("content://com.lounge/games?player=anyName"), null, null, null, null);
        assertEquals("Should be two games in the upper list", 2, games.getCount()); 
    }

    private void assertEquals(Match match, Cursor row) {
        assertEquals(match.id, row.getInt(0)); 
        // TODO store server generated global match ID 
        assertEquals(match.host, row.getString(2));
        assertEquals(match.players, row.getString(3)); // Involved Players
        assertEquals(match.activePlayer, row.getString(4)); //active player
    }
 
}
