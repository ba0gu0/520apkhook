package top.niunaijun.bcore.entity.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class XposedConfig implements Parcelable {
    public boolean enable;
    public Map<String, Boolean> moduleState = new HashMap<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.enable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.moduleState.size());
        for (Map.Entry<String, Boolean> entry : this.moduleState.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
    }

    public XposedConfig() { }

    public XposedConfig(Parcel in) {
        this.enable = in.readByte() != 0;
        int mModuleStateSize = in.readInt();
        this.moduleState = new HashMap<>(mModuleStateSize);

        for (int i = 0; i < mModuleStateSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.moduleState.put(key, value);
        }
    }

    public static final Parcelable.Creator<XposedConfig> CREATOR = new Parcelable.Creator<XposedConfig>() {
        @Override
        public XposedConfig createFromParcel(Parcel source) {
            return new XposedConfig(source);
        }

        @Override
        public XposedConfig[] newArray(int size) {
            return new XposedConfig[size];
        }
    };
}
