package top.niunaijun.bcore.utils.compat;

import android.content.Context;
import android.content.ContextWrapper;

import black.android.app.ContextImpl;
import black.android.app.ContextImplKitkat;
import black.android.content.AttributionSource;
import black.android.content.AttributionSourceState;
import black.android.content.ContentResolver;
import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.app.BActivityThread;

public class ContextCompat {
    public static final String TAG = "ContextCompat";

    public static void fixAttributionSourceState(Object obj, int uid) {
        Object mAttributionSourceState;
        if (obj != null && AttributionSource.mAttributionSourceState != null) {
            mAttributionSourceState = AttributionSource.mAttributionSourceState.get(obj);

            AttributionSourceState.packageName.set(mAttributionSourceState, BlackBoxCore.getHostPkg());
            AttributionSourceState.uid.set(mAttributionSourceState, uid);
            fixAttributionSourceState(AttributionSource.getNext.call(obj), uid);
        }
    }

    public static void fix(Context context) {
        try {
            int deep = 0;
            while (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
                deep++;
                if (deep >= 10) {
                    return;
                }
            }

            ContextImpl.mPackageManager.set(context, null);
            try {
                context.getPackageManager();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            ContextImpl.mBasePackageName.set(context, BlackBoxCore.getHostPkg());
            ContextImplKitkat.mOpPackageName.set(context, BlackBoxCore.getHostPkg());
            ContentResolver.mPackageName.set(context.getContentResolver(), BlackBoxCore.getHostPkg());

            if (BuildCompat.isS()) {
                fixAttributionSourceState(ContextImpl.getAttributionSource.call(context), BActivityThread.getBUid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
