package com.changhong.tvserver.touying.image.domain;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * *
 * @author Jack Wang
 */
public class DragImageView extends ImageView {

    private Activity activity;

    private int screen_W, screen_H;

    private int bitmap_W, bitmap_H;

    private int MAX_W, MAX_H, MIN_W, MIN_H;

    private int current_Top, current_Right, current_Bottom, current_Left;

    private int start_Top = -1, start_Right = -1, start_Bottom = -1,  start_Left = -1;

    public float beforeLenght, afterLenght;

    public float scale_temp;

    public enum MODE {
        NONE, ZOOM
    }

    public MODE mode = MODE.NONE;

    public boolean isScaleAnim = false;

    /**
     * 根据高度还是宽度来限制放大
     */
    public boolean accordingTowidth = true;

    private MyAsyncTask myAsyncTask;

    public DragImageView(Context context) {
        super(context);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        screen_W = wm.getDefaultDisplay().getWidth();
        screen_H = wm.getDefaultDisplay().getHeight();
    }

    public DragImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /***********************************************图片基本信息部分***************************************************/

    public void setDragImageBitmap(int width, int height) {
        bitmap_W = width;
        bitmap_H = height;

        if (bitmap_W >= bitmap_H) {
            accordingTowidth = true;
        } else {
            accordingTowidth = false;
        }

        int zoomSize = 4;

        MAX_W = screen_W * zoomSize;
        MAX_H = screen_H * zoomSize;
        MIN_W = screen_W;
        MIN_H = screen_H;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (start_Top == -1) {
            start_Top = top;
            start_Left = left;
            start_Bottom = bottom;
            start_Right = right;
        }
    }

    private void setPosition(int left, int top, int right, int bottom) {
        this.layout(left, top, right, bottom);
    }

    /***********************************************手势部分对应的操作***************************************************/

    public void onPointerDown(int pointerCount, float x, float y) {
        if (pointerCount == 2) {
            mode = MODE.ZOOM;
            beforeLenght = getDistance(x, y);
        }
    }

    public void onTouchMove(float x, float y) {
        if (mode == MODE.ZOOM) {
            afterLenght = getDistance(x, y);

            float gapLenght = afterLenght - beforeLenght;

            if (Math.abs(gapLenght) > 5f) {
                scale_temp = afterLenght / beforeLenght;

                this.setScale(scale_temp);

                beforeLenght = afterLenght;
            }
        }
    }

    public float getDistance(float x, float y) {
        return FloatMath.sqrt(x * x + y * y);
    }

    /*************************************************图片缩放任务*******************************************************/

    void setScale(float scale) {
        int disX = (int) (this.getWidth() * Math.abs(1 - scale)) / 4;
        int disY = (int) (this.getHeight() * Math.abs(1 - scale)) / 4;

        if (accordingTowidth) {
            if (scale > 1 && this.getWidth() < MAX_W) {
                roomBigger(disX, disY);

            } else if (scale < 1 && this.getWidth() > MIN_W) {
                zoomSmaller(disX, disY);
            }
        } else {
            if (scale > 1 && this.getHeight() < MAX_H) {
                roomBigger(disX, disY);

            } else if (scale < 1 && this.getHeight() > MIN_H) {
                zoomSmaller(disX, disY);
            }
        }

    }

    private void roomBigger(int disX, int disY) {
        current_Left = this.getLeft() - disX;
        current_Top = this.getTop() - disY;
        current_Right = this.getRight() + disX;
        current_Bottom = this.getBottom() + disY;

        this.setFrame(current_Left, current_Top, current_Right, current_Bottom);
    }

    private void zoomSmaller(int disX, int disY) {
        current_Left = this.getLeft() + disX;
        current_Top = this.getTop() + disY;
        current_Right = this.getRight() - disX;
        current_Bottom = this.getBottom() - disY;

        /**
         * 先放大，再缩小，因为缩小的比例是按照移动的距离来的，如果比例为0.7X，那么缩小很可能小于原来的尺寸，所以判断一下
         */
        if (start_Left >= 0 && current_Left > start_Left) {
            current_Left = start_Left;
        }
        if (start_Top >= 0 && current_Top > start_Top) {
            current_Top = start_Top;
        }
        if (start_Right >= 0 && current_Right < start_Right) {
            current_Right = start_Right;
        }
        if (start_Bottom >= 0 && current_Bottom < start_Bottom) {
            current_Bottom = start_Bottom;
        }

        this.setFrame(current_Left, current_Top, current_Right, current_Bottom);
    }

    /*************************************************图片缩放任务*******************************************************/

    public void doScaleAnim() {
        myAsyncTask = new MyAsyncTask(screen_W, this.getWidth(), this.getHeight());
        myAsyncTask.setLTRB(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
        myAsyncTask.execute();
        isScaleAnim = false;
    }

    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        private int screen_W, current_Width, current_Height;

        private int left, top, right, bottom;

        private float scale_WH;

        public void setLTRB(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        private float STEP = 8f;

        private float step_H, step_V;

        public MyAsyncTask(int screen_W, int current_Width, int current_Height) {
            super();
            this.screen_W = screen_W;
            this.current_Width = current_Width;
            this.current_Height = current_Height;
            scale_WH = (float) current_Height / current_Width;
            step_H = STEP;
            step_V = scale_WH * STEP;
        }

        @Override
        protected Void doInBackground(Void... params) {

            while (current_Width <= screen_W) {

                left -= step_H;
                top -= step_V;
                right += step_H;
                bottom += step_V;

                current_Width += 2 * step_H;

                left = Math.max(left, start_Left);
                top = Math.max(top, start_Top);
                right = Math.min(right, start_Right);
                bottom = Math.min(bottom, start_Bottom);
                Log.d("jj", "top=" + top + ",bottom=" + bottom + ",left=" + left + ",right=" + right);
                onProgressUpdate(new Integer[]{left, top, right, bottom});
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(final Integer... values) {
            super.onProgressUpdate(values);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setFrame(values[0], values[1], values[2], values[3]);
                }
            });

        }
    }

}
