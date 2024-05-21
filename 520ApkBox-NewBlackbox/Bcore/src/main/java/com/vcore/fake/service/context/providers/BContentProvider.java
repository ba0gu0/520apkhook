package com.vcore.fake.service.context.providers;

import android.os.IInterface;

public interface BContentProvider {
    IInterface wrapper(final IInterface contentProviderProxy, final String appPkg);
}
