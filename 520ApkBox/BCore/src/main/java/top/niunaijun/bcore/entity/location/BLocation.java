package top.niunaijun.bcore.entity.location;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class BLocation implements Parcelable {
    private double mLatitude = 0.0;
    private double mLongitude = 0.0;
    private double mAltitude = 0.0f;
    private float mSpeed = 0.0f;
    private float mBearing = 0.0f;
    private float mAccuracy = 0.0f;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.mLatitude);
        dest.writeDouble(this.mLongitude);
        dest.writeDouble(this.mAltitude);
        dest.writeFloat(this.mSpeed);
        dest.writeFloat(this.mBearing);
        dest.writeFloat(this.mAccuracy);
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public BLocation() { }

    public BLocation(double latitude, double mLongitude) {
        this.mLatitude = latitude;
        this.mLongitude = mLongitude;
    }

    public BLocation(Parcel in) {
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
        this.mAltitude = in.readDouble();
        this.mAccuracy = in.readFloat();
        this.mSpeed = in.readFloat();
        this.mBearing = in.readFloat();
    }

    public boolean isEmpty() {
        return mLatitude == 0 && mLongitude == 0;
    }

    public static final Parcelable.Creator<BLocation> CREATOR = new Parcelable.Creator<BLocation>() {
        @Override
        public BLocation createFromParcel(Parcel source) {
            return new BLocation(source);
        }

        @Override
        public BLocation[] newArray(int size) {
            return new BLocation[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "BLocation{" + "latitude: " + mLatitude + ", longitude: " + mLongitude + ", altitude: " + mAltitude + ", speed: " + mSpeed
                + ", bearing: " + mBearing + ", accuracy: " + mAccuracy + '}';
    }

    public Location convert2SystemLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(mLatitude);
        location.setLongitude(mLongitude);
        location.setSpeed(mSpeed);
        location.setBearing(mBearing);
        location.setAccuracy(40f);
        location.setTime(System.currentTimeMillis());
        Bundle extraBundle = new Bundle();
        // GPS satellite number
        int satelliteCount = 10;
        extraBundle.putInt("satellites", satelliteCount);
        extraBundle.putInt("satellitesvalue", satelliteCount);
        location.setExtras(extraBundle);
        return location;
    }

    public static String getSouthEast(BLocation location) {
        if (location.mLongitude > 0.0d) {
            return "E";
        }
        return "W";
    }

    public static String getNorthWest(BLocation location) {
        if (location.mLatitude > 0.0d) {
            return "N";
        }
        return "S";
    }

    public static String getGPSLatitude(double v) {
        int du = (int) v;
        double fen = (v - (double) du) * 60.0d;

        return du + leftZeroPad((int) fen) + ":" + String.valueOf(fen).substring(2);
    }

    private static String leftZeroPad(int num) {
        return leftZeroPad(String.valueOf(num));
    }

    private static String leftZeroPad(String num) {
        StringBuilder sb = new StringBuilder(2);
        int i;

        if (num == null) {
            for (i = 0; i < 2; i++) {
                sb.append('0');
            }
        } else {
            for (i = 0; i < 2 - num.length(); i++) {
                sb.append('0');
            }
            sb.append(num);
        }
        return sb.toString();
    }

    public static String checkSum(String nema) {
        String checkStr = nema;
        if (nema.startsWith("$")) {
            checkStr = nema.substring(1);
        }

        int sum = 0;
        for (int i = 0; i < checkStr.length(); i++) {
            sum ^= (byte) checkStr.charAt(i);
        }
        return nema + "*" + String.format("%02X", sum).toLowerCase();
    }
}
