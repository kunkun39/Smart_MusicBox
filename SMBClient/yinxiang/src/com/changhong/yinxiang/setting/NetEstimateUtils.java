package com.changhong.yinxiang.setting;

import android.content.Context;
import android.widget.Toast;
import com.changhong.common.domain.NetworkStatus;
import com.changhong.common.utils.NetworkUtils;

/**
 * Created by Jack Wang
 */
public class NetEstimateUtils {

    public static NetworkStatus serverNetworkStatus = NetworkStatus.NET_NULL;

    /**
     * this method is used for tell end user which status for current box and phone
     * <p/>
     * if box network is wifi, should tell end user "请用有线连接机顶盒，否则很难保证数字电视播放流畅度"
     * if box network is wired and phone is connect to 2.4G, should tell end user "当前连接是在2.4G频段，可能高清节目的播放会卡顿"
     */
    public static void noticeEndUserNetworkStatus(Context context) {
        if (serverNetworkStatus.equals(NetworkStatus.NET_WIRELESS_24G)) {
            Toast.makeText(context, "请用有线连接机顶盒，否则很难保证数字电视播放流畅度", Toast.LENGTH_LONG).show();
            return;
        }

        NetworkStatus status = NetworkUtils.getMobileNetworkStatus(context);
        if (NetworkStatus.NET_WIRELESS_24G.equals(status)) {
            Toast.makeText(context, "当前连接是在2.4G频段，可能高清节目的播放会卡顿", Toast.LENGTH_LONG).show();
        }
    }
}
