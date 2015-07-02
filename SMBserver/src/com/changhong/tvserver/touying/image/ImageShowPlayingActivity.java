package com.changhong.tvserver.touying.image;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;
import com.changhong.tvserver.R;
import com.changhong.tvserver.touying.image.domain.DragImageView;
import com.changhong.tvserver.utils.StringUtils;
import com.changhong.tvserver.touying.image.loader.core.ImageLoadController;

/**
 * picture tou ying flow:
 *
 * <p>
 * start a new activity flow
 * 1 - if small_picture and original_picture both send from client side
 *     1.1 - download small_picture
 *     1.2 - display small_picture and will not show fade in animation
 *     1.3 - download original_picture
 *     1.4 - display original_picture will show fade in animation from 0.8f to 1.0f
 * 2 - if just original_picture send from client side
 *     2.1 - download original_picture
 *     2.2 - display original_picture and will show fade in animation from 0.2f to 1.0f
 *
 * <p>
 * show picture just in one exist activity
 * 3 - if small_picture and original_picture both send from client side
 *     3.1 - download small_picture
 *     3.2 - display small_picture and will not show fade in animation
 *     3.3 - download original_picture
 *     3.4 - display original_picture will show fade in animation from 0.8f to 1.0f
 * 4 - if just original_picture send from client side
 *     4.1 - download original_picture
 *     4.2 - display original_picture and will show fade in animation from 0.2f to 1.0f
 */
public class ImageShowPlayingActivity extends FragmentActivity {

    private static final String TAG = "ImageShowPlayingActivity";

    public static final String EXTRA_IMAGE_INDEX = "image_index";

    public static final String EXTRA_IMAGE_URLS = "image_urls";

    public static final String CLIENT_IP = "client_ip";

    public static Handler handler;

    /**********************************************Image load part*****************************************************/

    private ViewFlipper flipper;

    private ProgressBar progressBar;

    /*********************************************Image rotation part**************************************************/

    /**
     * left rotation animation for image
     */
    private RotateAnimation animationLeft;

    /**
     * right rotation animation right
     */
    private RotateAnimation animationRight;

    /**
     * current image rotation position
     */
    private float currentRotationPosition = 0f;

    /**
     * 自动播放时flipper的动画
     */
    private Animation flipperInAnimation;


    /*********************************************tou ying image info**************************************************/

    private DragImageView imageView;

    /**
     * tou ying small image url
     */
    private String smallImagePath;

    /**
     * normal image url
     */
    private String imagePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_player);

        /**
         * handler for this activity
         */
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                DragImageView imageView = (DragImageView) flipper.getChildAt(0);
                String message = null;
                String[] tokens = null;
                switch (msg.what) {
                    case 1:
                        break;
                    case 2:
                        if (currentRotationPosition == 0f) {
                            currentRotationPosition = 360f;
                        }
                        animationLeft = new RotateAnimation(currentRotationPosition, currentRotationPosition - 90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        animationLeft.setDuration(600);
                        animationLeft.setFillAfter(true);
                        currentRotationPosition = currentRotationPosition - 90f;

                        imageView.startAnimation(animationLeft);
                        break;
                    case 3:
                        if (currentRotationPosition == 360f) {
                            currentRotationPosition = 0f;
                        }
                        animationRight = new RotateAnimation(currentRotationPosition, currentRotationPosition + 90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        animationRight.setDuration(600);
                        animationRight.setFillAfter(true);
                        currentRotationPosition = currentRotationPosition + 90f;

                        imageView.startAnimation(animationRight);
                        break;
                    case 4:
                        try {
                            message = (String) msg.obj;
                            tokens = message.split(":");
                            imageView.onPointerDown(Integer.valueOf(tokens[1]), Float.valueOf(tokens[2]), Float.valueOf(tokens[3]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 5:
                        try {
                            message = (String) msg.obj;
                            tokens = message.split(":");
                            imageView.onTouchMove(Float.valueOf(tokens[1]), Float.valueOf(tokens[2]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 6:
                        imageView.mode = DragImageView.MODE.NONE;
                        break;
                    case 7:
                        imageView.mode = DragImageView.MODE.NONE;
//                        if (imageView.isScaleAnim) {
//                            imageView.doScaleAnim();
//                        }
                        break;
                    case 8:
                        if (StringUtils.hasLength(smallImagePath)) {
                            ImageLoadController.getInstance().gotoDownloadWay(this, 9, false, smallImagePath);
                        } else {
                            handler.sendEmptyMessage(9);
                        }
                        break;
                    case 9:
                        if (StringUtils.hasLength(smallImagePath)) {
                            ImageLoadController.gotoDisplayWay(this, 11, imageView, false, false, smallImagePath);
                        }
                        ImageLoadController.getInstance().gotoDownloadWay(this, 10, true, imagePath);
                        break;
                    case 10:
                        boolean showAnimation = false;
                        boolean hasSmallImage = StringUtils.hasLength(smallImagePath) ? true : false;
                        if (msg.obj != null && ((String) msg.obj).equals("show")) {
                            showAnimation = true;
                            hasSmallImage = false;
                        } else {
                            showAnimation = StringUtils.hasLength(smallImagePath) ? true : false;
                        }
                        ImageLoadController.gotoDisplayWay(this, 11, imageView, hasSmallImage, showAnimation, imagePath);
                        break;
                    case 11:
                        progressBar.setVisibility(View.GONE);
                        break;
                    case 100:
                        progressBar.setVisibility(View.VISIBLE);
                        //需要重置现在的位置
                        currentRotationPosition = 0;

                        Drawable drawable = imageView.getDrawable();
                        if (drawable != null) {
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            if (bitmap != null && !bitmap.isRecycled()) {
                                bitmap.recycle();
                                bitmap = null;
                            }
                            imageView.setImageDrawable(null);
                        }
                        imageView = null;

                        try {
                            String[] urls = (String[]) msg.obj;
                            imagePath = urls[0];
                            smallImagePath = urls[1];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        flipper.removeAllViews();
                        flipper.addView(getImageView());
                        flipper.setDisplayedChild(0);
                        break;
                    default:
                        break;
                }
            }
        };

        /**
         * Receiver finish filter
         */
        IntentFilter homefilter = new IntentFilter();
        homefilter.addAction("FinishActivity");
        registerReceiver(this.FinishReceiver, homefilter);

        /**
         * progress bar for image initial loading
         */
        progressBar = (ProgressBar) findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);

        /**
         * image url get
         */
        String[] urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
        imagePath = urls[0];
        smallImagePath = urls[1];

        /**
         * image load
         */
        flipper = (ViewFlipper)findViewById(R.id.image_flipper);
        flipper.setDrawingCacheEnabled(false);
        flipper.addView(getImageView());
        flipper.setDisplayedChild(0);
    }

    private View getImageView() {
        imageView = new DragImageView(this);
        imageView.setImageBitmap(null);
        imageView.setImageDrawable(null);
        ImageLoadController.getInstance().getImage(handler, 8, 10, imagePath);
        return imageView;
    }

    /**************************************************Broadcast*****************************************************/

    private BroadcastReceiver FinishReceiver = new BroadcastReceiver() {
        public void onReceive(Context mContext, Intent mIntent) {
            if (mIntent.getAction().equals("FinishActivity")) {
                finish();
            }
        }
    };

    /**********************************************Activity override method********************************************/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public void onDestroy() {
        if (FinishReceiver != null) {
            unregisterReceiver(FinishReceiver);
            FinishReceiver = null;
        }
        super.onDestroy();
    }

}