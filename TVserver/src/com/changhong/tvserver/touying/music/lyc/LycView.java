package com.changhong.tvserver.touying.music.lyc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.changhong.tvserver.R;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jack Wang
 */
public class LycView extends View {

    private static ArrayList<LyricSentence> mLyricSentences = new ArrayList<LyricSentence>();

    private final static Pattern mBracketPattern = Pattern.compile("(?<=\\[).*?(?=\\])");
    private final static Pattern mTimePattern = Pattern.compile("(?<=\\[)(\\d{2}:\\d{2}\\.?\\d{0,3})(?=\\])");

    private static final String TAG = null;

    private float mX;       //灞忓箷X杞寸殑涓偣锛屾鍊煎浐瀹氾紝淇濇寔姝岃瘝鍦╔涓棿鏄剧ず
    private float offsetY;      //姝岃瘝鍦╕杞翠笂鐨勫亸绉婚噺锛屾鍊间細鏍规嵁姝岃瘝鐨勬粴鍔ㄥ彉灏�
    public boolean blLrc = false;
    private int curlrcIndex = 0; //淇濆瓨姝岃瘝鐨刴LyricSentences涓嬫爣
    private int SIZEWORD = 0;//鏄剧ず姝岃瘝鏂囧瓧鐨勫ぇ灏忓�
    public int INTERVAL = 20;//姝岃瘝姣忚鐨勯棿闅�

    Paint paint = new Paint();//鐢荤瑪锛岀敤浜庣敾涓嶆槸楂樹寒鐨勬瓕璇�
    Paint paintHL = new Paint();  //鐢荤瑪锛岀敤浜庣敾楂樹寒鐨勬瓕璇嶏紝鍗冲綋鍓嶅敱鍒拌繖鍙ユ瓕璇�

    //双缓冲抗抖动
    Bitmap cacheBitmap = null;
    Canvas cacheCanvas = null;
    final int VIEW_WIDTH = 4000;
    final int VIEW_HEIGHT = 500;

    //娓叉煋 娓愬彉鏁堟灉
    private Shader shaders;
    private int[] colors;

    public LycView(Context context) {
        super(context);
        init();
    }

