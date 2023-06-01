package top.niunaijun.bcore.core.system.am;

import java.util.Objects;

public class PendingIntentRecord {
    public int uid;
    public String packageName;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof PendingIntentRecord)) {
            return false;
        }

        PendingIntentRecord that = (PendingIntentRecord) o;
        return uid == that.uid && Objects.equals(packageName, that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, packageName);
    }
}
