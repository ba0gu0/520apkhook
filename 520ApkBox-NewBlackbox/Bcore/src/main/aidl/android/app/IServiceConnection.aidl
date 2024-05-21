package android.app;

import android.content.ComponentName;

/** @hide */
interface IServiceConnection {
    void connected(in ComponentName name, IBinder service);
}
