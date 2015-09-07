package com.changhong.tvserver.alarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "musics.db";
	private static final int DATABASE_VERSION = 1;
	private static final String CREATE_TABLE = "create table musics(_id integer primary key autoincrement,mId INTEGER ,id INTEGER ,title TEXT,album TEXT,duration INTEGER,size INTEGER,artist TEXT,url TEXT);";
	private static final String DELETE_TABLE = "drop table if exists musics";

	public MusicHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建数据表
		db.execSQL(CREATE_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 如果数据表存在则删除，然后再创建
		db.execSQL(DELETE_TABLE);
		onCreate(db);
	}
}
