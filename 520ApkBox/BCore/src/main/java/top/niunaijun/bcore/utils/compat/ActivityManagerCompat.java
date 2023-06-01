package top.niunaijun.bcore.utils.compat;

import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;

import black.android.app.ActivityManagerNative;
import black.android.app.IActivityManager;
import black.android.app.IActivityManagerL;
import black.android.app.IActivityManagerN;

public class ActivityManagerCompat {
	/**
	 * Type for IActivityManager.getIntentSender: this PendingIntent is
	 * for a startActivity operation.
	 */
	public static final int INTENT_SENDER_ACTIVITY = 2;

	public static final int START_FLAG_DEBUG = 1<<1;
	public static final int START_FLAG_TRACK_ALLOCATION = 1<<2;
	public static final int START_FLAG_NATIVE_DEBUGGING = 1<<3;

	public static void finishActivity(IBinder token, int code, Intent data) {
		if (BuildCompat.isN()) {
			IActivityManagerN.finishActivity.call(ActivityManagerNative.getDefault.call(), token, code, data, 0);
		} else if (BuildCompat.isL()) {
			IActivityManagerL.finishActivity.call(ActivityManagerNative.getDefault.call(), token, code, data, false);
		}
	}

    public static void setActivityOrientation(Activity activity, int orientation) {
        try {
            activity.setRequestedOrientation(orientation);
        } catch (Throwable e) {
            e.printStackTrace();
            // Samsung is WindowManager.setRequestedOrientation
            Activity parent = black.android.app.Activity.mParent.get(activity);
            while (true) {
				Activity tmp = black.android.app.Activity.mParent.get(parent);
				if (tmp != null) {
					parent = tmp;
				} else {
					break;
				}
			}

            IBinder token = black.android.app.Activity.mToken.get(parent);
            try {
				IActivityManager.setRequestedOrientation.call(ActivityManagerNative.getDefault.call(), token, orientation);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }
}
