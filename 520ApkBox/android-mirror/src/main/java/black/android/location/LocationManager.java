package black.android.location;

import black.Reflector;

public class LocationManager {
    public static class GnssStatusListenerTransport {
        public static final Reflector REF = Reflector.on("android.location.LocationManager$GnssStatusListenerTransport");

        public static Reflector.MethodWrapper<Void> onGnssStarted = REF.method("onGnssStarted");
        public static Reflector.MethodWrapper<Void> onNmeaReceived = REF.method("onNmeaReceived", long.class, String.class);
    }

    public static class GpsStatusListenerTransport {
        public static final Reflector REF = Reflector.on("android.location.LocationManager$GpsStatusListenerTransport");

        public static Reflector.MethodWrapper<Void> onNmeaReceived = REF.method("onNmeaReceived", long.class, String.class);
    }

    public static class LocationListenerTransport {
        public static final Reflector REF = Reflector.on("android.location.LocationManager$LocationListenerTransport");

        public static Reflector.FieldWrapper<Object> mListener = REF.field("mListener");
    }
}
