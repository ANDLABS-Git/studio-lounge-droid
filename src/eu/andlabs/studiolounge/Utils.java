package eu.andlabs.studiolounge;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import eu.andlabs.studiolounge.Constants;

public class Utils implements Constants {

    static void launchGameApp(Context context, String packageName, int isHost,String hostName,String guestName) {
        final ResolveInfo info = getInstalledGameInfo(context, packageName);
        if (info != null) {
            final Intent intent = new Intent();
            intent.setComponent(new ComponentName(info.activityInfo.packageName,
                    info.activityInfo.name));
            intent.putExtra("HOST", isHost);
            intent.putExtra("HOSTNAME",hostName);
            intent.putExtra("GUESTNAME", guestName);
            context.startActivity(intent);
        }
    }

    static boolean isGameInstalled(Context context, String packageName) {
        return getInstalledGameInfo(context, packageName) != null;
    }

    private static ResolveInfo getInstalledGameInfo(Context context, String packageName) {
        final PackageManager pm = context.getPackageManager();
        // final Intent intent = pm.getLaunchIntentForPackage(packageName);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory("eu.andlabs.lounge");

        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);

        for (ResolveInfo info : list) {
            if (info.activityInfo.packageName.equalsIgnoreCase(packageName)) {
                return info;
            }
        }
        return null;

    }

    static Drawable getGameIcon(Context context, String packageName) {
        ResolveInfo info = getInstalledGameInfo(context, packageName);
        if (info == null) {
            return null;
        } else {
            return info.loadIcon(context.getPackageManager());
        }
    }

    static void openPlay(Context context, String packageName) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + packageName));

        context.startActivity(intent);
    }
}
