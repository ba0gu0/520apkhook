package top.niunaijun.bcore.utils;

import android.util.Log;

/**
 * @hide
 */
public final class Slog {
    private Slog() { }

    public static void v(String tag, String msg) {
        println(Log.VERBOSE, tag, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        println(Log.VERBOSE, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    public static void d(String tag, String msg) {
        println(Log.DEBUG, tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        println(Log.DEBUG, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    public static void i(String tag, String msg) {
        println(Log.INFO, tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        println(Log.INFO, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    public static void w(String tag, String msg) {
        println(Log.WARN, tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        println(Log.WARN, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    public static void w(String tag, Throwable tr) {
        println(Log.WARN, tag, Log.getStackTraceString(tr));
    }
    
    public static void e(String tag, String msg) {
        println(Log.ERROR, tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        println(Log.ERROR, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    public static void println(int priority, String tag, String msg) {
        Log.println(priority, tag, msg);
    }
}

