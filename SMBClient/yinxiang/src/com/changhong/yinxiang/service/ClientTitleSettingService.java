package com.changhong.yinxiang.service;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.yinxiang.activity.*;

/**
 * Created by Jack Wang
 */
public class ClientTitleSettingService {

    /**
     * every activity which contains list of box should set title here
     */
    public static void setClientActivityTitle() {
//        if (YinXiangRemoteControlActivity.title != null) {
//            YinXiangRemoteControlActivity.title.setText(ClientSendCommandService.titletxt);
//        }
        if (YinXiangMainActivity.title != null) {
            YinXiangMainActivity.title.setText(ClientSendCommandService.titletxt);
        }
//        if (YinXiangCategoryActivity.title != null) {
//        	YinXiangCategoryActivity.title.setText(ClientSendCommandService.titletxt);
//        }
        if (YinXiangPictureCategoryActivity.title != null) {
        	YinXiangPictureCategoryActivity.title.setText(ClientSendCommandService.titletxt);
        }
        if (YinXiangPictureViewActivity.title != null) {
        	YinXiangPictureViewActivity.title.setText(ClientSendCommandService.titletxt);
        }
        if (YinXiangVedioViewActivity.title != null) {
        	YinXiangVedioViewActivity.title.setText(ClientSendCommandService.titletxt);
        }
        if (YinXiangMusicViewActivity.title != null) {
        	YinXiangMusicViewActivity.title.setText(ClientSendCommandService.titletxt);
        }
        if (SearchActivity.title != null) {
        	SearchActivity.title.setText(ClientSendCommandService.titletxt);
        }
        if (YinXiangSourceActivity.title != null) {
        	YinXiangSourceActivity.title.setText(ClientSendCommandService.titletxt);
        }
        if (YinXiangNetMusicActivity.title != null) {
        	YinXiangNetMusicActivity.title.setText(ClientSendCommandService.titletxt);
        } 
      
    }
}
