package eu.andlabs.studiolounge;

import java.net.MalformedURLException;

import org.json.JSONObject;

import eu.andlabs.studiolounge.GameCommunicationService.ChatSession;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GameCommunicationService extends Service {

    protected SocketIO mSocketIO;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mSocketIO = new SocketIO();
        toast("starting Communication Service");
    }
    
    private void toast(final Object ding) {
        mHandler.post(new Runnable() {
            
            @Override
            public void run() {
                Toast.makeText(GameCommunicationService.this, ding.toString(),
                        1000).show();
            }
        });
    }
    
    public void joinLounge(final ChatSession session) {
        try {
            mSocketIO.connect("http://happylog.jit.su:7777", new IOCallback() {
                
                @Override
                public void onConnect() { // auto login
                    toast("connected to game server!"); 
                    mSocketIO.emit("Hi", "I am ");
                }
                
                @Override
                public void onMessage(String text, IOAcknowledge ack) {
                    // TODO parse text and notify session about chatter
                }
                
                @Override
                public void on(String type, IOAcknowledge ack, Object... data) {
                    Log.d("SocketIO", type + ":    " + data);
                    
                    if (type.equals("Welcome")) { // notify GUI
                        mHandler.post(new Runnable() {
                            
                            @Override
                            public void run() {
                                session.onJoin(session.name);
                            }
                        });
                    }
                }
                
                @Override
                public void onDisconnect() { toast("lost the game server."); }
                
                @Override
                public void onError(SocketIOException error) { toast(error); }
                
                @Override
                public void onMessage(JSONObject json, IOAcknowledge ack) { }
            });
        } catch (Exception e) {
            session.onChat("SocketIO ERROR: " + e.toString());
        }
    }


    // interface for Activity
    public abstract class ChatSession {
        
        private String name;

        public ChatSession(String name) {
            this.name = name;
        }
        
        /**
         * post some happy words into the lounge
         * @param message the happy text to tell
         */
        public void chat(String message) {
            if (mSocketIO.isConnected()) {
                mSocketIO.send(message);
            }
        }
        
        /**
         * is called after every successful login
         * @param name  the player who has joined
         */
        public abstract void onJoin(String name);

        /**
         * is called when a chat message arrives
         * @param msg  the content of the message
         */
        public abstract void onChat(String msg);
    }







    // warum muss man sowas hier eigentlich schreiben?
    private final IBinder mBinder = new LocalBinder();
    
    @Override
    public IBinder onBind(Intent intent) { return mBinder; }
    
    public class LocalBinder extends Binder {
        GameCommunicationService getService() {
            return GameCommunicationService.this;
        }
    }

}
