package top.niunaijun.bcore.core.system.am;

import android.os.IBinder;

import java.util.HashMap;
import java.util.Map;

public class UserSpace {
    // 单实例
    public final ActiveServices mActiveServices = new ActiveServices();
    // 单实例
    public final ActivityStack mStack = new ActivityStack();
    public final Map<IBinder, PendingIntentRecord> mIntentSenderRecords = new HashMap<>();
}
