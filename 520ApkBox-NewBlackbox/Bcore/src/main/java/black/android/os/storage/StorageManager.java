package black.android.os.storage;

import android.os.storage.StorageVolume;

import black.Reflector;

public class StorageManager {
    public static final Reflector REF = Reflector.on("android.os.storage.StorageManager");

    public static Reflector.StaticMethodWrapper<StorageVolume[]> getVolumeList = REF.staticMethod("getVolumeList", int.class, int.class);
}
