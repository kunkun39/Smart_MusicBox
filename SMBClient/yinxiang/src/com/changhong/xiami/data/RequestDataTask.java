package com.changhong.xiami.data;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.changhong.yinxiang.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xiami.core.exceptions.AuthExpiredException;
import com.xiami.core.exceptions.ResponseErrorException;
import com.xiami.sdk.XiamiSDK;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public abstract class RequestDataTask extends AsyncTask<HashMap<String, Object>, Long, JsonElement> {

	private XMMusicData mXMMusicData;
    private JsonUtil requestManager;
    private Context context;
    private String method,submethod;

    public RequestDataTask(XMMusicData xiamiMusicData, Context context,String method,String subMethod) {
        this.mXMMusicData = xiamiMusicData;
        this.context = context;
        this.method=method;
        this.submethod=subMethod;
        requestManager = JsonUtil.getInstance();
    }

    public abstract void postInBackground(JsonElement response);
    
    
    public List<HashMap<String, Object>>  getSubTaskParams(JsonElement response){
    	 return null;
    }


    @Override
    public JsonElement doInBackground(HashMap<String, Object>... params) {
        
            HashMap<String, Object> param = params[0];
            JsonElement element=xiamiRequest(method,param);
//            if(null !=element){
//                postInBackground(element);          	
//            }            
            return element;
    }
    
    
    private  JsonElement xiamiRequest(String method,HashMap<String, Object> param){
    	
    	 try {
    		 
    		 
             String result = mXMMusicData.xiamiRequest(method, param);
             
             if (!TextUtils.isEmpty(result)) {
                 //获取的json字符串由第三方自主解析，demo使用Gson解析
                 Gson gson = requestManager.getGson();
                 XiamiApiResponse response = gson.fromJson(result, XiamiApiResponse.class);
                 if (requestManager.isResponseValid(response)) {              	
                 	JsonElement JsonData=response.getData();
                     return JsonData;
                     
                 } else return null;
             } else {
                 //查询失败
                 Toast.makeText(context, R.string.error_response,Toast.LENGTH_SHORT).show();
                 return null;
             }
         } catch (NoSuchAlgorithmException e) {
             Toast.makeText(context, R.string.error_sign_algorithm,Toast.LENGTH_SHORT).show();

             e.printStackTrace();
             return null;
         } catch (IOException e) {
             Toast.makeText(context, R.string.error_io,Toast.LENGTH_SHORT).show();

             e.printStackTrace();
             return null;
         } catch (AuthExpiredException e) {
             Toast.makeText(context, R.string.error_auth_expired,Toast.LENGTH_SHORT).show();
             e.printStackTrace();
             return null;
         } catch (ResponseErrorException e) {
             Toast.makeText(context, R.string.error_response,Toast.LENGTH_SHORT).show();
             return null;
         }    	
    }
    

}
