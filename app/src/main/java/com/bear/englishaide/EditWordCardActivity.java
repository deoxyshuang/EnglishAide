package com.bear.englishaide;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class EditWordCardActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edtWord,edtMean,edtSentence,edtPlural,edtPast,edtPP,edtPresent,edtCompar,edtSuper;
    private TextView txtPlural,txtPast,txtPP,txtPresent,txtCompar,txtSuper;
    private Button btnSave;
    private Spinner spnFami,spnType,spnPart;
    private ImageView ivAdd;
    private Word word;
    private Gson gson;
    private View viewPos;
    private ArrayList spnPartList=new ArrayList<Spinner>(),edtMeanList=new ArrayList<EditText>(),edtSentenceList=new <EditText>ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wordcard);

        gson = new Gson();
        word = new Word();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.create);
        setSupportActionBar(toolbar);
        //setSupportActionBar(toolbar)一定要放在setNavigationOnClickListener前,
        //不然onclick无效
        toolbar.setNavigationIcon(R.drawable.ic_round_close_30);
        toolbar.setNavigationOnClickListener(v->finish());


//        ImageButton btnClose = findViewById(R.id.btnClose);
//        btnClose.setOnClickListener(this);

        ivAdd = findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(this);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        edtWord = findViewById(R.id.edtWord);
        edtMean = findViewById(R.id.edtMean);
        edtSentence = findViewById(R.id.edtSentence);
//        edtPlural = findViewById(R.id.edtPlural);
//        edtPast = findViewById(R.id.edtPast);
//        edtPP = findViewById(R.id.edtPP);
//        edtPresent = findViewById(R.id.edtPresent);
//        edtCompar = findViewById(R.id.edtCompar);
//        edtSuper = findViewById(R.id.edtSuper);
//        edtPlural.setVisibility(View.VISIBLE);
//        edtPast.setVisibility(View.GONE);
//        edtPP.setVisibility(View.GONE);
//        edtPresent.setVisibility(View.GONE);
//        edtCompar.setVisibility(View.GONE);
//        edtSuper.setVisibility(View.GONE);

