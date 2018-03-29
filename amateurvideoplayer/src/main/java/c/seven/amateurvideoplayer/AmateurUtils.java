package c.seven.amateurvideoplayer;

import android.app.Activity;
import android.content.Context;

/**
 * Created by j-songsaihua-ol on 2018/3/23.
 */

public class AmateurUtils {
    public static String stringFormatTime(long time) {
        if (time <= 0) {
            return "00:00";
        }
        time /= 1000;
        long minute = time / 60;
        long hour = minute / 60;
        long second = time % 60;
        minute %= 60;
        if (hour < 1) {
            return String.format("%02d:%02d", minute, second);
        }
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }


    public static boolean isFinishing(Context context) {
        if (context == null) {
            return true;
        }
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return true;
        }
        return false;
    }

    public static void finish(Context context) {
        if (context != null && context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}
