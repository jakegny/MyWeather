package com.madmanwithagreenhat.myweather.sync;


import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.app.Service;

/**
 * Created by jakegny on 2/6/2015.
 */
public class AppSyncService extends Service {
    private static final Object syncAdapterLock = new Object();
    private static AppSyncAdapter syncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("AppSyncService", "onCreate - AppSyncService");
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new AppSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}