package com.changhong.baidu;

import android.content.Context;
import android.widget.Toast;

import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;

public class VoiceRecogFactory {
	/**
	 * 
	 * 增加讯飞语音，采集语音放在startrecog里
	 * 需增加flag标志是采用百度语音还是采用讯飞语音，标志位会存在于startrecog和speakfinish中。
	 */
	public static VoiceRecogFactory VRFactory;

	/**
	 * 
	 * 百度语音的相关参数
	 */
	private BaiDuVoiceChannelDialogRecogListener recogListener = null;
	private static VoiceRecognitionClient recognitionClient;
	
	public static VoiceRecogFactory getInstance(){
		if(null==VRFactory){
			VRFactory=new VoiceRecogFactory();
		}
		return VRFactory;
	}
	
	public void startRecog(Context con) {
		/**
		 * stop first, because last action maybe not finished
		 */
		if (null == recognitionClient) {
			initBaiduConfiguration(con);
		}
		recognitionClient.stopVoiceRecognition();
		/**
		 * 语音的配置
		 */
		VoiceRecognitionConfig config = BaiDuVoiceConfiguration
				.getVoiceRecognitionConfig();
		/**
		 * 下面发起识别
		 */
		if (null == recogListener) {
			recogListener = new BaiDuVoiceChannelDialogRecogListener(con);
		}
		int code = recognitionClient.startVoiceRecognition(recogListener,
				config);
		if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
			Toast.makeText(con, "网络连接出错，请重新尝试", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 初始化百度的配置
	 */
	private void initBaiduConfiguration(Context con) {
		if (recognitionClient == null) {
			recognitionClient = VoiceRecognitionClient.getInstance(con);
			recognitionClient.setTokenApis(BaiDuVoiceConfiguration.API_KEY,
					BaiDuVoiceConfiguration.SECRET_KEY);
		}
	}
	
	public void speakFinish(){
		recognitionClient.speakFinish();
	}
}
