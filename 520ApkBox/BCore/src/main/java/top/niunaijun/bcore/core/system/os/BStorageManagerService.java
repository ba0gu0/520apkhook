package top.niunaijun.bcore.core.system.os;

import android.net.Uri;
import android.os.Process;
import android.os.storage.StorageVolume;

import java.io.File;

import black.android.os.storage.StorageManager;
import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.core.env.BEnvironment;
import top.niunaijun.bcore.core.system.ISystemService;
import top.niunaijun.bcore.core.system.user.BUserHandle;
import top.niunaijun.bcore.fake.provider.FileProvider;
import top.niunaijun.bcore.proxy.ProxyManifest;
import top.niunaijun.bcore.utils.compat.BuildCompat;

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
