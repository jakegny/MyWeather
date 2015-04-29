package com.madmanwithagreenhat.myweather.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by jakegny on 2/6/2015.
 */
public class AppAuthenticatorService  extends Service {
    // Instance field that stores the authenticator object
    private AppAuthenticator authenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        authenticator = new AppAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
