package top.niunaijun.bcore.proxy.record;

import android.content.Intent;
import android.content.pm.ActivityInfo;

public class ProxyActivityRecord {
    public final int mUserId;
    public final ActivityInfo mActivityInfo;
    public final Intent mTarget;
    public final String mActivityToken;

    public ProxyActivityRecord(int userId, ActivityInfo activityInfo, Intent target, String activityToken) {
        this.mUserId = userId;
        this.mActivityInfo = activityInfo;
        this.mTarget = target;
        this.mActivityToken = activityToken;
    }

    public static void saveStub(Intent shadow, Intent target, ActivityInfo activityInfo, String activityToken, int userId) {
        shadow.putExtra("_B_|_user_id_", userId);
        shadow.putExtra("_B_|_activity_info_", activityInfo);
        shadow.putExtra("_B_|_target_", target);
        shadow.putExtra("_B_|_activity_token_v_", activityToken);
    }

    public static ProxyActivityRecord create(Intent intent) {
        int userId = intent.getIntExtra("_B_|_user_id_", 0);
        ActivityInfo activityInfo = intent.getParcelableExtra("_B_|_activity_info_");

        Intent target = intent.getParcelableExtra("_B_|_target_");
        String activityToken = intent.getStringExtra("_B_|_activity_token_v_");
        return new ProxyActivityRecord(userId, activityInfo, target, activityToken);
    }
}
