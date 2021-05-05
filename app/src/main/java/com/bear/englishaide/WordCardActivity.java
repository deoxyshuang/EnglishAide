package com.bear.englishaide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

public class WordCardActivity extends AppCompatActivity{

    private static final int WORD_CARD = 2;
    private Gson gson = new Gson();
    private Word word;
    private int type,hasMark;
    private String wordJson;
    private DBOperation dbo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_card);

        Intent intent = getIntent();
        type = intent.getIntExtra("type", 1);
        hasMark = intent.getIntExtra("hasMark",0);
        wordJson = intent.getStringExtra("wordJson");
        if(wordJson!=null) word = gson.fromJson(wordJson,Word.class); //todo

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(25);//todo
        actionBar.setTitle(word.word);

        dbo = new DBOperation(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        if(hasMark==1) menu.findItem(R.id.star).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_round_star_pressed_28));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.star:
                ContentValues values = new ContentValues();
                if (hasMark==0){  //沒有星號標記
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_round_star_pressed_28));
                    hasMark = 1;
                    values.put("hasMark", hasMark);
                }else{ //有星號標記
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_round_star_28));
                    hasMark = 0;
                    values.put("hasMark", hasMark);
                }
                dbo.update(values, word.id);
                return true;
            case R.id.edit:
                Intent intent = new Intent(this, EditWordCardActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("wordJson", wordJson);
                startActivityForResult(intent, WORD_CARD);
                return true;
            case R.id.del:
                Utils.showDelDialog(this, iDialogListener, word.word);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WORD_CARD){
            if (resultCode == RESULT_OK){
                //todo
            }
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbo.destroy();
    }

    private IDialogListener iDialogListener = new IDialogListener() {
        @Override
        public void onClickPositiveButton() {
            dbo.delete(word.id);
            finish();
        }

        @Override
        public void onClickNegativeButton() {

        }
    };
}