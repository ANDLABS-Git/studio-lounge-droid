/*
 * Copyright (C) 2012 http://andlabs.eu
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
import android.os.Vibrator;

public class Lounge implements ServiceConnection {

    public interface LobbyListener {
        
        /**
         * is called after every successful login
         * @param name  the player who has joined
         */
        public void onPlayerLoggedIn(String player);
        
        /**
         * is called after every logout operation
         * @param name  the player who has left
         */
        public void onPlayerLeft(String player);
        
        public void onNewHostedGame(String player, String Game);
        
        public void onPlayerJoined(String player);
        
    }
    
    public interface ChatListener {
        
        /**
         * is called when a chat message arrives
         * @param text  the content of the message
         */
        public void onChatMessageRecieved(ChatMessage msg);
        
    }
    
    public static class ChatMessage {
        public String player;
        public String text;
    }



    private Vibrator mVibrator;

    public Lounge(Context context) {
        Intent intent = new Intent(context, GCPService.class);
        intent.putExtra("messenger", mMessenger); 
        context.bindService(intent, this, context.BIND_AUTO_CREATE);
        mVibrator = (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
    }


    // receive incoming android system IPC messages from backround GCP service
    final Messenger mMessenger = new Messenger(new Handler(){

        @Override
        public void handleMessage(Message msg) {
            ChatMessage message;
            switch (msg.what) {
            case GCPService.LOGIN:
                if (mLobbyListener != null)
                    mLobbyListener.onPlayerLoggedIn(msg.obj.toString());
                break;
            case GCPService.CHAT:
                mVibrator.vibrate(230);
                String[] msplit = msg.obj.toString().split(":");
                message = new ChatMessage();
                message.player = msplit[0];
                message.text = msplit[1];
                if (mChatListener != null)
                    mChatListener.onChatMessageRecieved(message);
                break;
            case GCPService.LEAVE:
                mLobbyListener.onPlayerLeft(msg.obj.toString());
                break;
                
            case GCPService.HOST:
            }
        }});

    private ChatListener mChatListener;
    public void register(ChatListener listener) { mChatListener = listener; }
    public void unregister(ChatListener listener) { mChatListener = null; }
    
    private LobbyListener mLobbyListener;
    public void register(LobbyListener listener) { mLobbyListener = listener; }
    public void unregister(LobbyListener listener) { mLobbyListener = null; }


    // send android system IPC message to backround GCP service
    private Messenger mService;
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = new Messenger(service);
    }
    
    public void sendMessage(int what, Object thing) {
        try {
            mService.send(Message.obtain(null, what, thing));
        } catch (RemoteException e) { e.printStackTrace(); }
    }
    
    public void sendChatMessage(ChatMessage msg) {
        sendMessage(GCPService.CHAT, msg.text);
    }
    
    public void hostGame() {
        sendMessage(GCPService.HOST, null);
    }
    
    public void joinGame(String hostplayer) {
        sendMessage(GCPService.JOIN, hostplayer);
    }
    
    @Override
    public void onServiceDisconnected(ComponentName name) {}
}
