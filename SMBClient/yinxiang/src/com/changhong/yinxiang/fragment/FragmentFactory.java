package com.changhong.yinxiang.fragment;

import android.app.Fragment;

public class FragmentFactory {
	 public static Fragment getInstanceByIndex(int index) {  
	        Fragment fragment = null;  
	        switch (index) {  
	            case 1:  //遥控器
	                fragment = new YinXiangRemoteControlFragment();  
	                break;  
	            case 2:  //网络电台
	                fragment = new YinXiangFMFragment();  
	                break;  
	            case 3:   //一键推送
	                fragment = new YinXiangCategoryFragment();  
	                break;  
	            case 4:  //设置Fragment
	                fragment = new YinXiangSettingFragment();  
	                break;  
//	            case 5:  
//	                fragment = new GlobalFragment();  
//	                break;  
	        }  
	        return fragment;  
	    }  
}
