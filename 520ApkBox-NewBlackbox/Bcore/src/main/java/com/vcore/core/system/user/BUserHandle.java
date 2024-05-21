package com.vcore.core.system.user;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;

import androidx.annotation.NonNull;

/**
 * Representation of a user on the device.
 */
public final class BUserHandle implements Parcelable {
    // NOTE: keep logic in sync with system/core/libcutils/multiuser.c

    /**
     * @hide Range of uids allocated for a user.
     */
    public static final int PER_USER_RANGE = 100000;

    /**
     * @hide A user id to indicate all users on the device
     */
    public static final int USER_ALL = -1;

    /**
     * @hide A user handle to indicate all users on the device
     */
    public static final BUserHandle ALL = new BUserHandle(USER_ALL);

    /**
     * @hide A user id to indicate the currently active user
     */
    public static final int USER_CURRENT = -2;

    /**
     * @hide A user handle to indicate the current user of the device
     */
    public static final BUserHandle CURRENT = new BUserHandle(USER_CURRENT);

    public static final int USER_XPOSED = -4;

    /**
     * @hide An undefined user id
     */
    public static final int USER_NULL = -10000;

    /**
     * @hide A user id constant to indicate the "owner" user of the device
     * @deprecated Consider using either {@link BUserHandle#USER_SYSTEM} constant or
     * check the target user's flag {@link }.
     */
    @Deprecated
    public static final int USER_OWNER = 0;

    /**
     * @hide A user handle to indicate the primary/owner user of the device
     * @deprecated Consider using either {@link BUserHandle#SYSTEM} constant or
     * check the target user's flag {@link }.
     */
    @Deprecated
    public static final BUserHandle OWNER = new BUserHandle(USER_OWNER);

    /**
     * @hide A user id constant to indicate the "system" user of the device
     */
    public static final int USER_SYSTEM = 0;

    /**
     * @hide A user handle to indicate the "system" user of the device
     */
    public static final BUserHandle SYSTEM = new BUserHandle(USER_SYSTEM);

    /**
     * @hide Enable multi-user related side effects. Set this to false if
     * there are problems with single user use-cases.
     */
    public static final boolean MU_ENABLED = true;

    /**
     * @hide
     */
    public static final int AID_APP_START = android.os.Process.FIRST_APPLICATION_UID;

    final int mHandle;

    /**
     * Whether a UID belongs to a system core component or not.
     *
     * @hide
     */
    public static boolean isCore(int uid) {
        if (uid >= 0) {
            final int appId = getAppId(uid);
            return appId < Process.FIRST_APPLICATION_UID;
        } else {
            return false;
        }
    }

    /**
     * Returns the user id for a given uid.
     *
     * @hide
     */
    public static int getUserId(int uid) {
        if (MU_ENABLED) {
            return uid / PER_USER_RANGE;
        } else {
            return BUserHandle.USER_SYSTEM;
        }
    }

    /**
     * @hide
     */
    public static BUserHandle of(int userId) {
        return userId == USER_SYSTEM ? SYSTEM : new BUserHandle(userId);
    }

    /**
     * Returns the uid that is composed from the userId and the appId.
     *
     * @hide
     */
    public static int getUid(int userId, int appId) {
        if (MU_ENABLED) {
            return userId * PER_USER_RANGE + (appId % PER_USER_RANGE);
        } else {
            return appId;
        }
    }

    /**
     * Returns the app id (or base uid) for a given uid, stripping out the user id from it.
     *
     * @hide
     */
    public static int getAppId(int uid) {
        return uid % PER_USER_RANGE;
    }

    /**
     * Returns true if this UserHandle refers to the owner user; false otherwise.
     *
     * @return true if this UserHandle refers to the owner user; false otherwise.
     * @hide
     * @deprecated please use {@link #isSystem()} or check for
     */
    @Deprecated
    public boolean isOwner() {
        return this.equals(OWNER);
    }

    /**
     * @return true if this UserHandle refers to the system user; false otherwise.
     * @hide
     */
    public boolean isSystem() {
        return this.equals(SYSTEM);
    }

    /**
     * @hide
     */
    public BUserHandle(int h) {
        this.mHandle = h;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserHandle{" + mHandle + "}";
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (obj instanceof BUserHandle) {
                BUserHandle other = (BUserHandle) obj;
                return mHandle == other.mHandle;
            }
        } catch (ClassCastException ignored) { }
        return false;
    }

    @Override
    public int hashCode() {
        return mHandle;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mHandle);
    }

    /**
     * Write a UserHandle to a Parcel, handling null pointers.  Must be
     * read with {@link #readFromParcel(Parcel)}.
     *
     * @param h   The UserHandle to be written.
     * @param out The Parcel in which the UserHandle will be placed.
     * @see #readFromParcel(Parcel)
     */
    public static void writeToParcel(BUserHandle h, Parcel out) {
        if (h != null) {
            h.writeToParcel(out, 0);
        } else {
            out.writeInt(USER_NULL);
        }
    }

    public static final Parcelable.Creator<BUserHandle> CREATOR = new Creator<BUserHandle>() {
        public BUserHandle createFromParcel(Parcel in) {
            return new BUserHandle(in);
        }

        public BUserHandle[] newArray(int size) {
            return new BUserHandle[size];
        }
    };

    /**
     * Instantiate a new UserHandle from the data in a Parcel that was
     * previously written with {@link #writeToParcel(Parcel, int)}.  Note that you
     * must not use this with data written by
     * {@link #writeToParcel(BUserHandle, Parcel)} since it is not possible
     * to handle a null UserHandle here.
     *
     * @param in The Parcel containing the previously written UserHandle,
     *           positioned at the location in the buffer where it was written.
     */
    public BUserHandle(Parcel in) {
        this.mHandle = in.readInt();
    }
}
