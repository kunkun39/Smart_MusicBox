package com.changhong.xiami.data;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.JsonElement;
import com.xiami.core.exceptions.AuthExpiredException;
import com.xiami.core.exceptions.ResponseErrorException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;



public  class RequestDataTask extends AsyncTask<HashMap<String, Object>, Long, JsonElement> {

    private XMMusicData mXMMusicData;
    private String method;
    private Handler parentHandler;
    private int ErrorCode;

    public RequestDataTask(XMMusicData xmmusicData,Handler parent,String method) {
        this.mXMMusicData = xmmusicData;  
        this.method=method;
        this. parentHandler=parent;
    }

    public  void postInBackground(JsonElement response){
    	if(null != parentHandler){
    	      Message msg=parentHandler.obtainMessage();
    	      if(-1 == ErrorCode){
	    	      msg.what=Configure.XIAMI_RESPOND_SECCESS;
	    	      msg.obj=response;
    	      }else{
    	    	  msg.what=Configure.XIAMI_RESPOND_FAILED;
	    	      msg.arg1=ErrorCode;
    	      }
    	      parentHandler.sendMessage(msg); 	      
    	}   	    
    }
    

    @Override
    public JsonElement doInBackground(HashMap<String, Object>... params) {
        	
       	 try {
       		   ErrorCode=-1;
                HashMap<String, Object> param = params[0];
                String result = mXMMusicData.xiamiRequest(method, param);           
                if (!TextUtils.isEmpty(result)) {
                   
                	//获取的响应为：json字符串
                    XiamiApiResponse response = mXMMusicData.getXiamiResponse(result);
                    if (mXMMusicData.isResponseValid(response)) {              	
                    	JsonElement JsonData=response.getData();
                        return JsonData;
                        
                    } else return null;
                } else {
                    //查询失败
                	ErrorCode=R.string.error_response;
                    return null;
                }
            } catch (NoSuchAlgorithmException e) {
                 ErrorCode=R.string.error_sign_algorithm;
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                ErrorCode=R.string.error_io;
                e.printStackTrace();
                return null;
            } catch (AuthExpiredException e) {
                ErrorCode=R.string.error_auth_expired;
                e.printStackTrace();
                return null;
            } catch (ResponseErrorException e) {
                ErrorCode=R.string.error_response;
                return null;
            }    	               
    }
}
