package com.changhong.baidu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.baidu.voicerecognition.android.Candidate;
import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.changhong.common.domain.AppInfo;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.utils.StringUtils;
import com.changhong.yinxiang.utils.YuYingWordsUtils;

public class BaiDuVoiceChannelDialogRecogListener  implements VoiceRecognitionClient.VoiceClientStatusChangeListener{
	 /**
     * 正在识别中
     */
    private boolean isRecognitioning = false;

    private int recognitioningFailedTimes = 0;

    /**
     * channel match list, integer value stand for match time, compare han zi one by one
     */
    private Map<String, Integer> matchChannel = new HashMap<String, Integer>();

    /**
     * baidu recognition client, void to init many times, so use static here
     */
    private static VoiceRecognitionClient recognitionClient;
    
    private Context con;
    
    public BaiDuVoiceChannelDialogRecogListener(Context con){
    	this.con=con;
    	initBaiduConfiguration();
    }

    /**
     * 初始化百度的配置
     */
    private void initBaiduConfiguration() {
        if (recognitionClient == null) {
            recognitionClient = VoiceRecognitionClient.getInstance(con);
            recognitionClient.setTokenApis(BaiDuVoiceConfiguration.API_KEY, BaiDuVoiceConfiguration.SECRET_KEY);
        }
    }
    @Override
    public void onClientStatusChange(int status, Object obj) {
        switch (status) {
            // 语音识别实际开始，这是真正开始识别的时间点，需在界面提示用户说话。
            case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
                isRecognitioning = true;
                break;
            // 检测到语音起点
            case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START:
                break;
            // 已经检测到语音终点，等待网络返回
            case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
                break;
            // 语音识别完成，显示obj中的结果
            case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
                isRecognitioning = false;
                updateRecognitionResult(obj);
                break;
            // 处理连续上屏
            case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
                break;
            // 用户取消
            case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                recognitionClient.stopVoiceRecognition();
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(int errorType, int errorCode) {
        Toast.makeText(con, "抱歉哟，我们不能识别空指令" , Toast.LENGTH_LONG).show();
        isRecognitioning = false;
        recognitionClient.stopVoiceRecognition();
    }

    @Override
    public void onNetworkStatusChange(int status, Object obj) {
        // 这里不做任何操作不影响简单识别
    }

    /**
     * 将识别结果更新到UI上，搜索模式结果类型为List<String>,输入模式结果类型为List<List<Candidate>>
     */
    private void updateRecognitionResult(Object result) {
        String recognitionResult = "";
        if (result != null && result instanceof List) {
            List results = (List) result;
            if (results.size() > 0) {
                if (results.get(0) instanceof List) {
                    List<List<Candidate>> sentences = (List<List<Candidate>>) result;
                    StringBuffer sb = new StringBuffer();
                    for (List<Candidate> candidates : sentences) {
                        if (candidates != null && candidates.size() > 0) {
                            sb.append(candidates.get(0).getWord());
                        }
                    }
                    recognitionResult = sb.toString().replace("。", "");
                } else {
                    recognitionResult = results.get(0).toString().replace("。", "");
                }
            }
        }

        /**
         * used for check yuying laucher is successful or not
         * <p>
         * we have two flows:
         * 1 - if text start with "打开"，"启动"，"开启" go to open box app way
         * 2 - else go to switch channel way
         */
        boolean hasResult = false;
        if (StringUtils.hasLength(recognitionResult)) {
            /********************************************处理用户说的话********************************************/

            String commands = YuYingWordsUtils.isSearchContainsControl(recognitionResult);
            if (StringUtils.hasLength(commands)) {
                //TODO:流程->主页
                String[] command = StringUtils.delimitedListToStringArray(commands, "|");
                if (command.length == 2 && command[0].equals("key:dtv")) {
                    ClientSendCommandService.msg = command[0];
                    ClientSendCommandService.handler.sendEmptyMessage(1);
                    SystemClock.sleep(300);
                    ClientSendCommandService.msg = command[1];
                    ClientSendCommandService.handler.sendEmptyMessage(1);
                } else {
                    for (String cmd : command) {
                        ClientSendCommandService.msg = cmd;
                        ClientSendCommandService.handler.sendEmptyMessage(1);
                    }
                }

                hasResult = true;
                Toast.makeText(con, "语音的结果为:" + recognitionResult, Toast.LENGTH_LONG).show();

            } else if (YuYingWordsUtils.isSearchContainsAppKeywords(recognitionResult)) {
                //TODO:流程->搜索应用
                recognitionResult = YuYingWordsUtils.appSearchWordsConvert(recognitionResult);

                /**
                 * search server side all applications
                 */
                if (ClientSendCommandService.serverAppInfo.isEmpty()) {
                    ClientSendCommandService.handler.sendEmptyMessage(6);
                    //wait for search channel finish
                    while (!ClientSendCommandService.searchApplicationFinished) {
                        SystemClock.sleep(500);
                    }
                }

                /**
                 * compare the matched app, use char compare one by one
                 */
                matchChannel.clear();
                for (int i = 0; i < recognitionResult.length(); i++) {
                    for (int j = 0; j < ClientSendCommandService.serverAppInfo.size(); j++) {
                        AppInfo info = ClientSendCommandService.serverAppInfo.get(j);
                        String appName = info.appName;
                        if (appName.indexOf(recognitionResult.charAt(i)) >= 0) {
                            Integer count = matchChannel.get(String.valueOf(j));
                            if (count == null) {
                                matchChannel.put(String.valueOf(j), 1);
                            } else {
                                matchChannel.put(String.valueOf(j), count + 1);
                            }
                        }
                    }
                }

                /**
                 * get best matched result, the value must bigger than 2
                 * 1 - first compare value which is bigger
                 * 2 - if value is equal compare which is shorter
                 * 3 - if length is equal compare which contains the input string
                 */
                int bestCounter = 0;
                String bestPostion = "";
                for (String position : matchChannel.keySet()) {
                    Integer value = matchChannel.get(position);

                    if (value >= 2) {
                        if (value > bestCounter) {
                            bestCounter = value;
                            bestPostion = position;
                        } else if (value == bestCounter) {
                            String bestApp = ClientSendCommandService.serverAppInfo.get(Integer.valueOf(bestPostion)).appName;
                            String newApp = ClientSendCommandService.serverAppInfo.get(Integer.valueOf(position)).appName;

                            if (newApp.length() < bestApp.length()) {
                                bestPostion = position;
                            } else if (newApp.length() == bestApp.length()) {
                                int bestIndex = bestApp.indexOf(recognitionResult);
                                int newIndex = newApp.indexOf(recognitionResult);
                                if (bestIndex < 0 && newIndex >= 0) {
                                    bestPostion = position;
                                }
                            }
                        }
                    }
                }

                /**
                 * send command to the server to decide which one should open
                 */
                if (StringUtils.hasLength(bestPostion)) {
                    AppInfo info = ClientSendCommandService.serverAppInfo.get(Integer.valueOf(bestPostion));
                    ClientSendCommandService.msg = "app_open:" + info.packageName;
                    ClientSendCommandService.handler.sendEmptyMessage(1);
                    Log.e("yinxiang", "message:" + "app_open:" + info.packageName);

                    Toast.makeText(con, "应用的结果为:" + info.appName + "\n语音的结果为:" + recognitionResult, Toast.LENGTH_LONG).show();
                    hasResult = true;
                } else {
                    hasResult = false;
                }

            } else {
            	//搜索MP3
            	
            }
            /********************************************结束处理用户说的话******************************************/
        }

        if (!hasResult) {
            recognitioningFailedTimes = recognitioningFailedTimes + 1;
            if (recognitioningFailedTimes == 3) {
                recognitioningFailedTimes = 0;
                BaiDuVoiceChannelControlDialog yuYingHelpDialog = new BaiDuVoiceChannelControlDialog(con);
                yuYingHelpDialog.show();
            } else {
                Toast.makeText(con, "抱歉哟，目前还不支持该指令:" + recognitionResult, Toast.LENGTH_LONG).show();
            }
        } else {
            recognitioningFailedTimes = 0;
        }
    }
}
