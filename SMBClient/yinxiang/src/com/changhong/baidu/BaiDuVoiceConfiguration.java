package com.changhong.baidu;

import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.changhong.yinxiang.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jack Wang
 */
public class BaiDuVoiceConfiguration {

    public static final String API_KEY = "q9G0hzYK4I0VmmDRpS8PlHWo";

    public static final String SECRET_KEY = "7Nl53v0sjG17siTsU5qLDbHqp2MEGzM2";

    /**
     * 当前识别语言
     */
    public static String CURRENT_LANGUAGE = VoiceRecognitionConfig.LANGUAGE_CHINESE;

    /**
     * 当前垂直领域类型
     */
    public static int CURRENT_PROP = VoiceRecognitionConfig.PROP_INPUT;

    /**
     * 播放开始音
     */
    public static boolean PLAY_START_SOUND = true;

    /**
     * 播放结束音
     */
    public static boolean PLAY_END_SOUND = true;

    /**
     * 显示音量
     */
    public static boolean SHOW_VOL = false;

    /**
     * 获得百度语音配置文件
     */
    public static VoiceRecognitionConfig getVoiceRecognitionConfig() {
        VoiceRecognitionConfig config = new VoiceRecognitionConfig();
        config.setProp(BaiDuVoiceConfiguration.CURRENT_PROP);
        config.setLanguage(BaiDuVoiceConfiguration.CURRENT_LANGUAGE);
        config.enableContacts(); // 启用通讯录
        config.enableVoicePower(BaiDuVoiceConfiguration.SHOW_VOL); // 音量反馈。

        if (BaiDuVoiceConfiguration.PLAY_START_SOUND) {
            config.enableBeginSoundEffect(R.raw.bdspeech_recognition_start); // 设置识别开始提示音
        }
        if (BaiDuVoiceConfiguration.PLAY_END_SOUND) {
            config.enableEndSoundEffect(R.raw.bdspeech_speech_end); // 设置识别结束提示音
        }

        config.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K); // 设置采样率,需要与外部音频一致

        return config;
    }

}
