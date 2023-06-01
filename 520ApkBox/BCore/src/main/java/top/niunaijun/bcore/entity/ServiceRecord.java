package top.niunaijun.bcore.entity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceRecord {
    private Service mService;
    private final Map<Intent.FilterComparison, BoundInfo> mBounds = new HashMap<>();
    private boolean rebind;
    private int mStartId;

    public static class BoundInfo {
        private IBinder mIBinder;
        private final AtomicInteger mBindCount = new AtomicInteger(0);

        public void incrementAndGetBindCount() {
            mBindCount.incrementAndGet();
        }

        public int decrementAndGetBindCount() {
            return mBindCount.decrementAndGet();
        }

        public IBinder getIBinder() {
            return mIBinder;
        }

        public void setIBinder(IBinder IBinder) {
            mIBinder = IBinder;
        }
    }

    public int getStartId() {
        return mStartId;
    }

    public void setStartId(int startId) {
        mStartId = startId;
    }

    public Service getService() {
        return mService;
    }

    public void setService(Service service) {
        mService = service;
    }

    public IBinder getBinder(Intent intent) {
        BoundInfo boundInfo = getOrCreateBoundInfo(intent);
        return boundInfo.getIBinder();
    }

    public boolean hasBinder(Intent intent) {
        BoundInfo boundInfo = getOrCreateBoundInfo(intent);
        return boundInfo.getIBinder() != null;
    }

    public void addBinder(Intent intent, final IBinder iBinder) {
        final Intent.FilterComparison filterComparison = new Intent.FilterComparison(intent);
        BoundInfo boundInfo = getOrCreateBoundInfo(intent);
        if (boundInfo == null) {
            boundInfo = new BoundInfo();
            mBounds.put(filterComparison, boundInfo);
        }

        boundInfo.setIBinder(iBinder);
        try {
            iBinder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    iBinder.unlinkToDeath(this, 0);
                    mBounds.remove(filterComparison);
                }
            }, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void incrementAndGetBindCount(Intent intent) {
        BoundInfo boundInfo = getOrCreateBoundInfo(intent);
        boundInfo.incrementAndGetBindCount();
    }

    public boolean decreaseConnectionCount(Intent intent) {
        Intent.FilterComparison filterComparison = new Intent.FilterComparison(intent);
        BoundInfo boundInfo = mBounds.get(filterComparison);
        if (boundInfo == null) {
            return true;
        }
        int i = boundInfo.decrementAndGetBindCount();
        return i <= 0;
    }

    public BoundInfo getOrCreateBoundInfo(Intent intent) {
        Intent.FilterComparison filterComparison = new Intent.FilterComparison(intent);
        BoundInfo boundInfo = mBounds.get(filterComparison);
        if (boundInfo == null) {
            boundInfo = new BoundInfo();
            mBounds.put(filterComparison, boundInfo);
        }
        return boundInfo;
    }

    public boolean isRebind() {
        return rebind;
    }

    public void setRebind(boolean rebind) {
        this.rebind = rebind;
    }
}
