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

package eu.andlabs.studiolounge.sample;

import eu.andlabs.studiolounge.Lounge;
import eu.andlabs.studiolounge.Lounge.Multiplayable;
import eu.andlabs.studiolounge.ui.LobbyActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class HelloLounge extends Activity implements Multiplayable {

    static final String TAG = "Hello";
    
    @Override
    protected void onStart() {
        super.onStart();
        
        String matchId = getIntent().getStringExtra(Lounge.MATCH_ID);
        
        if (matchId == null) {
            startActivity(new Intent(this, LobbyActivity.class));
            Log.d(TAG, "entering lobby");
        } else {
            Lounge.checkIn(this, matchId);
            Log.d(TAG, "ready to play " + matchId);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Lounge.checkOut(this);
    }

    @Override
    public void onCheckIn(String player) { }

    @Override
    public void onAllPlayerCheckedIn() {
        Log.d(TAG, "all ready to play");
        Bundle msg = new Bundle();
        msg.putString("foo", "bar");
        Lounge.sendGameMessage(msg);
    }

    @Override
    public void onGameMessage(Bundle msg) {
        Log.d(TAG, "MESSAGE received!!!");
        TextView hello = new TextView(this);
        hello.setText("Hello " + msg.getString("foo") + ":-)");
        setContentView(hello);
    }

    @Override
    public void onCheckOut(String player) {
        // TODO Auto-generated method stub
    }
}
