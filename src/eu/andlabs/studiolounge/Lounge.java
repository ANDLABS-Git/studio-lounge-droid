/*
  * Copyright (C) 2012, 2013 by it's authors. Some rights reserved.
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

import eu.andlabs.studiolounge.util.Utils;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
/**
 * <p>Lounge binds the background {@link eu.andlabs.studiolounge.GCPService GCPService}
 * and provides an interface to integrate Multiplayer abilities into Android Games.
 * 
 * <pre>
 * @Override
 * protected void onStart() { Lounge.init(this); }
 *
 *  @Override
 *  protected void onStop() { Lounge.pause(this); }
 * </pre>
 */

public class Lounge {

    private static int gameId;
    private static String matchId;
    private static IGCPService service;
    private static ServiceConnection connection;
    protected static final String TAG = "Lounge";
    public static final String MATCH_ID = "MATCH";
    
    
    /**
     * tells the other players that this player is ready to play
     * @param game  a Context that implements {@link eu.andlabs.studiolounge.Lounge$Multiplayable Multiplayable}
     * @param matchId  the Id of the Lounge match to play
     */
    public static void checkIn(final Multiplayable game, final String matchId) {
        
        Context app = (Context) game;
        Intent intent = new Intent();
        intent.putExtra(Lounge.MATCH_ID, matchId);
        Handler handler = new Handler() {
            
            @Override
            public void handleMessage(Message msg) {
                game.onGameMessage((Bundle) msg.obj);
            }
        };
        intent.putExtra("messenger", new Messenger(handler));
        connection = new ServiceConnection() {
            
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                service = IGCPService.Stub.asInterface(binder);
                Log.d(TAG, "GCPService connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "GCPService disconnected");
                service = null;
            }
        };
        Log.d(TAG, "GCP background service has well been intended "+GCPService.class.getName());
        intent.setClassName("eu.andlabs.studiolounge.test", GCPService.class.getName());
        app.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        app.startService(intent); // keep running during unbinds
        Log.d(TAG, "GCP background service has well been intended.");
        gameId = game.hashCode();
        Lounge.matchId = matchId;
    }
    
    /**
     * tells the other players that this player paused playing
     * @param game  a Context that implements {@link eu.andlabs.studiolounge.Lounge$Multiplayable Multiplayable}
     */
    public static void checkOut(Multiplayable game) {
        if (gameId == game.hashCode() && connection != null) {
            ((Context) game).unbindService(connection);
        }
    }

    /**
     * HOST a new 'match' of a game for others to join
     * @param game the global unique pkgId of the game
     */
    public static void host(String game) {
        try {
            service.host(game);
        } catch (RemoteException e) {
            Log.e(TAG, "service connection is broken!");
            e.printStackTrace();
        }
    }

    /**
     * JOIN a match of a game to subscribe game events
     * @param match  the global unique Id of the match
     */
    public static void join(String match) {
        try {
            service.join(match);
        } catch (RemoteException e) {
            Log.e(TAG, "service connection is broken!");
            e.printStackTrace();
        }
    }

    /**
     * CHAT in the public Lounge Lobby
     * @param msg  something to say
     */
    public static void chat(String msg) {
        try {
            service.chat(msg);
        } catch (RemoteException e) {
            Log.e(TAG, "service connection is broken!");
            e.printStackTrace();
        }
    }
    
    /**
     * send custom game message
     * @param msg the data to send
     */
    public static void sendGameMessage(Bundle msg) {
        try {
            service.custom(matchId, msg);
        } catch (RemoteException e) {
            Log.e(TAG, "service connection is broken!");
            e.printStackTrace();
        }
    }



    /**
     * MULTI PLAY ABLE  ---   the ability to play multiple..
     * Implement this interface to enable multiplayer capabilities.
     */
    public interface Multiplayable {

        /**
         * is called every time one of the other players is ready to play
         * @param player
         */
        public void onCheckIn(String player);
        
        /**
         * is called when all other players are ready to play
         */
        public void onAllPlayerCheckedIn();
        
        /**
         * is called when a custom game message is received
         * @param the content of the custom game message
         */
        public void onGameMessage(Bundle msg);
        
        /**
         * is called when another player pauses playing
         * @param player
         */
        public void onCheckOut(String player);
    }
}
