package com.changhong.yinxiang.utils;

import com.changhong.common.utils.StringUtils;

import java.util.*;

/**
 * Created by Jack Wang
 */
public class YuYingWordsUtils {

    /**************************************************控制部分*********************************************************/

    /**
     * yu yin control mapping
     */
    private final static Map<String, List<String>> TV_CONTROL_KEYWORDS = new HashMap<String, List<String>>();

    static {
        List<String> shouYe = new ArrayList<String>();
        shouYe.add("主页");shouYe.add("首页");shouYe.add("桌面");shouYe.add("退出");
        TV_CONTROL_KEYWORDS.put("key:home", shouYe);

        List<String> back = new ArrayList<String>();
        back.add("返回");back.add("回退");back.add("后退");back.add("上一级");back.add("上1级");back.add("上一");back.add("上1");
        TV_CONTROL_KEYWORDS.put("key:back", back);

        List<String> ok = new ArrayList<String>();
        ok.add("ｏｋ");ok.add("确定");ok.add("确认");
        TV_CONTROL_KEYWORDS.put("key:ok", ok);

        List<String> volumnUp = new ArrayList<String>();
        volumnUp.add("声音大点");volumnUp.add("大点声");volumnUp.add("大声点");volumnUp.add("加声音");volumnUp.add("开点声音");
        TV_CONTROL_KEYWORDS.put("key:volumeup|key:volumeup", volumnUp);

        List<String> volumnDown = new ArrayList<String>();
        volumnDown.add("声音小点");volumnDown.add("小点声");volumnDown.add("小声点");volumnDown.add("减声音");volumnDown.add("关点声音");volumnDown.add("观点声音");
        TV_CONTROL_KEYWORDS.put("key:volumedown|key:volumedown", volumnDown);

        List<String> tv = new ArrayList<String>();
        tv.add("电视");tv.add("频道");
        TV_CONTROL_KEYWORDS.put("key:dtv", tv);

        List<String> tvUp = new ArrayList<String>();
        tvUp.add("上一台");tvUp.add("上1台");tvUp.add("上一个");tvUp.add("上1个");
        TV_CONTROL_KEYWORDS.put("key:dtv|key:up", tvUp);

        List<String> tvDown = new ArrayList<String>();
        tvDown.add("下一台");tvDown.add("下1台");tvDown.add("下一个");tvDown.add("下1个");
        tvDown.add("换台");tvDown.add("换频道");tvDown.add("调台");tvDown.add("跳台");
        TV_CONTROL_KEYWORDS.put("key:dtv|key:down", tvDown);

        List<String> left = new ArrayList<String>();
        left.add("左");left.add("左面");left.add("左边");left.add("向左");left.add("向左边");left.add("往左");left.add("往左边");
        TV_CONTROL_KEYWORDS.put("key:left", left);

        List<String> right = new ArrayList<String>();
        right.add("右");right.add("右面");right.add("右边");right.add("向右");right.add("向右边");right.add("往右");right.add("往右边");
        TV_CONTROL_KEYWORDS.put("key:right", right);

        List<String> up = new ArrayList<String>();
        up.add("上");up.add("上面");up.add("上边");up.add("向上");up.add("向上边");up.add("往上");up.add("往上边");
        TV_CONTROL_KEYWORDS.put("key:up", up);

        List<String> down = new ArrayList<String>();
        down.add("下");down.add("下面");down.add("下边");down.add("向下");down.add("向下边");down.add("往下");down.add("往下边");
        TV_CONTROL_KEYWORDS.put("key:down", down);
        
        //添加指令
        List<String> diyinup = new ArrayList<String>();
        diyinup.add("低音加");diyinup.add("低音大点");
        TV_CONTROL_KEYWORDS.put("key:dyup", diyinup);
        
        List<String> diyindown = new ArrayList<String>();
        diyindown.add("低音减");diyindown.add("低音小点");
        TV_CONTROL_KEYWORDS.put("key:dydown", diyindown);
        
        List<String> sbtt = new ArrayList<String>();
        sbtt.add("随便听听");
        TV_CONTROL_KEYWORDS.put("key:music", sbtt);
        
        List<String> yxmovie = new ArrayList<String>();
        yxmovie.add("电影音效");
        TV_CONTROL_KEYWORDS.put("key:yxmovie", yxmovie);
        
        List<String> yxtv = new ArrayList<String>();
        yxtv.add("电视音效");
        TV_CONTROL_KEYWORDS.put("key:yxtv", yxtv);
        
        List<String> yxmusic = new ArrayList<String>();
        yxmusic.add("音乐音效");
        TV_CONTROL_KEYWORDS.put("key:yxmusic", yxmusic);
        
        List<String> yxgame = new ArrayList<String>();
        yxgame.add("游戏音效");
        TV_CONTROL_KEYWORDS.put("key:yxgame", yxgame);
        
        List<String> yxyd = new ArrayList<String>();
        yxyd.add("运动音效");
        TV_CONTROL_KEYWORDS.put("key:yxyd", yxyd);
        
        List<String> yxxt = new ArrayList<String>();
        yxxt.add("小厅音效");
        TV_CONTROL_KEYWORDS.put("key:yxxt", yxxt);
        
        List<String> lightsup = new ArrayList<String>();
        lightsup.add("灯光亮点");lightsup.add("灯光亮");
        TV_CONTROL_KEYWORDS.put("key:lightsup", lightsup);
        
        List<String> lightsdown = new ArrayList<String>();
        lightsdown.add("灯光暗点");lightsdown.add("灯光暗");
        TV_CONTROL_KEYWORDS.put("key:lightsdown", lightsdown);
        
        List<String> lightsmoon = new ArrayList<String>();
        lightsmoon.add("月光");
        lightsmoon.add("月光灯效");
        TV_CONTROL_KEYWORDS.put("key:lightsmoon", lightsmoon);
        
        List<String> lightssun = new ArrayList<String>();
        lightssun.add("日光");
        lightssun.add("日光灯效");
        lightssun.add("太阳光");
        TV_CONTROL_KEYWORDS.put("key:lightssun", lightssun);
        
        List<String> lightscontrol = new ArrayList<String>();
        lightscontrol.add("灯光开");
        lightscontrol.add("灯光关");
        TV_CONTROL_KEYWORDS.put("key:lightscontrol", lightscontrol);
    }

