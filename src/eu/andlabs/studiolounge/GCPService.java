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

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import eu.andlabs.studiolounge.util.LoginManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * <p>
 * The {@link eu.andlabs.studiolounge.GCPService GCPService} background
 * system service acts like a router, forwarding and translating messages
 * between online nodes and Mobile Apps. It sends and receives <a href=
 * "https://github.com/ANDLABS-Git/studio-lounge-node/blob/master/spec/protocol.coffee"
 * > GCP</a> protocol messages over Socket.IO/TCP/IP and translates them to <a
 * href=
 * "https://github.com/ANDLABS-Git/studio-lounge-node/blob/master/spec/protocol.coffee"
 * > GCP</a> protocol over Android Binder IPC messages.
 * </p>
 * 
 * <p>
 * Mobile Apps can use the messaging API directly by directly starting and
 * binding to the service or usually use the convenience client implementation
 * {@link eu.andlabs.studiolounge.Lounge Lounge}.
 * </p>
 */
public class GCPService extends Service {

    public static String mName;
    private Handler mHandler;
    private SocketIO mSocketIO;
    private Messenger mApp;
    private String packagename;
    private boolean connecting;
    protected boolean loggedIn;

    public static final int LOGIN = 1;
    public static final int CHAT = 2;
    public static final int LEAVE = 3;
    public static final int HOST = 4;
    public static final int JOIN = 5;
    protected static final int CUSTOM = 6;
    protected static final int UNHOST = 7;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        log("creating GCP Service   PId=" + Process.myPid());
//        connect();
    }

    private void connect() {
        if (connecting)
            return;
        log("connecting");
        try {
            connecting = true;
            mSocketIO = new SocketIO("http://may.base45.de:7777", new IOCallback() {

                @Override
                public void onConnect() { // auto login
                    log("connected to GCP game server!");
                    if (mApp != null) {
                        mSocketIO.emit("login", "I am " + mName);
                        mSocketIO.emit("state");
                    }
                    connecting = false;
                }

                @Override
                public void onMessage(String text, IOAcknowledge ack) {
                    Bundle b = new Bundle();
                    String[] msplit = text.split(":");
                    b.putString("player", msplit[0]);
                    b.putString("msg", msplit[1]);
                    Log.d("CHAT", msplit[0]);
                }

                @Override
                public void on(String type, IOAcknowledge ack, Object... data) {
                     log("incoming message:" + type + " --- " + data);
                    try {
                        Bundle b = new Bundle();
                        JSONObject json = (JSONObject) data[0];
                        b.putString("game", json.getString("game"));
                        b.putString("player", json.getString("player"));
                        if (type.equals("welcome")) {
                            loggedIn = true;
                        } else if (type.equals("host")) {
                        } else if (type.equals("unhost")) {
                        } else if (type.equals("join")) {
                        } else if (type.equals("leave")) {
                        } else if (type.equals("move")) {
                            for (Iterator<String> i = json.keys(); i.hasNext();) {
                                String key = i.next();
                                b.putString(key, json.getString(key));
                            }
                        } else {
                            Log.d("GCP", "BAD protocol msg: ");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onMessage(JSONObject json, IOAcknowledge ack) {
                }

                @Override
                public void onDisconnect() {
                    log("lost game server.");
                    connecting = false;
                    loggedIn = false;
                }

                @Override
                public void onError(SocketIOException error) {
                    error.printStackTrace();
                    connecting = false;
                    loggedIn = false;
                    log(error);
                }
            });
        } catch (Exception e) {
            log(e);
            loggedIn = false;
            connecting = false;
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("on startCommand id=" + startId + "  flags=" + flags);
        return START_NOT_STICKY;
    }

    IBinder mBinder = new IGCPService.Stub() {
        
        @Override
        public void host(String game) throws RemoteException {
            log(game);
        }
    };
    
    @Override
    public IBinder onBind(Intent intent) {
        log("on bind");
        return mBinder;
    }

    // receive android system IPC messages from game apps
    final Messenger mMessenger = new Messenger(new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (mSocketIO.isConnected()) {
                try {
                    JSONObject json = new JSONObject();
                    switch (msg.what) {
                    case CHAT:
                        mSocketIO.send(((String) msg.obj));
                        break;
                    case HOST:
                        json.put("host", mName);
                        json.put("game", msg.obj);
                        mSocketIO.emit("host", json);
                        break;
                    case JOIN:
                        json.put("game", msg.obj);
                        mSocketIO.emit("join", json);
                        break;
                    case CUSTOM:
                        Bundle b = (Bundle) msg.obj;
                        json.put("who", mName);
                        Set<String> keys = b.keySet();
                        for (String key : keys) {
                            json.put(key, b.get(key));
                            Log.i("json",key + " : "+b.getByte(key));
                        }
                        mSocketIO.emit("move", json);
                        break;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    public void onDestroy() {
        log("on destroy");
//        mSocketIO.disconnect();
        super.onDestroy();
    }

    private void log(final Object ding) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(GCPService.this, ding.toString(), 1000).show();
            }
        });
        Log.d("GCP-Service", ding.toString());
    }
}
