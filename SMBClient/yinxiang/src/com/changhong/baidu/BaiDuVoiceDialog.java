package com.changhong.baidu;

import android.app.Dialog;
import android.content.Context;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.baidu.voicerecognition.android.Candidate;
import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.remotecontrol.TVInputDialogActivity;

import java.util.List;

/**
 * Created by Jack Wang
 */
public class BaiDuVoiceDialog extends Dialog {

    /**
     * 自己的实例
     */
    private static BaiDuVoiceDialog baiDuVoiceDialog;

    /**
     * 百度语音识别客户端
     */
    private VoiceRecognitionClient recognitionClient;

    /**
     * 识别回调接口
     */
    private BaiDuVoiceDialogRecogListener recogListener = new BaiDuVoiceDialogRecogListener();

    /**
     * Dialog的提示信息
     */
    public TextView notification = null;

    /**
     * 识别语音开始按钮
     */
    private Button pressButton = null;

    /**
     * 识对话框关闭按钮
     */
    private Button closeButton = null;

    public static BaiDuVoiceDialog getIntenance(Context context) {
        baiDuVoiceDialog =  new BaiDuVoiceDialog(context);
        return baiDuVoiceDialog;
    }

    private BaiDuVoiceDialog(final Context context) {
        super(context, R.style.Translucent_NoTitle);
        setContentView(R.layout.baidu_voice_dialog);

        getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams baiduParam = getWindow().getAttributes();
        baiduParam.width = this.getWindow().getAttributes().width;
        getWindow().setAttributes(baiduParam);

        notification = (TextView)findViewById(R.id.baidu_notification);
        pressButton = (Button)findViewById(R.id.baidu_pressbutton);
        closeButton = (Button)findViewById(R.id.baidu_closebutton);

        /**
         * 初始化识别的客户端
         */
        recognitionClient = VoiceRecognitionClient.getInstance(context);
        recognitionClient.setTokenApis(BaiDuVoiceConfiguration.API_KEY, BaiDuVoiceConfiguration.SECRET_KEY);

        recogListener.setResultInput(TVInputDialogActivity.mEditText);
        pressButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        notification.setText("开始监听你的语音输入...");
                        /**
                         * 设置按钮enable为false, 防止二次输入出错
                         */
                        pressButton.setEnabled(false);
                        /**
                         * 设置初始化状态和隐藏输入法
                         */
                        TVInputDialogActivity.mEditText.setText(null);
                        /**
                         * 语音的配置
                         */
                        VoiceRecognitionConfig config = BaiDuVoiceConfiguration.getVoiceRecognitionConfig();
                        /**
                         * 下面发起识别
                         */
                        int code = recognitionClient.startVoiceRecognition(recogListener, config);
                        if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
                            notification.setText("网络连接出错，请从新尝试");
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        recognitionClient.speakFinish();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognitionClient.stopVoiceRecognition();
                pressButton.setEnabled(true);

                baiDuVoiceDialog.dismiss();
            }
        });
    }

    public class BaiDuVoiceDialogRecogListener implements VoiceRecognitionClient.VoiceClientStatusChangeListener {

        /**
         * 正在识别中
         */
        private boolean isRecognitioning = false;

        /**
         * 返回内容的输入框
         */
        private EditText resultText;

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
                    pressButton.setEnabled(true);
                    break;
                // 处理连续上屏
                case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
                    break;
                // 用户取消
                case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                    notification.setText("按下开始语音识别，送开完成");
                    isRecognitioning = false;

                    recognitionClient.stopVoiceRecognition();
                    pressButton.setEnabled(true);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onError(int errorType, int errorCode) {
            notification.setText("语音分析出错，请从新尝试");
            isRecognitioning = false;

            recognitionClient.stopVoiceRecognition();
            pressButton.setEnabled(true);
        }

        @Override
        public void onNetworkStatusChange(int status, Object obj) {
            // 这里不做任何操作不影响简单识别
        }

        /**
         * 设置文本返回的控件
         */
        public void setResultInput(EditText resultText) {
            this.resultText = resultText;
        }

        /**
         * 将识别结果更新到UI上，搜索模式结果类型为List<String>,输入模式结果类型为List<List<Candidate>>
         */
        private void updateRecognitionResult(Object result) {
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
                        resultText.setText(sb.toString().replace("。", ""));
                    } else {
                        resultText.setText(results.get(0).toString().replace("。", ""));
                    }
                }

            }

            notification.setText("按下开始语音识别，送开完成");
        }
    }
}