    /**
     * 根据用户的语音返回相应的指令，处理音量是模糊匹配，其余都是等于匹配, 如果你想特殊处理某个语音指令，HERE
     */
    public static String isSearchContainsControl(String searchWords) {
        Set<String> commands = TV_CONTROL_KEYWORDS.keySet();

        for (String key : commands) {
            List<String> keywords = TV_CONTROL_KEYWORDS.get(key);

            for (String keyword : keywords) {
                if(keyword.equals("key:volumeup|key:volumeup") || keyword.equals("key:volumedown|key:volumedown")) {
                    if (searchWords.startsWith(keyword)) {
                        return key;
                    }
                } else {
                    if (searchWords.equals(keyword)) {
                        return key;
                    }
                }
            }
        }

        return null;
    }

    /**************************************************应用部分*********************************************************/

    public static boolean isSearchContainsAppKeywords(String searchWords) {
        if ((searchWords.indexOf("打开") >= 0) || (searchWords.indexOf("启动") >= 0) || (searchWords.indexOf("开启") >= 0)) {
            return true;
        }
        return false;
    }

    public static String appSearchWordsConvert(String needConvert) {
        needConvert = needConvert.replace("打开", "");
        needConvert = needConvert.replace("启动", "");
        needConvert = needConvert.replace("开启", "");
        return needConvert;
    }

    /**************************************************频道部分*********************************************************/

    /**
     * yu yin channel switch need replace chars, key is baidu word, value is convert value which used for compare
     * follow test must execute:
     * 1 - 中央1台 -> 中央一台高清（没有的话，中央一台）
     * 2 - 湖南（福建） -> 湖南（福建）高清（没有的话，湖南（福建）卫视）
     * 3 - 湖南卫视 -> 湖南高清（没有的话，湖南卫视）
     * 4 - 湖南高清 -> 湖南高清（没有的话，湖南卫视）
     * 5 - 湖南卫视标清 -> 湖南卫视
     * 6 - 四川1台 -> 四川一台高清（没有的话，四川一台）
     * 7 - 福建 ->福建高清（没有的话，福建卫视）
     * 8 - 福建高清 ->福建高清（没有的话，福建卫视）
     */
    private final static Map<String, String> TV_CHANNEL_REPLACE_KEYWORDS = new HashMap<String, String>();

