package top.niunaijun.bcore.app.configuration;

import java.io.File;

public abstract class ClientConfiguration {
    public boolean isHideRoot() {
        return false;
    }

    public boolean isHideXposed() {
        return false;
    }

    public abstract String getHostPackageName();

    public boolean isEnableDaemonService() {
        return true;
    }

    public boolean isEnableLauncherActivity() {
        return true;
    }

    /**
     * This method is called when an internal application requests to install a new application.
     *
     * @return Is it handled?
     */
    public boolean requestInstallPackage(File file, int userId) {
        return false;
    }
}
