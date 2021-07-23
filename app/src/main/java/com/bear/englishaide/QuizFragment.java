package com.bear.englishaide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class QuizFragment extends Fragment implements DBOperation.IQueryListener{

    private Context context;
    private View mainView;
    private Spinner spnQuizNum,spnType;
    private Button btnStart;
    private DBOperation dbo;
    private QuizViewModel quizViewModel;

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
        quizViewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_quiz, container, false);
        btnStart = mainView.findViewById(R.id.btnStart);
        spnQuizNum = mainView.findViewById(R.id.spnQuizNum);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.quizNumAry,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnQuizNum.setAdapter(adapter);

        spnType = mainView.findViewById(R.id.spnType);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.typeList2,
                android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnType.setAdapter(adapter2);

        btnStart.setOnClickListener(view -> {
            Log.d("quiz", "btnStart.setOnClickListener");
            dbo.dataQuery(VocabType.getType(spnType.getSelectedItemPosition()), null);
        });

        // Inflate the layout for this fragment
        return mainView;
    }

    @Override
    public void onQueryComplete(ArrayList wordList) {
        Log.d("quiz", "onQueryComplete: wordList="+wordList.size());
        if (wordList.size()>0) { //todo 待增加細節防呆
            Intent intent = new Intent(context, QuizActivity.class);
            String[] quizNumAry = getResources().getStringArray(R.array.quizNumAry);
            quizViewModel.getVocabType().setValue(spnType.getSelectedItemPosition());
            quizViewModel.getQuizNum().setValue(quizNumAry[spnQuizNum.getSelectedItemPosition()]);
            startActivity(intent);
        }
    }
}