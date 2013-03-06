package eu.andlabs.studiolounge;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;


//TODO: Remove 
public class TestDataHandler {

    private static final String LOUNGE_PROVIDER_URI = "content://foo.lounge";

    private Game wrms;
    private Game panda;
    private Game gravty;
    private Game molecul;
    private Player lukas;
    private Player ananDa;
    private Player anyName;
    public int INC = 42;
    private ContentResolver mRes;
    private Context context;
    
    public TestDataHandler(Context context) {
        this.context = context;
    }

    public void insertTestData() {

        mRes = this.context.getContentResolver();
        // first gather the troops
        lukas = new Player("Lukas");
        ananDa = new Player("Ananda");
        anyName = new Player("Anyname");

        wrms = new Game("Worms", "de.worms", false);
        panda = new Game("Pandararr", "de.panda", true);
        molecul = new Game("Molecoooool", "de.mole", true);
        gravty = new Game("Graffity Wins", "de.gravity", true);
        
     // crash test dummy games
        mRes.insert(Uri.parse(LOUNGE_PROVIDER_URI + "/games"), wrms.toContenValues());
        mRes.insert(Uri.parse(LOUNGE_PROVIDER_URI + "/games"), panda.toContenValues());
        mRes.insert(Uri.parse(LOUNGE_PROVIDER_URI + "/games"), molecul.toContenValues());
        mRes.insert(Uri.parse(LOUNGE_PROVIDER_URI + "/games"), gravty.toContenValues());
        // crash test dummy players
        mRes.insert(Uri.parse(LOUNGE_PROVIDER_URI + "/players"), anyName.toContenValues());
        mRes.insert(Uri.parse(LOUNGE_PROVIDER_URI + "/players"), ananDa.toContenValues());
        mRes.insert(Uri.parse(LOUNGE_PROVIDER_URI + "/players"), lukas.toContenValues());
        
        
        // here comes the STORY  (what happened before..)
        Match anynamesPanda = anyName.hosts(panda);
        ananDa.joins(anynamesPanda);
        anynamesPanda.receiveGameMsg(ananDa, anyName, "foo");
        
        Match lukasPanda = lukas.hosts(panda);
        anyName.joins(lukasPanda);
        // no activePlayer yet for this game -> means not running yet
        
        Match anynamesMole = anyName.hosts(molecul);
        ananDa.joins(anynamesMole);
        
        Match anandasMole = ananDa.hosts(molecul);
        lukas.joins(anandasMole);
        


    }

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
            cv.put("installed", installed ? 1 : 0);
            return cv;
        }
    }

    class Player {
        String name;

        public Player(String n) {
            name = n;
        }

        public ContentValues toContenValues() {
            ContentValues cv = new ContentValues();
            cv.put("player", name);
            return cv;
        }

        public Match hosts(Game game) {
            Match match = new Match();
            match.guid = "matchId++" + ++INC; // global unique match id comes
                                              // from server
            match.game = game.pkgId;
            match.host = name;

            mRes.insert(
                    Uri.parse(LOUNGE_PROVIDER_URI + "/games/" + game.pkgId
                            + "/matches"), match.toContenValues());
            return match;
        }

        public void joins(Match match) {
            match.players = name;
            mRes.insert(
                    Uri.parse(LOUNGE_PROVIDER_URI + "/matches/" + match.guid
                            + "/players"), toContenValues());
        }
    }

    class Match {
        String guid;
        String game;
        String host;
        String players;
        String activePlayer;

        public ContentValues toContenValues() {
            ContentValues cv = new ContentValues();
            cv.put("guid", guid);
            cv.put("host", host);
            return cv;
        }

        public void receiveGameMsg(Player sender, Player next, String msg) {
            activePlayer = next.name;
            ContentValues cv = new ContentValues();
            cv.put("sender", sender.name);
            cv.put("next", next.name);
            cv.put("msg", msg);
            mRes.insert(Uri.parse(LOUNGE_PROVIDER_URI + "/matches/" + guid
                    + "/msges"), cv);
        }
    }
}
