package com.bear.englishaide;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

    private Resources res;
    private EditText edtWord,edtMean,edtSentence,edtPlural,edtPast,edtPP,edtPresent,edtCompar,edtSuper;
    private TextView txtPlural,txtPast,txtPP,txtPresent,txtCompar,txtSuper;
    private Button btnSave;
    private Spinner spnFami,spnType,spnPart;
    private ImageView ivAdd;
    private Word word;
    private Gson gson;
    private View viewPos;
    private ScrollView sv;
    private ArrayList<Spinner> spnPartList=new ArrayList<>();
    private ArrayList<EditText> edtMeanList=new ArrayList<>(),edtSentenceList=new ArrayList<>();
    private ArrayList<ImageView> ivRemoveList=new ArrayList<>();
    private LayoutInflater layoutInflater;
    private int[] famiKeyList,partKeyList;
    private LinearLayout cvArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wordcard);

        res = getResources();
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

        edtMeanList.add(edtMean);
        edtSentenceList.add(edtSentence);
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
        spnPartList.add(spnPart);

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
                    params.setMarginStart((int) res.getDimension(R.dimen.comSpacing));
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
        sv = findViewById(R.id.sv);
        cvArea = findViewById(R.id.cvArea);

        famiKeyList = res.getIntArray(R.array.famiKeyList);
        partKeyList = res.getIntArray(R.array.partKeyList);
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
                    word.fami = famiKeyList[spnFami.getSelectedItemPosition()];
                    word.word = edtWord.getText().toString();

                    for(int i=0;i<spnPartList.size();i++){
                        Mean mean = new Mean();
                        mean.part = partKeyList[spnPartList.get(i).getSelectedItemPosition()];
                        mean.mean = edtMeanList.get(i).getText().toString();

                        Sentence sentence = new Sentence();
                        sentence.eng = edtSentenceList.get(i).getText().toString();

                        mean.sentenceListInit();
                        mean.sentenceList.add(sentence);
                        //Log.d("sj","mean:" + gson.toJson(mean)); //{"mean":"","part":1,"sentenceList":[{"eng":""}]}
                        word.meanList.add(mean);
                    }

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
                        //todo temp
                        //db.insert(DBHelper.TABLE_NAME, null, values);
                        msg=res.getString(R.string.saveOK);
                        colorId=ContextCompat.getColor(this,R.color.successGreen);
                    }catch (SQLException e){
                        e.printStackTrace();
                        msg=res.getString(R.string.saveNG)+e.getMessage();
                        colorId=ContextCompat.getColor(this,R.color.failureRed);
                    }finally {
                        Snackbar.make(viewPos, msg, Snackbar.LENGTH_SHORT)
                                //.setAction("關閉", null)
                                .setBackgroundTint(colorId)
                                .show();
                    }
                }else{
                    Snackbar.make(viewPos, res.getString(R.string.alertMsg), Snackbar.LENGTH_SHORT)
                            .show();
                }

                break;
//            case R.id.btnClose:
//                EditWordCardActivity.this.finish();
//                break;

            case R.id.ivAdd:
                //包裹cardview及刪除按鈕
                FrameLayout frameLayout = new FrameLayout(this);
                frameLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                //cardview
                MaterialCardView cv = new MaterialCardView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,(int) Utils.convertDpToPx(this,12),0,(int) Utils.convertDpToPx(this,10));
                cv.setLayoutParams(layoutParams);
                cv.setCardElevation((int) Utils.convertDpToPx(this,2));
                cv.setRadius((int) Utils.convertDpToPx(this,15));
                cv.setContentPadding((int) Utils.convertDpToPx(this,10),(int) Utils.convertDpToPx(this,10),(int) Utils.convertDpToPx(this,10),(int) Utils.convertDpToPx(this,10));
                cv.setStrokeWidth((int) Utils.convertDpToPx(this,2));
                cv.setCardBackgroundColor(ContextCompat.getColor(this,R.color.bgGray));
                cv.setStrokeColor(ContextCompat.getColor(this,R.color.lightBlue2));
                //包裹兩行的Linear
                LinearLayout wrapper = new LinearLayout(this);
                wrapper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                wrapper.setOrientation(LinearLayout.VERTICAL);
                //第一行
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                Spinner spinner = (Spinner)layoutInflater.inflate(R.layout.spinner_style, null);
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                spinner.setLayoutParams(viewParams);
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
                selfParam.setMarginStart((int) res.getDimension(R.dimen.comSpacing));
                et.setLayoutParams(selfParam);
                et.setTextSize(TypedValue.COMPLEX_UNIT_PX,res.getDimension(R.dimen.contentTextSize));
                et.setHint(res.getString(R.string.mean));
                et.setHintTextColor(ContextCompat.getColor(this,R.color.lightGray));
                edtMeanList.add(et);

                linearLayout.addView(spinner);
                linearLayout.addView(et);

                //第二行
                EditText editText = new EditText(this);
                LinearLayout.LayoutParams edtParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                edtParams.setMargins(0,(int) Utils.convertDpToPx(this,10),0,0);
                editText.setLayoutParams(edtParams);
                editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,res.getDimension(R.dimen.contentTextSize));
                editText.setHint(res.getString(R.string.example));
                editText.setHintTextColor(ContextCompat.getColor(this,R.color.lightGray));
                edtSentenceList.add(editText);

                wrapper.addView(linearLayout);
                wrapper.addView(editText);
                cv.addView(wrapper);
                frameLayout.addView(cv);

                //cv右上角 移除按鈕
                ImageView ivRemove = new ImageView(this);
                FrameLayout.LayoutParams ivParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                ivParams.gravity= Gravity.TOP | Gravity.END;
                ivRemove.setLayoutParams(ivParams);
                ivRemove.setElevation((int) Utils.convertDpToPx(this,5));
                ivRemove.setBackground(ContextCompat.getDrawable(this,R.drawable.selector_iv_remove));
                ivRemove.setClickable(true);
                frameLayout.addView(ivRemove);
                cvArea.addView(frameLayout);

                ivRemove.setOnClickListener(v->{
                    cvArea.removeView(frameLayout);
                });
                sv.post(() -> {
                    sv.fullScroll(ScrollView.FOCUS_DOWN); //強制滾動至底
                });
                break;

        }
    }
}
