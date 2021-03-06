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
package eu.andlabs.studiolounge.gcp;

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

import eu.andlabs.studiolounge.gcp.Lounge.ChatMessage;
import eu.andlabs.studiolounge.lobby.LoginManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * <p>
 * The {@link eu.andlabs.studiolounge.gcp.GCPService GCPService} background
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
 * {@link eu.andlabs.studiolounge.gcp.Lounge Lounge}.
 * </p>
 */
public class GCPService extends Service {

    public static Lounge bind(Context ctx) {
        Log.d("GCP-Service", "binding GCP Service");
        String name = LoginManager.getInstance(ctx).getUserId().getPlayername();
        Lounge lounge = new Lounge(ctx);
        Intent intent = new Intent(ctx, GCPService.class);
        intent.putExtra("packageName", ctx.getPackageName());
        intent.putExtra("messenger", lounge.mMessenger);
        intent.putExtra("name", name);
        ctx.startService(intent);
        ctx.bindService(intent, lounge, Context.BIND_AUTO_CREATE);
        return lounge;
    }

    public static void unbind(Context ctx, Lounge lounge) {
        ctx.unbindService(lounge);
    }

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
        // mName = "LUKAS";
        mHandler = new Handler();
        log("starting GCP Service");
        connect();
    }

    private void connect() {
        if (connecting)
            return;
        log("connecting");
        try {
            connecting = true;
            mSocketIO = new SocketIO("http://may.base45.de:7777", new IOCallback() {
//            mSocketIO = new SocketIO("http://192.168.2.109:7777", new IOCallback() {

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
                    dispatchMessage(CHAT, b);
                }

                @Override
                public void on(String type, IOAcknowledge ack, Object... data) {
                     log("incoming message:" + type + " --- " + data);
                    try {
                        if (type.equals("login")) {
                            loggedIn = true;
                            dispatchMessage(LOGIN, data[0].toString());
                        } else if (type.equals("welcome")) {
                            // dispatchMessage(LOGIN,
                            // data[0].toString().split("in as ")[1]);
                        } else if (type.equals("host")) {
                            JSONObject json = (JSONObject) data[0];
                            Bundle b = new Bundle();
                            b.putString("game", json.getString("game"));
                            b.putString("host", json.getString("host"));
                            dispatchMessage(HOST, b);
                        } else if (type.equals("join")) {
                            JSONObject json = (JSONObject) data[0];
                            Bundle b = new Bundle();
                            b.putString("game", json.getString("game"));
                            b.putString("guest",json.getString("guest"));
                            dispatchMessage(JOIN, b);
                        } else if (type.equals("state")) {
                            JSONObject json = (JSONObject) data[0];
                            JSONArray players = json.getJSONArray("players");
                            for (int i = 0; i < players.length(); i++) {
                                JSONObject player = players.getJSONObject(i);
                                dispatchMessage(LOGIN, player.getString("name"));
                                if (player.has("game")) {
                                    Bundle b = new Bundle();
                                    JSONObject game = player.getJSONObject("game");
                                    b.putString("game", game.getString("id"));
                                    b.putString("host", player.getString("name"));
                                    b.putInt("joined", game.getInt("joined"));
                                    b.putInt("min", game.getInt("min"));
                                    b.putInt("max", game.getInt("max"));
                                    dispatchMessage(HOST, b);
                                }
                            }
                            JSONArray chat = json.getJSONArray("chat");
                            for (int i = 0; i < chat.length(); i++) {
                                JSONObject msg = chat.getJSONObject(i);
                                Bundle b = new Bundle();
                                b.putString("player", msg.getString("player"));
                                b.putString("msg", msg.getString("msg"));
                                dispatchMessage(CHAT, b);
                            }
                        } else if (type.equals("move")) {
                            JSONObject json = (JSONObject) data[0];
                            Bundle b = new Bundle();
                            for (Iterator<String> i = json.keys(); i
                                    .hasNext();) {
                                String key = i.next();
                                b.putString(key, json.getString(key));
                                 Log.i("json",
                                 "converting -  key:"+key +
                                 "  /  Value: "+json.getString(key));
                            }
                            dispatchMessage(CUSTOM, b);
                        } else if (type.equals("unhost")) {
                            Bundle b = new Bundle();
                            JSONObject json = (JSONObject) data[0];
                            b.putString("host", json.getString("host"));
                            b.putString("game", json.getString("game"));
                            dispatchMessage(UNHOST, b);
                        } else {
                            dispatchMessage(CHAT,
                                    "BAD protocol message: " + type);
                            Log.d("GCP", ""
                                    + data[0].getClass().getName());
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
            dispatchMessage(CHAT, "GCP Service Error:   " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("on startCommand id=" + startId + "  flags=" + flags);
        mApp = (Messenger) intent.getParcelableExtra("messenger");
        packagename = intent.getExtras().getString("packageName");
        mName = intent.getExtras().getString("name");
        if (mSocketIO.isConnected() && loggedIn)
            mSocketIO.emit("state");
        else
            connect();
        return START_NOT_STICKY;
    }

    // bind game app(s)
    @Override
    public IBinder onBind(Intent intent) {
        log("on bind");
        return mMessenger.getBinder();
    }

    // send android system IPC message to game apps
    private void dispatchMessage(int what, Object thing) {
        try {
            mApp.send(Message.obtain(mHandler, what, thing));
        } catch (RemoteException e) {
            log("Error: " + e.toString());
        }
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
        mSocketIO.disconnect();
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
