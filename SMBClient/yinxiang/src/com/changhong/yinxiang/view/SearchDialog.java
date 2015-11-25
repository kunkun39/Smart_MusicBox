package com.changhong.yinxiang.view;

import com.changhong.yinxiang.R;

import android.app.Dialog;
import android.content.Context;
import android.sax.TextElementListener;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class SearchDialog extends Dialog {

    /**
     * all buttons for number dialog
     */
    public Button btnSubmit;
    public EditText search_keywords;

    public SearchDialog(Context context) {
        super(context, R.style.InputTheme);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.x=-20;
        window.setAttributes(wlp);
        window.setGravity(Gravity.BOTTOM);
        setContentView(R.layout.search_dialog);
        
        btnSubmit = (Button) findViewById(R.id.search_submit);
        search_keywords = (EditText) findViewById(R.id.search_input);
    }

    @Override
    public void show() {
        super.show();
    }
}
