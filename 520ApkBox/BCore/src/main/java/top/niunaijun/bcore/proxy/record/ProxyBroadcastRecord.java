package top.niunaijun.bcore.proxy.record;

import android.content.Intent;

import androidx.annotation.NonNull;

public class ProxyBroadcastRecord {
    public final Intent mIntent;
    public final int mUserId;

    public ProxyBroadcastRecord(Intent intent, int userId) {
        this.mIntent = intent;
        this.mUserId = userId;
    }

    public static void saveStub(Intent shadow, Intent target, int userId) {
        shadow.putExtra("_B_|_target_", target);
        shadow.putExtra("_B_|_user_id_", userId);
    }

    public static ProxyBroadcastRecord create(Intent intent) {
        Intent target = intent.getParcelableExtra("_B_|_target_");
        int userId = intent.getIntExtra("_B_|_user_id_", 0);
        return new ProxyBroadcastRecord(target, userId);
    }

    @NonNull
    @Override
    public String toString() {
        return "ProxyBroadcastRecord{" + "mIntent=" + mIntent + ", mUserId=" + mUserId + '}';
    }
}
