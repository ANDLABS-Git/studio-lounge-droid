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

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import org.json.JSONArray;
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

    public static final int LOGIN = 1;
    public static final int CHAT = 2;
    public static final int LEAVE = 3;
	public static final int HOST = 4;
	public static final int JOIN = 4;
    
    @Override
    public void onCreate() {
        super.onCreate();
        mName = "AndroidoKa";
        mHandler = new Handler();
        
        log("starting GCP Service");
        try {
            mSocketIO = new SocketIO("http://may.base45.de:7777", new IOCallback() {
                
                @Override
                public void onConnect() { // auto login
                    log("connected to GCP game server!"); 
                    mSocketIO.emit("login", "I am " + mName);
                }
                
                @Override
                public void onMessage(String text, IOAcknowledge ack) {
                    dispatchMessage(CHAT, text);
                }
                
                @Override
                public void on(String type, IOAcknowledge ack, Object... data) {
//                    log("incoming message:" + type + " --- " + data);
                    if (type.equals("login")) {
                        dispatchMessage(LOGIN, data[0].toString());
                    } else if (type.equals("players")) {
                        dispatchMessage(LOGIN, data[0].toString());
                    } else {
                        dispatchMessage(CHAT, "BAD protocol message: " + type);
                            Log.d("GCP", ""+data[0].getClass().getName());
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
            log(e);
            dispatchMessage(CHAT, "GCP Service Error:   " + e.toString());
        }
    }

    // bind game app(s)
    @Override
    public IBinder onBind(Intent intent) {
        log("on bind");
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
            if (mSocketIO.isConnected()) {
                switch (msg.what) {
                case CHAT:
                    mSocketIO.send(((String) msg.obj));
                    break;
                case HOST:
                    mSocketIO.emit("host", getPackageName());
                    break;
                }
            }
        }});

    @Override
    public void onDestroy() {
        log("on destroy");
        mSocketIO.disconnect();
        super.onDestroy();
    }



    private void log(final Object ding) {
        mHandler.post(new Runnable() {
            
            @Override
            public void run() {
                Toast.makeText(GCPService.this, ding.toString(),
                        1000).show();
            }
        });
        Log.d("GCP-Service", ding.toString());
    }
}
