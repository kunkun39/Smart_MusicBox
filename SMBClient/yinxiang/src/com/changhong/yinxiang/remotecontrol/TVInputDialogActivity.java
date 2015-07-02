package com.changhong.yinxiang.remotecontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.changhong.baidu.BaiDuVoiceDialog;
import com.changhong.common.service.ClientSocketInterface;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.PropertyUtil;
import com.changhong.yinxiang.R.id;
import com.changhong.yinxiang.R.layout;


public class TVInputDialogActivity extends Activity {

    private static final String TAG = "TVInputDialogActivity";
    public static EditText mEditText = null;
    AlertDialog mDialog = null;
    BroadcastReceiver mReceiver = null;
    int mTextIputType = 0;
    Messenger mMessenger = null, rMessenger = null;
    Handler mHandler = null;
    static final int POPINPUT_METHOD = 1;

    private BaiDuVoiceDialog baiDuDialog;

    //Listeners:
    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            sendContent();
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
        }
    };

    private ServiceConnection serviceCon = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            try {
                rMessenger.send(mHandler.obtainMessage(ClientSocketInterface.EVENT_IMCANCEL));
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            rMessenger = null;
        }


        @Override
        public void onServiceConnected(ComponentName comp, IBinder binder) {
            if (comp.getPackageName().contains(TVInputDialogActivity.this.getPackageName())) {
                rMessenger = new Messenger(binder);

                if (mMessenger != null) {
                    try {
                        Message msg = mHandler.obtainMessage();
                        msg.what = ClientSocketInterface.EVENT_IMLAUNCH;
                        msg.replyTo = mMessenger;
                        rMessenger.send(msg);
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                }


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialViews();

        initialEvents();

        stateChange(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        stateChange(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceCon);
        unregisterReceiver(mReceiver);
        mMessenger = null;
        clear();
    }

    @Override
    protected void onPause() {
        onUnBindService();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BindService();
    }

    // Functions:
    @SuppressLint("HandlerLeak")
    private void initialViews() {
        mTextIputType = InputType.TYPE_NULL;


        if (mHandler == null) {
            mHandler = new Handler() {
                public void handleMessage(Message msg) {
                    Message tempMsg = msg;
                    switch (tempMsg.what) {
                        case ClientSocketInterface.EVENT_IMSHOW: {
                            mTextIputType = Integer.parseInt((String) tempMsg.obj);
                            CreateDialogView();
                            sendEmptyMessage(POPINPUT_METHOD);
                            showToast();


                        }
                        break;
                        case ClientSocketInterface.EVENT_IMDATA_GET: {
                            String text = (String) tempMsg.obj;
                            if (mEditText != null
                                    && text != null
                                    && text.length() > 0) {

                                mEditText.setText(text);
                                Selection.setSelection(mEditText.getText(), ((String) tempMsg.obj).length());
                            }
                        }
                        break;
                        case ClientSocketInterface.EVENT_IMHIDE: {
                            hideInputMethod();
                            clear();
                        }
                        break;
                        case POPINPUT_METHOD: {
                            showInputMethod();
                        }
                        break;
                        default:
                            break;
                    }
                }

                ;
            };
        }

        mMessenger = new Messenger(mHandler);
        bindService(new Intent().setClass(this, TVRemoteControlService.class), serviceCon, Context.BIND_AUTO_CREATE);
    }

    private void initialEvents() {

        IntentFilter filter = new IntentFilter(ClientSocketInterface.INTENT_SOCKET);
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                stateChange(intent);
            }
        };
        registerReceiver(mReceiver, filter);

    }


    private void clear() {

        if (mDialog != null) {
            mDialog.dismiss();
        }
        mEditText = null;
        mDialog = null;

    }

    private void CreateDialogView() {
        if (mEditText != null
                && mTextIputType != mEditText.getInputType()) {
            clear();
        }

        if (mDialog != null)
            return;


        View view = LayoutInflater.from(this).inflate(layout.remote_control_input_dialog, null);
        mEditText = (EditText) view.findViewById(id.textbox);
        mEditText.setInputType(mTextIputType);
        mEditText.setSingleLine(false);
        mEditText.addTextChangedListener(mTextWatcher);

        Button btnSend = (Button) view.findViewById(id.send);
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MyApplication.vibrator.vibrate(100);
                sendContent();
                commitContent();
                hideInputMethod();
                clear();
            }

        });

        Button btnDelete = (Button) view.findViewById(id.delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MyApplication.vibrator.vibrate(100);
                deleteACharactor();
            }

        });


        Button btnClear = (Button) view.findViewById(id.clear);
        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MyApplication.vibrator.vibrate(100);
                mEditText.setText("");
                sendContent();
            }
        });


        /**
         * 百度语音输入法部分
         */
        baiDuDialog = BaiDuVoiceDialog.getIntenance(TVInputDialogActivity.this);
        Button yuyin = (Button) view.findViewById(id.yuyin);
        yuyin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditText != null) {
                    PropertyUtil.getInputMgrProperty(getApplicationContext()).getInputManger().hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                }
                baiDuDialog.show();
            }
        });

        mDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .setOnKeyListener(new OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int key,
                                         KeyEvent event) {
                        if (key == KeyEvent.KEYCODE_BACK
                                || key == KeyEvent.KEYCODE_CLEAR
                                //||key == KeyEvent.KEYCODE_DEL
                                || key == KeyEvent.KEYCODE_SOFT_RIGHT) {

                            hideInputMethod();
                            clear();
                        }
                        return false;
                    }

                }).create();

        mDialog.show();

        LayoutParams param = mDialog.getWindow().getAttributes();
        param.width = this.getWindow().getAttributes().width;
        param.gravity = Gravity.TOP;
        param.y = 80;
        mDialog.getWindow().setAttributes(param);
    }

    protected void showInputMethod() {
        if (mEditText != null) {
            PropertyUtil.getInputMgrProperty(getApplicationContext())
                    .getInputManger()
                    .showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
        }

    }


    private void onUnBindService() {
        if (rMessenger != null) {
            try {
                rMessenger.send(mHandler.obtainMessage(ClientSocketInterface.EVENT_IMCANCEL));
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }

    }

    private void BindService() {
        if (rMessenger != null
                && mMessenger != null) {
            try {
                Message msg = mHandler.obtainMessage();
                msg.what = ClientSocketInterface.EVENT_IMLAUNCH;
                msg.replyTo = mMessenger;
                rMessenger.send(msg);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }

    }

    protected void hideInputMethod() {
        if (mEditText != null) {
            PropertyUtil.getInputMgrProperty(getApplicationContext())
                    .getInputManger()
                    .hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

            try {

                Message msg = mHandler.obtainMessage();
                msg.what = ClientSocketInterface.EVENT_IMHIDE;
                rMessenger.send(msg);

            } catch (RemoteException e) {
                Log.e(TAG, e.getMessage());
            }
        }


    }

    private void showToast() {
        if (mEditText != null) {
            try {

                Message msg = mHandler.obtainMessage();
                msg.what = ClientSocketInterface.EVENT_IMLIKEDHINT;
                rMessenger.send(msg);

            } catch (RemoteException e) {
                Log.e(TAG, e.getMessage());
            }
        }

    }

    protected void stateChange(Intent intent) {
        switch (intent.getIntExtra(ClientSocketInterface.INTENT_PARM_INT_INPUT, ClientSocketInterface.EVENT_DEFAULT_VALUE)) {
            case ClientSocketInterface.EVENT_IMSHOW: {
                mTextIputType = intent.getIntExtra(ClientSocketInterface.STR_IM_SHOW, InputType.TYPE_NULL);

                CreateDialogView();
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(POPINPUT_METHOD);
                }

            }
            break;
            case ClientSocketInterface.EVENT_IMHIDE: {
                hideInputMethod();

            }
            break;
        }
    }

    private void deleteACharactor() {
        if (mEditText != null) {
            Editable editable = mEditText.getText();
            if (editable.length() > 0) {
                editable.delete(editable.length() - 1, editable.length());
            }

        }

        try {

            Message msg = mHandler.obtainMessage();
            msg.what = ClientSocketInterface.EVENT_IMDELETE;
            rMessenger.send(msg);

        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void sendContent() {
        String content = mEditText.getText().toString();

        //if (StringUtils.hasLength(content))
        {
            try {
                Message msg = mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString(ClientSocketInterface.STR_IM_FINISHINPUT, content);
                msg.what = ClientSocketInterface.EVENT_IMFININPUT;
                msg.setData(bundle);
                rMessenger.send(msg);

            } catch (Exception e) {
                // TODO: handle exception
                Log.d(TAG, e.getMessage());
            }

        }
    }

    private void commitContent() {

        try {

            Message msg = mHandler.obtainMessage();
            msg.what = ClientSocketInterface.EVENT_IMCOMMITE;
            rMessenger.send(msg);

        } catch (RemoteException e) {
            // TODO: handle exception
            Log.e(TAG, e.getMessage());
        }
    }

    protected void onUpdate() {
        TVRemoteControlService.update();
    }
}

