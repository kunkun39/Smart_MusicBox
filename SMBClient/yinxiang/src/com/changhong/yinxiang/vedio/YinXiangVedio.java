package com.changhong.yinxiang.vedio;

import java.io.Serializable;

/**
 * Created by Jack Wang
 */
public class YinXiangVedio implements Serializable {

    private int id;

    private String title;

    private String displayName;

    private String mimeType;

    private String path;

    private long duration;

    private long createTime;
    /**
     *
     */
    public YinXiangVedio() {
        super();
    }

    public YinXiangVedio(int id, String title,
                         String displayName, String mimeType, String path,
                         long duration, long createTime) {
        super();
        this.id = id;
        this.title = title;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.path = path;
        this.duration = duration;
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
