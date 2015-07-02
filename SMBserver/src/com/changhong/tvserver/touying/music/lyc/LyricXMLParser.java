/**
 * Copyright (c) www.longdw.com
 */
package com.changhong.tvserver.touying.music.lyc;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * 歌词解析
 *
 * @author longdw(longdawei1988@gmail.com)
 */
public class LyricXMLParser {
    private static final String TAG = "LyricXMLParser";

    private final String ELEMENT_COUNT = "count";

    private final String ELEMENT_LRCID = "lrcid";

    public int parseLyricId(InputStream is) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance(); // 取得SAXParserFactory实例
        SAXParser parser = factory.newSAXParser(); // 从factory获取SAXParser实例
        MyHandler handler = new MyHandler(); // 实例化自定义Handler
        parser.parse(is, handler); // 根据自定义Handler规则解析输入流
        is.close();
        return handler.getFirstLyricId();
    }

    class MyHandler extends DefaultHandler {
        private int mSongCount = 0;
        private ArrayList<Integer> mLyricIds = new ArrayList<Integer>();
        private StringBuilder mStringBuilder = new StringBuilder();

        public int getSongCount() {
            return mSongCount;
        }

        public int getFirstLyricId() {
            if (mSongCount == 0) {
                return -1;
            } else {
                return mLyricIds.get(0);
            }
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            mLyricIds.clear();
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            mStringBuilder.setLength(0); // 将字符长度设置为0 以便重新开始读取元素内的字符节点
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (localName.equals(ELEMENT_COUNT)) {
                mSongCount = Integer.valueOf(mStringBuilder.toString());
            }
            if (localName.equals(ELEMENT_LRCID)) {
                mLyricIds.add(Integer.valueOf(mStringBuilder.toString()));
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            mStringBuilder.append(ch, start, length);
        }
    }
}
