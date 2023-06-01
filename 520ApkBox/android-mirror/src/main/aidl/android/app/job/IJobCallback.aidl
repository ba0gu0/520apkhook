package android.app.job;

import android.app.job.JobWorkItem;

/**
 * The server side of the JobScheduler IPC protocols.  The app-side implementation
 * invokes on this interface to indicate completion of the (asynchronous) instructions
 * issued by the server.
 *
 * In all cases, the 'who' parameter is the caller's service binder, used to track
 * which Job Service instance is reporting.
 *
 */
interface IJobCallback {
    /**
    * Immediate callback to the system after sending a start signal, used to quickly detect ANR.
    *
    * @param jobId Unique integer used to identify this job.
    * @param ongoing True to indicate that the client is processing the job. False if the job is
    * complete
    */
    void acknowledgeStartMessage(int jobId, boolean ongoing);
    /**
    * Immediate callback to the system after sending a stop signal, used to quickly detect ANR.
    *
    * @param jobId Unique integer used to identify this job.
    * @param reschedule Whether or not to reschedule this job.
    */
    void acknowledgeStopMessage(int jobId, boolean reschedule);
    /*
    * Called to deqeue next work item for the job.
    */
    JobWorkItem dequeueWork(int jobId);
    /*
    * Called to report that job has completed processing a work item.
    */
    boolean completeWork(int jobId, int workId);
    /*
    * Tell the job manager that the client is done with its execution, so that it can go on to
    * the next one and stop attributing wakelock time to us etc.
    *
    * @param jobId Unique integer used to identify this job.
    * @param reschedule Whether or not to reschedule this job.
    */
    void jobFinished(int jobId, boolean reschedule);
}
