package top.niunaijun.bcore.entity.am;

import android.app.ActivityManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class RunningServiceInfo implements Parcelable {
    public final List<ActivityManager.RunningServiceInfo> mRunningServiceInfoList;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mRunningServiceInfoList);
    }

    public RunningServiceInfo() {
        this.mRunningServiceInfoList = new ArrayList<>();
    }

    protected RunningServiceInfo(Parcel in) {
        this.mRunningServiceInfoList = in.createTypedArrayList(ActivityManager.RunningServiceInfo.CREATOR);
    }

    public static final Parcelable.Creator<RunningServiceInfo> CREATOR = new Parcelable.Creator<RunningServiceInfo>() {
        @Override
        public RunningServiceInfo createFromParcel(Parcel source) {
            return new RunningServiceInfo(source);
        }

        @Override
        public RunningServiceInfo[] newArray(int size) {
            return new RunningServiceInfo[size];
        }
    };
}
