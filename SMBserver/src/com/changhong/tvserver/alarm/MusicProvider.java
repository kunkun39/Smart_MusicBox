package com.changhong.tvserver.alarm;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.changhong.tvserver.MyApplication;

public class MusicProvider extends ContentProvider {

	private static final int MUSICS = 1;
	private static final int MUSICS_ID = 2;

	private static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		URI_MATCHER.addURI("com.changhong.provider.musicprovider", "musics",
				MUSICS);
		URI_MATCHER.addURI("com.changhong.provider.musicprovider", "musics/#",
				MUSICS_ID);
	}

	private ContentResolver contentResolver;

	public MusicProvider() {
		if (null == contentResolver) {
			contentResolver = MyApplication.getContext().getContentResolver();
		}
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		contentResolver.delete(arg0, arg1, arg2);
		contentResolver.notifyChange(arg0, null);
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		contentResolver.insert(arg0, arg1);
		contentResolver.notifyChange(arg0, null);
		return arg0;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		Cursor cur;
		cur = contentResolver.query(arg0, arg1, arg2,
				arg3, arg4);
		return cur;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		int count = 0;

		contentResolver.update(arg0, arg1, arg2, arg3);
		contentResolver.notifyChange(arg0, null);
		return count;
	}

}
