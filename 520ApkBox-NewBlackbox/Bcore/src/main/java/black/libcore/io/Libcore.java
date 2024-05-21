package black.libcore.io;

import black.Reflector;

public class Libcore {
    public static final Reflector REF = Reflector.on("libcore.io.Libcore");

    public static Reflector.FieldWrapper<Object> os = REF.field("os");
}
