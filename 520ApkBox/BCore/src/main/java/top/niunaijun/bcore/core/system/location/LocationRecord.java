package top.niunaijun.bcore.core.system.location;

public class LocationRecord {
    public final String packageName;
    public final int userId;

    public LocationRecord(String packageName, int userId) {
        this.packageName = packageName;
        this.userId = userId;
    }
}
