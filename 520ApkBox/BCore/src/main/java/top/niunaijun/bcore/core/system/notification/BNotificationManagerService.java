package top.niunaijun.bcore.core.system.notification;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import black.android.app.NotificationO;
import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.core.system.BProcessManagerService;
import top.niunaijun.bcore.core.system.ISystemService;
import top.niunaijun.bcore.core.system.ProcessRecord;
import top.niunaijun.bcore.utils.compat.BuildCompat;

public class BNotificationManagerService extends IBNotificationManagerService.Stub implements ISystemService {
    private final static BNotificationManagerService sService = new BNotificationManagerService();
    public static final String CHANNEL_BLACK = "@black-";
    public static final String GROUP_BLACK = "@black-group-";
    private final Map<String, NotificationRecord> mNotificationRecords = new HashMap<>();

    private final NotificationManager mRealNotificationManager = (NotificationManager) BlackBoxCore.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

    public static BNotificationManagerService get() {
        return sService;
    }

    @Override
    public void systemReady() { }

    private NotificationRecord getNotificationRecord(String packageName, int userId) {
        String key = packageName + "-" + userId;
        synchronized (mNotificationRecords) {
            NotificationRecord notificationRecord = mNotificationRecords.get(key);
            if (notificationRecord == null) {
                notificationRecord = new NotificationRecord();
                mNotificationRecords.put(key, notificationRecord);
            }
            return notificationRecord;
        }
    }

