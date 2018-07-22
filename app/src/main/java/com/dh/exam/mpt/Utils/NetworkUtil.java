package com.dh.exam.mpt.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dh.exam.mpt.MPTApplication;

/**
 * @author DavidHuang  at 下午10:02 18-7-22
 */
public class NetworkUtil {
    /**
     * 判断网络是否可用
     * @return 网络是否可用
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) MPTApplication.getContext()
                .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }
}
