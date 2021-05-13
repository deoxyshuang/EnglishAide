package com.bear.englishaide;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class WordCardListFragment extends Fragment implements DBOperation.IQueryListener {

    private static final String TAG = WordCardListFragment.class.getSimpleName();
    private static final int WORD_CARD_LIST = 1;
    private Context context;
    private Resources res;
    private SharedPreferences pref;
    private FloatingActionButton fab;
    private LinearLayout noDataLayout;
    private Spinner spnType,spnSort;
    private View mainView;
    private RecyclerView recyclerView; //todo 動畫效果
    private Gson gson = new Gson();
    private MyAdapter myAdapter;
    private String[] partList2;
    private DBOperation dbo;
    private ArrayList<HashMap> wordList = new ArrayList<>();
    private Handler handler;
    private AppCompatActivity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("sj","frag onAttach");
        this.context = context;
        res = getResources();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("sj","frag onCreate");
        pref = context.getSharedPreferences(res.getString(R.string.app_name_en), MODE_PRIVATE);
        dbo = new DBOperation(context);
        dbo.setQueryListener(this);
        partList2 = res.getStringArray(R.array.partList2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("sj","frag onCreateView");
        mainView = inflater.inflate(R.layout.fragment_word_card_list, container, false);
        noDataLayout= mainView.findViewById(R.id.noDataLayout);
        recyclerView = mainView.findViewById(R.id.recyclerView);
        //懸浮按鈕
        fab = mainView.findViewById(R.id.fab);
        fab.setOnClickListener(v->{
            Intent intent = new Intent(context, EditWordCardActivity.class);
            startActivityForResult(intent, WORD_CARD_LIST);
        });
        //
        Toolbar toolbar = mainView.findViewById(R.id.toolbar);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        spnType = mainView.findViewById(R.id.spnType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.typeList2,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnType.setAdapter(adapter);

        spnSort = mainView.findViewById(R.id.spnSort);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.sortList,
                android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnSort.setAdapter(adapter2);

        int vocabType = pref.getInt("vocabType", 0);
        int sortType = pref.getInt("sortType", 0);
        spnType.setSelection(vocabType);
        spnSort.setSelection(sortType);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        myAdapter = new MyAdapter(context,wordList);
        recyclerView.setAdapter(myAdapter);

        //字卡類型監聽事件(單字/片語)
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dbo.dataQuery(VocabType.getType(i), SortType.getType(spnSort.getSelectedItemPosition()));
                pref.edit().putInt("vocabType", i).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //字卡排序監聽事件
        spnSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dbo.dataQuery(VocabType.getType(spnType.getSelectedItemPosition()), SortType.getType(i));
                pref.edit().putInt("sortType", i).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                noDataLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        };

        return mainView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WORD_CARD_LIST){
            if (resultCode == RESULT_OK){
                dbo.dataQuery(VocabType.getType(spnType.getSelectedItemPosition()),
                        SortType.getType(spnSort.getSelectedItemPosition()));
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("sj","frag onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("sj","frag onActivityCreated");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d("sj","frag onViewStateRestored");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("sj","frag onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("sj","frag onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("sj","frag onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("sj","frag onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("sj","frag onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("sj","frag onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbo.destroy();
    }

    @Override
    public void onQueryComplete(ArrayList wordList) {
        this.wordList.clear();
        this.wordList.addAll(wordList);
        myAdapter.notifyDataSetChanged();

        if(wordList.size()>0){
            noDataLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }else{
            noDataLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private Context mContext;
        private ArrayList<HashMap> wordList;

        public MyAdapter(Context context,ArrayList<HashMap> wordList) {
            this.mContext = context;
            this.wordList = wordList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_wordcard, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Word word = getWordObj(position);
            Mean firstMean = word.meanList.get(0);
            String part = (getWordType(position)==1)?partList2[firstMean.part-1]:res.getString(R.string.phr);
            String mean = firstMean.mean.trim();
            holder.tvWord.setText(word.word);
            holder.tvWordDesc.setText((!"".equals(mean)?part+" "+mean:""));
            if(getWordMark(position)==1)
                holder.ivStar.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_round_star_pressed_28));
            else
                holder.ivStar.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_round_star_28));
        }

        @Override
        public int getItemCount() {
            return wordList.size();
        }

        private Word getWordObj(int pos){
            return (Word) wordList.get(pos).get("word");
        }

        private int getWordType(int pos){
            return (int) wordList.get(pos).get("type");
        }

        private int getWordMark(int pos){
            return (int) wordList.get(pos).get("hasMark");
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            TextView tvWord,tvWordDesc;
            ImageView ivMore,ivStar;

            public ViewHolder(View itemView) {
                super(itemView);
                tvWord = itemView.findViewById(R.id.tvWord);
                tvWordDesc = itemView.findViewById(R.id.tvWordDesc);
                ivMore = itemView.findViewById(R.id.ivMore);
                ivStar = itemView.findViewById(R.id.ivStar);

                itemView.setOnClickListener(v ->{
                    int position = getAdapterPosition();
                    Intent intent = new Intent(mContext, WordCardActivity.class);
                    intent.putExtra("position",position+1);
                    intent.putExtra("type", getWordType(position));
                    intent.putExtra("wordJson", gson.toJson(getWordObj(position)));
                    intent.putExtra("hasMark", getWordMark(position));
                    intent.putExtra("vocabType",spnType.getSelectedItemPosition());
                    intent.putExtra("sortType",spnSort.getSelectedItemPosition());
                    startActivityForResult(intent, WORD_CARD_LIST);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
                itemView.setOnLongClickListener(v -> {
                    Utils.showDelDialog(context, iDialogListener, getWordObj(getAdapterPosition()).word);
                    return true;
                });
                ivMore.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    initPopupWindow(ivMore,getWordType(position),getWordObj(position));
                });
                ivStar.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    long id = getWordObj(position).id;
                    int hasMark;
                    ContentValues values = new ContentValues();
                    if (getWordMark(position)==0){  //沒有星號標記
                        ivStar.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_round_star_pressed_28));
                        hasMark = 1;
                        values.put("hasMark", hasMark);
                    }else{ //有星號標記
                        ivStar.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_round_star_28));
                        hasMark = 0;
                        values.put("hasMark", hasMark);
                    }
                    dbo.update(values, id);
                    wordList.get(position).put("hasMark", hasMark);
                    notifyItemChanged(position);
                });
            }

            private PopupWindow initPopupWindow(View view, int type, Word word) {
                PopupWindow mDropdown;
                View layout = LayoutInflater.from(context).inflate(R.layout.word_card_popup_window, null);
                mDropdown = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,true);
                final PopupWindow mDropdownFinal = mDropdown;

                TextView tvEdit = layout.findViewById(R.id.tvEdit);
                TextView tvDel = layout.findViewById(R.id.tvDel);
                tvEdit.setOnClickListener(v->{
                    Intent intent = new Intent(mContext, EditWordCardActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("wordJson", gson.toJson(word));
                    startActivityForResult(intent, WORD_CARD_LIST);
                    mDropdownFinal.dismiss();
                });
                tvDel.setOnClickListener(v->{
                    Utils.showDelDialog(context, iDialogListener, word.word);
                    mDropdownFinal.dismiss();
                });

                mDropdown.showAsDropDown(view);
                return mDropdown;
            }

            private IDialogListener iDialogListener = new IDialogListener() {
                @Override
                public void onClickPositiveButton() {
                    int position = getAdapterPosition();
                    dbo.delete(getWordObj(position).id);
                    wordList.remove(position);
                    notifyItemRemoved(position);
                    if(wordList.size()==0) handler.sendEmptyMessage(0);
                }

                @Override
                public void onClickNegativeButton() {

                }
            };
        }
    }
}