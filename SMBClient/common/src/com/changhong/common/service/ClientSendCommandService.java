package com.changhong.common.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import com.changhong.common.domain.AppInfo;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.MobilePerformanceUtils;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.utils.WebUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jack Wang
 */
public class ClientSendCommandService extends Service implements ClientSocketInterface {

    /**
     * message handler
     */
    public static Handler handler = null;

    /**
     * server ip list
     */
    public static ArrayList<String> serverIpList = new ArrayList<String>();
	public static Map<String, String> serverIpListMap = new HashMap<String, String>();
    /**
     * the parameter which which is used for check get json data from box server is finished or not
     */
    public static boolean searchApplicationFinished = false;
    public static boolean searchFMFinished = false;
    /**
     * 服务端应用列表
     */
    public static List<AppInfo> serverAppInfo = new ArrayList<AppInfo>();
    public static List<String> serverFMInfo = new ArrayList<String>();

    /**
     * box server ip address
     */
    public static String serverIP = null;

    public static Socket client = null;

    public static String msg = null;

    public static String msgSwitchChannel = null;

    public static String msgXpointYpoint = null;

    public static String titletxt = null;
    
    //自动控制状态
    public static boolean isAutoCtrl = false;
    public static int  curFMIndex = -1;
    
   private boolean isFirst=true;

