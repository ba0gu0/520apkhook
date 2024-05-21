package black.android.content.res;

import black.Reflector;

public class AssetManager {
    public static final Reflector REF = Reflector.on("android.content.res.AssetManager");

    public static Reflector.ConstructorWrapper<android.content.res.AssetManager> _new = REF.constructor();

    public static Reflector.MethodWrapper<Integer> addAssetPath = REF.method("addAssetPath", String.class);
}
