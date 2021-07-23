package com.bear.englishaide;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bear.englishaide.databinding.ActivityQuizBinding;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

//    private TextView tvTotalNum, tvCurrentNum;
//    private Button btnNext,btnPrev,btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QuizViewModel quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        ActivityQuizBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz);
        binding.setData(quizViewModel);
        binding.setLifecycleOwner(this);

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_close_24);
        actionBar.setElevation(25);//todo
        actionBar.setTitle("隨機抽考");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnNext:
                break;
            case R.id.btnPrev:
                break;
            case R.id.btnExit:
                //todo
                finish();
                break;
        }
    }
}
