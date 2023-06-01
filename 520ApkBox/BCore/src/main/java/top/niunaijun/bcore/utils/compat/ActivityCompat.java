package top.niunaijun.bcore.utils.compat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;

import top.niunaijun.bcore.app.BActivityThread;
import top.niunaijun.bcore.utils.ArrayUtils;
import top.niunaijun.bcore.utils.DrawableUtils;

public class ActivityCompat {
    public static void fix(Activity activity) {
        Context baseContext = activity.getBaseContext();
        try {
            TypedArray typedArray = activity.obtainStyledAttributes(ArrayUtils.toInt(black.com.android.internal.R.styleable.Window.get()));
            if (typedArray != null) {
                boolean isShowWallpaper = typedArray.getBoolean(black.com.android.internal.R.styleable.Window_windowShowWallpaper.get(), false);
                if (isShowWallpaper) {
                    activity.getWindow().setBackgroundDrawable(WallpaperManager.getInstance(activity).getDrawable());
                }

                boolean isFullscreen = typedArray.getBoolean(black.com.android.internal.R.styleable.Window_windowFullscreen.get(), false);
                if (isFullscreen) {
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                typedArray.recycle();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (BuildCompat.isL()) {
            Intent intent = activity.getIntent();
            ApplicationInfo applicationInfo = baseContext.getApplicationInfo();
            PackageManager packageManager = activity.getPackageManager();

            if (intent != null && activity.isTaskRoot()) {
                try {
                    String taskDescriptionLabel = TaskDescriptionCompat.getTaskDescriptionLabel(BActivityThread.getUserId(), applicationInfo.loadLabel(packageManager));

                    Bitmap icon = null;
                    Drawable activityIcon = getActivityIcon(activity);
                    if (activityIcon != null) {
                        ActivityManager activityManager = (ActivityManager) baseContext.getSystemService(Context.ACTIVITY_SERVICE);

                        int iconSize = activityManager.getLauncherLargeIconSize();
                        icon = DrawableUtils.drawableToBitmap(activityIcon, iconSize, iconSize);
                    }
                    activity.setTaskDescription(new ActivityManager.TaskDescription(taskDescriptionLabel, icon));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Drawable getActivityIcon(Activity activity) {
        PackageManager packageManager = activity.getPackageManager();
        try {
            Drawable icon = packageManager.getActivityIcon(activity.getComponentName());
            if (icon != null) {
                return icon;
            }
        } catch (PackageManager.NameNotFoundException ignore) { }

        ApplicationInfo applicationInfo = activity.getApplicationInfo();
        return applicationInfo.loadIcon(packageManager);
    }
}