package com.changhong.xiami.data;


import java.io.Serializable;

import com.google.gson.JsonElement;


public class XiamiApiResponse implements Serializable {

    private String status;
    private JsonElement data;
    private String message;
    private int state;

    public XiamiApiResponse() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
