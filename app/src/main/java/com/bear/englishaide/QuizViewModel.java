package com.bear.englishaide;

import android.app.Application;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class QuizViewModel extends AndroidViewModel implements DBOperation.IQueryListener {

    private MutableLiveData<Integer> currentNum,vocabType,quizNum;
    private MutableLiveData<String> prefix,suffix;
    private DBOperation dbo;
    private MutableLiveData<List> strList;
    private MutableLiveData<StringBuilder> meaning;

    public QuizViewModel(@NonNull Application application) {
        super(application);
        dbo = new DBOperation(application);
        dbo.setQueryListener(this);
    }
    public MutableLiveData<Integer> getCurrentNum() {
        if(currentNum == null){
            currentNum = new MutableLiveData<>();
            currentNum.setValue(1);
        }
        return currentNum;
    }
    public MutableLiveData<Integer> getVocabType() {
        if(vocabType == null){
            vocabType = new MutableLiveData<>();
            vocabType.setValue(0);
        }
        return vocabType;
    }
    public MutableLiveData<Integer> getQuizNum() {
        if(quizNum == null){
            quizNum = new MutableLiveData<>();
            quizNum.setValue(5); //todo 讀取可變來源
        }
        return quizNum;
    }
    public MutableLiveData<String> getPrefix() {
        if(prefix == null){
            prefix = new MutableLiveData<>();
        }
        return prefix;
    }
    public MutableLiveData<String> getSuffix() {
        if(suffix == null){
            suffix = new MutableLiveData<>();
        }
        return suffix;
    }
    public MutableLiveData<List> getStrList() {
        if(strList == null){
            strList = new MutableLiveData<>();
        }
        return strList;
    }
    public MutableLiveData<StringBuilder> getMeaning() {
        if(meaning == null){
            meaning = new MutableLiveData<>();
        }
        return meaning;
    }

    //產生隨機題目
    void generateQuiz() {
        dbo.dataQuery(VocabType.getType(getVocabType().getValue()),null);
    }

    @Override
    public void onQueryComplete(ArrayList wordList) {
        Log.d("quizTemp", "onQueryComplete: wordList="+wordList.size());
        ArrayList<Integer> quizIndex = new ArrayList<>();
        int quizNum = getQuizNum().getValue().intValue();
        Random r = new Random();
        while (quizIndex.size() < quizNum) {
            int val = r.nextInt(quizNum);
            if (!quizIndex.contains(val)) quizIndex.add(val);
        }

        HashMap<String,Object> hashMap = (HashMap<String, Object>) wordList.get(quizIndex.get(0));
        Word word = (Word) hashMap.get("word");
        Log.d("quizTemp", "word="+word.word);

        /*word.meanList.forEach((mean -> {
            getMeaning().setValue(mean.part);
            Log.d("quizTemp", "mean="+mean.mean);
        }));*/
        /*String[] partList2 = res.getStringArray(R.array.partList2);
        String part = (getWordType(position)==1)?partList2[firstMean.part-1]:res.getString(R.string.phr);
        private int getWordType(int pos){
            return (int) wordList.get(pos).get("type");
        }*/
        String[] strAry = word.word.split("");
        String[] resStrAry = Arrays.stream(strAry).filter(x->!"".equals(x)).toArray(String[]::new);
        getPrefix().setValue(resStrAry[0]);
        getSuffix().setValue(resStrAry[resStrAry.length-1]);
        getStrList().setValue(Arrays.asList(resStrAry));
    }
}