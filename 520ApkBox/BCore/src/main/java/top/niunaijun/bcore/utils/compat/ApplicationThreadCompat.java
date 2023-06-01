package top.niunaijun.bcore.utils.compat;

import android.os.IBinder;
import android.os.IInterface;

import black.android.app.ApplicationThreadNative;
import black.android.app.IApplicationThread;

public class ApplicationThreadCompat {
    public static IInterface asInterface(IBinder binder) {
        if (BuildCompat.isOreo()) {
            return IApplicationThread.Stub.asInterface.call(binder);
        }
        return ApplicationThreadNative.asInterface.call(binder);
    }
}
