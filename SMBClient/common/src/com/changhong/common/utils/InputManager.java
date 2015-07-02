package com.changhong.common.utils;


import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import java.util.Iterator;
import java.util.List;


public class InputManager {

    private static final String TAG = "InputManager";

    private InputMethodManager mInpuMtMgr;

    private Context mContext;

    private String mInputMethod;

    public InputManager(InputMethodManager inputMgr,  Context context) {
        putInputManger(inputMgr, context);
    }

    public InputMethodManager getInputManger() {
        return mInpuMtMgr;
    }

    public InputManager putInputManger(InputMethodManager inputMgr, Context context) {
        if (inputMgr != null)
            mInpuMtMgr = inputMgr;

        if (context != null) {
            mContext = context;
        }

        return this;
    }

    public boolean setInputMethod(String inputMethodName) {
        if (inputMethodName == null) {
            return false;
        }

        if (getInputMethodCur().contains(inputMethodName)) {
            return true;
        }

        Iterator<InputMethodInfo> infoIterator;
        List<InputMethodInfo> imList = getInputMethodList();

        infoIterator = imList.iterator();
        while (infoIterator.hasNext()) {
            InputMethodInfo info = infoIterator.next();
            if (info.getId().contains(inputMethodName)) {
                saveDefaultMethod();
                return selectInputMethodList(info.getId());
            }
        }

        return false;
    }

    public boolean setDefaultMethod() {
        return setInputMethod(mInputMethod);
    }

    protected void saveDefaultMethod() {
        String inputMethod;
        if (mInputMethod == null) {
            inputMethod = getInputMethodCur();
            if (!inputMethod.contains(getName())) {
                mInputMethod = inputMethod;
            }
        }

    }

    protected final String getInputMethodCur() {
        if (mContext == null) {
            return null;
        }

        try {
            return Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.DEFAULT_INPUT_METHOD);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            return null;
        }
    }

    protected List<InputMethodInfo> getInputMethodList() {
        if (mInpuMtMgr == null) {
            return null;
        }

        List<InputMethodInfo> inputMethodList;

        inputMethodList = mInpuMtMgr.getInputMethodList();
        return inputMethodList;
    }

    protected void selectInputMethodListItem(int index) {
        selectInputMethodList(mInpuMtMgr.getInputMethodList().get(index).getId());
    }

    protected boolean selectInputMethodList(String id) {
        if (mContext == null)
            return false;

        try {
            Settings.Secure.putString(mContext.getContentResolver(),
                    Settings.Secure.DEFAULT_INPUT_METHOD,
                    id);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            return false;
        }

        return true;
    }

    public String getName() {
        return "RemoteInputListenService";
    }

    public boolean setMeAsDefault() {
        return PropertyUtil.getInputMgrProperty(mContext.getApplicationContext()).setInputMethod(getName());
    }

    public boolean restoreDefault() {
        return PropertyUtil.getInputMgrProperty(mContext.getApplicationContext()).setDefaultMethod();
    }
}