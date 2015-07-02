package com.changhong.tvserver.touying.image.loader.task;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import com.changhong.tvserver.touying.image.domain.DragImageView;
import com.changhong.tvserver.touying.image.loader.cache.DiskCacheFilenameGenerator;
import com.changhong.tvserver.touying.image.loader.core.ImageBasicDecoder;

import java.io.*;

/**
 * Created by Jack Wang
 */
public class ImageDisplayTask {

    /**
     * handle which used for tell activity should handle other things after image display finished
     */
    private final Handler handler;

    /**
     * message id for activity handle after image display finish
     */
    private final int finishID;

    /**
     * view which show image source
     */
    private final ImageView imageView;

    /**
     * if has small image, the AlphaAnimation will from 0.8f to 1.0f and if not from 0.2f to 1.0f
     */
    private final boolean hasSmallImage;

    /**
     * the parameter which used for decide which http image is orignal image or just zoom in image
     */
    private final boolean showAnimation;

    /**
     * local resource for image show
     */
    private final File localFile;

    public ImageDisplayTask(Handler handler, int finishID, ImageView imageView, boolean hasSmallImage, boolean showAnimation, String imageUri) {
        this.handler = handler;
        this.finishID = finishID;
        this.imageView = imageView;
        this.hasSmallImage = hasSmallImage;
        this.showAnimation = showAnimation;
        this.localFile = DiskCacheFilenameGenerator.getDiskCacheFile(imageUri);
    }

    public void run() {
        Bitmap bitmap = null;

        if (localFile != null && localFile.exists()) {
            //decode bitmap and set the bitmap rotation
            bitmap = ImageBasicDecoder.decodeSampledBitmapFromFile(localFile.getAbsolutePath(), imageView.getWidth(), imageView.getHeight());
            int degree = ImageBasicDecoder.readPictureDegree(localFile.getAbsolutePath());
            if (degree > 0) {
                Matrix m = new Matrix();
                m.postRotate(degree);
                bitmap =  bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            }

            //image show bitmap
            imageView.setImageBitmap(bitmap);
            ((DragImageView) imageView).setDragImageBitmap(bitmap.getWidth(), bitmap.getHeight());
            addImageShowAnimation();

            //send display finish message to activity
            if (handler != null && finishID > 0) {
                handler.sendEmptyMessage(finishID);
            }
        }
    }

    private void addImageShowAnimation() {
        if (showAnimation) {
            float fromAlpha = 0.5f;
            if (hasSmallImage) {
                fromAlpha = 0.8f;
            }
            AlphaAnimation fadeImage = new AlphaAnimation(fromAlpha, 1f);
            fadeImage.setDuration(1000);
            imageView.startAnimation(fadeImage);
        }
    }

}
