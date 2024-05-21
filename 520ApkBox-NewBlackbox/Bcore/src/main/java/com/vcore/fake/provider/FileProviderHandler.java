package com.vcore.fake.provider;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.net.Uri;

import java.io.File;
import java.util.List;

import com.vcore.BlackBoxCore;
import com.vcore.app.BActivityThread;
import com.vcore.utils.compat.BuildCompat;

public class FileProviderHandler {
    public static Uri convertFileUri(Context context, Uri uri) {
        if (BuildCompat.isN()) {
            File file = convertFile(context, uri);
            if (file == null) {
                return null;
            }
            return BlackBoxCore.getBStorageManager().getUriForFile(file.getAbsolutePath());
        }
        return uri;
    }

    public static File convertFile(Context context, Uri uri) {
        List<ProviderInfo> providers = BActivityThread.getProviders();
        for (ProviderInfo provider : providers) {
            try {
                File fileForUri = FileProvider.getFileForUri(context, provider.authority, uri);
                if (fileForUri != null && fileForUri.exists()) {
                    return fileForUri;
                }
            } catch (Exception ignored) { }
        }
        return null;
    }
}