//        txtPlural = findViewById(R.id.txtPlural);
//        txtPast = findViewById(R.id.txtPast);
//        txtPP = findViewById(R.id.txtPP);
//        txtPresent = findViewById(R.id.txtPresent);
//        txtCompar = findViewById(R.id.txtCompar);
//        txtSuper = findViewById(R.id.txtSuper);
//        txtPlural.setVisibility(View.VISIBLE);
//        txtPast.setVisibility(View.GONE);
//        txtPP.setVisibility(View.GONE);
//        txtPresent.setVisibility(View.GONE);
//        txtCompar.setVisibility(View.GONE);
//        txtSuper.setVisibility(View.GONE);



        spnType = (Spinner) findViewById(R.id.spnType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.typeList,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnType.setAdapter(adapter);

        spnPart = (Spinner) findViewById(R.id.spnPart);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                        R.array.partList,
                        android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
//        spnPart.setBackground(getDrawable(R.drawable.spinner_back));
        spnPart.setAdapter(adapter2);

        spnFami = (Spinner) findViewById(R.id.spnFami);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.famiList,
                android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnFami.setAdapter(adapter3);
        spnFami.setSelection(2);

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) edtMean.getLayoutParams();
                if(i>0){ //片語
                    spnPart.setVisibility(View.GONE);
                    //ConstraintLayout.LayoutParams cons=(ConstraintLayout.LayoutParams) meanET.getLayoutParams();
                    //cons.setMarginStart(0);
                    //cons.startToStart=-1;
                    //edtMean.setLayoutParams(cons);
                    params.setMarginStart(0);
                    edtMean.setLayoutParams(params);

//                    edtPlural.setVisibility(View.GONE);
//                    edtPast.setVisibility(View.GONE);
//                    edtPP.setVisibility(View.GONE);
//                    edtPresent.setVisibility(View.GONE);
//                    edtCompar.setVisibility(View.GONE);
//                    edtSuper.setVisibility(View.GONE);
//
//                    txtPlural.setVisibility(View.GONE);
//                    txtPast.setVisibility(View.GONE);
//                    txtPP.setVisibility(View.GONE);
//                    txtPresent.setVisibility(View.GONE);
//                    txtCompar.setVisibility(View.GONE);
//                    txtSuper.setVisibility(View.GONE);
                }else{ //單字
                    spnPart.setVisibility(View.VISIBLE);
                    params.setMarginStart((int) getResources().getDimension(R.dimen.comSpacing));
                    edtMean.setLayoutParams(params);
                    //setDetailEdt(spnPart.getSelectedItemPosition());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spnPart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setDetailEdt(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        viewPos = findViewById(R.id.myCoordinatorLayout);
    }


    //依據詞性切換內容
    private void setDetailEdt(int position){
//        switch (position){
//            case 0: //n.
//                edtPlural.setVisibility(View.VISIBLE);
//                edtPast.setVisibility(View.GONE);
//                edtPP.setVisibility(View.GONE);
//                edtPresent.setVisibility(View.GONE);
//                edtCompar.setVisibility(View.GONE);
//                edtSuper.setVisibility(View.GONE);
//
//                txtPlural.setVisibility(View.VISIBLE);
//                txtPast.setVisibility(View.GONE);
//                txtPP.setVisibility(View.GONE);
//                txtPresent.setVisibility(View.GONE);
//                txtCompar.setVisibility(View.GONE);
//                txtSuper.setVisibility(View.GONE);
//                break;
//            case 1: //v.
//                edtPlural.setVisibility(View.GONE);
//                edtPast.setVisibility(View.VISIBLE);
//                edtPP.setVisibility(View.VISIBLE);
//                edtPresent.setVisibility(View.VISIBLE);
//                edtCompar.setVisibility(View.GONE);
//                edtSuper.setVisibility(View.GONE);
//
//                txtPlural.setVisibility(View.GONE);
//                txtPast.setVisibility(View.VISIBLE);
//                txtPP.setVisibility(View.VISIBLE);
//                txtPresent.setVisibility(View.VISIBLE);
//                txtCompar.setVisibility(View.GONE);
//                txtSuper.setVisibility(View.GONE);
//                break;
//            case 2: //adj.
//                edtPlural.setVisibility(View.GONE);
//                edtPast.setVisibility(View.GONE);
//                edtPP.setVisibility(View.GONE);
//                edtPresent.setVisibility(View.GONE);
//                edtCompar.setVisibility(View.VISIBLE);
//                edtSuper.setVisibility(View.VISIBLE);
//
//                txtPlural.setVisibility(View.GONE);
//                txtPast.setVisibility(View.GONE);
//                txtPP.setVisibility(View.GONE);
//                txtPresent.setVisibility(View.GONE);
//                txtCompar.setVisibility(View.VISIBLE);
//                txtSuper.setVisibility(View.VISIBLE);
//                break;
//            default:
//                edtPlural.setVisibility(View.GONE);
//                edtPast.setVisibility(View.GONE);
//                edtPP.setVisibility(View.GONE);
//                edtPresent.setVisibility(View.GONE);
//                edtCompar.setVisibility(View.GONE);
//                edtSuper.setVisibility(View.GONE);
//
//                txtPlural.setVisibility(View.GONE);
//                txtPast.setVisibility(View.GONE);
//                txtPP.setVisibility(View.GONE);
//                txtPresent.setVisibility(View.GONE);
//                txtCompar.setVisibility(View.GONE);
//                txtSuper.setVisibility(View.GONE);
//
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                if(!"".equals((edtWord.getText().toString().trim()))){
                    word.meanListInit();
                    int[] famiKeyList = getResources().getIntArray(R.array.famiKeyList);
                    word.fami = famiKeyList[spnFami.getSelectedItemPosition()];
                    word.word = edtWord.getText().toString();

                    Mean mean = new Mean();
                    mean.part = 1; //名詞
                    mean.mean = edtMean.getText().toString();

                    Sentence sentence = new Sentence();
                    sentence.eng = edtSentence.getText().toString();

                    mean.sentenceListInit();
                    mean.sentenceList.add(sentence);
                    word.meanList.add(mean);

                    String json = gson.toJson(word);
                    Log.d("sj","obj->json:" + json);

                    int colorId=0;
                    String msg="";
                    try{
                        DBHelper dbHelper = new DBHelper(this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("type", spnType.getSelectedItemPosition()+1);
                        values.put("data", json);
                        db.insert(DBHelper.TABLE_NAME, null, values);
                        msg=getResources().getString(R.string.saveOK);
                        colorId=getResources().getColor(R.color.successGreen);
                    }catch (SQLException e){
                        e.printStackTrace();
                        msg=getResources().getString(R.string.saveNG)+e.getMessage();
                        colorId=getResources().getColor(R.color.failureRed);
                    }finally {
                        Snackbar.make(viewPos, msg, Snackbar.LENGTH_SHORT)
                                //.setAction("關閉", null)
                                .setBackgroundTint(colorId)
                                .show();
                    }
                }else{
                    Snackbar.make(viewPos, getResources().getString(R.string.alertMsg), Snackbar.LENGTH_SHORT)
                            .show();
                }

                break;
//            case R.id.btnClose:
//                EditWordCardActivity.this.finish();
//                break;

            case R.id.ivAdd:
                LinearLayout cvArea = findViewById(R.id.cvArea);
                //cardview
                MaterialCardView cv = new MaterialCardView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                cv.setLayoutParams(layoutParams);
                cv.setCardElevation(2);
                cv.setRadius(15);
                cv.setContentPadding(10,10,10,10);
                cv.setStrokeWidth(2);
                cv.setCardBackgroundColor(ContextCompat.getColor(this,R.color.bgGray)); //todo 全面修改getColor寫法
                cv.setStrokeColor(ContextCompat.getColor(this,R.color.lightBlue2));
                //包裹兩行的Linear
                LinearLayout wrapper = new LinearLayout(this);
                wrapper.setLayoutParams(layoutParams);
                wrapper.setOrientation(LinearLayout.VERTICAL);
                //第一行
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setLayoutParams(layoutParams);
                //todo set style
                Spinner spinner = new Spinner(this);
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                spinner.setLayoutParams(viewParams);
                spinner.setPrompt(getResources().getString(R.string.partOfspeech));
                ArrayAdapter<CharSequence> ad = ArrayAdapter.createFromResource(this,
                        R.array.partList,
                        android.R.layout.simple_spinner_item);
                ad.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                spinner.setAdapter(ad);
                spnPartList.add(spinner);
                //
                EditText et = new EditText(this);
                LinearLayout.LayoutParams selfParam = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
                selfParam.weight=1;
                selfParam.setMarginStart((int) getResources().getDimension(R.dimen.comSpacing));
                et.setLayoutParams(selfParam);
                        //todo 了解單位轉換
                et.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.contentTextSize));
                et.setHint("中文解釋");
                et.setHintTextColor(ContextCompat.getColor(this,R.color.lightGray));
                edtMeanList.add(et);

                linearLayout.addView(spinner);
                linearLayout.addView(et);

                //第二行
                EditText editText = new EditText(this);
                layoutParams.setMargins(0,10,0,0);
                editText.setLayoutParams(layoutParams);
                editText.setTextSize(getResources().getDimension(R.dimen.contentTextSize));
                Log.d("sj","getResources().getDimension(R.dimen.contentTextSize)="+getResources().getDimension(R.dimen.contentTextSize));
                editText.setHint("例句");
                editText.setHintTextColor(ContextCompat.getColor(this,R.color.lightGray));
                edtSentenceList.add(editText);

                wrapper.addView(linearLayout);
                wrapper.addView(editText);
                cv.addView(wrapper);
                cvArea.addView(cv);

                ScrollView sv = findViewById(R.id.sv);
                sv.post(new Runnable() {
                    @Override
                    public void run() {
                        sv.fullScroll(ScrollView.FOCUS_DOWN); //自動滑至底部
                    }
                });
                break;

        }
    }
}
