package com.changhong.tvserver.alarm;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ClockProvider extends ContentProvider {

	private AlarmProvider.DatabaseHelper helper;
	private static final int ALARMS = 1;
	private static final int ALARMS_ID = 2;
	private static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		URI_MATCHER.addURI("com.changhong.provider.clockprovider", "alarms",
				ALARMS);
		URI_MATCHER.addURI("com.changhong.provider.clockprovider", "alarms/#",
				ALARMS_ID);
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		helper = new AlarmProvider.DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		int match = URI_MATCHER.match(uri);
		switch (match) {
		case ALARMS:
			cursor = db.query("clocks", projection, selection, selectionArgs,
					null, null, sortOrder);
			break;

		case ALARMS_ID:
			String clock_id = uri.getPathSegments().get(1);
			cursor = db.query("clocks", projection, "_id=?",
					new String[] { clock_id }, null, null, sortOrder);
			break;
		default:
			break;
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (URI_MATCHER.match(uri)) {
		case ALARMS:
			return "vnd.android.cursor.dir/vnd.com.changhong.provider.clockprovider.clocks";

		case ALARMS_ID:
			return "vnd.android.cursor.item/vnd.com.changhong.provider.clockprovider.clocks";

		default:
			throw new IllegalArgumentException("Unknown URL");
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		Uri uriReturn;
		SQLiteDatabase db = helper.getWritableDatabase();
		long id = db.insert("clocks", null, values);
		uriReturn = Uri
				.parse("content://com.changhong.provider.clockprovider/clocks/"
						+ id);

		return uriReturn;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = helper.getWritableDatabase();
		int deleteRow = 0;
		int match = URI_MATCHER.match(uri);
		switch (match) {
		case ALARMS:
			deleteRow = db.delete("clocks", selection, selectionArgs);
			break;

		case ALARMS_ID:
			String clock_id = uri.getPathSegments().get(1);

			deleteRow = db.delete("clocks", "_id=?", new String[] { clock_id });
			break;

		default:
			break;

		}
		return deleteRow;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		SQLiteDatabase db = helper.getWritableDatabase();
		int changeRow = 0;
		int match = URI_MATCHER.match(uri);
		switch (match) {
		case ALARMS:
			changeRow = db.update("clocks", values, selection, selectionArgs);
			break;

		case ALARMS_ID:
			String music_id = uri.getPathSegments().get(1);
			changeRow = db.update("clocks", values, "_id=?",
					new String[] { music_id });
			break;

		default:
			break;

		}
		return changeRow;

	}

}
