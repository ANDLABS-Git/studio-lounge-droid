package eu.andlabs.studiolounge.lobby;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import eu.andlabs.studiolounge.LoungeConstants;

public class Utils implements LoungeConstants {

    static void launchGameApp(Context context, String packageName, int isHost) {
        final ResolveInfo info = getInstalledGameInfo(context, packageName);
        if (info != null) {
            final Intent intent = new Intent();
            intent.setComponent(new ComponentName(
                    info.activityInfo.packageName, info.activityInfo.name));
            intent.putExtra("HOST", isHost);
            context.startActivity(intent);
        }
    }

    static boolean isGameInstalled(Context context, String packageName) {
        return getInstalledGameInfo(context, packageName) != null;
    }

    private static ResolveInfo getInstalledGameInfo(Context context,
            String packageName) {
        final PackageManager pm = context.getPackageManager();
        final Intent intent = new Intent();
        intent.addCategory(CATEGORY);
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);

        for (ResolveInfo info : list) {
            if (info.activityInfo.packageName.equalsIgnoreCase(packageName)) {
                return info;
            }
        }
        return null;

    }

    static Drawable getGameIcon(Context context, String packageName) {
        final PackageManager pm = context.getPackageManager();
        final Intent intent = pm.getLaunchIntentForPackage(packageName); // because
                                                                         // somehow
                                                                         // the
                                                                         // getInstalledGameInfo
                                                                         // doesn't
                                                                         // retrieve
                                                                         // a
                                                                         // icon
        if (intent == null) {
            return null;
        }

        final ResolveInfo info = pm.queryIntentActivities(intent, 0).get(0);
        if (info == null) {
            return null;
        } else {
            return info.loadIcon(context.getPackageManager());
        }
    }

    static void openPlay(Context context, String packageName) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(PLAY_BASE_URL + packageName));

        context.startActivity(intent);
    }
}
