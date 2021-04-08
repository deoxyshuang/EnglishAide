package com.bear.englishaide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private WordCardsFragment wordCardsFragment;
    private SearchFragment searchFragment;
    private QuizFragment quizFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BottomNavigationView nav;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private long firstTime = 0; //使用者首次按下返回鍵的時間

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //建立DB
        /*dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("select 1 from " + DBHelper.TABLE_NAME,null);
        if (cursor != null) {
            Log.d("sj","有幾筆資料:" + cursor.getCount());
            if(cursor.getCount()==0){

                //Toast.makeText(this, "尚無資料!", Toast.LENGTH_SHORT).show();
            }
        }*/

        //fragment相關
        wordCardsFragment = new WordCardsFragment();
        searchFragment = new SearchFragment();
        quizFragment = new QuizFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.containerLayout, wordCardsFragment);
        fragmentTransaction.commit();

        //懸浮按鈕
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, EditWordCardActivity.class);
            startActivity(intent);
        });
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, EditWordCardActivity.class);
//                startActivity(intent);
//            }
//        });

        //下方導覽列
        nav = findViewById(R.id.nav);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.wordCards:
                        fab.show();
                        //todo add() + show() + hide() 取代 replace()
                        //todo fragmentManager.beginTransaction(); fragmentTransaction.commit(); 移位置
                        fragmentTransaction = fragmentManager.beginTransaction();
                        /*if(wordCardsFragment.isAdded()){

                        }else{

                        }*/
                        //.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        fragmentTransaction.replace(R.id.containerLayout, wordCardsFragment);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.search:
                        fab.hide();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.containerLayout, searchFragment);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.quiz:
                        fab.hide();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.containerLayout, quizFragment);
                        fragmentTransaction.commit();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(MainActivity.this, R.string.exitMsg, Toast.LENGTH_SHORT).show();
                //Snackbar.make(nav,  R.string.exitMsg, Snackbar.LENGTH_SHORT).show();
                firstTime = secondTime;
            } else {
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}