    static {
        //baidu yuyin charaters, which is different from normal char, so replace it to normal one
        TV_CHANNEL_REPLACE_KEYWORDS.put("ｃｃｔｖ", "CCTV-");
        TV_CHANNEL_REPLACE_KEYWORDS.put("中央", "CCTV-");
        TV_CHANNEL_REPLACE_KEYWORDS.put("四川", "SCTV-");
        TV_CHANNEL_REPLACE_KEYWORDS.put("成都", "CDTV-");
        TV_CHANNEL_REPLACE_KEYWORDS.put("一", "1");
        TV_CHANNEL_REPLACE_KEYWORDS.put("二", "2");
        TV_CHANNEL_REPLACE_KEYWORDS.put("三", "3");
        TV_CHANNEL_REPLACE_KEYWORDS.put("四", "4");
        TV_CHANNEL_REPLACE_KEYWORDS.put("五", "5");
        TV_CHANNEL_REPLACE_KEYWORDS.put("六", "6");
        TV_CHANNEL_REPLACE_KEYWORDS.put("七", "7");
        TV_CHANNEL_REPLACE_KEYWORDS.put("八", "8");
        TV_CHANNEL_REPLACE_KEYWORDS.put("九", "9");
        TV_CHANNEL_REPLACE_KEYWORDS.put("十", "10");
        TV_CHANNEL_REPLACE_KEYWORDS.put("十一", "11");
        TV_CHANNEL_REPLACE_KEYWORDS.put("十二", "12");
        TV_CHANNEL_REPLACE_KEYWORDS.put("十三", "13");
        TV_CHANNEL_REPLACE_KEYWORDS.put("台", "");
        TV_CHANNEL_REPLACE_KEYWORDS.put("套", "");
        TV_CHANNEL_REPLACE_KEYWORDS.put("卫视", "");
        TV_CHANNEL_REPLACE_KEYWORDS.put("电视", "");
        TV_CHANNEL_REPLACE_KEYWORDS.put("电视台", "");
        TV_CHANNEL_REPLACE_KEYWORDS.put("高清", "");
    }

    /**
     * 该方法主要用于语音词语转化
     */
    public static String yuYingChannelSearchWordsConvert(String needConvert) {
        for (String key : TV_CHANNEL_REPLACE_KEYWORDS.keySet()) {
            String value = TV_CHANNEL_REPLACE_KEYWORDS.get(key);
            needConvert = needConvert.replace(key, value);
        }

        /**
         * 保证标清关键字的转化在卫视的后面，所以在这里转化一次，而没有配置在上面的MAP中
         */
        needConvert = needConvert.replace("标清", "卫视");
        if (needConvert.contains("CCTV-")) {
            needConvert = needConvert + "高清";
        }
        return needConvert;
    }

    private final static Map<String, String> TV_CHANNEL_SEARCH_KEYWORDS = new HashMap<String, String>();

    static {
        //baidu yuyin charaters, which is different from normal char, so replace it to normal one
        TV_CHANNEL_SEARCH_KEYWORDS.put("中央", "CCTV-");
        TV_CHANNEL_SEARCH_KEYWORDS.put("四川", "SCTV-");
        TV_CHANNEL_SEARCH_KEYWORDS.put("成都", "CDTV-");
        TV_CHANNEL_SEARCH_KEYWORDS.put("一", "1");
        TV_CHANNEL_SEARCH_KEYWORDS.put("二", "2");
        TV_CHANNEL_SEARCH_KEYWORDS.put("三", "3");
        TV_CHANNEL_SEARCH_KEYWORDS.put("四", "4");
        TV_CHANNEL_SEARCH_KEYWORDS.put("五", "5");
        TV_CHANNEL_SEARCH_KEYWORDS.put("六", "6");
        TV_CHANNEL_SEARCH_KEYWORDS.put("七", "7");
        TV_CHANNEL_SEARCH_KEYWORDS.put("八", "8");
        TV_CHANNEL_SEARCH_KEYWORDS.put("九", "9");
        TV_CHANNEL_SEARCH_KEYWORDS.put("十", "10");
        TV_CHANNEL_SEARCH_KEYWORDS.put("十一", "11");
        TV_CHANNEL_SEARCH_KEYWORDS.put("十二", "12");
        TV_CHANNEL_SEARCH_KEYWORDS.put("十三", "13");
    }

    /**
     * 该方法主要用于手机上搜索频道的词语转化
     */
    public static String normalChannelSearchWordsConvert(String needConvert) {
        for (String key : TV_CHANNEL_SEARCH_KEYWORDS.keySet()) {
            String value = TV_CHANNEL_SEARCH_KEYWORDS.get(key);
            needConvert = needConvert.replace(key, value);
        }
        return needConvert;
    }
}
