package top.niunaijun.bcore.core.system;

import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.ConditionVariable;
import android.os.IInterface;

import java.util.Arrays;

import top.niunaijun.bcore.core.IBActivityThread;
import top.niunaijun.bcore.entity.AppConfig;
import top.niunaijun.bcore.proxy.ProxyManifest;

public class ProcessRecord extends Binder {
    public final ApplicationInfo info;
    final public String processName;
    public IBActivityThread bActivityThread;
    public IInterface appThread;
    public int uid;
    public int pid;
    public int bUID;
    public int bPID;
    public int callingBUid;
    public int userId;

    public final ConditionVariable initLock = new ConditionVariable();

    public ProcessRecord(ApplicationInfo info, String processName) {
        this.info = info;
        this.processName = processName;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{processName, pid, bUID, bPID, uid, pid, userId});
    }

    public String getProviderAuthority() {
        return ProxyManifest.getProxyAuthorities(bPID);
    }

    public AppConfig getClientConfig() {
        AppConfig config = new AppConfig();
        config.packageName = info.packageName;
        config.processName = processName;
        config.bPID = bPID;
        config.bUID = bUID;
        config.uid = uid;
        config.callingBUid = callingBUid;
        config.userId = userId;
        config.token = this;
        return config;
    }

    public String getPackageName() {
        return info.packageName;
    }
}
