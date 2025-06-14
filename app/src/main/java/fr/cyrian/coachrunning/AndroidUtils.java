package fr.cyrian.coachrunning;

import android.os.Build;

public class AndroidUtils {

    private static String RECENT_ACTIVITY;

    static {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            RECENT_ACTIVITY = "com.android.systemui.recents.RecentsActivity";
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            RECENT_ACTIVITY = "com.android.systemui.recent.RecentsActivity";
        } else {
            RECENT_ACTIVITY = "com.android.internal.policy.impl.RecentAppApplicationDialog";
        }
    }

    public static boolean isRecentActivity(String className) {
        if(RECENT_ACTIVITY.equalsIgnoreCase(className)) {
            return true;
        }
        return false;
    }
}
