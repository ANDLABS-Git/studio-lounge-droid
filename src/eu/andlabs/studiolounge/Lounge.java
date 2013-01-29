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

package eu.andlabs.studiolounge;

import java.util.ArrayList;

import eu.andlabs.studiolounge.util.Constants;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;
/**
 * <p>The Lounge class starts and binds the background {@link eu.andlabs.studiolounge.GCPService GCPService}
 * and provides an object oriented interface to integrate Lounge into Mobile Apps.
 * 
 * The ServiceConnection needs to bind and unbind during Activity flow.
 * <pre>
 * @Override
 * protected void onStart() { mLounge = GCPService.bind(this); }
 *
 *  @Override
 *  protected void onStop() { GCPService.unbind(this, mLounge); }
 * </pre>
 */
public class Lounge implements ServiceConnection {

    protected static final String TAG = "Lounge";
    
    /**
     * This callback interface is used to subscribe game arrangement messages
     */
    public interface LobbyListener {

        /**
         * is called after every successful login
         * 
         * @param player name of the player who logged in.
         */
        public void onPlayerLoggedIn(String player);

        /**
         * is called after every logout operation
         * 
         * @param player name of the player who has left.
         */
        public void onPlayerLeft(String player);

        /**
         * is called when new games are hosted
         * 
         * @param player name of the host player
         */
        public void onNewHostedGame(String player);

        /**
         * is called when player join games.
         * 
         * @param player name of the player who has joined
         * @param game package identifier of the hosted game
         */
        public void onPlayerJoined(String player, String game);

    }

    /**
     * This callback interface is used to subscribe chat messages.
     */
    public interface ChatListener {

        /**
         * is called when a chat message arrives
         * 
         * @param text text content of the chat message
         */
        public void onChatMessageRecieved(ChatMessage msg);

    }

    /**
     * This callback interface is used to subscribe custom game messages.
     */
    public interface GameMsgListener {

        /**
         * is called when custom game messages come in
         * 
         * @param the content of the custom game message
         */
        public void onMessageReceived(Bundle msg);

    }

    /**
     * This class encapsulates chat messages.
     */
    public static class ChatMessage {
        public String player;
        public String text;
    }


    public Lounge(Context context) {
        Log.d("Lounge", "Lounge Constructor");
    }
    
//    ArrayList<Player> mPlayers = new ArrayList<Player>();
    
    // receive incoming android system IPC messages from backround GCP service
    public final Messenger mMessenger = new Messenger(new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Log.i("GCP","Handler Code: "+msg.what);
            ChatMessage message;
            Bundle b;
        }
    });

    private ChatListener mChatListener;

    private ArrayList<ChatListener> mChatListeners = new ArrayList<Lounge.ChatListener>(2);
    
    /**
     * subscribe to chat messages
     * 
     * @param listener callback interface
     */
    public void register(ChatListener listener) {
        mChatListeners.add(listener);
    }

    /**
     * unsubscribe chat messages
     * 
     * @param listener callback interface
     */
    public void unregister(ChatListener listener) {
        mChatListener = null;
    }

    private LobbyListener mLobbyListener;

    /**
     * subscribe to game arrangement messages
     * 
     * @param listener callback interface
     */
    public void register(LobbyListener listener) {
        mLobbyListener = listener;
    }

    /**
     * unsubscribe game arrangement game messages
     * 
     * @param listener callback interface
     */
    public void unregister(LobbyListener listener) {
        mLobbyListener = null;
    }

    private GameMsgListener mMsgListener;

    /**
     * subscribe to custom game messages
     * 
     * @param listener callback interface
     */
    public void register(GameMsgListener listener) {
        mMsgListener = listener;
    }

    /**
     * unsubscribe custom game messages
     * 
     * @param listener callback interface
     */
    public void unregister(GameMsgListener listener) {
        mMsgListener = null;
    }

    // send android system IPC message to backround GCP service
    private Messenger mService;

    private String myGame;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("Lounge", "Service Connected " + service);
        mService = new Messenger(service);
    }

    private void sendMessage(int what, Object thing) {
        try {
            if (mService != null) {
                mService.send(Message.obtain(null, what, thing));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendChatMessage(ChatMessage msg) {
        sendMessage(GCPService.CHAT, msg.text);
    }
    
    public void hostGame(ComponentName componentName) {
    	hostGame(componentName.flattenToShortString());
    }

    public void hostGame(String pkgName) {
        sendMessage(GCPService.HOST, pkgName);
        myGame = pkgName;
    }

    public void joinGame(String game) {
        Log.i("Players","Join game "+game); 
        sendMessage(GCPService.JOIN, game);
        myGame = game;
    }

    /**
     * send custom game message
     * 
     * @param msg the data to send
     */
    public void sendGameMessage(Bundle msg) {
        sendMessage(GCPService.CUSTOM, msg);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("Lounge", "Service DISconnected");
    }
}
