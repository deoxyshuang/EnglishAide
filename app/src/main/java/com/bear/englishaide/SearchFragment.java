package com.bear.englishaide;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment implements DBOperation.IQueryListener{

    private static final String TAG = SearchFragment.class.getSimpleName();
    private Context context;
    private View mainView;
    private ArrayList<HashMap> wordList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    private DBOperation dbo;
    private LinearLayout noResultLayout;
    private UIHandler uiHandler;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbo = new DBOperation(context);
        dbo.setQueryListener(this);
        uiHandler = new UIHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_search, container, false);
        noResultLayout = mainView.findViewById(R.id.noResultLayout);
        Toolbar toolbar = mainView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        SearchView searchView = mainView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /**調用RecyclerView內的Filter方法*/
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        recyclerView = mainView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new RecyclerViewAdapter(wordList);
        recyclerView.setAdapter(mAdapter);
        dbo.dataQuery(VocabType.ALL,SortType.OLD_TO_NEW);

        return mainView;
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
        mAdapter.notifyDataSetChanged();
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
            implements Filterable {

        /**綁定RecyclerView的原始資料集*/
        ArrayList<HashMap> arrayList;
        /**儲存搜尋後結果*/
        ArrayList<HashMap> arrayListFilter;

        public RecyclerViewAdapter(ArrayList arrayList) {
            this.arrayList = arrayList;
            arrayListFilter = new ArrayList<>();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView tv;
            public ViewHolder(View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv);
                /**點擊事件*/
//                tv.setOnClickListener(this);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_result,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Word word = (Word) arrayListFilter.get(position).get("word");
            holder.tv.setText(word.word);
        }

        @Override
        public int getItemCount() {
            return arrayListFilter.size();
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }
        /**使用Filter濾除方法*/
        private Filter mFilter = new Filter() {
            /**此處為正在濾除字串時所做的事*/
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                /**先將完整陣列複製過去*/
                ArrayList<HashMap> filteredList = new ArrayList<>();
                /**如果沒有輸入，則將原本的陣列帶入*/
                if (constraint == null || constraint.length() == 0) {
                    filteredList.clear();
                    uiHandler.sendEmptyMessage(0);
                } else {
                    /**如果有輸入，則照順序濾除相關字串*/
                    filteredList = (ArrayList<HashMap>) arrayList.stream()
                            .filter(map -> {
                                Word w = (Word) map.get("word");
                                return w.word.toLowerCase().contains(constraint.toString().toLowerCase());
                            })
                            .sorted((map1, map2)->{
                                Word w1 = (Word) map1.get("word");
                                Word w2 = (Word) map2.get("word");
                                return w1.word.compareTo(w2.word);
                            }).collect(Collectors.toList());

                    if(filteredList.size()>0) uiHandler.sendEmptyMessage(0);
                    else uiHandler.sendEmptyMessage(1);
                }
                /**回傳濾除結果*/
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }
            /**將濾除結果推給原先RecyclerView綁定的陣列，並通知RecyclerView刷新*/
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrayListFilter.clear();
                arrayListFilter.addAll((Collection<? extends HashMap>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    noResultLayout.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
                    break;
                default:
                    noResultLayout.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }
}