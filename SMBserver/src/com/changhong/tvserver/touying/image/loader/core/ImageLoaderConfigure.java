package com.changhong.tvserver.touying.image.loader.core;

import com.changhong.tvserver.touying.image.loader.cache.DiskCache;
import com.changhong.tvserver.touying.image.loader.cache.LruDiskCache;

/**
 * in this class, you can find all init fields for whole image loader
 *
 * Created by Jack Wang
 */
public class ImageLoaderConfigure {

    /**
     * disk cache implementation
     */
    private DiskCache diskCache;

    /**
     * init all empty fileds which not has been initiation
     */
    public void initAllEmptyFields() {
        if (diskCache == null) {
            diskCache = new LruDiskCache();
        }
    }

    public ImageLoaderConfigure initDiskCache(DiskCache diskCache) {
        this.diskCache = diskCache;
        return this;
    }

    public DiskCache getDiskCache() {
        return diskCache;
    }

}
