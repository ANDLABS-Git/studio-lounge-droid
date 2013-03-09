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

package eu.andlabs.studiolounge.util;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import eu.andlabs.studiolounge.CacheProvider;

public class Utils {

    private static final String TAG = "Lounge";

    public static String discoverCacheAuthority(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        String authority = null;
        for (ResolveInfo info : getInstalledLoungeGames(ctx)) {
            int on = pm.getComponentEnabledSetting(new ComponentName(
                    info.resolvePackageName, CacheProvider.class.getName()));
            if (on == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                Log.d(TAG, "GCP cache already exists - authority="
                        + info.resolvePackageName);
                authority = info.resolvePackageName;
            } else {
                Log.d(TAG, "no GCP cache " + info.resolvePackageName);
            }
        }
        if (authority != null)
            return authority;

        Log.d(TAG, "NO gcp Cache ContentProvider found. Setting up a new one.");
        pm.setComponentEnabledSetting(new ComponentName(ctx.getPackageName(),
                CacheProvider.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
        return ctx.getPackageName();
    }

    private static List<ResolveInfo> getInstalledLoungeGames(Context ctx) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory("eu.andlabs.lounge");
        return ctx.getPackageManager().queryIntentActivities(intent, 0);
    }

    static void launchGameApp(Context context, String packageName, int isHost,
            String hostName, String guestName) {
        final ResolveInfo info = null;// getInstalledGameInfo(context,
                                      // packageName);
        if (info != null) {
            final Intent intent = new Intent();
            intent.setComponent(new ComponentName(
                    info.activityInfo.packageName, info.activityInfo.name));
            intent.putExtra("HOST", isHost);
            intent.putExtra("HOSTNAME", hostName);
            intent.putExtra("GUESTNAME", guestName);
            context.startActivity(intent);
        }
    }

    static Drawable getGameIcon(Context context, String packageName) {
        ResolveInfo info = null; // getInstalledGameInfo(context, packageName);
        if (info == null) {
            return null;
        } else {
            return info.loadIcon(context.getPackageManager());
        }
    }

    static void openPlay(Context context, String packageName) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri
                .parse("http://play.google.com/store/apps/details?id="
                        + packageName));

        context.startActivity(intent);
    }

    public static int ipc(Context context, int colorA, int colorB,
            float proportion) {
        float[] hsva = new float[3];
        float[] hsvb = new float[3];
        Color.colorToHSV(context.getResources().getColor(colorA), hsva);
        Color.colorToHSV(context.getResources().getColor(colorB), hsvb);
        for (int i = 0; i < 3; i++) {
            hsvb[i] = Utils.interpolate(hsva[i], hsvb[i], proportion);
        }
        return Color.HSVToColor(hsvb);
    }

    public static float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }
}
