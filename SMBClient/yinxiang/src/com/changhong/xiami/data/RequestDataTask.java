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

    private JsonUtil mJsonUtil;
    private Context context;
    private String method;

    public RequestDataTask(JsonUtil jsonUtil, Context context,String method) {
        this.context = context;
        this.method=method;
        this.mJsonUtil = jsonUtil;
    }

    public abstract void postInBackground(JsonElement response);
    

    @Override
    public JsonElement doInBackground(HashMap<String, Object>... params) {
        	
       	 try {
       		 
                HashMap<String, Object> param = params[0];
                String result = mJsonUtil.xiamiRequest(method, param);           
                if (!TextUtils.isEmpty(result)) {
                   
                	//获取的响应为：json字符串
                    XiamiApiResponse response = mJsonUtil.xiamiRespond(result);
                    if (mJsonUtil.isResponseValid(response)) {              	
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
