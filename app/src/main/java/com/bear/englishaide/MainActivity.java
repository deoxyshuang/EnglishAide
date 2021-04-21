package com.bear.englishaide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private static final int WORD_CARDS = 1;
    private WordCardsFragment wordCardsFragment;
    private SearchFragment searchFragment;
    private QuizFragment quizFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BottomNavigationView nav;
    private long firstTime = 0; //使用者首次按下返回鍵的時間

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("sj", "onCreate: ");

        //fragment相關
        wordCardsFragment = new WordCardsFragment();
        searchFragment = new SearchFragment();
        quizFragment = new QuizFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.containerLayout, wordCardsFragment);
        fragmentTransaction.commit();

        //下方導覽列
        nav = findViewById(R.id.nav);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                fragmentTransaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.wordCards:
                        //fragmentTransaction.replace(R.id.containerLayout, wordCardsFragment);
                        if(searchFragment.isAdded()) fragmentTransaction.hide(searchFragment);
                        if(quizFragment.isAdded()) fragmentTransaction.hide(quizFragment);
                        if(wordCardsFragment.isAdded()){
                            fragmentTransaction.show(wordCardsFragment);
                        }else{
                            fragmentTransaction.add(R.id.containerLayout, wordCardsFragment);
                        }
                        break;
                    case R.id.search:
                        //fragmentTransaction.replace(R.id.containerLayout, searchFragment);
                        if(wordCardsFragment.isAdded()) fragmentTransaction.hide(wordCardsFragment);
                        if(quizFragment.isAdded()) fragmentTransaction.hide(quizFragment);
                        if(searchFragment.isAdded()){
                            fragmentTransaction.show(searchFragment);
                        }else{
                            fragmentTransaction.add(R.id.containerLayout, searchFragment);
                        }
                        break;
                    case R.id.quiz:
                        //fragmentTransaction.replace(R.id.containerLayout, quizFragment);
                        if(wordCardsFragment.isAdded()) fragmentTransaction.hide(wordCardsFragment);
                        if(searchFragment.isAdded()) fragmentTransaction.hide(searchFragment);
                        if(quizFragment.isAdded()){
                            fragmentTransaction.show(quizFragment);
                        }else{
                            fragmentTransaction.add(R.id.containerLayout, quizFragment);
                        }
                        break;
                }
                fragmentTransaction.commit();
                //.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("sj", "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("sj", "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("sj", "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("sj", "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("sj", "onDestroy: ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WORD_CARDS){
            if (resultCode == RESULT_OK){
                Log.d("sj", "onActivityResult: ");
                wordCardsFragment.dataQuery();
            }else{
                //finish();
            }
        }
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