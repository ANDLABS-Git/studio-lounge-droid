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
package eu.andlabs.studiolounge.ui;

import eu.andlabs.studiolounge.R;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;
import android.os.Bundle;

public class LobbyActivity extends FragmentActivity implements OnPageChangeListener {

    private TextView mSectionLabel;
    private ViewPager mViewPager;
    private ImageView mLobbyIcon;
    private ImageView mStatsIcon;
    private ImageView mAboutIcon;
    private ImageView mChatIcon;
    private ImageView mHeader;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lobby);
        
        mSectionLabel = (TextView) findViewById(R.id.sectionlabel);
        mAboutIcon = (ImageView) findViewById(R.id.ic_tab_about);
        mLobbyIcon = (ImageView) findViewById(R.id.ic_tab_lobby);
        mStatsIcon = (ImageView) findViewById(R.id.ic_tab_stat);
        mChatIcon = (ImageView) findViewById(R.id.ic_tab_chat);
        mHeader = (ImageView) findViewById(R.id.header);
        
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(
                new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() { return 4; }

            @Override
            public Fragment getItem(int position) {
                Log.d(TAG, "ITEM " + position);
                switch (position) {
                case LOBBY:
                    return new GamesFragment();
                case CHAT:
                    return new ChatFragment();
                case STATS:
                    return new StatsFragment();
                case ABOUT:
                    return new AboutFragment();
                }
                return null;
            }
        });
        onPageSelected(LOBBY);
    }
    
    public void onNavigationClicked(View v) {
        if (v.getId() == R.id.ic_tab_lobby) {
            mViewPager.setCurrentItem(LOBBY, true);
        }
        if (v.getId() == R.id.ic_tab_chat) {
            mViewPager.setCurrentItem(CHAT, true);
        }
        if (v.getId() == R.id.ic_tab_stat) {
            mViewPager.setCurrentItem(STATS, true);
        }
        if (v.getId() == R.id.ic_tab_about) {
            mViewPager.setCurrentItem(ABOUT, true);
        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "page selected");
        switch (position) {
        case LOBBY:
            mSectionLabel.setText("Lobby");
            mLobbyIcon.setAlpha(255);
            mChatIcon.setAlpha(ALPHA_OFF);
            mStatsIcon.setAlpha(ALPHA_OFF);
            mAboutIcon.setAlpha(ALPHA_OFF);
            break;

        case CHAT:
            mSectionLabel.setText("Chat");
            mLobbyIcon.setAlpha(ALPHA_OFF);
            mChatIcon.setAlpha(255);
            mStatsIcon.setAlpha(ALPHA_OFF);
            mAboutIcon.setAlpha(ALPHA_OFF);
            break;

        case STATS:
            mSectionLabel.setText("Statistics");
            mLobbyIcon.setAlpha(ALPHA_OFF);
            mChatIcon.setAlpha(ALPHA_OFF);
            mStatsIcon.setAlpha(255);
            mAboutIcon.setAlpha(ALPHA_OFF);
            break;
            

        case ABOUT:
            mSectionLabel.setText("About");
            mLobbyIcon.setAlpha(ALPHA_OFF);
            mChatIcon.setAlpha(ALPHA_OFF);
            mStatsIcon.setAlpha(ALPHA_OFF);
            mAboutIcon.setAlpha(255);
            break;
        default:
            break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onPageScrolled(int position, float foo, int arg2) {
        switch (position) {
        case LOBBY:
            mHeader.setBackgroundColor(ipc(R.color.orange, R.color.green, foo));
            break;
        case CHAT:
            mHeader.setBackgroundColor(ipc(R.color.green, R.color.blue, foo));
            break;
        case STATS:
            mHeader.setBackgroundColor(ipc(R.color.blue, R.color.yellow, foo));
            break;
        }
    }
    
    private int ipc(int a, int b, float proportion) {
        float[] hsva = new float[3];
        float[] hsvb = new float[3];
        Color.colorToHSV(getResources().getColor(a), hsva);
        Color.colorToHSV(getResources().getColor(b), hsvb);
        for (int i = 0; i < 3; i++) {
          hsvb[i] = interpolate(hsva[i], hsvb[i], proportion);
        }
        return Color.HSVToColor(hsvb);
      }
      private float interpolate(float a, float b, float proportion) {
          return (a + ((b - a) * proportion));
      }



    private static final int ALPHA_OFF = (int) (255 * 0.3f);
    private static final String TAG = "Lounge";
    private static final int LOBBY = 0;
    private static final int CHAT = 1;
    private static final int STATS = 2;
    private static final int ABOUT = 3;
}
