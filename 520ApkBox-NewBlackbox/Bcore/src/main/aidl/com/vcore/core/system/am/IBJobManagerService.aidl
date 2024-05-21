// IBJobManagerService.aidl
package com.vcore.core.system.am;

import android.content.Intent;
import android.content.ComponentName;
import android.os.IBinder;
import java.lang.String;
import android.app.job.JobInfo;
import com.vcore.entity.JobRecord;

// Declare any non-default types here with import statements
interface IBJobManagerService {
    JobInfo schedule(in JobInfo info, int userId);
    JobRecord queryJobRecord(String processName, int jobId, int userId);
    void cancelAll(String processName, int userId);
    int cancel(String processName, int jobId, int userId);
}
