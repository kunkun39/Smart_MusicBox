package com.nostra13.universalimageloader.cache.disc.utils;

import android.graphics.Bitmap;
import android.util.Log;
import com.changhong.common.system.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Jack Wang
 */
public class DiskCacheFileManager {

    public final static int MAX_DISKCACHE_ITEM_SIZE = 500;

    public final static int DELETE_ITEM_SIZE = 2;

    /**
     * check if cached file numbers is exceed max limitation
     * if exceed, return
     */
    public synchronized static void checkMaxFileItemExceedAndProcess() {
        final String[] list = MyApplication.smallImageCachePath.list();

        if (list != null && list.length > MAX_DISKCACHE_ITEM_SIZE) {
            Log.e("FILE_DELETE", "now small picture number is  " + list.length);

            int alreadyDeleteNumber = 0;
            File[] files = MyApplication.smallImageCachePath.listFiles();
            Arrays.sort(files, new FileComparator());

            for (File file : files) {
                try {
                    if (!file.isDirectory()) {
                        file.delete();
                        alreadyDeleteNumber++;
                        if (alreadyDeleteNumber >= DELETE_ITEM_SIZE) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    static class FileComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            Long last1 = f1.lastModified();
            Long last2 = f2.lastModified();
            return last1.compareTo(last2);
        }
    }

    /*************************************************保存和查找小文件生成的临时文件***************************************/

    public static void saveSmallImage(Bitmap bitmap, String vedioFilePath) {
        if (vedioFilePath.startsWith("file://")) {
            vedioFilePath = vedioFilePath.replaceFirst("file://", "");
        }
        int lastIndexOfSlash = vedioFilePath.lastIndexOf(File.separator);
        int lastIndexOfDot = vedioFilePath.lastIndexOf(".");
        String prefix = vedioFilePath.substring(lastIndexOfSlash, lastIndexOfDot);

        String vedioImagePath = MyApplication.smallImageCachePath + prefix + ".mk";
        File vedioImage = new File(vedioImagePath);
        if (vedioImage.exists()) {
            vedioImage.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(vedioImage);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String isSmallImageExist(String vedioFilePath) {
        if (vedioFilePath.startsWith("file://")) {
            vedioFilePath = vedioFilePath.replaceFirst("file://", "");
        }

        int lastIndexOfSlash = vedioFilePath.lastIndexOf(File.separator);
        int lastIndexOfDot = vedioFilePath.lastIndexOf(".");
        String prefix = vedioFilePath.substring(lastIndexOfSlash, lastIndexOfDot);

        String vedioImagePath = MyApplication.smallImageCachePath + prefix + ".mk";
        File vedioImage = new File(vedioImagePath);
        if (vedioImage != null && vedioImage.exists()) {
            return vedioImagePath;
        }
        return "";
    }
}
