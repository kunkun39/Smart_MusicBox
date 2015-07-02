package com.changhong.yinxiang.remotecontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.ClientSocketInterface;
import android.content.Context;
import android.os.Handler;

import android.util.Log;

public abstract class SocketController implements ClientSocketInterface {

    protected static final String TAG = "TVHelperControlService";

    protected boolean mIsExit = false;
    protected Context mContext = null;
    protected Handler mHandle = null;
    protected RemoteInfoContainer mRemoteInfo = null;
    public static boolean mIsBroadCast = false;
    public static boolean mIsdirty = false;//just a joke

    private ThreadHeartBeatGet mThreadHeartBeatGet = null;

    SocketController(Context context, Handler handle) {
        mContext = context;
        mHandle = handle;

        mRemoteInfo = new RemoteInfoContainer();
        mThreadHeartBeatGet = new ThreadHeartBeatGet();
        mThreadHeartBeatGet.start();

        new ThreadLinkChecked().start();
        new UpdateDirty().start();
    }

    // Static Function:
    public static void setIsBroadCastMsg(boolean isBroadCast) {
        mIsBroadCast = isBroadCast;
    }

    public static void setIsDirty(boolean isDirty) {
        mIsdirty = isDirty;
    }

    protected void clear() {
        mIsExit = true;

        if (mThreadHeartBeatGet != null) {
            try {
                mThreadHeartBeatGet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (mRemoteInfo != null) {
            mRemoteInfo.exit();
        }
        mRemoteInfo = null;


    }

    protected void sendContent(String data) {
        if (mIsBroadCast) {
            mRemoteInfo.setBroadcastPackage(data);
        } else {
            mRemoteInfo.setSinglePackage(data);
        }
    }

    // Threads:
    class ThreadHeartBeatGet extends Thread {
        DatagramSocket clientSocket = null;

        @Override
        public void run() {

            while (!mIsExit) {
                try {
                    clientSocket = new DatagramSocket(INPUT_IP_POST_PORT);
                    clientSocket.setReuseAddress(true);
                    byte[] receiveData = new byte[512];
                    String ip = null;

                    Log.d("RemoteSocketServer", "ThreadHeartBeatGet in");
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    while (true) {

                        try {
                            /**
                             * 接收数据部分
                             */
                            if (mIsExit) {
                                break;
                            }

                            clientSocket.receive(receivePacket);
                            ip = receivePacket.getAddress().getHostAddress();
                            if (mRemoteInfo != null)
                                mRemoteInfo.setIp(ip);
                            Log.d(TAG, "getIP:" + ip);

                            byte[] ipBytes = mRemoteInfo.getIp().getBytes("ISO-8859-1");//ip.getBytes();
                            try {
                                clientSocket.send(new DatagramPacket(ipBytes, ipBytes.length, receivePacket.getAddress(), INPUT_IP_GET_PORT));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }


                            /**
                             * 线程停止3000
                             */
                            //Thread.sleep(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }

                    }
                }
            }
            Log.d("RemoteSocketServer", "ThreadHeartBeatGet out");
        }

        public void close() {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            }
        }

    }

    class UpdateDirty extends Thread {
        public void run() {
            while (!mIsExit) {
                if (mIsdirty) {
                    update();
                }

                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ThreadLinkChecked extends Thread {
        public void run() {
            while (!mIsExit) {
                mRemoteInfo.update();

                try {
                    sleep(ClientSocketInterface.RELAX_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void update() {
        if (mRemoteInfo.getIp() != null && ClientSendCommandService.serverIP != null && !mRemoteInfo.getIp().contains(ClientSendCommandService.serverIP)) {
            mRemoteInfo.setIp(ClientSendCommandService.serverIP);
            String ip = mRemoteInfo.getIp();

            if (ip != null && ip.length() > 0) {
                byte[] ipBytes = ip.getBytes();
                try {
                    new DatagramSocket().send(new DatagramPacket(ipBytes, ipBytes.length, InetAddress.getByName(mRemoteInfo.getIp()), INPUT_IP_GET_PORT));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    protected abstract void onIpObtained(String ip);

    protected abstract void onIpRemoved(String ip);

    class RemoteInfoContainer implements ClientSocketInterface {

        public static final int WAITING_TIME = 3000;
        private Map<String, Long> mServerIP = new HashMap<String, Long>();
        private Queue<DatagramPacket> mDataPackageList = new LinkedList<DatagramPacket>();


        private String mServerIpCur = null;

        public void exit() {
            mServerIpCur = null;
            mServerIP.clear();
            mDataPackageList.clear();
        }

        public final String getIp() {
            return mServerIpCur;
        }

        public final List<String> getIpList() {
            return new ArrayList<String>(mServerIP.keySet());
        }

        public void addIp(String serverIP) {
            Log.d(TAG, "addip in");
            String ip = ipCheck(serverIP) ? serverIP : null;

            if (ip == null)
                return;

            Log.d(TAG, "set cur ip");
            if (mServerIP.isEmpty()) {
                mServerIpCur = ip;
            }

            //refresh map for date changed or get a new ip
            //if(!mServerIP.containsKey(ip))
            {
                mServerIP.put(ip, new Date().getTime());
                onIpObtained(ip);
            }

            Log.d(TAG, "Add ip" + mServerIpCur);

        }

        public void update() {
            Long timeCur = new Date().getTime();

            Iterator<Entry<String, Long>> itEntry = mServerIP.entrySet().iterator();
            for (; itEntry.hasNext(); ) {
                Entry<String, Long> entry = itEntry.next();
                if (timeCur - entry.getValue() > (WAITING_TIME * 3)) {
                    //mServerIP.remove(entry);
                    Log.d(TAG, "remove ip(timeCur:" + timeCur + "timeSaved:" + entry.getValue() + ")");
                    removeIp(entry);
                    itEntry.remove();

                }
            }

        }

        public void setIp(String serverIP) {
            Log.d(TAG, "setIp in");
            String ip = ipCheck(serverIP) ? serverIP : null;
            Log.d(TAG, "setIp:" + ip);

            if (ip == null)
                return;
            Log.d(TAG, "ip != null");

            // add ip or update time with ip
            //if(!mServerIP.containsKey(ip))
            {
                Log.d(TAG, "come to add ip");
                addIp(ip);
                mServerIpCur = ip;

                if (ClientSendCommandService.serverIP != null
                        && ClientSendCommandService.serverIP.length() > 0) {
                    mServerIpCur = ClientSendCommandService.serverIP;
                }


                Log.d(TAG, "addIp:" + ip);

            }
            /*
	    	else {
	    		mServerIpCur = ip;
			}
			*/
        }

        public void removeIp(String ip) {
            if (ipCheck(ip)) {
                for (Entry<String, Long> entry : mServerIP.entrySet()) {
                    if (entry.getKey().equals(ip)) {
                        //mServerIP.remove(entry);
                        removeIp(entry);


                    }
                }


            }
        }

        private void removeIp(Entry<String, Long> entry) {
            if (entry == null) {
                return;
            }

            //mServerIP.remove(entry.getKey());
            onIpRemoved(entry.getKey());
            Log.d(TAG, "remove ip" + entry.getKey());

            if (entry.getKey() == mServerIpCur) {
                if (!mServerIP.isEmpty()) {
                    mServerIpCur = mServerIP.keySet().toArray()[0].toString();
                } else {
                    mServerIpCur = null;
                }
            }
        }


        public void setBroadcastPackage(String content) {
            String cont = content;
            Map<String, Long> ipMap = mServerIP;

            for (String entry : ipMap.keySet()) {
                byte[] mbytes = cont.getBytes();
                try {
                    mDataPackageList.offer(new DatagramPacket(mbytes,
                            mbytes.length,
                            InetAddress.getByName(entry),
                            CONTENT_PORT));
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "add Broadcast Package failed");
                    e.printStackTrace();
                }
            }

        }

        public void setSinglePackage(String content) {
            if (mServerIpCur == null)
                return;

            byte[] mbytes = content.getBytes();
            try {
                mDataPackageList.offer(new DatagramPacket(mbytes,
                        mbytes.length,
                        InetAddress.getByName(mServerIpCur),
                        CONTENT_PORT));
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                Log.d(TAG, "add Single Package failed");
                e.printStackTrace();
            }
        }

        public DatagramPacket getPackage() {
            return mDataPackageList.poll();
        }

        private boolean ipCheck(String ip) {
            // 255.255.255.255
            if (ip == null || ip.length() == 0)
                return false;

            if (ip.length() > 0 && ip.length() <= 15) {
                //b begin
                int b = 0, count = 0;
                while ((b = ip.indexOf(".", b) + 1) != 0) {
                    count++;

                }
                if (count == 3) return true;
            }

            return false;
        }


    }


}
