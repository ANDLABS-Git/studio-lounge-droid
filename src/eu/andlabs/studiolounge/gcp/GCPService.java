package eu.andlabs.studiolounge.gcp;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;


public class GCPService extends Service {

    private String mName;
    private Handler mHandler;
    private SocketIO mSocketIO;
    private Messenger mChatGame;

    public static final int JOIN = 1;
    public static final int CHAT = 2;
    public static final int LEAVE = 3;
    
    @Override
    public void onCreate() {
        super.onCreate();
        mName = "Lucky Luke";
        mHandler = new Handler();
        mSocketIO = new SocketIO();
        
        log("starting GCP Service");
        try {
            mSocketIO.connect("http://happylog.jit.su:80", new IOCallback() {
                
                @Override
                public void onConnect() { // auto login
                    log("connected to GCP game server!"); 
                    mSocketIO.emit("Hi", "I am " + mName);
                }
                
                @Override
                public void onMessage(String text, IOAcknowledge ack) {
                    dispatchMessage(CHAT, text);
                }
                
                @Override
                public void on(String type, IOAcknowledge ack, Object... data) {                    
                    log("incoming message:" + type + " --- " + data);
                    if (type.equals("Welcome")) {
                        dispatchMessage(JOIN, data[0].toString());
                    } else {
                        dispatchMessage(CHAT, "BAD protocol message: " + type);
                    }
                }
                
                @Override
                public void onMessage(JSONObject json, IOAcknowledge ack) { }
                
                @Override
                public void onDisconnect() { log("lost game server."); }
                
                @Override
                public void onError(SocketIOException error) { log(error); }
            });
        } catch (Exception e) {
            dispatchMessage(CHAT, "GCP Service Error:   " + e.toString());
        }
    }
    
    
    // bind game app(s)
    @Override
    public IBinder onBind(Intent intent) {
        mChatGame = (Messenger) intent.getParcelableExtra("messenger");
        return mMessenger.getBinder();
    }
    
    // send android system IPC message to game apps
    private void dispatchMessage(int what, String thing) {
        try {
            mChatGame.send(Message.obtain(mHandler, what, thing));
        } catch (RemoteException e) {
            log("Error: " + e.toString());
        } 
    }
    
    // receive android system IPC messages from game apps
    final Messenger mMessenger = new Messenger(new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case CHAT:
                if (mSocketIO.isConnected()) {
                    mSocketIO.send(msg.obj.toString());
                }
                break;
            }
        }});



    private void log(final Object ding) {
        mHandler.post(new Runnable() {
            
            @Override
            public void run() {
                Toast.makeText(GCPService.this, ding.toString(),
                        1000).show();
            }
        });
        Log.d("GameCommunicationsProtocol-Service", ding.toString());
    }
}
