package eu.andlabs.studiolounge.gcp;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class Lounge implements ServiceConnection {

    public interface LobbyListener {
        
        /**
         * is called after every successful login
         * @param name  the player who has joined
         */
        public void onPlayerJoined(String player);
        
        /**
         * is called after every logout operation
         * @param name  the player who has left
         */
        public void onPlayerLeft(String player);
        
    }
    
    public interface ChatListener {
        
        /**
         * is called when a chat message arrives
         * @param msg  the content of the message
         */
        public void onChatMessageRecieved(String msg);
        
    }



    public Lounge(Context context) {
        Intent intent = new Intent(context, GCPService.class);
        intent.putExtra("messenger", mMessenger); 
        context.bindService(intent, this, context.BIND_AUTO_CREATE);
    }


    // receive incoming android system IPC messages from backround GCP service
    final Messenger mMessenger = new Messenger(new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GCPService.JOIN:
                mLobbyListener.onPlayerJoined(msg.obj.toString());
                break;
            case GCPService.CHAT:
                mChatListener.onChatMessageRecieved(msg.obj.toString());
                break;
            case GCPService.LEAVE:
                mLobbyListener.onPlayerLeft(msg.obj.toString());
                break;
            }
        }});

    private ChatListener mChatListener;
    public void register(ChatListener listener) { mChatListener = listener; }
    
    private LobbyListener mLobbyListener;
    public void register(LobbyListener listener) { mLobbyListener = listener; }


    // send android system IPC message to backround GCP service
    private Messenger mService;
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = new Messenger(service);
    }
    
    public void sendMessage(int what, Object thing) {
        try {
            mService.send(Message.obtain(null, what, thing.toString()));
        } catch (RemoteException e) { e.printStackTrace(); }
    }
    
    @Override
    public void onServiceDisconnected(ComponentName name) {}
}
