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

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import eu.andlabs.studiolounge.gcp.GCPService;
import eu.andlabs.studiolounge.gcp.Lounge;

public class LoungeActivity extends FragmentActivity implements
        OnPageChangeListener {
    private static final int ALPHA_OFF = (int) (255 * 0.3f);
    private ViewPager mViewPager;
    private Lounge mLounge;
    private LoungeFragmentAdapter mAdapter;
    private ImageView mLobbyIcon;
    private ImageView mChatIcon;
    private ImageView mStatsIcon;
    private ImageView mAboutIcon;
    private String mName;
    private TextView sectionLabel;
    
    private static final int TAB_LOBBY = 0;
    private static final int TAB_CHAT = TAB_LOBBY + 1;
    private static final int TAB_STATISTICS = TAB_CHAT + 1;
    private static final int TAB_ABOUT = TAB_STATISTICS + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        // mLounge = new Lounge(this);

        Log.i("Luc", "test");
        setContentView(R.layout.main_pager);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new LoungeFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(5);
        mLobbyIcon = (ImageView) findViewById(R.id.ic_tab_lobby);
        mChatIcon = (ImageView) findViewById(R.id.ic_tab_chat);
        mStatsIcon = (ImageView) findViewById(R.id.ic_tab_stat);
        mAboutIcon = (ImageView) findViewById(R.id.ic_tab_about);
        sectionLabel = (TextView) findViewById(R.id.sectionlabel);

        onPageSelected(0);
    }

    @Override
    protected void onStart() {
        Log.d("Lounge", "on START");
        mLounge = GCPService.bind(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("Lounge", "on STOP");
        GCPService.unbind(this, mLounge);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
        case 0:
            mLobbyIcon.setAlpha(255);
            mChatIcon.setAlpha(ALPHA_OFF);
            mStatsIcon.setAlpha(ALPHA_OFF);
            mAboutIcon.setAlpha(ALPHA_OFF);
            sectionLabel.setText("Lobby");
            break;

        case 1:
            mLobbyIcon.setAlpha(ALPHA_OFF);
            mChatIcon.setAlpha(255);
            mStatsIcon.setAlpha(ALPHA_OFF);
            mAboutIcon.setAlpha(ALPHA_OFF);
            sectionLabel.setText("Chat");
            break;

        case 2:
            mLobbyIcon.setAlpha(ALPHA_OFF);
            mChatIcon.setAlpha(ALPHA_OFF);
            mStatsIcon.setAlpha(255);
            mAboutIcon.setAlpha(ALPHA_OFF);
            sectionLabel.setText("Statistics");
            break;

        case 3:
            mLobbyIcon.setAlpha(ALPHA_OFF);
            mChatIcon.setAlpha(ALPHA_OFF);
            mStatsIcon.setAlpha(ALPHA_OFF);
            mAboutIcon.setAlpha(255);
            sectionLabel.setText("About");
            break;

        default:
            break;
        }
    }

    public Lounge getLounge() {
        return mLounge;
    }

    public void onNavigationClicked(View v) {
        if (v.getId() == R.id.ic_tab_lobby) {
            mViewPager.setCurrentItem(TAB_LOBBY, true);
        }
        if (v.getId() == R.id.ic_tab_chat) {
            mViewPager.setCurrentItem(TAB_CHAT, true);
        }
        if (v.getId() == R.id.ic_tab_stat) {
            mViewPager.setCurrentItem(TAB_STATISTICS, true);
        }
        if (v.getId() == R.id.ic_tab_about) {
            mViewPager.setCurrentItem(TAB_ABOUT, true);
        }
    }
}
