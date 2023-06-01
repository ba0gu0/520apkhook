package top.niunaijun.bcore.entity;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

public class AppConfig implements Parcelable {
    public static final String KEY = "BlackBox_client_config";

    public String packageName;
    public String processName;
    public int bPID;
    public int bUID;
    public int uid;
    public int userId;
    public int callingBUid;
    public IBinder token;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.packageName);
        dest.writeString(this.processName);
        dest.writeInt(this.bPID);
        dest.writeInt(this.bUID);
        dest.writeInt(this.uid);
        dest.writeInt(this.userId);
        dest.writeInt(this.callingBUid);
        dest.writeStrongBinder(token);
    }

    public AppConfig() { }

    protected AppConfig(Parcel in) {
        this.packageName = in.readString();
        this.processName = in.readString();
        this.bPID = in.readInt();
        this.bUID = in.readInt();
        this.uid = in.readInt();
        this.userId = in.readInt();
        this.callingBUid = in.readInt();
        this.token = in.readStrongBinder();
    }

    public static final Parcelable.Creator<AppConfig> CREATOR = new Parcelable.Creator<AppConfig>() {
        @Override
        public AppConfig createFromParcel(Parcel source) {
            return new AppConfig(source);
        }

        @Override
        public AppConfig[] newArray(int size) {
            return new AppConfig[size];
        }
    };
}
