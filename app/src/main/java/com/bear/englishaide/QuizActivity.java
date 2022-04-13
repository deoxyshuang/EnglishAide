package com.bear.englishaide;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bear.englishaide.databinding.ActivityQuizBinding;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private QuizViewModel quizViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        quizViewModel.getVocabType().setValue(intent.getIntExtra("vocabType",0));
        quizViewModel.getQuizNum().setValue(intent.getIntExtra("quizNum",0));
        quizViewModel.generateQuiz();

        ActivityQuizBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz);
        binding.setData(quizViewModel);
        binding.setLifecycleOwner(this);
        binding.btnOk.setOnClickListener(this);
        binding.btnExit.setOnClickListener(this);
        binding.tvMean.setText("");

        quizViewModel.getStrList().observe(this, new Observer<List>() {
            @Override
            public void onChanged(List list) {
                Log.d("quizTemp", "List="+list.size());
                List<EditText> editTextList = new ArrayList<>();
                for (int i = 1; i < list.size()-1; i++) {
                    Log.d("quizTemp", "str="+list.get(i));
                    if (!" ".equals(list.get(i))) {
                        EditText edt = new EditText(getApplicationContext());
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(90,LinearLayout.LayoutParams.MATCH_PARENT); //todo width to res
                        edt.setLayoutParams(layoutParams);
                        edt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,33);
                        edt.setPadding(0,0,0,0);
                        edt.setGravity(Gravity.CENTER_HORIZONTAL);
                        edt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(1) }); //限制輸入固定長度
                        edt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); //disable text autocorrection //TYPE_TEXT_FLAG_NO_SUGGESTIONS not work
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //SDK>=29
                            edt.setTextCursorDrawable(R.drawable.cursor_edt);
                        } else {
                            try {
                                Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
                                f.setAccessible(true);
                                f.set(edt, R.drawable.cursor_edt);
                            } catch (IllegalAccessException | NoSuchFieldException e) {
                                e.printStackTrace();
                            }
                        }
                        binding.charContainer.addView(edt);
                        editTextList.add(edt);
                    } else {
                        TextView tv = new TextView(getApplicationContext());
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(60,LinearLayout.LayoutParams.WRAP_CONTENT); //todo width to res
                        tv.setLayoutParams(layoutParams2);
                        binding.charContainer.addView(tv);
                    }
                }

                //輸入字母後自動跳格
                for (int j = 0; j < editTextList.size(); j++) {
                    Log.d("quizTemp", "onTextChanged: j="+j);
                    final int nextJ=j+1;
                    editTextList.get(j).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (nextJ < editTextList.size()) {
                                editTextList.get(nextJ).requestFocus();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }
            }
        });


        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_close_24);
        actionBar.setElevation(25);//todo
        actionBar.setTitle("隨機抽考");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnOk:
                //下一題 並記錄對錯
                break;
            case R.id.btnExit:
                //todo
                finish();
                break;
        }
    }
}
