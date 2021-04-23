package com.bear.englishaide;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class WordCardsFragment extends Fragment {

    private static final String TAG = WordCardsFragment.class.getSimpleName();
    private static final int WORD_CARDS = 1; //todo 與main重複
    private Context context;
    private FloatingActionButton fab;
    private TextView tv;
    private ImageView ivNoData;
    private Spinner spnType,spnSort;
    private View mainView;
    private RecyclerView recyclerView; //todo 動畫效果
    private RecyclerView.LayoutManager layoutManager;
    private Gson gson = new Gson();
    private ArrayList<HashMap> wordList=new ArrayList();
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Cursor cursor;
    private MyAdapter myAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("sj","frag onAttach");
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("sj","frag onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("sj","frag onCreateView");
        mainView = inflater.inflate(R.layout.fragment_wordcards, container, false);
        tv = mainView.findViewById(R.id.tv);
        ivNoData = mainView.findViewById(R.id.ivNoData);
        recyclerView = mainView.findViewById(R.id.recyclerView);
        //懸浮按鈕
        fab = mainView.findViewById(R.id.fab);
        fab.setOnClickListener(v->{
            Intent intent = new Intent(context, EditWordCardActivity.class);
            startActivityForResult(intent, WORD_CARDS);
        });
        //
        Toolbar toolbar = mainView.findViewById(R.id.toolbar);
        //AppCompatActivity activity = (AppCompatActivity) getActivity();
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        dataQuery(null);

        //字卡類型監聽事件(單字/片語)
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 1: //單字
                        dataQuery(1);
                        break;
                    case 2: //片語
                        dataQuery(2);
                        break;
                    default: //全部
                        dataQuery(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //字卡排序監聽事件
        spnSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 1:
                        break;
                    case 2:
                        break;
                    default: //預設
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return mainView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WORD_CARDS){
            if (resultCode == RESULT_OK){
                dataQuery(null);
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
    void dataQuery(Integer type){ //字卡查詢
        wordList.clear();

        dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
        String[] param=null;
        String sqlWhere="";
        if(type!=null && type>0) {
            sqlWhere = " where type=? ";
            param = new String[]{String.valueOf(type)};
        }
        cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME +sqlWhere,param);
        if (cursor != null) {
            Log.d("sj","有幾筆資料:" + cursor.getCount());
            if(cursor.getCount()>0){
                while (cursor.moveToNext()) {
                    Word word = gson.fromJson(cursor.getString(2), Word.class);
                    word.id = cursor.getInt(0);

                    HashMap<String,Object> wordMap = new HashMap();
                    wordMap.put("word",word);
                    wordMap.put("type",cursor.getInt(1)); //1=單字 2=片語
                    wordList.add(wordMap);
                }

                tv.setVisibility(View.GONE);
                ivNoData.setVisibility(View.GONE);
                recyclerView.setHasFixedSize(true); //當我們確定Item的改變不會影響RecyclerView的寬高
                layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);
                myAdapter = new MyAdapter(context,wordList);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setBackgroundColor(ContextCompat.getColor(context, R.color.bgGray));
                recyclerView.setVisibility(View.VISIBLE);
            }else{
                tv.setVisibility(View.VISIBLE);
                ivNoData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                //Toast.makeText(this, "尚無資料!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!cursor.isClosed()) cursor.close();
        if(!db.isOpen()) db.close();
        if(dbHelper!=null) dbHelper.close();
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
            ViewHolder holder = new ViewHolder(view);

            /*holder.ivPosterThumbnail = (ImageView) view.findViewById(R.id.ivPosterThumbnail);
            holder.tvPosterName = (TextView) view.findViewById(R.id.tvPosterName);
            holder.tvContent = (TextView) view.findViewById(R.id.tvContent);
            holder.btnLike = (ImageButton) view.findViewById(R.id.btnLike);
            holder.btnComment = (ImageButton) view.findViewById(R.id.btnComment);*/

            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            HashMap map = wordList.get(position);
            Word word = (Word) map.get("word");
            holder.tvWord.setText(word.word);

            String[] partList2 = getResources().getStringArray(R.array.partList2);
            Mean firstMean = word.meanList.get(0);
            int type = (int) map.get("type");
            String part = (type==1)?partList2[firstMean.part-1]:getResources().getString(R.string.phr);
            String mean = firstMean.mean.trim();
            holder.tvWordDesc.setText((!"".equals(mean)?part+" "+mean:""));
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDelDialog(word,position);
                    return false;
                }
            });
            holder.ivMore.setOnClickListener(v->{
//                Intent intent = new Intent(mContext, EditWordCardActivity.class);
//                intent.putExtra("type", type);
//                intent.putExtra("wordJson", gson.toJson(word));
//                startActivityForResult(intent, WORD_CARDS);

//                PopupMenu popup = new PopupMenu(mContext, v);
//                popup.getMenuInflater().inflate(R.menu.word_card_menu, popup.getMenu());
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            /*case R.id.action_first:
//                                Toast.makeText(mContext, "Action 1", Toast.LENGTH_SHORT).show();
//                                return true;
//                            case R.id.action_second:
//                                Toast.makeText(mContext, "Action 2", Toast.LENGTH_SHORT).show();
//                                return true;*/
//                            default:
//                        }
//                        return false;
//                    }
//                });
//                popup.show();

                initPopupWindow(holder.ivMore,type,word,position);
            });
        }
        private PopupWindow initPopupWindow(View view,int type, Word word,int position) { //todo 參數待整理

            PopupWindow mDropdown = null;

            try {
                View layout = LayoutInflater.from(context).inflate(R.layout.word_card_popup_window, null);
                mDropdown = new PopupWindow(layout);
                mDropdown = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,true);

                TextView tvEdit = layout.findViewById(R.id.tvEdit);
                TextView tvDel = layout.findViewById(R.id.tvDel);
                tvEdit.setOnClickListener(v->{
                    Intent intent = new Intent(mContext, EditWordCardActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("wordJson", gson.toJson(word));
                    startActivityForResult(intent, WORD_CARDS);
                });
                tvDel.setOnClickListener(v->{
                    showDelDialog(word,position);
                });

                mDropdown.showAsDropDown(view);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return mDropdown;
        }
        private void showDelDialog(Word word, int position){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.delConfirm, word.word));
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.delete(DBHelper.TABLE_NAME,"id = " + word.id,null);
                    wordList.remove(position);
                    notifyDataSetChanged();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int id) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        @Override
        public int getItemCount() {
            return wordList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvWord,tvWordDesc;
            public ImageView ivMore;

            public ViewHolder(View itemView) {
                super(itemView); //**這個子類constructor必需呼叫super constructor

                tvWord = itemView.findViewById(R.id.tvWord);
                tvWordDesc = itemView.findViewById(R.id.tvWordDesc);
                ivMore = itemView.findViewById(R.id.ivMore);
            }
        }
    }
}