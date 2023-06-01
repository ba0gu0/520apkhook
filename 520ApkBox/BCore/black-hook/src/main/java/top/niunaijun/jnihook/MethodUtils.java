package top.niunaijun.jnihook;

import androidx.annotation.Keep;

import java.lang.reflect.Method;
import java.util.Objects;

@Keep
public class MethodUtils {
    // native call
    public static String getDeclaringClass(final Method method) {
        return method.getDeclaringClass().getName().replace(".", "/");
    }

    // native call
    public static String getMethodName(final Method method) {
        return method.getName();
    }

    // native call
    public static String getDesc(final Method method) {
        final StringBuilder buf = new StringBuilder();
        buf.append("(");

        final Class<?>[] types = method.getParameterTypes();
        for (Class<?> type : types) {
            buf.append(getDesc(type));
        }

        buf.append(")");
        buf.append(getDesc(method.getReturnType()));
        return buf.toString();
    }

    private static String getDesc(final Class<?> returnType) {
        if (returnType.isPrimitive()) {
            return getPrimitiveLetter(returnType);
        }

        if (returnType.isArray()) {
            return "[" + getDesc(Objects.requireNonNull(returnType.getComponentType()));
        }
        return "L" + getType(returnType) + ";";
    }

    private static String getType(final Class<?> parameterType) {
        if (parameterType.isArray()) {
            return "[" + getDesc(Objects.requireNonNull(parameterType.getComponentType()));
        }

        if (!parameterType.isPrimitive()) {
            final String clsName = parameterType.getName();
            return clsName.replaceAll("\\.", "/");
        }
        return getPrimitiveLetter(parameterType);
    }

    private static String getPrimitiveLetter(final Class<?> type) {
        if (Integer.TYPE.equals(type)) {
            return "I";
        }

        if (Void.TYPE.equals(type)) {
            return "V";
        }

        if (Boolean.TYPE.equals(type)) {
            return "Z";
        }

        if (Character.TYPE.equals(type)) {
            return "C";
        }

        if (Byte.TYPE.equals(type)) {
            return "B";
        }

        if (Short.TYPE.equals(type)) {
            return "S";
        }

        if (Float.TYPE.equals(type)) {
            return "F";
        }

        if (Long.TYPE.equals(type)) {
            return "J";
        }

        if (Double.TYPE.equals(type)) {
            return "D";
        }
        throw new IllegalStateException("Type: " + type.getCanonicalName() + " is not a primitive type");
    }
}
