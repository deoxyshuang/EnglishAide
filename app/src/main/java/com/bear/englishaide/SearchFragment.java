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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment{

    private static final String TAG = SearchFragment.class.getSimpleName();
    private Context context;
    private View mainView;
    private RecyclerViewAdapter mAdapter;
    private DBOperation dbo;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbo = new DBOperation(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_search, container, false);

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

        RecyclerView recyclerView = mainView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new RecyclerViewAdapter(dbo.dataQuery(VocabType.ALL,SortType.OLD_TO_NEW));
        recyclerView.setAdapter(mAdapter);

        return mainView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbo.destroy();
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
            implements Filterable {

        /**上方的arrayList為RecyclerView所綁定的ArrayList*/
        ArrayList<HashMap> arrayList;
        /**儲存最原先ArrayList的狀態(也就是充當回複RecyclerView最原先狀態的陣列)*/
        ArrayList<HashMap> arrayListFilter;

        public RecyclerViewAdapter(ArrayList arrayList) {
            this.arrayList = arrayList;
            arrayListFilter = new ArrayList<>();
            /**這裡把初始陣列複製進去了*/
            arrayListFilter.addAll(arrayList);
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView tv;
            public ViewHolder(View itemView) {
                super(itemView);
                tv = itemView.findViewById(android.R.id.text1);
                /**點擊事件*/
//                tv.setOnClickListener(this);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Word word = (Word) arrayList.get(position).get("word");
            holder.tv.setTextSize(Utils.convertDpToPx(context, 7));
            holder.tv.setText(word.word);
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }
        /**使用Filter濾除方法*/
        Filter mFilter = new Filter() {
            /**此處為正在濾除字串時所做的事*/
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                /**先將完整陣列複製過去*/
                ArrayList<HashMap> filteredList = new ArrayList<>();
                /**如果沒有輸入，則將原本的陣列帶入*/
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(arrayListFilter);
                } else {
                    /**如果有輸入，則照順序濾除相關字串
                     * 如果你有更好的搜尋演算法，就是寫在這邊*/
                    /*for (String movie: arrayListFilter) {
                        if (movie.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredList.add(movie);
                        }
                    }*/
                    filteredList = (ArrayList<HashMap>) arrayListFilter.stream()
                            .filter(map -> {
                                Word w = (Word) map.get("word");
                                return w.word.toLowerCase().contains(constraint.toString().toLowerCase());
                            })
                            .sorted((map1, map2)->{
                                Word w1 = (Word) map1.get("word");
                                Word w2 = (Word) map2.get("word");
                                return w1.word.compareTo(w2.word);
                            }).collect(Collectors.toList());
                }
                /**回傳濾除結果*/
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }
            /**將濾除結果推給原先RecyclerView綁定的陣列，並通知RecyclerView刷新*/
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrayList.clear();
                arrayList.addAll((Collection<? extends HashMap>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}