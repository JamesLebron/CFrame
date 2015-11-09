package com.android.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.util.AcUtil;
import com.android.util.Logs;


/**
 * Created by xudong on 2015/3/18.
 */
public class NetWorkReceiver extends BroadcastReceiver {
    public static boolean isNetWorkOk = true;
    public static int mNetWorkType = 0;

    /**
     * wifi网络
     */
    public static final int NETWORKTYPE_WIFI = 5;
    /**
     * 3G和3G以上网络，或统称为快速网络
     */
    public static final int NETWORKTYPE_3G = 3;
    /**
     * 4G 网络
     */
    public static final int NETWORKTYPE_LET = 4;
    /**
     * 没有网络
     */
    public static final int NETWORKTYPE_INVALID = 0;
    /**
     * wap网络
     */
    public static final int NETWORKTYPE_WAP = 1;
    /**
     * 2G网络
     */
    public static final int NETWORKTYPE_2G = 2;

    /**
     * 网络变更
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {


        mNetWorkType = getNetWorkType(context);
        for (com.android.base.BaseInterface ac : AcUtil.list) {
            ac.onNetWorkChange(mNetWorkType);
        }
        Logs.i("NETWORK_CHANGE ：", "NET CONNECT:" + isNetWorkOk + " mNetWorkType:" + mNetWorkType);
    }

    /**
     * 判断当前网络
     *
     * @param context
     * @return
     */
    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                mNetWorkType = NETWORKTYPE_2G;
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                mNetWorkType = NETWORKTYPE_2G;
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                mNetWorkType = NETWORKTYPE_2G;
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                mNetWorkType = NETWORKTYPE_3G;
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                mNetWorkType = NETWORKTYPE_3G;
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                mNetWorkType = NETWORKTYPE_2G;
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                mNetWorkType = NETWORKTYPE_3G;
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                mNetWorkType = NETWORKTYPE_3G;
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                mNetWorkType = NETWORKTYPE_3G;
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                mNetWorkType = NETWORKTYPE_3G;
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                mNetWorkType = NETWORKTYPE_3G;
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                mNetWorkType = NETWORKTYPE_3G;
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                mNetWorkType = NETWORKTYPE_2G;
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                mNetWorkType = NETWORKTYPE_LET;
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    /**
     * 获取当前网络类型
     *
     * @param context
     * @return
     */
    public static int getNetWorkType(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();

            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
//                isNetWorkOk = true;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                if (TextUtils.isEmpty(proxyHost)) {
                    isFastMobileNetwork(context);
                } else
                    mNetWorkType = NETWORKTYPE_WAP;
//                ? isFastMobileNetwork(context) : NETWORKTYPE_WAP;
//                mNetWorkType = TextUtils.isEmpty(proxyHost)
//                        ? (isFastMobileNetwork(context) ? NETWORKTYPE_3G : NETWORKTYPE_2G)
            }
            isNetWorkOk = true;
        } else {
            mNetWorkType = NETWORKTYPE_INVALID;
            isNetWorkOk = false;
        }

        return mNetWorkType;
    }
}
