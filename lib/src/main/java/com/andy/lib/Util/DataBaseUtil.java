package com.andy.lib.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andy on 2019/4/11.
 */

public class DataBaseUtil extends SQLiteOpenHelper {
    protected SQLiteDatabase writeDb;
    protected SQLiteDatabase readDb;
    protected String[] sqlsOnCreate;
    protected String TAG;

    public DataBaseUtil(Context context, String dbName, int dbVersion, String sqlsOnCreate[]) {
        super(context, dbName, null, dbVersion);
        this.sqlsOnCreate = sqlsOnCreate;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TAG = this.getClass().getSimpleName();
        //执行预定sql
        for (String sql : sqlsOnCreate) {
            db.execSQL(sql);
        }
    }

    /**
     * 初始化数据库对象
     */
    public void initDB() {
        writeDb = this.getWritableDatabase();
        readDb = this.getReadableDatabase();
    }

    public void closeDB() {
        writeDb.close();
        readDb.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
