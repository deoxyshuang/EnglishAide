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
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordCardActivity extends AppCompatActivity implements DBOperation.IQueryListener, DBOperation.IDataListener {

    private static final int WORD_CARD = 2;
    private Gson gson = new Gson();
    private ActionBar actionBar;
    private Word word;
    private int position,type,hasMark;
    private DBOperation dbo;
    private TextView tvPos, tvTotal;
    private ImageView ivPrev, ivNext;
    private ArrayList<HashMap> wordList = new ArrayList<>();
    private enum Status{INIT,REFRESH}
    private ViewPager2 pager;
    private MyPagerAdapter pagerAdapter;
    private MenuItem starItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_card);

        pager = findViewById(R.id.pager);
        tvPos = findViewById(R.id.tvPos);
        tvTotal = findViewById(R.id.tvTotal);
        ivPrev = findViewById(R.id.ivPrev);
        ivNext = findViewById(R.id.ivNext);

        dbo = new DBOperation(this);
        dbo.setQueryListener(this);
        dbo.setDataListener(this);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(25);//todo

        parseIntent(Status.INIT,getIntent());

        pagerAdapter = new MyPagerAdapter(this, wordList);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(position);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tvPos.setText(String.valueOf(position+1));
                type = (int) wordList.get(position).get("type");
                hasMark = (int) wordList.get(position).get("hasMark");
                word = (Word) wordList.get(position).get("word");
                actionBar.setTitle(word.word);
                WordCardActivity.this.position = position;
                if(starItem!=null){
                    if(hasMark==1) starItem.setIcon(ContextCompat.getDrawable(WordCardActivity.this,R.drawable.ic_round_star_pressed_28));
                    else starItem.setIcon(ContextCompat.getDrawable(WordCardActivity.this,R.drawable.ic_round_star_28));
                }
            }
        });

        ivPrev.setOnClickListener(view -> {
            pager.setCurrentItem(--position, true);
        });
        ivNext.setOnClickListener(view -> {
            pager.setCurrentItem(++position, true);
        });
    }

    private void parseIntent(Status status, Intent intent){
        switch (status){
            case INIT:
                position = intent.getIntExtra("position",1);
                dbo.dataQuery(VocabType.getType(intent.getIntExtra("vocabType",0)),
                        SortType.getType(intent.getIntExtra("sortType",0)));
                hasMark = (int) wordList.get(position).get("hasMark");
                tvPos.setText(String.valueOf(position+1));
                tvTotal.setText(String.valueOf(wordList.size()));
                type = (int) wordList.get(position).get("type");
                word = (Word) wordList.get(position).get("word");
                break;
            case REFRESH:
                type = intent.getIntExtra("type",1);
                word = gson.fromJson(intent.getStringExtra("wordJson"),Word.class);
                wordList.get(position).put("type", type);
                wordList.get(position).put("word", word);
                break;
        }
        actionBar.setTitle(word.word);
    }

    @Override
    public void onQueryComplete(ArrayList wordList) {
        this.wordList = wordList;
    }
    @Override
    public void onSuccess(DBOperation.Operation operation) {
        switch (operation){
            case UPDATE:
                wordList.get(position).put("hasMark", hasMark);
                break;
        }
    }
    @Override
    public void onError(DBOperation.Operation operation) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        starItem = menu.findItem(R.id.star);
        if(hasMark==1) starItem.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_round_star_pressed_28));
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
                intent.putExtra("wordJson", gson.toJson(word));
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
                pagerAdapter.fragmentList.get(position).createContent(type,word);
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
        public SparseArray<WordCardFragment> fragmentList = new SparseArray<>();

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
            fragmentList.put(position,wordCardFragment);
            return wordCardFragment;
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }
}