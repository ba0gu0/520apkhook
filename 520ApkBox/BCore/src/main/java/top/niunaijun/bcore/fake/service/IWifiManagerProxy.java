package top.niunaijun.bcore.fake.service;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;

import black.android.net.wifi.IWifiManager;
import black.android.net.wifi.WifiSsid;
import black.android.os.ServiceManager;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.hook.ProxyMethod;

public class IWifiManagerProxy extends BinderInvocationStub {
    public static final String TAG = "IWifiManagerProxy";

    public IWifiManagerProxy() {
        super(ServiceManager.getService.call(Context.WIFI_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IWifiManager.Stub.asInterface.call(ServiceManager.getService.call(Context.WIFI_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("getConnectionInfo")
    public static class GetConnectionInfo extends MethodHook {
        /*
         * It doesn't have public method to set BSSID and SSID fields in WifiInfo class,
         * So the reflection framework invocation appeared.
         * commented by BlackBoxing at 2022/03/08
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            WifiInfo wifiInfo = (WifiInfo) method.invoke(who, args);
            black.android.net.wifi.WifiInfo.mBSSID.set(wifiInfo, "ac:62:5a:82:65:c4");
            black.android.net.wifi.WifiInfo.mMacAddress.set(wifiInfo, "ac:62:5a:82:65:c4");
            black.android.net.wifi.WifiInfo.mWifiSsid.set(wifiInfo, WifiSsid.createFromAsciiEncoded.call("BlackBox_Wifi"));
            return wifiInfo;
        }
    }

    @ProxyMethod("getScanResults")
    public static class GetScanResults extends MethodHook {
        /*
         * It doesn't have public method to set BSSID and SSID fields in WifiInfo class,
         * So the reflection framework invocation appeared.
         * commented by BlackBoxing at 2022/03/08
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "GetScanResults");
            return new ArrayList<ScanResult>();
        }
    }
}
