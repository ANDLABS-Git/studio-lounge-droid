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

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;

public class Lounge implements ServiceConnection {

    protected static final String TAG = "Lounge";

    public interface LobbyListener {

        /**
         * is called after every successful login
         * 
         * @param name
         *            the player who has joined
         */
        public void onPlayerLoggedIn(String player);

        /**
         * is called after every logout operation
         * 
         * @param name
         *            the player who has left
         */
        public void onPlayerLeft(String player);

        public void onNewHostedGame(String player, String Game);

        public void onPlayerJoined(String player);

    }

    public interface ChatListener {

        /**
         * is called when a chat message arrives
         * 
         * @param text
         *            the content of the message
         */
        public void onChatMessageRecieved(ChatMessage msg);

    }

    public interface GameMsgListener {

        /**
         * is called when custom game messages come in
         * 
         * @param msg
         *            the content of the json message
         */
        public void onMessageRecieved(Bundle msg);

    }

    public static class ChatMessage {
        public String player;
        public String text;
    }

    private Vibrator mVibrator;

    public Lounge(Context context) {
        Log.d("Lounge", "Lounge Constructor");
        mVibrator = (Vibrator) context
                .getSystemService(context.VIBRATOR_SERVICE);
    }

    // receive incoming android system IPC messages from backround GCP service
    public final Messenger mMessenger = new Messenger(new Handler() {

        @Override
        public void handleMessage(Message msg) {
            ChatMessage message;
            switch (msg.what) {
            case GCPService.LOGIN:
                Log.d(TAG, mLobbyListener+" Lounge on LOGIN " + msg.obj);
                if (mName == null){
                    mName = (String) msg.obj;
                }
                Log.i("Luc","Lobby Listner "+mLobbyListener);
                if (mLobbyListener != null){
                    mLobbyListener.onPlayerLoggedIn(msg.obj.toString());
                }
                break;
            case GCPService.CHAT:
                mVibrator.vibrate(230);
                String[] msplit = msg.obj.toString().split(":");
                message = new ChatMessage();
                message.player = msplit[0];
                message.text = msplit[1];
                Log.d(TAG, "CHATLISTENER " + mChatListener);
                for (ChatListener l : mChatListeners) { 
                    l.onChatMessageRecieved(message);
                }
                break;
            case GCPService.HOST:
                Log.d(TAG, "Lounge on HOST " + msg.obj);
                if (mLobbyListener != null) {
                    Bundle b = (Bundle) msg.obj;
                    mLobbyListener.onNewHostedGame(b.getString("host"),
                            b.getString("game"));
                }
                break;
            case GCPService.JOIN:
                if (mLobbyListener != null) {
                    mLobbyListener.onPlayerJoined((String) msg.obj);
                }
                break;
            case GCPService.CUSTOM:
                if (mMsgListener != null) {
                    Log.d(TAG, "CUSTOM " + msg.obj);
                    mMsgListener.onMessageRecieved((Bundle) msg.obj);
                }
                break;
            case GCPService.LEAVE:
                mLobbyListener.onPlayerLeft(msg.obj.toString());
                break;
            }
        }
    });

    private ChatListener mChatListener;

    private ArrayList<ChatListener> mChatListeners = new ArrayList<Lounge.ChatListener>(2);

    public void register(ChatListener listener) {
        mChatListeners.add(listener);
    }

    public void unregister(ChatListener listener) {
        mChatListener = null;
    }

    private LobbyListener mLobbyListener;

    public void register(LobbyListener listener) {
        mLobbyListener = listener;
    }

    public void unregister(LobbyListener listener) {
        mLobbyListener = null;
    }

    private GameMsgListener mMsgListener;

    public void register(GameMsgListener listener) {
        mMsgListener = listener;
    }

    public void unregister(GameMsgListener listener) {
        mMsgListener = null;
    }

    // send android system IPC message to backround GCP service
    private Messenger mService;

    private String mName;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("Lounge", "Service Connected " + service);
        mService = new Messenger(service);
        sendMessage(GCPService.REGISTER, mMessenger);
    }

    private void sendMessage(int what, Object thing) {
        try {
        	if(mService!=null)
            mService.send(Message.obtain(null, what, thing));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendChatMessage(ChatMessage msg) {
        sendMessage(GCPService.CHAT, msg.text);
    }

    public void hostGame(String pkgName) {
        sendMessage(GCPService.HOST, pkgName);
    }

    public void joinGame(String hostplayer, String gamepackage) {
        Bundle b = new Bundle();
        b.putString("host", hostplayer);
        b.putString("game", gamepackage);
        sendMessage(GCPService.JOIN, b);
    }

    public void sendGameMessage(Bundle msg) {
        sendMessage(GCPService.CUSTOM, msg);
    }

    public String getName() {
        return mName;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("Lounge", "Service DISconnected");
    }
}
