package com.bear.englishaide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordCardActivity extends AppCompatActivity implements DBOperation.IQueryListener {

    private static final int WORD_CARD = 2;
    private Gson gson = new Gson();
    private ActionBar actionBar;
    private Word word;
    private int position,type,hasMark;
    private String wordJson;
    private DBOperation dbo;
    private TextView tvPos, tvTotal;
    private ArrayList<HashMap> wordList = new ArrayList<>();
    private enum Status{INIT,REFRESH}
    private ViewPager2 pager;
    private FragmentStateAdapter pagerAdapter;
    private Menu actionbarMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_card);

        pager = findViewById(R.id.pager);
        tvPos = findViewById(R.id.tvPos);
        tvTotal = findViewById(R.id.tvTotal);

        dbo = new DBOperation(this);
        dbo.setQueryListener(this);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(25);//todo

        parseIntent(Status.INIT,getIntent());

        pagerAdapter = new MyPagerAdapter(this, wordList);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(position-1);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                tvPos.setText(String.valueOf(position));
                int hasMark = (int) wordList.get(position).get("hasMark");
                Word word = (Word) wordList.get(position).get("word");
                actionBar.setTitle(word.word);
                //todo
                if(hasMark==1) actionbarMenu.findItem(R.id.star).setIcon(ContextCompat.getDrawable(WordCardActivity.this,R.drawable.ic_round_star_pressed_28));
                else actionbarMenu.findItem(R.id.star).setIcon(ContextCompat.getDrawable(WordCardActivity.this,R.drawable.ic_round_star_28));
            }
        });
    }

    private void parseIntent(Status status, Intent intent){
        type = intent.getIntExtra("type", 1);
        wordJson = intent.getStringExtra("wordJson");
        if(wordJson!=null) word = gson.fromJson(wordJson,Word.class); //todo
        actionBar.setTitle(word.word);

        switch (status){
            case INIT:
                dbo.dataQuery(VocabType.getType(intent.getIntExtra("vocabType",0)),
                        SortType.getType(intent.getIntExtra("sortType",0)));
                position = intent.getIntExtra("position",1);
                hasMark = intent.getIntExtra("hasMark",0);
                tvPos.setText(String.valueOf(position));
                tvTotal.setText(String.valueOf(wordList.size()));
                break;
            case REFRESH:
                break;
        }
    }

    @Override
    public void onQueryComplete(ArrayList wordList) {
        this.wordList = wordList;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        actionbarMenu = menu;
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
                //todo
//                createContent();
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

    public class MyPagerAdapter extends FragmentStateAdapter {

        private List<HashMap> dataList;

        public MyPagerAdapter(@NonNull AppCompatActivity appCompatActivity, ArrayList<HashMap> wordList) {
            super(appCompatActivity);
            this.dataList = wordList;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            WordCardFragment wordCardFragment = new WordCardFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", (Integer) wordList.get(position).get("type"));
            bundle.putString("wordJson", gson.toJson(wordList.get(position).get("word")));
            wordCardFragment.setArguments(bundle);
            return wordCardFragment;
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }
}