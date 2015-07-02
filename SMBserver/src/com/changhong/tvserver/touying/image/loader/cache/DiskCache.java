package com.changhong.tvserver.touying.image.loader.cache;

import java.io.File;

/**
 * Created by Jack Wang
 */
public interface DiskCache {

    /**
     * this method is used for get cached file in local disk
     * @param imageUri image url which is http url
     * @return file is exist, else return null
     */
    File getDiskCachedFile(String imageUri);

}