    public LycView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (blLrc) {

            paintHL.setTextSize(SIZEWORD);
            paint.setTextSize(SIZEWORD);

            String lrc = mLyricSentences.get(curlrcIndex).getContentText();
            canvas.drawText(lrc, mX, offsetY + (SIZEWORD + INTERVAL) * curlrcIndex, paintHL);

            for (int i = curlrcIndex - 1; i >= 0; i--) {
                lrc = mLyricSentences.get(i).getContentText();
                if (offsetY + (SIZEWORD + INTERVAL) * i < 40) {
                    break;
                }

                canvas.drawText(lrc, mX, offsetY + (SIZEWORD + INTERVAL) * i, paint);
            }

            for (int i = curlrcIndex + 1; i < mLyricSentences.size(); i++) {
                lrc = mLyricSentences.get(i).getContentText();
                if (offsetY + (SIZEWORD + INTERVAL) * i > 450) {
                    break;
                }

                canvas.drawText(lrc, mX, offsetY + (SIZEWORD + INTERVAL) * i, paint);
            }

        }
        /*Paint bmpPaint = new Paint();
		canvas.drawBitmap(cacheBitmap , 0 , 0, bmpPaint );*/
        super.onDraw(canvas);

    }


    public void init() {

        colors = new int[]{0x0fffffff, Color.WHITE, Color.WHITE, 0x0fffffff};
        shaders = new LinearGradient(0, 0, 0, 450, colors, null, TileMode.CLAMP);

        offsetY = 320;
        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(this.getResources().getColor(R.color.trans_white));
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setAlpha(125);
        paint.setShader(shaders);

        paintHL = new Paint();
        paintHL.setTextAlign(Paint.Align.CENTER);

        paintHL.setColor(Color.GREEN);
        paintHL.setAntiAlias(true);
        paintHL.setAlpha(145);

       /* cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH
    			, VIEW_HEIGHT, Config.ARGB_8888);
        cacheCanvas = new Canvas();
        cacheCanvas.setBitmap(cacheBitmap);*/
    }

    public void drawLrcToCache() {
        if (blLrc) {
            //cacheBitmap.recycle();
            // cacheBitmap=null;
            //cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH
            //, VIEW_HEIGHT , Config.ARGB_8888);
            //  cacheCanvas.setBitmap(cacheBitmap);

            //  cacheCanvas.drawColor(Color.BLACK);

            paintHL.setTextSize(SIZEWORD);
            paint.setTextSize(SIZEWORD);

            String lrc = mLyricSentences.get(curlrcIndex).getContentText();
            cacheCanvas.drawText(lrc, mX, offsetY + (SIZEWORD + INTERVAL) * curlrcIndex, paintHL);

            for (int i = curlrcIndex - 1; i >= 0; i--) {
                lrc = mLyricSentences.get(i).getContentText();
                if (offsetY + (SIZEWORD + INTERVAL) * i < 40) {
                    break;
                }

                cacheCanvas.drawText(lrc, mX, offsetY + (SIZEWORD + INTERVAL) * i, paint);
            }

            for (int i = curlrcIndex + 1; i < mLyricSentences.size(); i++) {
                lrc = mLyricSentences.get(i).getContentText();
                if (offsetY + (SIZEWORD + INTERVAL) * i > 450) {
                    break;
                }

                cacheCanvas.drawText(lrc, mX, offsetY + (SIZEWORD + INTERVAL) * i, paint);
            }

        }

        invalidate();

    }


    /**
     * 璁剧疆姝岃瘝鏂囦欢澶у皬
     */
    public void setTextSize() {
        if (!blLrc) {
            return;
        }
        SIZEWORD = 320 / 10;

    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mX = w * 0.5f;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 姝岃瘝婊氬姩鐨勯�搴�
     *
     * @return 杩斿洖姝岃瘝婊氬姩鐨勯�搴�
     */
    public Float speedLrc() {
        float speed = 0;
        if (offsetY + (SIZEWORD + INTERVAL) * curlrcIndex > 225) {
            speed = ((offsetY + (SIZEWORD + INTERVAL) * curlrcIndex - 225) / 20);

        } else if (offsetY + (SIZEWORD + INTERVAL) * curlrcIndex < 200) {

            speed = 0;
        }

        return speed;
    }

    /**
     * 鎸夊綋鍓嶇殑姝屾洸鐨勬挱鏀炬椂闂达紝浠庢瓕璇嶉噷闈㈣幏寰楅偅涓�彞
     *
     * @param currentTime 褰撳墠姝屾洸鐨勬挱鏀炬椂闂�
     * @return 杩斿洖褰撳墠姝岃瘝鐨勭储寮曞�
     */
    public int selectIndex(int currentTime) {
        if (!blLrc) {
            return 0;
        }

        int findIndex = 0;

        if (curlrcIndex > 0) {
            // 如果已经指定了歌词，则现在位置开始
            findIndex = curlrcIndex;
        }


        try {

            long lyricTime = mLyricSentences.get(findIndex).getStartTime();

            if (currentTime > lyricTime) {

                if (findIndex == (mLyricSentences.size() - 1)) {

                    curlrcIndex = findIndex;
                    return findIndex;
                }

                int new_index = findIndex + 1;
                while (new_index < mLyricSentences.size()
                        && mLyricSentences.get(new_index).getStartTime() <= currentTime) {
                    ++new_index;
                }

                curlrcIndex = new_index - 1;

                return new_index - 1;

            } else if (currentTime < lyricTime) {

                if (findIndex == 0) {

                    curlrcIndex = findIndex;
                    return curlrcIndex;
                }

                int new_index = findIndex - 1;

                while (new_index > 0
                        && mLyricSentences.get(new_index).getStartTime() > currentTime) {
                    --new_index;
                }

                curlrcIndex = new_index;
                return curlrcIndex;

            } else {
                curlrcIndex = findIndex;
                return curlrcIndex;
            }

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 璇诲彇姝岃瘝鏂囦欢
     *
     * @param file 姝岃瘝鏂囦欢璺緞
     */
    public void read(String file) {
        mLyricSentences.clear();

        String data = "";

        File saveFile = new File(file);

        if (!saveFile.exists()) {
            blLrc = false;
            return;
        }

        if (!saveFile.isFile()) {
            blLrc = false;
            return;
        }

        try {

            FileInputStream stream = new FileInputStream(saveFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"));

            while ((data = br.readLine()) != null) {
                parseLine(data);

            }

            stream.close();

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            blLrc = false;
        }

        Collections.sort(mLyricSentences,
                new Comparator<LyricSentence>() {
                    public int compare(LyricSentence object1, LyricSentence object2) {
                        if (object1.getStartTime() > object2.getStartTime()) {
                            return 1;
                        } else if (object1.getStartTime() < object2.getStartTime()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });

        blLrc = true;

    }


    /**
     * 瑙ｆ瀽姝岃瘝锛屾寜鍙ュ瓙鍒嗙粍
     */
    private static void parseLine(String line) {
        if (line.equals("")) {
            return;
        }
        String content = null;
        int timeLength = 0;
        int index = 0;
        Matcher matcher = mTimePattern.matcher(line);
        int lastIndex = -1;
        int lastLength = -1;

        List<String> times = new ArrayList<String>();

        while (matcher.find()) {
            String s = matcher.group();
            index = line.indexOf("[" + s + "]");
            if (lastIndex != -1 && index - lastIndex > lastLength + 2) {
                content = trimBracket(line.substring(
                        lastIndex + lastLength + 2, index));
                for (String string : times) {
                    long t = parseTime(string);
                    if (t != -1) {

                        mLyricSentences.add(new LyricSentence(t, content));
                    }
                }
                times.clear();
            }
            times.add(s);
            lastIndex = index;
            lastLength = s.length();
        }
        if (times.isEmpty()) {
            return;
        }

        timeLength = lastLength + 2 + lastIndex;
        if (timeLength > line.length()) {
            content = trimBracket(line.substring(line.length()));
        } else {
            content = trimBracket(line.substring(timeLength));
        }
        for (String s : times) {
            long t = parseTime(s);
            if (t != -1) {
                mLyricSentences.add(new LyricSentence(t, content));
            }
        }
    }


    @SuppressLint("DefaultLocale")
    private static long parseTime(String strTime) {
        String beforeDot = new String("00:00:00");
        String afterDot = new String("0");

        int dotIndex = strTime.indexOf(".");
        if (dotIndex < 0) {
            beforeDot = strTime;
        } else if (dotIndex == 0) {
            afterDot = strTime.substring(1);
        } else {
            beforeDot = strTime.substring(0, dotIndex);
            afterDot = strTime.substring(dotIndex + 1);
        }

        long intSeconds = 0;
        int counter = 0;
        while (beforeDot.length() > 0) {
            int colonPos = beforeDot.indexOf(":");
            try {
                if (colonPos > 0) {
                    intSeconds *= 60;
                    intSeconds += Integer.valueOf(beforeDot.substring(0, colonPos));
                    beforeDot = beforeDot.substring(colonPos + 1);
                } else if (colonPos < 0) {
                    intSeconds *= 60;
                    intSeconds += Integer.valueOf(beforeDot);
                    beforeDot = "";
                } else {
                    return -1;
                }
            } catch (NumberFormatException e) {
                return -1;
            }
            ++counter;
            if (counter > 3) {
                return -1;
            }
        }

        String totalTime = String.format("%d.%s", intSeconds, afterDot);
        Double doubleSeconds = Double.valueOf(totalTime);
        return (long) (doubleSeconds * 1000);
    }

    private static String trimBracket(String content) {
        String s = null;
        String result = content;
        Matcher matcher = mBracketPattern.matcher(content);
        while (matcher.find()) {
            s = matcher.group();
            result = result.replace("[" + s + "]", "");
        }
        return result;
    }

    public boolean isBlLrc() {
        return blLrc;
    }

    public float getOffsetY() {
        return offsetY;
    }

    /**
     * @param offsetY the offsetY to set
     */
    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    /**
     * 鏉╂柨娲栧宀冪槤閺傚洤鐡ч惃鍕亣鐏忥拷
     */
    public int getSIZEWORD() {
        return SIZEWORD;
    }

    /**
     * 鐠佸墽鐤嗗宀冪槤閺傚洤鐡ч惃鍕亣鐏忥拷
     *
     * @param sIZEWORD the sIZEWORD to set
     */
    public void setSIZEWORD(int sIZEWORD) {
        SIZEWORD = sIZEWORD;
    }
}
