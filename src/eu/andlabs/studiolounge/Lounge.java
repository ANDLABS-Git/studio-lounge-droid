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
    
    public Lounge(Context context) {
        Log.d("Lounge", "Lounge Constructor");
    }
    
    
    // send android system IPC message to backround GCP service
    private Messenger mService;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("Lounge", "Service Connected " + service);
        mService = new Messenger(service);
    }
    
    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("Lounge", "Service DISconnected");
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

    public void hostGame(String game) {
        sendMessage(GCPService.HOST, game);
    }

    public void joinGame(String game) {
        sendMessage(GCPService.JOIN, game);
    }

    /**
     * send custom game message
     * @param msg the data to send
     */
    public void sendGameMessage(Bundle msg) {
        sendMessage(GCPService.CUSTOM, msg);
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

    // receive incoming android system IPC messages from backround GCP service
    public final Messenger mMessenger = new Messenger(new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
        }
        
    });
}



