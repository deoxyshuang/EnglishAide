package com.bear.englishaide;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class WordCardFragment extends Fragment {

    private Context context;
    private Gson gson = new Gson();
    private LinearLayout mainLayout;
    private String[] partList;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        partList = getResources().getStringArray(R.array.partList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_word_card, container, false);
        mainLayout = mainView.findViewById(R.id.mainLayout);
        int type = getArguments().getInt("type",1);
        String wordJson = getArguments().getString("wordJson");
        Word word = gson.fromJson(wordJson, Word.class);
        if(word!=null) createContent(type, word);
        return mainView;
    }

    private void createContent(int type,Word word){
//        mainLayout.removeAllViews();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        word.meanList.forEach((mean -> {
            LinearLayout layout1 = new LinearLayout(context);
            layout1.setOrientation(LinearLayout.HORIZONTAL);
            layout1.setLayoutParams(layoutParams);

            LinearLayout layout2 = new LinearLayout(context);
            layout2.setOrientation(LinearLayout.VERTICAL);
            layout2.setLayoutParams(layoutParams);

            //詞性
            TextView tvPart = new TextView(context);
            tvPart.setLayoutParams(wrapParams);
            tvPart.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_textview));
            tvPart.setTextColor(ContextCompat.getColor(context,R.color.white));
            if(type==1) tvPart.setText(partList[mean.part-1]);
            else tvPart.setText(getResources().getString(R.string.phr));
            //todo
            tvPart.setTextSize(15);
            tvPart.setPadding((int)Utils.convertDpToPx(context,6), (int)Utils.convertDpToPx(context,6), (int)Utils.convertDpToPx(context,6), (int)Utils.convertDpToPx(context,6));
            //解釋
            TextView tvMean = new TextView(context);
            tvMean.setLayoutParams(wrapParams);
            tvMean.setTextColor(ContextCompat.getColor(context,R.color.black));
            tvMean.setText(mean.mean);
            //todo
            tvMean.setTextSize(17);
            tvMean.setPadding((int)Utils.convertDpToPx(context,6), (int)Utils.convertDpToPx(context,6), (int)Utils.convertDpToPx(context,6), (int)Utils.convertDpToPx(context,6));

            layout1.addView(tvPart);
            layout1.addView(tvMean);
            if(!"".equals(mean.mean)) mainLayout.addView(layout1);

            mean.sentenceList.forEach(sentence -> {
                TextView tvSen = new TextView(context);
                tvSen.setLayoutParams(wrapParams);
                tvSen.setTextColor(ContextCompat.getColor(context,R.color.black));
                //todo
                tvSen.setText(sentence.eng);
                tvSen.setTextSize(17);
                tvSen.setPadding((int)Utils.convertDpToPx(context,6), (int)Utils.convertDpToPx(context,6), (int)Utils.convertDpToPx(context,6), (int)Utils.convertDpToPx(context,6));

                layout2.addView(tvSen);
            });

            View line  = new View(context);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)Utils.convertDpToPx(context,1));
            lineParams.setMargins((int) Utils.convertDpToPx(context,5), (int) Utils.convertDpToPx(context,15), (int) Utils.convertDpToPx(context,5), (int) Utils.convertDpToPx(context,15));
            line.setLayoutParams(lineParams);
            line.setBackgroundColor(ContextCompat.getColor(context,R.color.lightGray));

            if(mean.sentenceList.size()>1) layout2.addView(line);
            mainLayout.addView(layout2);
        }));
    }
}