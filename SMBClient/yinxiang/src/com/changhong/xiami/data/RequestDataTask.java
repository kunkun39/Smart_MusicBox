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

public class RequestDataTask extends	AsyncTask<HashMap<String, Object>, Long, JsonElement> {

	private XMMusicData mXMMusicData;
	private String method;
	private Handler parentHandler;
	private int respondCode;
	

	public RequestDataTask(XMMusicData xmmusicData, Handler parent,
			String method) {
		this.mXMMusicData = xmmusicData;
		this.method = method;
		this.parentHandler = parent;
	}

	protected void doPostExecute(JsonElement result) {
		if (null != parentHandler) {
			Message msg = parentHandler.obtainMessage();
			if (Configure.XIAMI_RESPOND_SECCESS == respondCode) {
				msg.what = Configure.getRequestType(method);
				msg.obj = result;
			} else {
				msg.what = Configure.XIAMI_RESPOND_FAILED;
				msg.arg1 = respondCode;
			}
			parentHandler.sendMessage(msg);
		}
		super.onPostExecute(result);

	}

	@Override
	public JsonElement doInBackground(HashMap<String, Object>... params) {

		try {
		
			int count=params.length;			
			for (int i = 0; i < params.length; i++) {			
					HashMap<String, Object> param = params[i];
					String result = mXMMusicData.xiamiRequest(method, param);
					if (!TextUtils.isEmpty(result)) {		
						// 获取的响应为：json字符串
						XiamiApiResponse response = mXMMusicData.getXiamiResponse(result);
						if (mXMMusicData.isResponseValid(response)) {
							respondCode = Configure.XIAMI_RESPOND_SECCESS;
							JsonElement JsonData = response.getData();
							doPostExecute(JsonData);
						}
					} else {
						// 查询失败
						respondCode = R.string.error_response;
					}			
			}
			return null;

		} catch (NoSuchAlgorithmException e) {
			respondCode = R.string.error_sign_algorithm;
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			respondCode = R.string.error_io;
			e.printStackTrace();
			return null;
		} catch (AuthExpiredException e) {
			respondCode = R.string.error_auth_expired;
			e.printStackTrace();
			return null;
		} catch (ResponseErrorException e) {
			respondCode = R.string.error_response;
			return null;
		}
	}
	
	
}
