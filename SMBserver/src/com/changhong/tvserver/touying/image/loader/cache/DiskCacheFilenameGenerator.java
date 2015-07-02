package com.changhong.tvserver.touying.image.loader.cache;

import com.changhong.tvserver.MyApplication;

import java.io.File;

/**
 * Created by Jack Wang
 */
public class DiskCacheFilenameGenerator {

    public static File getDiskCacheFile(String imageUri) {
        return new File(MyApplication.imageDownloadRootPath + File.separator + generateDiskCacheFilename(imageUri));
    }

    public static String generateDiskCacheFilename(String imageUri) {
        return String.valueOf(imageUri.hashCode());
    }

}
