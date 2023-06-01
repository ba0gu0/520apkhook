package black.android.os.storage;

import java.io.File;

import black.Reflector;

public class StorageVolume {
    public static final Reflector REF = Reflector.on("android.os.storage.StorageVolume");

    public static Reflector.FieldWrapper<File> mInternalPath = REF.field("mInternalPath");
    public static Reflector.FieldWrapper<File> mPath = REF.field("mPath");
}
