package com.vcore.core.system.os;

import android.net.Uri;
import android.os.Process;
import android.os.storage.StorageVolume;

import java.io.File;

import black.android.os.storage.StorageManager;
import com.vcore.BlackBoxCore;
import com.vcore.core.env.BEnvironment;
import com.vcore.core.system.ISystemService;
import com.vcore.core.system.user.BUserHandle;
import com.vcore.fake.provider.FileProvider;
import com.vcore.proxy.ProxyManifest;
import com.vcore.utils.compat.BuildCompat;

public class BStorageManagerService extends IBStorageManagerService.Stub implements ISystemService {
    private static final BStorageManagerService sService = new BStorageManagerService();

    public static BStorageManagerService get() {
        return sService;
    }

    public BStorageManagerService() { }

    @Override
    public StorageVolume[] getVolumeList(int uid, String packageName, int flags, int userId) {
        if (StorageManager.getVolumeList == null) {
            return null;
        }

        try {
            StorageVolume[] storageVolumes = StorageManager.getVolumeList.call(BUserHandle.getUserId(Process.myUid()), 0);
            if (storageVolumes == null) {
                return null;
            }

            for (StorageVolume storageVolume : storageVolumes) {
                black.android.os.storage.StorageVolume.mPath.set(storageVolume, BEnvironment.getExternalUserDir(userId));
                if (BuildCompat.isPie()) {
                    black.android.os.storage.StorageVolume.mInternalPath.set(storageVolume, BEnvironment.getExternalUserDir(userId));
                }
            }
            return storageVolumes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Uri getUriForFile(String file) {
        return FileProvider.getUriForFile(BlackBoxCore.getContext(), ProxyManifest.getProxyFileProvider(), new File(file));
    }

    @Override
    public void systemReady() { }
}
