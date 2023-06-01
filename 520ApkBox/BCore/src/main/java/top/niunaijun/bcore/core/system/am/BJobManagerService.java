package top.niunaijun.bcore.core.system.am;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.RemoteException;

import java.util.HashMap;
import java.util.Map;

import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.core.system.BProcessManagerService;
import top.niunaijun.bcore.core.system.ISystemService;
import top.niunaijun.bcore.core.system.ProcessRecord;
import top.niunaijun.bcore.core.system.pm.BPackageManagerService;
import top.niunaijun.bcore.entity.JobRecord;
import top.niunaijun.bcore.proxy.ProxyManifest;

public class BJobManagerService extends IBJobManagerService.Stub implements ISystemService {
    private static final BJobManagerService sService = new BJobManagerService();

    private final Map<String, JobRecord> mJobRecords = new HashMap<>();

    public static BJobManagerService get() {
        return sService;
    }

    @Override
    public JobInfo schedule(JobInfo info, int userId) {
        ComponentName componentName = info.getService();
        Intent intent = new Intent();
        intent.setComponent(componentName);
        ResolveInfo resolveInfo = BPackageManagerService.get().resolveService(intent, PackageManager.GET_META_DATA, null, userId);
        if (resolveInfo == null) {
            return info;
        }

        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        ProcessRecord processRecord = BProcessManagerService.get().findProcessRecord(serviceInfo.packageName, serviceInfo.processName, userId);
        if (processRecord == null) {
            processRecord = BProcessManagerService.get().startProcessLocked(serviceInfo.packageName, serviceInfo.processName, userId,
                    -1, Binder.getCallingPid());
            if (processRecord == null) {
                throw new RuntimeException("Unable to create Process " + serviceInfo.processName);
            }
        }
        return scheduleJob(processRecord, info, serviceInfo);
    }

    @Override
    public JobRecord queryJobRecord(String processName, int jobId, int userId) {
        return mJobRecords.get(formatKey(processName, jobId));
    }

    public JobInfo scheduleJob(ProcessRecord processRecord, JobInfo info, ServiceInfo serviceInfo) {
        JobRecord jobRecord = new JobRecord();
        jobRecord.mJobInfo = info;
        jobRecord.mServiceInfo = serviceInfo;

        mJobRecords.put(formatKey(processRecord.processName, info.getId()), jobRecord);
        black.android.app.job.JobInfo.service.set(info, new ComponentName(BlackBoxCore.getHostPkg(), ProxyManifest.getProxyJobService(processRecord.bPID)));
        return info;
    }

    @Override
    public void cancelAll(String processName, int userId) { }

    @Override
    public int cancel(String processName, int jobId, int userId) throws RemoteException {
        return jobId;
    }

    private String formatKey(String processName, int jobId) {
        return processName + "_" + jobId;
    }

    @Override
    public void systemReady() { }
}
