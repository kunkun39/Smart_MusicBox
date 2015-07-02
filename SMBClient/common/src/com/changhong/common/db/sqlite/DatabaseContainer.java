package com.changhong.common.db.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.changhong.common.system.MyApplication;

import java.io.File;

/**
 * Created by Jack Wang
 *
 * 数据库支持的数据类型，TEXT，VARCHAR, INTEGER, REAL, BLOG,
 */
public class DatabaseContainer extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "tvhelper.db";

    private final static String EPG_DATABASE_NAME = "epg_database.db";

    private static int CURRENT_VERSION = 2;

    private SQLiteDatabase epgDatabase;

    public DatabaseContainer(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_VERSION);
    }

    public SQLiteDatabase openEPGDatabase() {
        try {
            File epgDBFile = new File(MyApplication.epgDBCachePath.getAbsolutePath(), EPG_DATABASE_NAME);
            if (epgDBFile.exists() && epgDatabase == null) {
                epgDatabase = SQLiteDatabase.openDatabase(epgDBFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return epgDatabase;
    }

    /**
     * 当更新了EPG的时候，都需要重新打开一次DB
     */
    public void reopenEPGDatabase() {
        try {
            File epgDBFile = new File(MyApplication.epgDBCachePath.getAbsolutePath(), EPG_DATABASE_NAME);
            if (epgDBFile.exists()) {
                epgDatabase = SQLiteDatabase.openDatabase(epgDBFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库第一次创建的时候别调用的
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE music_lrc" +
                "(music_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "singer VARCHAR(30), " +
                "name VARCHAR(100), " +
                "path VARCHAR(200))");
        db.execSQL("CREATE TABLE channel_shoucang" +
                "(shoucang_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "service_id VARCHAR(30))");
        db.execSQL("CREATE TABLE order_program" +
                "(channel_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_date VARCHAR(100), " +
                "channel_index VARCHAR(100), " +
                "week_index VARCHAR(100), " +
                "program_start_time VARCHAR(100),"+
                "program_end_time VARCHAR(100),"+
                "status VARCHAR(100),"+
                "program_name VARCHAR(200),"+
                "channel_name VARCHAR(200))");

    }

    /**
     * 数据库版本更新的时候被调用
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE channel_shoucang" +
                "(shoucang_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "service_id VARCHAR(30))");
        db.execSQL("CREATE TABLE order_program" +
                "(channel_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_date VARCHAR(100), " +
                "channel_index VARCHAR(100), " +
                "week_index VARCHAR(100), " +
                "program_start_time VARCHAR(100),"+
                "program_end_time VARCHAR(100),"+
                "status VARCHAR(100),"+
                "program_name VARCHAR(200),"+
                "channel_name VARCHAR(200))");
    }
}
