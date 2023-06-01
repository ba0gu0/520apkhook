package black.android.telephony;

import android.os.IInterface;

import black.Reflector;

public class TelephonyManager {
    public static final Reflector REF = Reflector.on("android.telephony.TelephonyManager");

    public static Reflector.StaticMethodWrapper<Object> getSubscriberInfoService = REF.staticMethod("getSubscriberInfoService");

    public static Reflector.FieldWrapper<Boolean> sServiceHandleCacheEnabled = REF.field("sServiceHandleCacheEnabled");
    public static Reflector.FieldWrapper<IInterface> sIPhoneSubInfo = REF.field("sIPhoneSubInfo");

    public static Reflector.MethodWrapper<IInterface> getSubscriberInfo = REF.method("getSubscriberInfo");
}
