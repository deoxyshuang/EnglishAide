package com.bear.englishaide;

import android.app.Application;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class QuizViewModel extends AndroidViewModel implements DBOperation.IQueryListener {

    private MutableLiveData<Integer> currentNum,vocabType;
    private MutableLiveData<String> quizNum;
    private DBOperation dbo;

    public QuizViewModel(@NonNull Application application) {
        super(application);
        dbo = new DBOperation(application);
        dbo.setQueryListener(this);
//        dbo.dataQuery(VocabType.getType(vocabType),null);
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
    public MutableLiveData<String> getQuizNum() {
        if(quizNum == null){
            quizNum = new MutableLiveData<>();
//            quizNum.setValue("");
        }
        return quizNum;
    }

    @Override
    public void onQueryComplete(ArrayList wordList) {
        Log.d("quiz", "onQueryComplete: wordList222="+wordList.size());
    }
}
