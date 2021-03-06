package com.bear.englishaide;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class EditWordCardActivity extends AppCompatActivity implements View.OnClickListener, DBOperation.IDataListener{

    private Resources res;
    private EditText edtWord,edtMean,edtSentence;
//            ,edtPlural,edtPast,edtPP,edtPresent,edtCompar,edtSuper;
//    private TextView txtPlural,txtPast,txtPP,txtPresent,txtCompar,txtSuper;
    private Button btnSave;
    private Spinner spnFami,spnType,spnPart;
    private ImageView ivAdd,ivAddSen;
    private int type;
    private Word word;
    private Gson gson;
    private View viewPos;
    private ScrollView sv;
    private ArrayList<HashMap> dataList=new ArrayList<>();
    private ArrayList<EditText> firstSenList=new ArrayList<>();
    private HashMap<String,Object> firstData = new HashMap<>();
    private LayoutInflater layoutInflater;
    private int[] famiKeyList,partKeyList;
    private LinearLayout cvArea,cvSection;
    private ArrayList<Mean> meanList;
    private String wordJson;
    private DBOperation dbo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word_card);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_close_24);
        actionBar.setElevation(25);//todo
        Intent intent = getIntent();
        res = getResources();
        gson = new Gson();
        dbo = new DBOperation(this);
        dbo.setDataListener(this);
        //?????????
        famiKeyList = res.getIntArray(R.array.famiKeyList);
        partKeyList = res.getIntArray(R.array.partKeyList);
        //????????????
        viewPos = findViewById(R.id.myCoordinatorLayout);
        sv = findViewById(R.id.sv);
        cvArea = findViewById(R.id.cvArea); //???????????????cardview?????????layout
        cvSection = findViewById(R.id.cvSection); //?????????cardview??? ???????????????layout
        edtWord = findViewById(R.id.edtWord);
        edtMean = findViewById(R.id.edtMean);
        edtSentence = findViewById(R.id.edtSentence);
        ivAdd = findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(this);
        ivAddSen = findViewById(R.id.ivAddSen);
        ivAddSen.setOnClickListener(this);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        spnFami = findViewById(R.id.spnFami);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.famiList,
                android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnFami.setAdapter(adapter3);

        spnType = findViewById(R.id.spnType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.typeList,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnType.setAdapter(adapter);

        spnPart =  findViewById(R.id.spnPart);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.partList,
                android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
//        spnPart.setBackground(getDrawable(R.drawable.spinner_back));
        spnPart.setAdapter(adapter2);

        //????????????
        firstData.put("mean",edtMean);
        firstSenList.add(edtSentence);
        firstData.put("sentence",firstSenList);
        firstData.put("part",spnPart);
        dataList.add(firstData);

        wordJson = intent.getStringExtra("wordJson");
        type = intent.getIntExtra("type",-1);
        Log.d("sj", "wordJson:"+wordJson);
        if(wordJson!=null){ //????????????
            actionBar.setTitle(R.string.editWordCard);
            word = gson.fromJson(wordJson,Word.class);
            meanList = word.meanList;

            Integer fami = Arrays.stream(famiKeyList).filter(key -> key == word.fami).boxed().toArray( Integer[]::new )[0];
            switch (fami){
                case 3:
                    spnFami.setSelection(0);
                    break;
                case 2:
                    spnFami.setSelection(1);
                    break;
                default:
                    spnFami.setSelection(2);
            }
            edtWord.setText(word.word);
            if(type>-1){
                spnType.setSelection(type-1);
                Mean mean = meanList.get(0);
                if(type==VocabType.WORD.getKey()) spnPart.setSelection(mean.part-1); //????????????
                else spnPart.setSelection(0);
                edtMean.setText(mean.mean);
                edtSentence.setText(mean.sentenceList.get(0).eng);
                IntStream.range(1,mean.sentenceList.size()).forEach(i -> {
                    createNewSen(cvSection, firstSenList, mean.sentenceList.get(i).eng);
                });
                IntStream.range(1,meanList.size()).forEach(i -> {
                    createNewMean(meanList.get(i));
                });
            }else{
                spnType.setSelection(0);
            }
        }else{ //????????????
            actionBar.setTitle(R.string.create);
            word = new Word();
            spnFami.setSelection(2);
        }

        //????????????????????????(??????/??????)
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) edtMean.getLayoutParams();
                if(i>0){ //??????
                    params.setMarginStart(0);
                    dataList.forEach(hashMap->{
                        Spinner p = (Spinner) hashMap.get("part");
                        p.setVisibility(View.GONE);
                        EditText m = (EditText) hashMap.get("mean");
                        m.setLayoutParams(params);
                    });
                }else{ //??????
                    params.setMarginStart((int) res.getDimension(R.dimen.comSpacing));
                    dataList.forEach(hashMap->{
                        Spinner p = (Spinner) hashMap.get("part");
                        p.setVisibility(View.VISIBLE);
                        EditText m = (EditText) hashMap.get("mean");
                        m.setLayoutParams(params);
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("type", type);
        data.putExtra("wordJson", wordJson);
        setResult(RESULT_OK, data);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbo.destroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                if(!"".equals((edtWord.getText().toString().trim()))){
                    word.meanListInit();
                    word.fami = famiKeyList[spnFami.getSelectedItemPosition()];
                    word.word = edtWord.getText().toString();

                    dataList.forEach(hashMap->{
                        Mean mean = new Mean();
                        Spinner p = (Spinner) hashMap.get("part");
                        mean.part = partKeyList[p.getSelectedItemPosition()];
                        EditText m = (EditText) hashMap.get("mean");
                        mean.mean = m.getText().toString();

                        ArrayList<EditText> senList = (ArrayList<EditText>) hashMap.get("sentence");
                        mean.sentenceListInit();
                        senList.forEach(editText -> {
                            Sentence sentence = new Sentence();
                            sentence.eng = editText.getText().toString();

                            mean.sentenceList.add(sentence);
                        });
                        //Log.d("sj","mean:" + gson.toJson(mean)); //{"mean":"","part":1,"sentenceList":[{"eng":""}]}
                        word.meanList.add(mean);
                    });

                    String json = gson.toJson(word);
                    Log.d("sj","obj->json:" + json);

                    ContentValues values = new ContentValues();
                    type = spnType.getSelectedItemPosition()+1;
                    values.put("type", type);
                    values.put("data", json);
                    if(wordJson==null) {
                        values.put("createTime", System.currentTimeMillis());
                        word.id = dbo.insert(values);
                    } else dbo.update(values, word.id);
                    wordJson = json;
                }else{
                    Snackbar.make(viewPos, res.getString(R.string.alertMsg), Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;

            case R.id.ivAdd:
                createNewMean();
                break;

            case R.id.ivAddSen:
                createNewSen(cvSection, firstSenList);
                break;

        }
    }

    @Override
    public void onSuccess(DBOperation.Operation operation) {
        Snackbar.make(viewPos, res.getString(R.string.saveOK), Snackbar.LENGTH_SHORT)
                //.setAction("??????", null)
                .setBackgroundTint(ContextCompat.getColor(this,R.color.successGreen))
                .show();
    }

    @Override
    public void onError(DBOperation.Operation operation) {
        Snackbar.make(viewPos, res.getString(R.string.saveNG), Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(this,R.color.failureRed))
                .show();
    }

    /**
     * ????????????????????????
     * @return ???HashMap?????????????????????????????????
     */
    private HashMap createNewMean() {
        HashMap hashMap = new HashMap();

        //??????cardview???????????????
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
        //???????????????Linear
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        wrapper.setOrientation(LinearLayout.VERTICAL);
        hashMap.put("wrapper",wrapper);
        //?????????
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
        HashMap<String,Object> dataMap = new HashMap();
        dataMap.put("part",spinner);
        hashMap.put("part",spinner);
        //
        EditText et = new EditText(this);
        LinearLayout.LayoutParams selfParam = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
        selfParam.weight=1;
        if(spnType.getSelectedItemPosition()==0) { //??????
            selfParam.setMarginStart((int) res.getDimension(R.dimen.comSpacing));
        } else { //??????
            spinner.setVisibility(View.GONE);
        }
        et.setLayoutParams(selfParam);
        et.setTextSize(TypedValue.COMPLEX_UNIT_PX,res.getDimension(R.dimen.contentTextSize));
        et.setHint(res.getString(R.string.mean));
        et.setHintTextColor(ContextCompat.getColor(this,R.color.lightGray));
        dataMap.put("mean",et);
        hashMap.put("mean",et);

        linearLayout.addView(spinner);
        linearLayout.addView(et);

        //?????????
        LinearLayout linearLayout2 = new LinearLayout(this);;
        LinearLayout.LayoutParams linear2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        linear2.setMargins(0, (int) res.getDimension(R.dimen.comSpacing),0,0);
        linearLayout2.setLayoutParams(linear2);
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);

        EditText editText = new EditText(this);
        LinearLayout.LayoutParams edtParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
        edtParams.weight=1;
        editText.setLayoutParams(edtParams);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,res.getDimension(R.dimen.contentTextSize));
        editText.setHint(res.getString(R.string.example));
        editText.setHintTextColor(ContextCompat.getColor(this,R.color.lightGray));
        ArrayList<EditText> edtList = new ArrayList<>();
        edtList.add(editText);
        dataMap.put("sentence",edtList);
        hashMap.put("sentence",edtList);
        dataList.add(dataMap);

        //
        ImageView ivAddSen = new ImageView(this);
        LinearLayout.LayoutParams par2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        par2.gravity=Gravity.CENTER_VERTICAL;
        ivAddSen.setLayoutParams(par2);
        ivAddSen.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_round_add_24));
        ivAddSen.setClickable(true);

        linearLayout2.addView(editText);
        linearLayout2.addView(ivAddSen);

        wrapper.addView(linearLayout);
        wrapper.addView(linearLayout2);
        cv.addView(wrapper);
        frameLayout.addView(cv);

        //cv????????? ????????????
        ImageView ivRemove = new ImageView(this);
        FrameLayout.LayoutParams ivParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        ivParams.gravity= Gravity.TOP | Gravity.END;
        ivRemove.setLayoutParams(ivParams);
        ivRemove.setElevation((int) Utils.convertDpToPx(this,5));
        ivRemove.setBackground(ContextCompat.getDrawable(this,R.drawable.selector_iv_remove));
        ivRemove.setClickable(true);
        frameLayout.addView(ivRemove);
        cvArea.addView(frameLayout);

        ivAddSen.setOnClickListener(v->{
            createNewSen(wrapper,edtList);
        });
        ivRemove.setOnClickListener(v->{ //cardview???????????????
            dataList.remove(dataMap);
            cvArea.removeView(frameLayout);
        });
        sv.post(() -> {
            sv.fullScroll(ScrollView.FOCUS_DOWN); //??????????????????
        });

        return hashMap;
    }
    /**
     * ????????????????????????
     * @param mean
     */
    private void createNewMean(Mean mean) {
        HashMap hashMap = createNewMean();
        Spinner spn = (Spinner) hashMap.get("part");
        spn.setSelection(mean.part-1);
        EditText edtMean = (EditText) hashMap.get("mean");
        edtMean.setText(mean.mean);
        LinearLayout linearLayout = (LinearLayout) hashMap.get("wrapper");
        ArrayList<EditText> edtList = (ArrayList<EditText>) hashMap.get("sentence");
        IntStream.range(0,mean.sentenceList.size()).forEach(i->{
            if(i>0) createNewSen(linearLayout,edtList);
            edtList.get(i).setText(mean.sentenceList.get(i).eng);
        });
    }
    /**
     * ????????????
     * @param parentLayout CardView??? ???????????????layout
     * @param arrayList ????????????EditText???arrayList
     * @return ??????EditText??????
     */
    private EditText createNewSen(LinearLayout parentLayout, ArrayList<EditText> arrayList){
        LinearLayout newSection = new LinearLayout(this);
        LinearLayout.LayoutParams newSectionParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        newSectionParams.setMargins(0, (int) res.getDimension(R.dimen.comSpacing),0,0);
        newSection.setLayoutParams(newSectionParams);
        newSection.setOrientation(LinearLayout.HORIZONTAL);

        EditText edtSen = new EditText(this);
        LinearLayout.LayoutParams edtSenParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
        edtSenParams.weight=1;
        edtSen.setLayoutParams(edtSenParams);
        edtSen.setTextSize(TypedValue.COMPLEX_UNIT_PX,res.getDimension(R.dimen.contentTextSize));
        edtSen.setHint(res.getString(R.string.example));
        edtSen.setHintTextColor(ContextCompat.getColor(this,R.color.lightGray));
        arrayList.add(edtSen);

        ImageView ivMinusSen = new ImageView(this);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        par.gravity=Gravity.CENTER_VERTICAL;
        ivMinusSen.setLayoutParams(par);
        ivMinusSen.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_round_remove_24));
        ivMinusSen.setClickable(true);

        newSection.addView(edtSen);
        newSection.addView(ivMinusSen);
        parentLayout.addView(newSection);

        ivMinusSen.setOnClickListener(v->{
            arrayList.remove(edtSen);
            parentLayout.removeView(newSection);
        });

        return edtSen;
    }
    /**
     * ????????????
     * @param parentLayout CardView??? ???????????????layout
     * @param arrayList ????????????EditText???arrayList
     */
    private void createNewSen(LinearLayout parentLayout, ArrayList<EditText> arrayList, String sen){
        EditText edt = createNewSen(parentLayout, arrayList);
        edt.setText(sen);
    }
}