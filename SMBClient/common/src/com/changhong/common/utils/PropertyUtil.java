package com.changhong.common.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

// Create by yves yang

public class PropertyUtil {

    private static final String TAG = "PropertyUtil";

    private static PropertyUtil mThis;

    private InputManager mInputMgr;

    private Context mContext;

    private PropertyUtil() {
    }

    public static PropertyUtil clear() {
        if (mThis == null) {
            return null;
        }

        mThis.mContext = null;
        mThis.mInputMgr = null;
        mThis = null;
        return mThis;
    }

    // Main
    public static InputManager getInputMgrProperty(Context context) {
        if (mThis == null) {
            mThis = new PropertyUtil();
            mThis.mContext = context;
            mThis.mInputMgr = new InputManager(
                    (InputMethodManager) mThis.mContext.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE),
                    mThis.mContext);
        }
        return mThis.mInputMgr;
    }

}

