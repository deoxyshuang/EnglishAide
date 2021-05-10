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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class WordCardActivity extends AppCompatActivity{

    private static final int WORD_CARD = 2;
    private Gson gson = new Gson();
    private ActionBar actionBar;
    private Word word;
    private int type,hasMark;
    private String wordJson;
    private DBOperation dbo;
    private LinearLayout mainLayout;
    private String[] partList;
    private enum Status{INIT,REFRESH}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_card);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(25);//todo

        parseIntent(Status.INIT,getIntent());

        mainLayout = findViewById(R.id.mainLayout);
        partList = getResources().getStringArray(R.array.partList);
        dbo = new DBOperation(this);
        createContent();
    }

    private void parseIntent(Status status, Intent intent){
        type = intent.getIntExtra("type", 1);
        switch (status){
            case INIT:
                hasMark = intent.getIntExtra("hasMark",0);
                break;
            case REFRESH:
                break;
        }
        wordJson = intent.getStringExtra("wordJson");
        if(wordJson!=null) word = gson.fromJson(wordJson,Word.class); //todo
        actionBar.setTitle(word.word);
    }

    private void createContent(){
        mainLayout.removeAllViews();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        word.meanList.forEach((mean -> {
            LinearLayout topLayout = new LinearLayout(this);
            topLayout.setOrientation(LinearLayout.HORIZONTAL);
            topLayout.setLayoutParams(layoutParams);

            LinearLayout bottomLayout = new LinearLayout(this);
            bottomLayout.setOrientation(LinearLayout.VERTICAL);
            bottomLayout.setLayoutParams(layoutParams);

            //詞性
            TextView tvPart = new TextView(this);
            tvPart.setLayoutParams(wrapParams);
            tvPart.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_textview));
            tvPart.setTextColor(ContextCompat.getColor(this,R.color.white));
            if(type==1) tvPart.setText(partList[mean.part-1]);
            else tvPart.setText(getResources().getString(R.string.phr));
            //todo
            tvPart.setTextSize(15);
            tvPart.setPadding((int)Utils.convertDpToPx(this,6), (int)Utils.convertDpToPx(this,6), (int)Utils.convertDpToPx(this,6), (int)Utils.convertDpToPx(this,6));
            //解釋
            TextView tvMean = new TextView(this);
            tvMean.setLayoutParams(wrapParams);
            tvMean.setTextColor(ContextCompat.getColor(this,R.color.black));
            tvMean.setText(mean.mean);
            //todo
            tvMean.setTextSize(17);
            tvMean.setPadding((int)Utils.convertDpToPx(this,6), (int)Utils.convertDpToPx(this,6), (int)Utils.convertDpToPx(this,6), (int)Utils.convertDpToPx(this,6));

            topLayout.addView(tvPart);
            topLayout.addView(tvMean);
            if(!"".equals(mean.mean)) mainLayout.addView(topLayout);

            mean.sentenceList.forEach(sentence -> {
                TextView tvSen = new TextView(this);
                tvSen.setLayoutParams(wrapParams);
                tvSen.setTextColor(ContextCompat.getColor(this,R.color.black));
                //todo
                tvSen.setText(sentence.eng);
                tvSen.setTextSize(17);
                tvSen.setPadding((int)Utils.convertDpToPx(this,6), (int)Utils.convertDpToPx(this,6), (int)Utils.convertDpToPx(this,6), (int)Utils.convertDpToPx(this,6));

                bottomLayout.addView(tvSen);
            });

            View line  = new View(this);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)Utils.convertDpToPx(this,1));
            lineParams.setMargins((int) Utils.convertDpToPx(this,5), (int) Utils.convertDpToPx(this,15), (int) Utils.convertDpToPx(this,5), (int) Utils.convertDpToPx(this,15));
            line.setLayoutParams(lineParams);
            line.setBackgroundColor(ContextCompat.getColor(this,R.color.lightGray));

            if(mean.sentenceList.size()>1) bottomLayout.addView(line);
            mainLayout.addView(bottomLayout);
        }));
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
                parseIntent(Status.REFRESH, data);
                createContent();
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