   //更新广播信息
   public static final String  ACTION_FMINFOR_UPDATE="com.changhong.updateFM";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new SendCommend().start();      
    }
	
	public static String getCurrentConnectBoxName() {
        String boxName = ClientSendCommandService.serverIpListMap.get(ClientSendCommandService.serverIP);
        if (StringUtils.hasLength(boxName)) {
            return boxName;
        }
        return ClientSendCommandService.serverIP;
    }

    public static String getConnectBoxName(String serverIP) {
        String boxName = ClientSendCommandService.serverIpListMap.get(serverIP);
        if (StringUtils.hasLength(boxName)) {
            return boxName;
        }
        return NetworkUtils.BOX_DEFAULT_NAME;
    }

    private class SendCommend extends Thread {
        public void run() {
            Looper.prepare();

            handler = new Handler() {
                @Override
                public void handleMessage(Message msg1) {
                    switch (msg1.what) {
                        case 1:
                            //TODO:这个消息只能用于遥控器消息发送，注意其他部分不要使用该消息
                            MobilePerformanceUtils.sharingRemoteControlling = true;
                            MobilePerformanceUtils.sharingRemoteControlLastHappen = System.currentTimeMillis();
                            MobilePerformanceUtils.openPerformance(ClientSendCommandService.this);

                            if (serverIP != null && msg != null) {
                                DatagramSocket dgSocket = null;
                                try {
                                    dgSocket = new DatagramSocket();
                                    byte b[] = msg.getBytes();

                                    DatagramPacket dgPacket = new DatagramPacket(b, b.length, InetAddress.getByName(serverIP), KEY_PORT);
                                    dgSocket.send(dgPacket);
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
                            } else {
                                Log.e(TAG, "未获取到服务器IP");
                            }
                            break;
                        case 2:
                            /**
                             * TODO:comment by Jack Wang:
                             * why I will change this part of the code, because for module dependency, just allow main module use common not allowed command
                             * module use main module, so....
                             * <p>
                             *
                             * the old code here will execute mHandler.sendEmptyMessage(1); that means tell TVPlayerActivity refresh the tv channel datas
                             * <p>
                             *
                             * I check all places which will execute this part of code, except one place, all other happens at change the server ip, so
                             * 1 - if this activity is not TVPlayerActivity, it's OK, because when TVPlayerActivity.onstart() will refresh all
                             *     channel again
                             * 2 - if current activity is TVPlayerActivity, when change teh server ip, after search channel finished, refresh again, so
                             *     the parameter "searchChannelFinished" is used for this
                             */
//                            try {
//                                searchChannelFinished = false;
//                                getChannelList("http://" + serverIP + ":8000/DtvProgInfoJson.json");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            } finally {
//                                searchChannelFinished = true;
//                            }
                        	/**
                             * get server all FM
                             */
                            try {
                            	searchFMFinished = false;
                                getFMList("http://" + serverIP + ":12345/OttFMInfoJson.json");
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                            	searchFMFinished = true;
                            	//发送广播
                            	// 创建Intent对象
                    			Intent intent = new Intent();
                    			// 设置Intent的Action属性
                    			intent.setAction(ACTION_FMINFOR_UPDATE);
                    			// 发送广播
                    			sendBroadcast(intent);
                            	
                            }
                            /**
                             * when user reselected the ip, reload all app info
                             */
                            this.sendEmptyMessage(6);
                            break;
                        case 3:
                            //换台UDP广播
                            if (serverIP != null && msgSwitchChannel != null) {
                                DatagramSocket dgSocket = null;
                                try {
                                    dgSocket = new DatagramSocket();
                                    byte b[] = msgSwitchChannel.getBytes();

                                    DatagramPacket dgPacket = new DatagramPacket(b, b.length, InetAddress.getByName(serverIP), SWITCH_KEY_PORT);
                                    dgSocket.send(dgPacket);
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
                            } else {
                                Log.e(TAG, "未获取到服务器IP");
                            }
                            break;
                        case 4:
                            DatagramSocket dgSocket = null;
                            try {
                                dgSocket = new DatagramSocket();
                                byte b[] = msg.getBytes();
                                DatagramPacket dgPacket = new DatagramPacket(b, b.length, InetAddress.getByName(serverIP), KEY_PORT);
                                dgSocket.send(dgPacket);
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
                            break;
                        case 5:
                            //坐标UDP广播
                            if (serverIP != null && msgXpointYpoint != null) {
                                DatagramSocket xydgSocket = null;
                                try {
                                    xydgSocket = new DatagramSocket();
                                    byte b[] = msgXpointYpoint.getBytes();
                                    DatagramPacket dgPacket = new DatagramPacket(b, b.length, InetAddress.getByName(serverIP), 9008);
                                    xydgSocket.send(dgPacket);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (xydgSocket != null) {
                                            xydgSocket.close();
                                        }
                                    } catch (Exception e) {
                                        xydgSocket.close();
                                    }
                                }
                            } else {
                                Log.e(TAG, "未获取到服务器IP");
                            }
                            break;
                        case 6:
                            /**
                             * get server all applications
                             */
                            try {
                                searchApplicationFinished = false;
                                getProgramList("http://" + serverIP + ":12345/OttAppInfoJson.json");
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                searchApplicationFinished = true;
                            }
                            break;
                        default: {
                            break;
                        }
                    }
                    super.handleMessage(msg1);
                }
            };
            requestFMInfor();
            Looper.loop();
        }
    }
    @SuppressLint("NewApi")
    private void getProgramList(String url) {
        if (url == null) {
            return;
        }

        //get network json data
        String sss = null;
        URL urlAddress = null;
        try {
            urlAddress = new URL(url);
            HttpURLConnection hurlconn = (HttpURLConnection) urlAddress.openConnection();
            hurlconn.setRequestMethod("GET");
            hurlconn.setConnectTimeout(2000);
            hurlconn.setRequestProperty("Charset", "UTF-8");
            hurlconn.setRequestProperty("Connection", "Close");
            if (hurlconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                hurlconn.connect();
                InputStream instream = hurlconn.getInputStream();
                InputStreamReader inreader = new InputStreamReader(instream, "UTF-8");
                StringBuffer stringappend = new StringBuffer();
                char[] b = new char[256];
                int length = -1;
                while ((length = inreader.read(b)) != -1) {
                    stringappend.append(new String(b, 0, length));
                }
                sss = stringappend.toString();
//                Log.i(TAG, sss);
                inreader.close();
                instream.close();
            } else {
                Log.e(TAG, ">>>>>>>hurlconn.getResponseCode()!= HttpURLConnection.HTTP_OK");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //parse the json data
        serverAppInfo.clear();
        try {
            if (StringUtils.hasLength(sss)) {
                JSONArray all = new JSONArray(sss);
                for (int i = 0; i < all.length(); i++) {
                    JSONObject single = all.getJSONObject(i);
                    String packageName = single.getString("packageName");
                    String applicationName = single.getString("applicationName");

                    AppInfo app = new AppInfo(packageName, applicationName);
                    serverAppInfo.add(app);
                }
            } else {
                Log.e(TAG, "未获取到服务器Json");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("NewApi")
    private void getFMList(String url) {
    	if (url == null) {
    		return;
    	}
    	
    	//get network json data
    	String sss = null;
    	URL urlAddress = null;
    	try {
    		urlAddress = new URL(url);
    		HttpURLConnection hurlconn = (HttpURLConnection) urlAddress.openConnection();
    		hurlconn.setRequestMethod("GET");
    		hurlconn.setConnectTimeout(2000);
    		hurlconn.setRequestProperty("Charset", "UTF-8");
    		hurlconn.setRequestProperty("Connection", "Close");
    		if (hurlconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    			hurlconn.connect();
    			InputStream instream = hurlconn.getInputStream();
    			InputStreamReader inreader = new InputStreamReader(instream, "UTF-8");
    			StringBuffer stringappend = new StringBuffer();
    			char[] b = new char[256];
    			int length = -1;
    			while ((length = inreader.read(b)) != -1) {
    				stringappend.append(new String(b, 0, length));
    			}
    			sss = stringappend.toString();
//    			Log.i(TAG, sss);
    			inreader.close();
    			instream.close();
    		} else {
    			Log.e(TAG, ">>>>>>>hurlconn.getResponseCode()!= HttpURLConnection.HTTP_OK");
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	//parse the json data
    	serverFMInfo.clear();
    	try {
    		if (StringUtils.hasLength(sss)) {
    			JSONArray all = new JSONArray(sss);
    			for (int i = 0; i < all.length(); i++) {
    				JSONObject single = all.getJSONObject(i);
    				String name = single.getString("FMname");
    				String state= single.getString("state");
    				if("autoCtrl".equals(name) ){
        				isAutoCtrl=state.equals("on")?true:false;    				
    				}else{
        				serverFMInfo.add(name);
        				//获取当前播放FM 的information
        				if("1".equals(state)){
        					curFMIndex=i;       				
        				}
    				}
    				
    			}
    			
    		} else {
    			Log.e(TAG, "未获取到服务器Json");
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
    private void requestFMInfor(){
    	
    	if(isFirst){
		    	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	handler.sendEmptyMessage(2);
		    	isFirst=false;
    	}
    }
}
