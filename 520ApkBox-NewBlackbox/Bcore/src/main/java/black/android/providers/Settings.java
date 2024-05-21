package black.android.providers;

import android.os.IInterface;

import black.Reflector;

public class Settings {
    public static class System {
        public static final Reflector REF = Reflector.on("android.provider.Settings$System");

        public static Reflector.FieldWrapper<Object> sNameValueCache = REF.field("sNameValueCache");
    }

    public static class Secure {
        public static final Reflector REF = Reflector.on("android.provider.Settings$Secure");

        public static Reflector.FieldWrapper<Object> sNameValueCache = REF.field("sNameValueCache");
    }

    public static class ContentProviderHolder {
        public static final Reflector REF = Reflector.on("android.provider.Settings$ContentProviderHolder");

        public static Reflector.FieldWrapper<IInterface> mContentProvider = REF.field("mContentProvider");
    }

    public static class NameValueCacheOreo {
        public static final Reflector REF = Reflector.on("android.provider.Settings$NameValueCache");

        public static Reflector.FieldWrapper<Object> mProviderHolder = REF.field("mProviderHolder");
    }

    public static class NameValueCache {
        public static final Reflector REF = Reflector.on("android.provider.Settings$NameValueCache");

        public static Reflector.FieldWrapper<Object> mContentProvider = REF.field("mContentProvider");
    }

    public static class Global {
        public static final Reflector REF = Reflector.on("android.provider.Settings$Global");

        public static Reflector.FieldWrapper<Object> sNameValueCache = REF.field("sNameValueCache");
    }
}
