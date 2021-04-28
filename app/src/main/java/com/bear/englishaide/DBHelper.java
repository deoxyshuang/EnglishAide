package com.bear.englishaide;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "EnglishAide.db";
    static final String TABLE_NAME = "wordCards";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type INTEGER, " +
                "data TEXT, " +
                "createTime INTEGER" +
                ");";
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {

            db.beginTransaction();
            boolean success = false;

            // 依照舊的版本做相應的更新
            switch (oldVersion) {
                case 1:
                    success = true;
                    break;
            }
            if (success) {
                db.setTransactionSuccessful();
            }
            db.endTransaction();
        } else {
            onCreate(db);
        }
    }
}
