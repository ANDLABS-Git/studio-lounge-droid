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

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;


public class GCPService extends Service {

    public static String mName;
    private Handler mHandler;
    private SocketIO mSocketIO;
    private Messenger mApp;
	private String packagename;
    private boolean connecting;

    public static final int LOGIN = 1;
    public static final int CHAT = 2;
    public static final int LEAVE = 3;
	public static final int HOST = 4;
	public static final int JOIN = 5;
    protected static final int CUSTOM = 6;
    public static final int REGISTER = 7;
    
    @Override
    public void onCreate() {
        super.onCreate();
        mName = "lucas";
        mHandler = new Handler();
        
        log("starting GCP Service");
        connect();
    }

    private void connect() {
        if (connecting) return;
        log("connecting");
        try {
            connecting = true;
            mSocketIO = new SocketIO("http://may.base45.de:7777", new IOCallback() {
                
                @Override
                public void onConnect() { // auto login
                    log("connected to GCP game server!");
                    if (mApp != null) mSocketIO.emit("login", "I am " + mName);
                    connecting = false;
                }
                
                @Override
                public void onMessage(String text, IOAcknowledge ack) {
                    dispatchMessage(CHAT, text);
                }
                
                @Override
                public void on(String type, IOAcknowledge ack, Object... data) {
                    //                    log("incoming message:" + type + " --- " + data);
                    try {
                        if (type.equals("login")) {
                            dispatchMessage(LOGIN, data[0].toString());
                        } else if (type.equals("welcome")) {
//                            dispatchMessage(LOGIN, data[0].toString().split("in as ")[1]);
                        } else if (type.equals("players")) {
                            JSONArray json = (JSONArray) data[0];
                            for (int i = 0; i < json.length(); i++) {
                                dispatchMessage(LOGIN, json.getString(i));
                            }
                        } else if (type.equals("games")) {
                            JSONArray json = (JSONArray) data[0];
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject o = json.getJSONObject(i);
                                Bundle b = new Bundle();
                                b.putString("game", o.getString("game"));
                                b.putString("host", o.getString("host"));
                                dispatchMessage(HOST, b);
                            }
                        } else if (type.equals("host")){
                            JSONObject json = (JSONObject) data[0];
                            Bundle b = new Bundle();
                            b.putString("game", json.getString("game"));
                            b.putString("host", json.getString("host"));
                            dispatchMessage(HOST, b);
                        } else if (type.equals("join")){
                            JSONObject json = (JSONObject) data[0];
                            dispatchMessage(JOIN, json.get("guest"));
                        } else if (type.equals("move")){
                            JSONObject json = (JSONObject) data[0];
                            
                            
                            Bundle b = new Bundle();
                            
                            for ( Iterator<String> i = json.keys(); i.hasNext(); )
                            {
                            	String key = i.next();
                            	b.putString(key, json.getString(key));
//                            	Log.i("json", "converting -  key:"+key + "  /  Value: "+json.getString(key));
                            }
                            
                            dispatchMessage(CUSTOM, b);
                        } else {
                            dispatchMessage(CHAT, "BAD protocol message: " + type);
                            Log.d("GCP", ""+data[0].getClass().getName());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                
                @Override
                public void onMessage(JSONObject json, IOAcknowledge ack) { }
                
                @Override
                public void onDisconnect() { log("lost game server."); }
                
                @Override
                public void onError(SocketIOException error) { error.printStackTrace(); log(error); }
            });
        } catch (Exception e) {
            log(e);
            dispatchMessage(CHAT, "GCP Service Error:   " + e.toString());
            e.printStackTrace();
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("on startCommand id="+startId+"  flags="+flags);
        mApp = (Messenger) intent.getParcelableExtra("messenger");
        packagename = intent.getExtras().getString("packageName");
        if (mSocketIO.isConnected()) mSocketIO.emit("login", "I am " + mName);
        else connect();
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
                        json.put("game", packagename);
                        mSocketIO.emit("host", json);
                        
                        break;
                    case JOIN:
                        Bundle bb = (Bundle) msg.obj;
                        json.put("host", bb.getString("host"));
                        json.put("game", bb.getString("game"));
                        mSocketIO.emit("join", json);
                        break;
                    case CUSTOM:
                        Bundle b = (Bundle) msg.obj;
                        json.put("who", mName);
                        json.put("color", b.getString("color"));
                        
                        json.put("x", b.getLong("x"));
                        json.put("y", b.getLong("y"));
                        mSocketIO.emit("move", json);
                        break;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
