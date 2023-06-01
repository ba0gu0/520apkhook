package black.android.content;

import android.os.Bundle;
import android.os.IBinder;

import black.Reflector;

public class BroadcastReceiver {
    public static final Reflector REF = Reflector.on("android.content.BroadcastReceiver");

    public static Reflector.MethodWrapper<android.content.BroadcastReceiver.PendingResult> getPendingResult = REF.method("getPendingResult");
    public static Reflector.MethodWrapper<Void> setPendingResult = REF.method("setPendingResult", Reflector.findClass("android.content.BroadcastReceiver$PendingResult"));

    public static class PendingResultM {
        public static final Reflector REF = Reflector.on("android.content.BroadcastReceiver$PendingResult");

        public static Reflector.ConstructorWrapper<android.content.BroadcastReceiver.PendingResult> _new = REF.constructor(int.class, String.class, Bundle.class, int.class, boolean.class, boolean.class, IBinder.class, int.class, int.class);

        public static Reflector.FieldWrapper<Boolean> mAbortBroadcast = REF.field("mAbortBroadcast");
        public static Reflector.FieldWrapper<Boolean> mFinished = REF.field("mFinished");
        public static Reflector.FieldWrapper<Integer> mFlags = REF.field("mFlags");
        public static Reflector.FieldWrapper<Boolean> mInitialStickyHint = REF.field("mInitialStickyHint");
        public static Reflector.FieldWrapper<Boolean> mOrderedHint = REF.field("mOrderedHint");
        public static Reflector.FieldWrapper<String> mResultData = REF.field("mResultData");
        public static Reflector.FieldWrapper<Bundle> mResultExtras = REF.field("mResultExtras");
        public static Reflector.FieldWrapper<Integer> mSendingUser = REF.field("mSendingUser");
        public static Reflector.FieldWrapper<IBinder> mToken = REF.field("mToken");
        public static Reflector.FieldWrapper<Integer> mType = REF.field("mType");
    }

    public static class PendingResult {
        public static final Reflector REF = Reflector.on("android.content.BroadcastReceiver$PendingResult");

        public static Reflector.ConstructorWrapper<android.content.BroadcastReceiver.PendingResult> _new = REF.constructor(int.class, String.class, Bundle.class, int.class, boolean.class, boolean.class, IBinder.class, int.class);

        public static Reflector.FieldWrapper<Boolean> mAbortBroadcast = REF.field("mAbortBroadcast");
        public static Reflector.FieldWrapper<Boolean> mFinished = REF.field("mFinished");
        public static Reflector.FieldWrapper<Boolean> mInitialStickyHint = REF.field("mInitialStickyHint");
        public static Reflector.FieldWrapper<Boolean> mOrderedHint = REF.field("mOrderedHint");
        public static Reflector.FieldWrapper<String> mResultData = REF.field("mResultData");
        public static Reflector.FieldWrapper<Bundle> mResultExtras = REF.field("mResultExtras");
        public static Reflector.FieldWrapper<Integer> mSendingUser = REF.field("mSendingUser");
        public static Reflector.FieldWrapper<IBinder> mToken = REF.field("mToken");
        public static Reflector.FieldWrapper<Integer> mType = REF.field("mType");
    }
}