    private void removeNotificationRecord(String packageName, int userId) {
        String key = packageName + "-" + userId;
        synchronized (mNotificationRecords) {
            mNotificationRecords.remove(key);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.O)
    public NotificationChannel getNotificationChannel(String channelId, int userId) {
        int callingPid = getCallingPid();
        ProcessRecord processByPid = BProcessManagerService.get().findProcessByPid(callingPid);
        if (processByPid == null) {
            return null;
        }

        NotificationRecord notificationRecord = getNotificationRecord(processByPid.getPackageName(), userId);
        synchronized (notificationRecord.mNotificationChannels) {
            return notificationRecord.mNotificationChannels.get(channelId);
        }
    }

    @Override
    public List<NotificationChannel> getNotificationChannels(String packageName, int userId) {
        NotificationRecord notificationRecord = getNotificationRecord(packageName, userId);
        synchronized (notificationRecord.mNotificationChannels) {
            return new ArrayList<>(notificationRecord.mNotificationChannels.values());
        }
    }

    @Override
    public List<NotificationChannelGroup> getNotificationChannelGroups(String packageName, int userId) {
        NotificationRecord notificationRecord = getNotificationRecord(packageName, userId);
        synchronized (notificationRecord.mNotificationChannelGroups) {
            return new ArrayList<>(notificationRecord.mNotificationChannelGroups.values());
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannel(NotificationChannel notificationChannel, int userId) {
        int callingPid = getCallingPid();
        ProcessRecord processByPid = BProcessManagerService.get().findProcessByPid(callingPid);
        if (processByPid == null) {
            return;
        }

        handleNotificationChannel(notificationChannel, userId);
        mRealNotificationManager.createNotificationChannel(notificationChannel);

        resetNotificationChannel(notificationChannel);
        NotificationRecord notificationRecord = getNotificationRecord(processByPid.getPackageName(), userId);
        synchronized (notificationRecord.mNotificationChannels) {
            notificationRecord.mNotificationChannels.put(notificationChannel.getId(), notificationChannel);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.O)
    public void deleteNotificationChannel(String channelId, int userId) {
        int callingPid = getCallingPid();
        ProcessRecord processByPid = BProcessManagerService.get().findProcessByPid(callingPid);
        if (processByPid == null) {
            return;
        }

        NotificationRecord notificationRecord = getNotificationRecord(processByPid.getPackageName(), userId);
        synchronized (notificationRecord.mNotificationChannels) {
            NotificationChannel remove = notificationRecord.mNotificationChannels.remove(channelId);
            if (remove != null) {
                String blackChannelId = getBlackChannelId(remove.getId(), userId);
                mRealNotificationManager.deleteNotificationChannel(blackChannelId);
            }
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannelGroup(NotificationChannelGroup notificationChannelGroup, int userId) {
        int callingPid = getCallingPid();
        ProcessRecord processByPid = BProcessManagerService.get().findProcessByPid(callingPid);
        if (processByPid == null) {
            return;
        }

        handleNotificationGroup(notificationChannelGroup, userId);
        mRealNotificationManager.createNotificationChannelGroup(notificationChannelGroup);

        resetNotificationGroup(notificationChannelGroup);
        NotificationRecord notificationRecord = getNotificationRecord(processByPid.getPackageName(), userId);
        synchronized (notificationRecord.mNotificationChannelGroups) {
            notificationRecord.mNotificationChannelGroups.put(notificationChannelGroup.getId(), notificationChannelGroup);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.O)
    public void deleteNotificationChannelGroup(String groupId, int userId) {
        int callingPid = getCallingPid();
        ProcessRecord processByPid = BProcessManagerService.get().findProcessByPid(callingPid);
        if (processByPid == null) {
            return;
        }

        NotificationRecord notificationRecord = getNotificationRecord(processByPid.getPackageName(), userId);
        synchronized (notificationRecord.mNotificationChannelGroups) {
            NotificationChannelGroup remove = notificationRecord.mNotificationChannelGroups.remove(groupId);
            if (remove != null) {
                String blackGroupId = getBlackGroupId(remove.getId(), userId);
                mRealNotificationManager.deleteNotificationChannelGroup(blackGroupId);
            }
        }
    }

    @Override
    public void enqueueNotificationWithTag(int id, String tag, Notification notification, int userId) {
        ProcessRecord processByPid = BProcessManagerService.get().findProcessByPid(Binder.getCallingPid());
        if (processByPid == null) {
            return;
        }

        int notificationId = getNotificationId(userId, id, processByPid.getPackageName());
        if (BuildCompat.isOreo()) {
            if (NotificationO.mChannelId != null) {
                String blackChannelId = getBlackChannelId(NotificationO.mChannelId.get(), userId);
                NotificationO.mChannelId.set(blackChannelId);
            }
			
            if (NotificationO.mGroupKey != null) {
                String blackGroupId = getBlackGroupId(NotificationO.mGroupKey.get(), userId);
                NotificationO.mGroupKey.set(blackGroupId);
            }
        }

        NotificationRecord notificationRecord = getNotificationRecord(processByPid.getPackageName(), userId);
        synchronized (notificationRecord.mIds) {
            notificationRecord.mIds.add(notificationId);
        }
        mRealNotificationManager.notify(notificationId, notification);
    }

    @Override
    public void cancelNotificationWithTag(int id, String tag, int userId) {
        ProcessRecord processByPid = BProcessManagerService.get().findProcessByPid(Binder.getCallingPid());
        if (processByPid == null) {
            return;
        }

        int notificationId = getNotificationId(userId, id, processByPid.getPackageName());
        mRealNotificationManager.cancel(notificationId);

        NotificationRecord notificationRecord = getNotificationRecord(processByPid.getPackageName(), userId);
        synchronized (notificationRecord.mIds) {
            notificationRecord.mIds.remove(notificationId);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void handleNotificationChannel(NotificationChannel notificationChannel, int userId) {
        String channelId = black.android.app.NotificationChannel.mId.get(notificationChannel);
        String blackChannelId = getBlackChannelId(channelId, userId);

        black.android.app.NotificationChannel.mId.set(notificationChannel, blackChannelId);
        notificationChannel.setGroup(getBlackGroupId(notificationChannel.getGroup(), userId));
    }

    private void resetNotificationChannel(NotificationChannel notificationChannel) {
        String channelId = black.android.app.NotificationChannel.mId.get(notificationChannel);
        String realChannelId = getRealChannelId(channelId);
        black.android.app.NotificationChannel.mId.set(notificationChannel, realChannelId);
    }

    private void handleNotificationGroup(NotificationChannelGroup notificationChannelGroup, int userId) {
        String groupId = black.android.app.NotificationChannelGroup.mId.get(notificationChannelGroup);
        String blackGroupId = getBlackGroupId(groupId, userId);
        black.android.app.NotificationChannelGroup.mId.set(notificationChannelGroup, blackGroupId);

        List<NotificationChannel> notificationChannels = black.android.app.NotificationChannelGroup.mChannels.get(notificationChannelGroup);
        if (notificationChannels != null) {
            for (NotificationChannel notificationChannel : notificationChannels) {
                createNotificationChannel(notificationChannel, userId);
            }
        }
    }

    private void resetNotificationGroup(NotificationChannelGroup notificationChannelGroup) {
        String groupId = black.android.app.NotificationChannelGroup.mId.get(notificationChannelGroup);
        String realGroupId = getRealGroupId(groupId);
        black.android.app.NotificationChannelGroup.mId.set(notificationChannelGroup, realGroupId);

        List<NotificationChannel> notificationChannels = black.android.app.NotificationChannelGroup.mChannels.get(notificationChannelGroup);
        if (notificationChannels != null) {
            for (NotificationChannel notificationChannel : notificationChannels) {
                resetNotificationChannel(notificationChannel);
            }
        }
    }

    @SuppressLint("NewApi")
    public void deletePackageNotification(String packageName, int userId) {
        NotificationRecord notificationRecord = getNotificationRecord(packageName, userId);
        if (BuildCompat.isOreo()) {
            for (NotificationChannelGroup value : notificationRecord.mNotificationChannelGroups.values()) {
                String blackGroupId = getBlackGroupId(value.getId(), userId);
                mRealNotificationManager.deleteNotificationChannelGroup(blackGroupId);
            }

            for (NotificationChannel value : notificationRecord.mNotificationChannels.values()) {
                String blackChannelId = getBlackChannelId(value.getId(), userId);
                mRealNotificationManager.deleteNotificationChannel(blackChannelId);
            }
        }

        for (Integer id : notificationRecord.mIds) {
            mRealNotificationManager.cancel(id);
        }
        removeNotificationRecord(packageName, userId);
    }

    private String getBlackChannelId(String channelId, int userId) {
        if (channelId == null || channelId.contains(CHANNEL_BLACK)) {
            return channelId;
        }
        return channelId + CHANNEL_BLACK + userId;
    }

    private String getRealChannelId(String channelId) {
        if (channelId == null || !channelId.contains(CHANNEL_BLACK)) {
            return channelId;
        }
        return channelId.split(CHANNEL_BLACK)[0];
    }

    private String getBlackGroupId(String groupId, int userId) {
        if (groupId == null || groupId.contains(GROUP_BLACK)) {
            return groupId;
        }
        return groupId + GROUP_BLACK + userId;
    }

    private String getRealGroupId(String groupId) {
        if (groupId == null || !groupId.contains(GROUP_BLACK)) {
            return groupId;
        }
        return groupId.split(GROUP_BLACK)[0];
    }

    public static int getNotificationId(int userId, int notificationId, String packageName) {
        return (packageName + userId + notificationId).hashCode();
    }
}
