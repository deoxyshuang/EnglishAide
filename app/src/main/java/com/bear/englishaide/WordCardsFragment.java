package com.bear.englishaide;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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


public class WordCardsFragment extends Fragment {

    private TextView tv;
    private ImageView iv;
    private View mainView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Gson gson = new Gson();
    private ArrayList<Word> vocabList=new ArrayList(),phrList=new ArrayList();
    private SQLiteDatabase db;
    private MyAdapter myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vocabList.clear();
        phrList.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_wordcards, container, false);
        tv = mainView.findViewById(R.id.tv);
        iv = mainView.findViewById(R.id.iv);
        recyclerView = mainView.findViewById(R.id.recyclerView);

        //查詢
        DBHelper dbHelper = new DBHelper(mainView.getContext());
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME,null);
        if (cursor != null) {
            Log.d("sj","有幾筆資料:" + cursor.getCount());
            if(cursor.getCount()>0){
                while (cursor.moveToNext()) {
                    Word word = gson.fromJson(cursor.getString(2), Word.class);
                    word.id = cursor.getInt(0);
                    if(cursor.getInt(1)==1){//單字
                        vocabList.add(word);
                    }else{//片語
                        phrList.add(word);
                    }
                }

                tv.setVisibility(View.GONE);
                iv.setVisibility(View.GONE);
                recyclerView.setHasFixedSize(true); //當我們確定Item的改變不會影響RecyclerView的寬高
                layoutManager = new LinearLayoutManager(mainView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                myAdapter = new MyAdapter(mainView.getContext(),vocabList,phrList);
                recyclerView.setAdapter(myAdapter);


                recyclerView.setVisibility(View.VISIBLE);
            }else{
                tv.setVisibility(View.VISIBLE);
                iv.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                //Toast.makeText(this, "尚無資料!", Toast.LENGTH_SHORT).show();
            }
        }
        return mainView;
    }



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private Context mContext;
        private ArrayList<Word> vocabList,phrList;

        public MyAdapter(Context context,ArrayList<Word> vocabList,ArrayList<Word> phrList) {
            this.mContext = context;
            this.vocabList = vocabList;
            this.phrList = phrList;
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
            Word word = vocabList.get(position);
            holder.tvWord.setText(word.word);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Word word = vocabList.get(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("確定要刪除字卡【"+word.word+"】嗎？");
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            db.delete(DBHelper.TABLE_NAME,"id = " + word.id,null);
                            vocabList.remove(position);
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
            return vocabList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvWord;

            public ViewHolder(View itemView) {
                super(itemView); //**這個子類constructor必需呼叫super constructor

                tvWord = itemView.findViewById(R.id.tvWord);
//                itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View view) {
//                        Word word = vocabList.get(getAdapterPosition());
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                        builder.setMessage("確定要刪除字卡【"+word.word+"】嗎？");
//                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                db.delete(DBHelper.TABLE_NAME,"id = " + word.id,null);
//                                //todo 畫面refresh
//                                notifyDataSetChanged();
//                            }
//                        });
//                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int id) {
//
//                            }
//                        });
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
//
//
//                        return false;
//                    }
//                });
            }
        }
    }
}