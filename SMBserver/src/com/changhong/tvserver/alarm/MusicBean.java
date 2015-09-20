package com.changhong.tvserver.alarm;

import java.io.Serializable;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 定义的音乐数据的子类，用来保存每首歌相应的信息。
 * 
 * @author sunyuanming 个人觉得插拔U盘不影响数据，只需要在contentprovider中找到相应的歌曲就行了，不管路径
 */
public class MusicBean implements Serializable {

	public int getmId() {
		return mId;
	}

	public void setmId(int mId) {
		this.mId = mId;
	}

	private static final long serialVersionUID = 1L;
	/* 这是标识闹钟的id,代表这首歌是属于id为mid的闹钟的音乐 */
	private int mId;
	/* 标识每首音乐的唯一id */
	private long id;
	/* 歌曲的名字 */
	private String title;
	/* 歌曲的专辑 */
	private String album;
	/* 歌曲的时间 */
	private int duration;
	/* 歌曲的大小 */
	private long size;
	/* 歌手 */
	private String artist;
	/* 歌曲的路径，相对路径，我觉得插拔U盘路径应该不会变化的 */
	private String url;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public MusicBean() {

	}

	public static class Columns implements BaseColumns {
		/**
		 * The content:// 为这个表定义一个共享的Url
		 */
		public static final Uri MUSIC_URL = Uri
				.parse("content://com.changhong.provider.musicprovider/musics");
		private static final long serialVersionUID = 1L;

		public static final String MID = "mId";

		public static final String ID = "id";

		public static final String TITLE = "title";

		public static final String ALBUM = "album";

		public static final String DURATION = "duration";

		public static final String SIZE = "size";

		public static final String ARTIST = "artist";

		public static final String URL = "url";

		 public static final String[] MUSIC_QUERY_COLUMNS = { "mId", "id",
		 "title", "album", "duration", "size", "artist", "url" };

		public static final String DEFAULT_SORT_ORDER = "_id asc";
	}
}
