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

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import java.util.Iterator;

import org.apache.http.client.utils.URIUtils;
import org.json.JSONException;
import org.json.JSONObject;

import eu.andlabs.studiolounge.util.Id;
import eu.andlabs.studiolounge.util.Utils;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
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
public class GCPService extends Service implements IOCallback {

    public static String mName;
    private boolean loggedIn;
    private SocketIO mSocketIO;
    private Messenger mApp;
    private String packagename;
    private Uri providerAuthority;
    private ContentResolver cache;
    private Handler handler;

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
        handler = new Handler();
        log("creating GCP Service   PId=" + Process.myPid());
        providerAuthority = Uri.parse("content://foo.lounge");
//        providerAuthority = Uri.parse("content://" + Utils.discoverCacheAuthority(this));
        cache = getContentResolver();
        connect();
    }

    private void connect() {
        try {
            log("connecting");
            mSocketIO = new SocketIO("http://may.base45.de:7777", this);
        } catch (Exception e) {
            log("Error while connecting! " + e);
            e.printStackTrace();
            loggedIn = false;
        }
    }

    @Override
    public void onConnect() { // auto login
        log("connected to GCP game server!");
        mSocketIO.emit("login", "I am " + Id.getName(this));
    }

    @Override
    public void onMessage(String text, IOAcknowledge ack) {}

    @Override
    public void on(String type, IOAcknowledge ack, Object... data) {
        log("incoming message:" + type + " --- " + data);
        try {
            if (type.equals("welcome")) {
                mSocketIO.emit("history");
                loggedIn = true;
                return;
            } 
            JSONObject json = (JSONObject) data[0];
            ContentValues cv = new ContentValues();
            if (type.equals("chat")) {
                cv.put("sender", json.getString("sender"));
                cv.put("msg", json.getString("text"));
                cache.insert(Uri.withAppendedPath(providerAuthority, "chat"), cv);
            } else if (type.equals("host")) {
                cv.put("id", json.getString("id"));
                cv.put("host", json.getString("host"));
                cache.insert(Uri.withAppendedPath(providerAuthority, 
                        "games/" + json.getString("game") + "/matches"), cv);
            } else if (type.equals("join")) {
                cv.put("player", json.getString("player"));
                cache.insert(Uri.withAppendedPath(providerAuthority,
                        "matches/" + json.getString("match") + "/players"), cv);
            } else if (type.equals("msg")) {
                if (json.has("persist")) {
                    cv.put("json", json.toString(0));
                    cache.insert(Uri.withAppendedPath(providerAuthority, 
                            "matches/" + json.getString("match") + "/msges"), cv);
                } else {
                    Bundle bundle = new Bundle();
                    for (Iterator<String> it = json.keys(); it.hasNext();) {
                        String key = it.next();
                        bundle.putString(key, json.getString(key));
                    }
                    Message msg = Message.obtain();
                    msg.setData(bundle);
                    try {
                        mApp.send(msg);
                    } catch (RemoteException e) {
                        log("Error: " + e);
                        e.printStackTrace();
                    }
                }
            } else {
                Log.d("GCP", "BAD protocol msg: ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(JSONObject json, IOAcknowledge ack) {
    }

    @Override
    public void onDisconnect() {
        log("game server disconnected.");
        loggedIn = false;
    }

    @Override
    public void onError(SocketIOException error) {
        log("socket.io Exception!");
        error.printStackTrace();
        loggedIn = false;
        log(error);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("on startCommand id=" + startId + "  flags=" + flags);
        mApp = (Messenger) intent.getParcelableExtra("messenger");
        return START_NOT_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        log("on bind " + intent);
        return mBinder;
    }
    
    public void onRebind(Intent intent) {
        log("on rebind " + intent);
    };
    
    public void unbindService(ServiceConnection conn) {
        log("on unbind " + conn);
    };

    IBinder mBinder = new IGCPService.Stub() {
        
        @Override
        public void chat(String msg) throws RemoteException {
            mSocketIO.emit("chat", msg);
        }
        
        @Override
        public void host(String game) throws RemoteException {
            try {
                mSocketIO.emit("host", new JSONObject().put("game", game));
            } catch (Exception e) {
                log("Error: " + e);
                e.printStackTrace();
            }
            log("send HOST " + game);
        }

        @Override
        public void join(String match) throws RemoteException {
            try {
                mSocketIO.emit("join", new JSONObject().put("match", match));
            } catch (Exception e) {
                log("Error: " + e);
                e.printStackTrace();
            }
            log("send JOIN " + match);
        }

        @Override
        public void custom(String match, Bundle msg) throws RemoteException {
            try {
                JSONObject json = new JSONObject();
                for (String key : msg.keySet()) {
                    json.put(key, msg.get(key));
                }
                json.put("match", match);
                mSocketIO.emit("move", json);
            } catch (JSONException e) {
                log("Error: " + e);
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onDestroy() {
        log("on destroy");
//        mSocketIO.disconnect();
        super.onDestroy();
    }

    private void log(final Object ding) {
        handler.post(new Runnable() {

            @Override
            public void run() {
//                Toast.makeText(GCPService.this, ding.toString(), 1000).show();
            }
        });
        Log.d("GCP-Service", ding.toString());
    }
}
