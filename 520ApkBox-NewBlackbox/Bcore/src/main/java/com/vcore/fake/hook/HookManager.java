package com.vcore.fake.hook;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import black.oem.flyme.IFlymePermissionService;
import black.oem.vivo.IPhysicalFlingManager;
import black.oem.vivo.IPopupCameraManager;
import black.oem.vivo.ISuperResolutionManager;
import black.oem.vivo.ISystemDefenceManager;
import black.oem.vivo.IVivoPermissonService;
import com.vcore.BlackBoxCore;
import com.vcore.fake.delegate.AppInstrumentation;
import com.vcore.fake.service.BuildProxy;
import com.vcore.fake.service.HCallbackProxy;
import com.vcore.fake.service.IAccessibilityManagerProxy;
import com.vcore.fake.service.IAccountManagerProxy;
import com.vcore.fake.service.IActivityClientProxy;
import com.vcore.fake.service.IActivityManagerProxy;
import com.vcore.fake.service.IActivityTaskManagerProxy;
import com.vcore.fake.service.IAlarmManagerProxy;
import com.vcore.fake.service.IAppOpsManagerProxy;
import com.vcore.fake.service.IAppWidgetManagerProxy;
import com.vcore.fake.service.IAutofillManagerProxy;
import com.vcore.fake.service.IBluetoothManagerProxy;
import com.vcore.fake.service.IConnectivityManagerProxy;
import com.vcore.fake.service.IContextHubServiceProxy;
import com.vcore.fake.service.IDeviceIdentifiersPolicyProxy;
import com.vcore.fake.service.IDevicePolicyManagerProxy;
import com.vcore.fake.service.IDisplayManagerProxy;
import com.vcore.fake.service.IFingerprintManagerProxy;
import com.vcore.fake.service.IFlymePermissionServiceProxy;
import com.vcore.fake.service.IGraphicsStatsProxy;
import com.vcore.fake.service.IJobServiceProxy;
import com.vcore.fake.service.ILauncherAppsProxy;
import com.vcore.fake.service.ILocationManagerProxy;
import com.vcore.fake.service.IMediaRouterServiceProxy;
import com.vcore.fake.service.IMediaSessionManagerProxy;
import com.vcore.fake.service.INetworkManagementServiceProxy;
import com.vcore.fake.service.INotificationManagerProxy;
import com.vcore.fake.service.IPackageManagerProxy;
import com.vcore.fake.service.IPermissionManagerProxy;
import com.vcore.fake.service.IPersistentDataBlockServiceProxy;
import com.vcore.fake.service.IPhoneSubInfoProxy;
import com.vcore.fake.service.IPhysicalFlingManagerProxy;
import com.vcore.fake.service.IPopupCameraManagerProxy;
import com.vcore.fake.service.IPowerManagerProxy;
import com.vcore.fake.service.IRoleManagerProxy;
import com.vcore.fake.service.ISearchManagerProxy;
import com.vcore.fake.service.IShortcutManagerProxy;
import com.vcore.fake.service.IStorageManagerProxy;
import com.vcore.fake.service.IStorageStatsManagerProxy;
import com.vcore.fake.service.ISubProxy;
import com.vcore.fake.service.ISuperResolutionManagerProxy;
import com.vcore.fake.service.ISystemDefenceManagerProxy;
import com.vcore.fake.service.ISystemUpdateProxy;
import com.vcore.fake.service.ITelephonyManagerProxy;
import com.vcore.fake.service.ITelephonyRegistryProxy;
import com.vcore.fake.service.IUserManagerProxy;
import com.vcore.fake.service.IVibratorServiceProxy;
import com.vcore.fake.service.IVivoPermissionServiceProxy;
import com.vcore.fake.service.IVpnManagerProxy;
import com.vcore.fake.service.IWifiManagerProxy;
import com.vcore.fake.service.IWifiScannerProxy;
import com.vcore.fake.service.IWindowManagerProxy;
import com.vcore.fake.service.context.ContentServiceProxy;
import com.vcore.fake.service.context.RestrictionsManagerProxy;
import com.vcore.fake.service.libcore.OsProxy;
import com.vcore.utils.Slog;
import com.vcore.utils.compat.BuildCompat;

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
            addInjector(new ISearchManagerProxy());
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
                addInjector(new IDeviceIdentifiersPolicyProxy());
                addInjector(new IRoleManagerProxy());
                addInjector(new IActivityTaskManagerProxy());
            }
            // 9.0
            if (BuildCompat.isPie()) {
                addInjector(new ISystemUpdateProxy());
            }
            //fix flyme service
            if (IFlymePermissionService.TYPE != null) {
                addInjector(new IFlymePermissionServiceProxy());
            }
            // 8.0
            if (BuildCompat.isOreo()) {
                addInjector(new IAutofillManagerProxy());
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
            if (IPhysicalFlingManager.TYPE != null) {
                addInjector(new IPhysicalFlingManagerProxy());
            }
            if (IPopupCameraManager.TYPE != null) {
                addInjector(new IPopupCameraManagerProxy());
            }
            if (ISuperResolutionManager.TYPE != null) {
                addInjector(new ISuperResolutionManagerProxy());
            }
            if (ISystemDefenceManager.TYPE != null) {
                addInjector(new ISystemDefenceManagerProxy());
            }
            if (IVivoPermissonService.TYPE != null) {
                addInjector(new IVivoPermissionServiceProxy());
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
