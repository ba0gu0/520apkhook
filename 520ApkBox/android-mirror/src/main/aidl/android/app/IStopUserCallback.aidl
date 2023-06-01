package android.app;

/**
 * Callback to find out when we have finished stopping a user.
 * {@hide}
 */
interface IStopUserCallback {
    void userStopped(int userId);
    void userStopAborted(int userId);
}
