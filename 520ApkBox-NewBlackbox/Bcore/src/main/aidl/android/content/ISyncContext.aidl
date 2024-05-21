package android.content;

import android.content.SyncResult;

/**
 * Interface used by the SyncAdapter to indicate its progress.
 * @hide
 */
interface ISyncContext {
    /**
     * Call to indicate that the SyncAdapter is making progress. E.g., if this SyncAdapter
     * downloads or sends records to/from the server, this may be called after each record
     * is downloaded or uploaded.
     */
    void sendHeartbeat();

    /**
     * Signal that the corresponding sync session is completed.
     * @param result information about this sync session
     */
    void onFinished(in SyncResult result);
}
