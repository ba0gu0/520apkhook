package top.niunaijun.bcore.proxy.record;

import android.content.Intent;

import androidx.annotation.NonNull;

public class ProxyPendingRecord {
    public final int mUserId;
    public final Intent mTarget;

    public ProxyPendingRecord(Intent target, int userId) {
        this.mUserId = userId;
        this.mTarget = target;
    }

    public static void saveStub(Intent shadow, Intent target, int userId) {
        shadow.putExtra("_B_|_P_user_id_", userId);
        shadow.putExtra("_B_|_P_target_", target);
    }

    public static ProxyPendingRecord create(Intent intent) {
        int userId = intent.getIntExtra("_B_|_P_user_id_", 0);
        Intent target = intent.getParcelableExtra("_B_|_P_target_");
        return new ProxyPendingRecord(target, userId);
    }

    @NonNull
    @Override
    public String toString() {
        return "ProxyPendingActivityRecord{" + "mUserId=" + mUserId + ", mTarget=" + mTarget + '}';
    }
}
