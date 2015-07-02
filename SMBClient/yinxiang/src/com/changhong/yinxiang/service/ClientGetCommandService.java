package com.changhong.yinxiang.service;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

import android.app.ActivityManager;
import android.content.Context;
import com.changhong.common.domain.NetworkStatus;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.ClientSocketInterface;
import com.changhong.common.utils.MobilePerformanceUtils;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.yinxiang.setting.NetEstimateUtils;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class ClientGetCommandService extends Service implements ClientSocketInterface {

    protected static final String TAG = "CHTVhelper";

    private ActivityManager manager;
    /**
     * message handler
     */
    public static Handler mHandler = null;

    /**
     * the parameter which used for check the application will exist or not
     */
    private boolean exit = false;

    /**
     * client and heart time internal check
     */
    private long time = 0l;

    @Override
    public void onCreate() {
        super.onCreate();

        initView();

        initThreads();
    }

    private void initView() {
        manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        //set every activity title when client connect to server
                        ClientTitleSettingService.setClientActivityTitle();
                        break;
                    case 1:
                        //use change server refresh the all channel, please also check ClientSendCommandService
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    private void initThreads() {

        new GetServerIP().start();

        new BoxMinitorThread().start();

    }

    /*************************************************手机端不停的获得盒子广播线程*****************************************/

    private class GetServerIP extends Thread {

        public void run() {
            ClientSendCommandService.serverIpList.clear();
            DatagramSocket dgSocket = null;

            try {
                dgSocket = new DatagramSocket(SERVER_IP_POST_PORT);
                DatagramPacket dgPacket = null;

                while (true) {
                    try {
                        /**
                         * 接收Socket
                         */
                        byte[] by = new byte[512];
                        dgPacket = new DatagramPacket(by, by.length);
                        dgSocket.receive(dgPacket);

                        /**
                         * 处理Socket
                         */
                        String serverAddress = dgPacket.getAddress().getHostAddress();
						String content = new String(by, 0, dgPacket.getLength());
                        String[] tokens = StringUtils.delimitedListToStringArray(content, "|");
                        String boxName = NetworkUtils.convertCHBoxName(tokens[0]);
                        if (StringUtils.hasLength(serverAddress)) {
                            Log.w(TAG, serverAddress);

                            if (!ClientSendCommandService.serverIpList.contains(serverAddress)) {
                                ClientSendCommandService.serverIpList.add(serverAddress);
								ClientSendCommandService.serverIpListMap.put(serverAddress, boxName);
                                /**
                                 * 如果用户已经选择了IP，就不用选择了，如果为空，就按照系统自动分配
                                 */
                                if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
                                    ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(0);

                                }
                                ClientSendCommandService.titletxt = boxName;
                                time = System.currentTimeMillis();
                                /**
                                 * 更细所有的频道TITLE
                                 */
                                mHandler.sendEmptyMessage(0);
                                Log.e("COMMAND_CLEAN_1", serverAddress + "-" + ClientSendCommandService.serverIP);

                            } else if (ClientSendCommandService.serverIP != null && serverAddress.equals(ClientSendCommandService.serverIP)) {
                                Log.e("COMMAND_CLEAN_2", serverAddress + "-" + ClientSendCommandService.serverIP);
                                /**
                                 * 更新当前server的活动时间
                                 */
                                time = System.currentTimeMillis();

                                /**
                                 * 设置服务端网络状态
                                 */
                                try {
                                    NetEstimateUtils.serverNetworkStatus = NetworkStatus.valueOf(tokens[1]);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                /**
                                 * 音乐和视频播放
                                 */
                                if (tokens.length == 6) {
                                    ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
                                    String shortClassName = info.topActivity.getClassName();

                                    //视频播放
                                    if (tokens[2].equals("vedio_play")) {
//                                        if ("com.changhong.touying.activity.VedioDetailsActivity".equals(shortClassName)) {
//                                            try {
//                                                if (VedioDetailsActivity.handler != null) {
//                                                    Message message = new Message();
//                                                    message.what = 0;
//                                                    message.obj = tokens[3] + "|" + tokens[4] + "|" + tokens[5];
//                                                    VedioDetailsActivity.handler.sendMessage(message);
//                                                }
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
                                    }

                                    //音乐播放
                                    if (tokens[2].equals("music_play")) {
//                                        if ("com.changhong.touying.activity.MusicDetailsActivity".equals(shortClassName)) {
//                                            try {
//                                                if (MusicDetailsActivity.handler != null) {
//                                                    Message message = new Message();
//                                                    message.what = 0;
//                                                    message.obj = tokens[3] + "|" + tokens[4] + "|" + tokens[5];
//                                                    MusicDetailsActivity.handler.sendMessage(message);
//                                                }
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }

                                    }
                                }

                                /**
                                 * 音乐和是视频播放结束
                                 */
                                if (tokens.length == 4 && tokens[2].equals("play_stop")) {
                                    //视频和音乐播放停止, 视频的停止信号为1，因为的停止信号为2
//                                    int stopTag = Integer.valueOf(tokens[3]);
//
//                                    if (stopTag == 1) {
//                                        if (VedioDetailsActivity.handler != null) {
//                                            VedioDetailsActivity.handler.sendEmptyMessage(1);
//                                        }
//                                    } else if (stopTag == 2) {
//                                        if (MusicDetailsActivity.handler != null) {
//                                            MusicDetailsActivity.handler.sendEmptyMessage(1);
//                                        }
//                                    }
                                }

                                /**
                                 * 没有播放音频和视频的情况, 关闭httpserver
                                 */
                                if(tokens.length == 2) {
                                    //HTTPD的使用状态
                                    MobilePerformanceUtils.httpServerUsing = false;
                                }
                            } else {
                                Log.e("COMMAND_CLEAN_3", serverAddress + "-" + ClientSendCommandService.serverIP);
                            }
                        }
                        if (exit) {
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        dgPacket = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dgSocket != null) {
                        dgSocket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /***********************************************手机端不停的监控盒子是否有广播发出**************************************/

    private class BoxMinitorThread extends Thread {

        public void run() {
            while (true) {
                long during = System.currentTimeMillis() - time;
                if (during > 4000 && time != 0l) {
                    Log.e("COMMAND_CLEAN", String.valueOf(during));
                    clearIpList();
                }
                SystemClock.sleep(1000);
            }
        }
    }

    private void clearIpList() {
        ClientSendCommandService.serverIpList.clear();
        ClientSendCommandService.serverIP = null;
        ClientSendCommandService.titletxt = "未连接";
        mHandler.sendEmptyMessage(0);
        time = 0l;
    }

    /**
     * *****************************************************系统重载部分*******************************************
     */

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exit = true;
    }

}

