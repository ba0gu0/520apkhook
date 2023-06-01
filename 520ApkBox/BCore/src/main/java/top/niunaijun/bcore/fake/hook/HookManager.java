package top.niunaijun.bcore.fake.hook;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.fake.delegate.AppInstrumentation;
import top.niunaijun.bcore.fake.service.BuildProxy;
import top.niunaijun.bcore.fake.service.HCallbackProxy;
import top.niunaijun.bcore.fake.service.IAccessibilityManagerProxy;
import top.niunaijun.bcore.fake.service.IAccountManagerProxy;
import top.niunaijun.bcore.fake.service.IActivityClientProxy;
import top.niunaijun.bcore.fake.service.IActivityManagerProxy;
import top.niunaijun.bcore.fake.service.IActivityTaskManagerProxy;
import top.niunaijun.bcore.fake.service.IAlarmManagerProxy;
import top.niunaijun.bcore.fake.service.IAppOpsManagerProxy;
import top.niunaijun.bcore.fake.service.IAppWidgetManagerProxy;
import top.niunaijun.bcore.fake.service.IAutofillManagerProxy;
import top.niunaijun.bcore.fake.service.IBluetoothManagerProxy;
import top.niunaijun.bcore.fake.service.IConnectivityManagerProxy;
import top.niunaijun.bcore.fake.service.IContextHubServiceProxy;
import top.niunaijun.bcore.fake.service.IDeviceIdentifiersPolicyProxy;
import top.niunaijun.bcore.fake.service.IDevicePolicyManagerProxy;
import top.niunaijun.bcore.fake.service.IDisplayManagerProxy;
import top.niunaijun.bcore.fake.service.IFingerprintManagerProxy;
import top.niunaijun.bcore.fake.service.IGraphicsStatsProxy;
import top.niunaijun.bcore.fake.service.IJobServiceProxy;
import top.niunaijun.bcore.fake.service.ILauncherAppsProxy;
import top.niunaijun.bcore.fake.service.ILocationManagerProxy;
import top.niunaijun.bcore.fake.service.IMediaRouterServiceProxy;
import top.niunaijun.bcore.fake.service.IMediaSessionManagerProxy;
import top.niunaijun.bcore.fake.service.INetworkManagementServiceProxy;
import top.niunaijun.bcore.fake.service.INotificationManagerProxy;
import top.niunaijun.bcore.fake.service.IPackageManagerProxy;
import top.niunaijun.bcore.fake.service.IPermissionManagerProxy;
import top.niunaijun.bcore.fake.service.IPersistentDataBlockServiceProxy;
import top.niunaijun.bcore.fake.service.IPhoneSubInfoProxy;
import top.niunaijun.bcore.fake.service.IPowerManagerProxy;
import top.niunaijun.bcore.fake.service.IShortcutManagerProxy;
import top.niunaijun.bcore.fake.service.IStorageManagerProxy;
import top.niunaijun.bcore.fake.service.IStorageStatsManagerProxy;
import top.niunaijun.bcore.fake.service.ISubProxy;
import top.niunaijun.bcore.fake.service.ISystemUpdateProxy;
import top.niunaijun.bcore.fake.service.ITelephonyManagerProxy;
import top.niunaijun.bcore.fake.service.ITelephonyRegistryProxy;
import top.niunaijun.bcore.fake.service.IUserManagerProxy;
import top.niunaijun.bcore.fake.service.IVibratorServiceProxy;
import top.niunaijun.bcore.fake.service.IVpnManagerProxy;
import top.niunaijun.bcore.fake.service.IWifiManagerProxy;
import top.niunaijun.bcore.fake.service.IWifiScannerProxy;
import top.niunaijun.bcore.fake.service.IWindowManagerProxy;
import top.niunaijun.bcore.fake.service.context.ContentServiceProxy;
import top.niunaijun.bcore.fake.service.context.RestrictionsManagerProxy;
import top.niunaijun.bcore.fake.service.libcore.OsProxy;
import top.niunaijun.bcore.utils.Slog;
import top.niunaijun.bcore.utils.compat.BuildCompat;

public class HookManager {
    public static final String TAG = "HookManager";

    private static final HookManager sHookManager = new HookManager();
    private final Map<Class<?>, IInjectHook> mInjectors = new HashMap<>();

