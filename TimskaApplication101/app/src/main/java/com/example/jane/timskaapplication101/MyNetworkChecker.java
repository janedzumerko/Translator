package com.example.jane.timskaapplication101;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by Jane on 8/20/2015.
 */
public final class MyNetworkChecker {

    /** Private constructor to disable instantiation. */
    private MyNetworkChecker() {}


    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i] != null) {
                        Log.i("MREZA", info[i].toString());
                    }
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isWifiAvailable(final Context context) {
        boolean isWiFi = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        } catch (Exception e) {
            return false;
        }finally {
            return isWiFi;
        }
    }


    public static boolean isMobileDataAvailable(final Context context) {
        boolean isMobileData = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isMobileData = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        } catch (Exception e) {
            return false;
        }finally {
            return isMobileData;
        }
    }

    // ova valjda e slicno so gornono - proveri go na 4G
    public static boolean is3GConnected(final Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = connectivity.getActiveNetworkInfo();
            boolean notWifi = network.getType() != ConnectivityManager.TYPE_WIFI
                    && network.isConnected();
            return notWifi;
        } catch (Exception e) {
            Log.i("MREZA" , "is3GConnected false " + e.getMessage());
            return false;
        }
    }

}
