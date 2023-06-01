package top.niunaijun.bcore.entity.am;

import android.app.ActivityManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class RunningAppProcessInfo implements Parcelable {
    public final List<ActivityManager.RunningAppProcessInfo> mAppProcessInfoList;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mAppProcessInfoList);
    }

    public RunningAppProcessInfo() {
        this.mAppProcessInfoList = new ArrayList<>();
    }

    protected RunningAppProcessInfo(Parcel in) {
        this.mAppProcessInfoList = in.createTypedArrayList(ActivityManager.RunningAppProcessInfo.CREATOR);
    }

    public static final Parcelable.Creator<RunningAppProcessInfo> CREATOR = new Parcelable.Creator<RunningAppProcessInfo>() {
        @Override
        public RunningAppProcessInfo createFromParcel(Parcel source) {
            return new RunningAppProcessInfo(source);
        }

        @Override
        public RunningAppProcessInfo[] newArray(int size) {
            return new RunningAppProcessInfo[size];
        }
    };
}
