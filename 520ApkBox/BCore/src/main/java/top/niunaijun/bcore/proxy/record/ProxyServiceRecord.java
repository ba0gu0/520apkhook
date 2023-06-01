package top.niunaijun.bcore.proxy.record;

import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.IBinder;

import top.niunaijun.bcore.utils.compat.BundleCompat;

public class ProxyServiceRecord {
    public final Intent mServiceIntent;
    public final ServiceInfo mServiceInfo;
    public final IBinder mToken;
    public final int mUserId;
    public final int mStartId;

    public ProxyServiceRecord(Intent serviceIntent, ServiceInfo serviceInfo, IBinder token, int userId, int startId) {
        this.mServiceIntent = serviceIntent;
        this.mServiceInfo = serviceInfo;
        this.mUserId = userId;
        this.mStartId = startId;
        this.mToken = token;
    }

    public static void saveStub(Intent shadow, Intent target, ServiceInfo serviceInfo, IBinder token, int userId, int startId) {
        shadow.putExtra("_B_|_target_", target);
        shadow.putExtra("_B_|_service_info_", serviceInfo);
        shadow.putExtra("_B_|_user_id_", userId);
        shadow.putExtra("_B_|_start_id_", startId);
        BundleCompat.putBinder(shadow, "_B_|_token_", token);
    }

    public static ProxyServiceRecord create(Intent intent) {
        Intent target = intent.getParcelableExtra("_B_|_target_");
        ServiceInfo serviceInfo = intent.getParcelableExtra("_B_|_service_info_");

        int userId = intent.getIntExtra("_B_|_user_id_", 0);
        int startId = intent.getIntExtra("_B_|_start_id_", 0);

        IBinder token = BundleCompat.getBinder(intent, "_B_|_token_");
        return new ProxyServiceRecord(target, serviceInfo, token, userId, startId);
    }
}
