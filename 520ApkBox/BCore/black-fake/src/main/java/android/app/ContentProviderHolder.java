package android.app;

import android.content.IContentProvider;
import android.content.pm.ProviderInfo;
import android.os.IBinder;

public class ContentProviderHolder {
    public final ProviderInfo info = null;
    public IContentProvider provider;
    public IBinder connection;
}
