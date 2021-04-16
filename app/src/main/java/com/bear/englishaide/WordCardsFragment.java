package com.bear.englishaide;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;


public class WordCardsFragment extends Fragment {

    private TextView tv;
    private ImageView iv;
    private View mainView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Gson gson = new Gson();
    private ArrayList<HashMap> wordList=new ArrayList();
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Cursor cursor;
    private MyAdapter myAdapter;

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
        iv = mainView.findViewById(R.id.iv);
        recyclerView = mainView.findViewById(R.id.recyclerView);
        dataQuery();
        return mainView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("sj","frag onAttach");
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

    void dataQuery(){ //字卡查詢
        wordList.clear();

        dbHelper = new DBHelper(mainView.getContext());
        db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME,null);
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
                iv.setVisibility(View.GONE);
                recyclerView.setHasFixedSize(true); //當我們確定Item的改變不會影響RecyclerView的寬高
                layoutManager = new LinearLayoutManager(mainView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                myAdapter = new MyAdapter(mainView.getContext(),wordList);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgGray));
                recyclerView.setVisibility(View.VISIBLE);
            }else{
                tv.setVisibility(View.VISIBLE);
                iv.setVisibility(View.VISIBLE);
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
            String part = ((int) map.get("type")==1)?partList2[firstMean.part-1]:getResources().getString(R.string.phr);
            String mean = firstMean.mean.trim();
            holder.tvWordDesc.setText((!"".equals(mean)?part+" "+mean:""));

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("確定要刪除字卡【"+word.word+"】嗎？");
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


                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return wordList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvWord,tvWordDesc;

            public ViewHolder(View itemView) {
                super(itemView); //**這個子類constructor必需呼叫super constructor

                tvWord = itemView.findViewById(R.id.tvWord);
                tvWordDesc = itemView.findViewById(R.id.tvWordDesc);
            }
        }
    }
}