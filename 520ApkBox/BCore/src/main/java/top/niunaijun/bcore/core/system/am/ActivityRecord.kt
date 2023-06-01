package top.niunaijun.bcore.core.system.am

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Binder
import android.os.IBinder
import top.niunaijun.bcore.core.system.ProcessRecord
import java.util.*

class ActivityRecord : Binder() {
    @JvmField
    var task: TaskRecord? = null
    @JvmField
    var token: IBinder? = null
    @JvmField
    var resultTo: IBinder? = null
    @JvmField
    var info: ActivityInfo? = null
    @JvmField
    var component: ComponentName? = null
    @JvmField
    var intent: Intent? = null
    @JvmField
    var userId = 0
    @JvmField
    var finished = false
    @JvmField
    var processRecord: ProcessRecord? = null
    @JvmField
    var mBToken: String? = null

    companion object {
        @JvmStatic
        fun create(intent: Intent?, info: ActivityInfo, resultTo: IBinder?, userId: Int): ActivityRecord {
            val record = ActivityRecord()
            record.intent = intent
            record.info = info
            record.component = ComponentName(info.packageName, info.name)
            record.resultTo = resultTo
            record.userId = userId
            record.mBToken = UUID.randomUUID().toString()
            return record
        }
    }
}