    public static HookManager get() {
        return sHookManager;
    }

    public void init() {
        if (BlackBoxCore.get().isBlackProcess() || BlackBoxCore.get().isServerProcess()) {
            addInjector(new IDisplayManagerProxy());
            addInjector(new OsProxy());

            addInjector(new ILocationManagerProxy());
            // AM and PM hook
            addInjector(new IActivityManagerProxy());
            addInjector(new IPackageManagerProxy());
            addInjector(new ITelephonyManagerProxy());
            addInjector(new HCallbackProxy());

            /*
             * It takes time to test and enhance the compatibility of WifiManager
             * (only tested in Android 10).
             * commented by BlackBoxing at 2022/03/08
             * */
            addInjector(new IWifiManagerProxy());
            addInjector(new IWifiScannerProxy());
            addInjector(new IBluetoothManagerProxy());

            addInjector(new ISubProxy());
            addInjector(new IAppOpsManagerProxy());
            addInjector(new INotificationManagerProxy());
            addInjector(new IAlarmManagerProxy());
            addInjector(new IAppWidgetManagerProxy());
            addInjector(new ContentServiceProxy());
            addInjector(new IWindowManagerProxy());
            addInjector(new IUserManagerProxy());
            addInjector(new RestrictionsManagerProxy());
            addInjector(new IMediaSessionManagerProxy());
            addInjector(new IStorageManagerProxy());
            addInjector(new ILauncherAppsProxy());
            addInjector(new IJobServiceProxy());
            addInjector(new IAccessibilityManagerProxy());
            addInjector(new ITelephonyRegistryProxy());
            addInjector(new IDevicePolicyManagerProxy());
            addInjector(new IAccountManagerProxy());
            addInjector(new IConnectivityManagerProxy());
            addInjector(new IPhoneSubInfoProxy());
            addInjector(new IMediaRouterServiceProxy());
            addInjector(new IPowerManagerProxy());
            addInjector(new IVibratorServiceProxy());
            addInjector(new IPersistentDataBlockServiceProxy());
            addInjector(AppInstrumentation.get());

            addInjector(new BuildProxy());
            // 12.0
            if (BuildCompat.isS()) {
                addInjector(new IActivityClientProxy(null));
                addInjector(new IVpnManagerProxy());
            }
            // 11.0
            if (BuildCompat.isR()) {
                addInjector(new IPermissionManagerProxy());
            }
            // 10.0
            if (BuildCompat.isQ()) {
                addInjector(new IActivityTaskManagerProxy());
            }
            // 9.0
            if (BuildCompat.isPie()) {
                addInjector(new ISystemUpdateProxy());
            }
            // 8.0
            if (BuildCompat.isOreo()) {
                addInjector(new IAutofillManagerProxy());
                addInjector(new IDeviceIdentifiersPolicyProxy());
                addInjector(new IStorageStatsManagerProxy());
            }
            // 7.0
            if (BuildCompat.isN()) {
                addInjector(new IContextHubServiceProxy());
                addInjector(new INetworkManagementServiceProxy());
                addInjector(new IShortcutManagerProxy());
            }
            // 6.0
            if (BuildCompat.isM()) {
                addInjector(new IFingerprintManagerProxy());
                addInjector(new IGraphicsStatsProxy());
            }
            // 5.0
            if (BuildCompat.isL()) {
                addInjector(new IJobServiceProxy());
            }
        }
        injectAll();
    }

    public void checkEnv(Class<?> clazz) {
        IInjectHook iInjectHook = mInjectors.get(clazz);
        if (iInjectHook != null && iInjectHook.isBadEnv()) {
            Log.d(TAG, "checkEnv: " + clazz.getSimpleName() + " is bad env");
            iInjectHook.injectHook();
        }
    }

    void addInjector(IInjectHook injectHook) {
        mInjectors.put(injectHook.getClass(), injectHook);
    }

    void injectAll() {
        for (IInjectHook value : mInjectors.values()) {
            try {
                Slog.d(TAG, "hook: " + value);
                value.injectHook();
            } catch (Exception e) {
                Slog.d(TAG, "hook error: " + value + " " + e.getMessage());
            }
        }
    }
}
