package com.changhong.yinxiang.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.voicerecognition.android.Candidate;
import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.changhong.baidu.YinXiangBaiDuVoiceConfiguration;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.yinxiang.R;

public class YinXiangSearchActivity extends Activity {

	
	 /**************************************************IP连接部分*******************************************************/

    public static TextView title = null;
    private Button listClients;
    private Button back;
    private ListView clients = null;
    private BoxSelectAdapter IpAdapter;
    
	private EditText recognitionWord;
	private ImageView colection;
	private Button confirm;

	/********************************************** 语音部分代码 *********************************************************/

	/**
	 * baidu recognition client, void to init many times, so use static here
	 */
	private static VoiceRecognitionClient recognitionClient;
	/**
	 * 识别回调接口
	 */
	private BaiDuVoiceChannelDialogRecogListener recogListener = new BaiDuVoiceChannelDialogRecogListener();

	/**
	 * 初始化百度的配置
	 */
	private void initBaiduConfiguration() {
		if (recognitionClient == null) {
			recognitionClient = VoiceRecognitionClient
					.getInstance(YinXiangSearchActivity.this);
			recognitionClient.setTokenApis(
					YinXiangBaiDuVoiceConfiguration.API_KEY,
					YinXiangBaiDuVoiceConfiguration.SECRET_KEY);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yinxiang_music_search);
		initBaiduConfiguration();

		initView();
		initEvent();
	}

	private void initView() {
		
		
		/**
         * IP连接部分
         */
        title = (TextView) findViewById(R.id.title);
        back = (Button) findViewById(R.id.btn_back);
        clients = (ListView) findViewById(R.id.clients);
        listClients = (Button) findViewById(R.id.btn_list);
		
        /**
         * 
         * 语音输入部分
         */
		colection = (ImageView) findViewById(R.id.voice);
		recognitionWord = (EditText) findViewById(R.id.result);
		confirm = (Button) findViewById(R.id.confirm);
	}

	private void initEvent() {
		/**
         * IP连接部分
         */
        IpAdapter = new BoxSelectAdapter(YinXiangSearchActivity.this,
                ClientSendCommandService.serverIpList);
        clients.setAdapter(IpAdapter);
        clients.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clients.setVisibility(View.GONE);
                return false;
            }
        });
        clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(arg2);
                ClientSendCommandService.titletxt=ClientSendCommandService.getCurrentConnectBoxName();
                title.setText(ClientSendCommandService.getCurrentConnectBoxName());
                ClientSendCommandService.handler.sendEmptyMessage(2);
                clients.setVisibility(View.GONE);
            }
        });
        listClients.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MyApplication.vibrator.vibrate(100);
                    if (ClientSendCommandService.serverIpList.isEmpty()) {
                        Toast.makeText(YinXiangSearchActivity.this, "未获取到服务器IP", Toast.LENGTH_LONG).show();
                    } else {
                        clients.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                finish();
            }
        });

		/**
		 * 
		 * 语音输入部分
		 */
		colection.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startVoiceRecognition();
					break;
				case MotionEvent.ACTION_UP:
					SystemClock.sleep(1000);
					recognitionClient.speakFinish();
					break;
				default:
					break;
				}
				return false;
			}
		});
		
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent =new  Intent(YinXiangSearchActivity.this, YinXiangMusicViewActivity.class);
				intent.putExtra("KeyWords", recognitionWord.getText().toString());
				startActivity(intent);
			}
		});
	}

	private void startVoiceRecognition() {
		/**
		 * stop first, because last action maybe not finished
		 */
		recognitionClient.stopVoiceRecognition();
		/**
		 * 语音的配置
		 */
		VoiceRecognitionConfig config = YinXiangBaiDuVoiceConfiguration
				.getVoiceRecognitionConfig();
		/**
		 * 下面发起识别
		 */
		int code = recognitionClient.startVoiceRecognition(recogListener,
				config);
		if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
			Toast.makeText(YinXiangSearchActivity.this, "网络连接出错，请重新尝试",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 百度语音监听器
	 */
	public class BaiDuVoiceChannelDialogRecogListener implements
			VoiceRecognitionClient.VoiceClientStatusChangeListener {
		/**
		 * 正在识别中
		 */
		private boolean isRecognitioning = false;

		private int recognitioningFailedTimes = 0;

		/**
		 * channel match list, integer value stand for match time, compare han
		 * zi one by one
		 */
		private Map<String, Integer> matchChannel = new HashMap<String, Integer>();

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
			Toast.makeText(YinXiangSearchActivity.this, "抱歉哟，我们不能识别空指令",
					Toast.LENGTH_LONG).show();
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
						recognitionResult = results.get(0).toString()
								.replace("。", "");
					}
				}
			}

			/**
			 * used for check yuying laucher is successful or not
			 * <p>
			 * we have two flows: 1 - if text start with "打开"，"启动"，"开启" go to
			 * open box app way 2 - else go to switch channel way
			 */
			boolean hasResult = false;
			if (!TextUtils.isEmpty(recognitionResult)) {
				/******************************************** 处理用户说的话 ********************************************/
				recognitionWord.setText(recognitionResult);
				/******************************************** 结束处理用户说的话 ******************************************/
			}

			// if (!hasResult) {
			// recognitioningFailedTimes = recognitioningFailedTimes + 1;
			// if (recognitioningFailedTimes == 3) {
			// recognitioningFailedTimes = 0;
			// } else {
			// Toast.makeText(SearchActivity.this, "抱歉哟，目前还不支持该指令:" +
			// recognitionResult, Toast.LENGTH_LONG).show();
			// }
			// } else {
			// recognitioningFailedTimes = 0;
			// }
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (ClientSendCommandService.titletxt != null) {
            title.setText(ClientSendCommandService.titletxt);
        }
	}
	
}
