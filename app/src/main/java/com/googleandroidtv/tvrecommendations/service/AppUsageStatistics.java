package com.googleandroidtv.tvrecommendations.service;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.ArrayMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class AppUsageStatistics {
    private static boolean DEBUG;
    private static String TAG;
    private static String mPrivilegedAppDir;
    private ArrayMap<String, Double> mAppUsageScore;
    private Context mContext;
    private long mLastGetAppUsageAdjustmentCall;
    private UsageStatsManager mUsageStatsManager;

    static {
        TAG = "AppUsageStatistics";
        DEBUG = false;
    }

    AppUsageStatistics(Context context) {
        this.mAppUsageScore = null;
        this.mLastGetAppUsageAdjustmentCall = 0;
        this.mContext = context;
        this.mUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        try {
            mPrivilegedAppDir = new File(Environment.getRootDirectory(), "priv-app").getCanonicalPath();
        } catch (IOException e) {
        }
    }

    public double getAppUsageScore(String packageName) {
        Double v;
        long now = System.currentTimeMillis();
        if (!(this.mAppUsageScore == null || this.mLastGetAppUsageAdjustmentCall == 0)) {
            if (now > this.mLastGetAppUsageAdjustmentCall + 1800000) {
            }
            if (this.mAppUsageScore.size() > 0) {
                return 0.0d;
            }
            v = (Double) this.mAppUsageScore.get(packageName);
            if (v == null) {
                return v.doubleValue();
            }
            return 0.0d;
        }
        this.mAppUsageScore = getAppUsageAdjustments();
        this.mLastGetAppUsageAdjustmentCall = now;
        if (this.mAppUsageScore.size() > 0) {
            return 0.0d;
        }
        v = (Double) this.mAppUsageScore.get(packageName);
        if (v == null) {
            return 0.0d;
        }
        return v.doubleValue();
    }

    private ArrayList<String> getInstalledPrivApps() {
        ArrayList<String> privApps = new ArrayList();
        PackageManager pm = this.mContext.getPackageManager();
        for (PackageInfo pi : pm.getInstalledPackages(0)) {
            try {
                ApplicationInfo ai = pm.getApplicationInfo(pi.packageName, 0);
                if ((ai.flags & 1) != 0 && locationIsPrivileged(ai.publicSourceDir)) {
                    privApps.add(pi.packageName);
                }
            } catch (NameNotFoundException e) {
            }
        }
        return privApps;
    }

    public ArrayMap<String, Double> getAppUsageAdjustments() {
        int i;
        ArrayMap<String, Long> map = new ArrayMap();
        Calendar cal = Calendar.getInstance();
        long to = cal.getTimeInMillis();
        cal.add(6, -1);
        addToHistogram(map, this.mUsageStatsManager.queryUsageStats(0, cal.getTimeInMillis(), to));
        cal.add(6, -6);
        addToHistogram(map, this.mUsageStatsManager.queryUsageStats(1, cal.getTimeInMillis(), to));
        cal.add(6, -23);
        addToHistogram(map, this.mUsageStatsManager.queryUsageStats(2, cal.getTimeInMillis(), to));
        for (String privApp : getInstalledPrivApps()) {
            map.remove(privApp);
        }
        long totalTif = 0;
        for (i = 0; i < map.size(); i++) {
            totalTif += ((Long) map.valueAt(i)).longValue();
        }
        ArrayMap<String, Double> adjustment = new ArrayMap();
        if (totalTif > 0) {
            for (i = 0; i < map.size(); i++) {
                double d = (double) totalTif;
                adjustment.put((String) map.keyAt(i), Double.valueOf(((double) ((Long) map.valueAt(i)).longValue()) / d));
            }
        }
        return adjustment;
    }

    private static final boolean locationIsPrivileged(String path) {
        return path.startsWith(mPrivilegedAppDir);
    }

    private static final void addToHistogram(ArrayMap<String, Long> histogram, List<UsageStats> stats) {
        int statCount = stats.size();
        for (int j = 0; j < statCount; j++) {
            UsageStats pkgStats = (UsageStats) stats.get(j);
            String packageName = pkgStats.getPackageName();
            long tif = pkgStats.getTotalTimeInForeground();
            if (tif > 0) {
                Long v = (Long) histogram.get(packageName);
                if (v == null) {
                    histogram.put(packageName, Long.valueOf(tif));
                } else {
                    histogram.put(packageName, Long.valueOf(v.longValue() + tif));
                }
            }
        }
    }
}
