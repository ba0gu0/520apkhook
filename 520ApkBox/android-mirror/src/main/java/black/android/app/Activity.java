package black.android.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.IBinder;

import black.Reflector;

public class Activity {
    public static final Reflector REF = Reflector.on("android.app.Activity");

    public static Reflector.FieldWrapper<ActivityInfo> mActivityInfo = REF.field("mActivityInfo");
    public static Reflector.FieldWrapper<Boolean> mFinished = REF.field("mFinished");
    public static Reflector.FieldWrapper<android.app.Activity> mParent = REF.field("mParent");
    public static Reflector.FieldWrapper<Integer> mResultCode = REF.field("mResultCode");
    public static Reflector.FieldWrapper<Intent> mResultData = REF.field("mResultData");
    public static Reflector.FieldWrapper<IBinder> mToken = REF.field("mToken");
}
