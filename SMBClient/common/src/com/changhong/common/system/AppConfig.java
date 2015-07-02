package com.changhong.common.system;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class AppConfig {

    /**
     * the parameter which decide the min size of the file we compress 1M
     */
    public final static int PICTURE_COMPRESS_MIN_SIZE = 2014 * 1024;

    /**
     * the parameter which decide the min size of the small picture touying size 512K
     */
    public final static int PICTURE_SMALL_TOUYING_MIN_SIZE = 512 * 1024;

    /**
     * the camera definition for every mobile company
     */
    public final static List<String> MOBILE_CARMERS_PACKAGE = new ArrayList<String>();

    static {
        MOBILE_CARMERS_PACKAGE.add("camera");
    }
}
