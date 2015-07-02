package com.changhong.common.utils;

import android.util.Log;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This is different from WebUtils, this load class from android sdk, another load from httpclient.jar
 *
 * Created by Jack Wang
 */
public class HttpUtils {

    public static String sendGetHttpAsString(String baseUrl, List<BasicNameValuePair> params) {
        String param = URLEncodedUtils.format(params, "UTF-8");
        HttpGet getMethod = null;
        if (param != null && !param.isEmpty()) {
            getMethod = new HttpGet(baseUrl + "?" + param);
        } else {
            getMethod = new HttpGet(baseUrl);
        }
        HttpClient httpClient = new DefaultHttpClient();

        try {
            HttpResponse response = httpClient.execute(getMethod);

            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] sendGetHttpAsByte(String baseUrl, List<BasicNameValuePair> params) {
        String param = URLEncodedUtils.format(params, "UTF-8");
        HttpGet getMethod = null;
        if (param != null && !param.isEmpty()) {
            getMethod = new HttpGet(baseUrl + "?" + param);
        } else {
            getMethod = new HttpGet(baseUrl);
        }
        HttpClient httpClient = new DefaultHttpClient();

        try {
            HttpResponse response = httpClient.execute(getMethod);

            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toByteArray(response.getEntity());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
