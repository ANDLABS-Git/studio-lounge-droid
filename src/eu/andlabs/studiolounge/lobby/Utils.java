package eu.andlabs.studiolounge.lobby;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import eu.andlabs.studiolounge.LoungeConstants;

public class Utils implements LoungeConstants {

	static void launchGameApp(Context context, String packageName) {
		final ResolveInfo info = getInstalledGameInfo(context, packageName);
		if(info != null) {
			final Intent intent = new Intent();
			intent.setComponent(new ComponentName(
					info.activityInfo.packageName, info.activityInfo.name));
			context.startActivity(intent);
		}
	}
	
	static boolean isGameInstalled(Context context, String packageName) {
		return getInstalledGameInfo(context, packageName) != null;
	}

	private static ResolveInfo getInstalledGameInfo(Context context, String packageName) {
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
	
	static void openPlay(Context context, String packageName) {
		final Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + packageName));
		
		context.startActivity(intent);
	}
}
