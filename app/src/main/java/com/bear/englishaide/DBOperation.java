package com.bear.englishaide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;


/**
 * 資料庫相關操作
 */
class DBOperation {
    //todo 寫進service
    private Context context;
    private Gson gson = new Gson();
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Cursor cursor;
    private ArrayList<HashMap> wordList = new ArrayList<>();
    private IDBOListener idboListener;

    DBOperation (Context context){
        this.context = context;
        dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
    }

    /**
     * 字卡查詢
     * @param vocabType 字卡類型
     * @param sortType 排序
     */
    void dataQuery(VocabType vocabType, SortType sortType){
        wordList.clear();

        dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
        String[] param=null;
        String sqlWhere="", sqlOrder="";
        if(vocabType!=VocabType.ALL) {
            sqlWhere = " where type=? ";
            param = new String[]{String.valueOf(vocabType.getKey())};
        }
        switch (sortType){
            case NEW_TO_OLD:
                sqlOrder = " order by createTime desc ";
                break;
            case OLD_TO_NEW:
                sqlOrder = " order by createTime";
                break;
        }
        cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME +sqlWhere + sqlOrder,param);
        if (cursor != null) {
            Log.d("sj","有幾筆資料:" + cursor.getCount());
            if(cursor.getCount()>0){
                while (cursor.moveToNext()) {
                    Word word = gson.fromJson(cursor.getString(2), Word.class);
                    word.id = cursor.getInt(0);

                    HashMap<String,Object> wordMap = new HashMap();
                    wordMap.put("type",cursor.getInt(1)); //1=單字 2=片語
                    wordMap.put("word",word);
                    wordMap.put("hasMark",cursor.getInt(3)); //有無星號標記 0=無 1=有
                    wordList.add(wordMap);
                }
                switch (sortType){
                    case A_TO_Z:
                        wordList = (ArrayList<HashMap>) wordList.stream().sorted((map1, map2)->{
                            Word w1 = (Word) map1.get("word");
                            Word w2 = (Word) map2.get("word");
                            return w1.word.compareTo(w2.word);
                        }).collect(Collectors.toList());
                        break;
                    case Z_TO_A:
                        wordList = (ArrayList<HashMap>) wordList.stream().sorted((map1, map2)->{
                            Word w1 = (Word) map1.get("word");
                            Word w2 = (Word) map2.get("word");
                            return w2.word.compareTo(w1.word);
                        }).collect(Collectors.toList());
                        break;
                }

            }
        }

        if(idboListener!=null) idboListener.onQueryComplete(wordList);
    }

    /**
     * 修改字卡
     * @param values 異動欄位與值
     * @param id 字卡id
     */
    void update(ContentValues values, int id){
        db.update(DBHelper.TABLE_NAME, values, " id = "+id,null);
    }

    /**
     * 刪除單筆字卡
     * @param id 字卡id
     */
    void delete(int id) {
        db.delete(DBHelper.TABLE_NAME,"id = " + id,null);
    }


    /**
     * 關閉資料庫
     */
    void destroy() {
        if(!cursor.isClosed()) cursor.close();
        if(!db.isOpen()) db.close();
        if(dbHelper!=null) dbHelper.close();
    }

    /**
     * 綁定監聽事件
     */
    public void setIDBOListener(IDBOListener idboListener) {
        this.idboListener = idboListener;
    }
    /**
     * 監聽事件
     */
    interface IDBOListener {
        public void onQueryComplete(ArrayList wordList);
    }
}
