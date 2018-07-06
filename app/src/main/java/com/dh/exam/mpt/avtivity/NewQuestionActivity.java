package com.dh.exam.mpt.avtivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.ACache;
import com.dh.exam.mpt.entity.Paper;
import com.dh.exam.mpt.entity.Question;
import com.google.gson.Gson;


import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;


public class NewQuestionActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private static final String TAG = "NewQuestionActivity";
    private String paperObjectId;
    private EditText et_title;
    private EditText et_option_a;
    private EditText et_option_b;
    private EditText et_option_c;
    private EditText et_option_d;
    private EditText et_analysis;
    private Button btn_pre;
    private Button btn_next;
    private Button btn_commit;
    private CheckBox cb_a;
    private CheckBox cb_b;
    private CheckBox cb_c;
    private CheckBox cb_d;
    private boolean[] answer={false,false,false,false};
    private int answerInt=0;
    private int questionNum=0;//题号

    private ACache mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        mCache=ACache.get(this);
        init();
    }

    public void init(){


        paperObjectId=getIntent().getStringExtra("param1");
        Toast.makeText(this, paperObjectId, Toast.LENGTH_SHORT).show();
        et_title=(EditText) findViewById(R.id.et_question_title);
        et_option_a=(EditText) findViewById(R.id.et_option_a);
        et_option_b=(EditText) findViewById(R.id.et_option_b);
        et_option_c=(EditText) findViewById(R.id.et_option_c);
        et_option_d=(EditText) findViewById(R.id.et_option_d);
        et_analysis=(EditText) findViewById(R.id.et_question_analysis);
        btn_pre=(Button) findViewById(R.id.btn_pre_que);
        btn_next=(Button) findViewById(R.id.btn_next_que);
        btn_commit=(Button) findViewById(R.id.btn_commit);
        cb_a=(CheckBox) findViewById(R.id.cb_option_a);
        cb_b=(CheckBox) findViewById(R.id.cb_option_b);
        cb_c=(CheckBox) findViewById(R.id.cb_option_c);
        cb_d=(CheckBox) findViewById(R.id.cb_option_d);
        btn_pre.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
        cb_a.setOnCheckedChangeListener(this);
        cb_b.setOnCheckedChangeListener(this);
        cb_c.setOnCheckedChangeListener(this);
        cb_d.setOnCheckedChangeListener(this);
    }

    /**
     * 读取一条指定的Cache
     *
     * @param questionCacheKey key
     * @return Json String
     */
    public String readCache(String questionCacheKey){
        return mCache.getAsString(questionCacheKey);
    }

    /**
     * 清除一条指定的Cache
     *
     * @param questionCacheKey key
     */
    public void clearCache(String questionCacheKey){
        mCache.remove(questionCacheKey);
    }

    /**
     *将question对象先存到Json里然后存储到一条Cache里
     */
    public void questionSave2Json2Cache(){
        final String title=et_title.getText().toString().trim();
        final String optionA=et_option_a.getText().toString().trim();
        final String optionB=et_option_b.getText().toString().trim();
        final String optionC=et_option_c.getText().toString().trim();
        final String optionD=et_option_d.getText().toString().trim();
        final String analysis=et_analysis.getText().toString().trim();
        String options[]={optionA,optionB,optionC,optionD};

        answerInt=getAnswer();
        if (TextUtils.isEmpty(title)||TextUtils.isEmpty(optionA)||TextUtils.isEmpty(optionB)||
                TextUtils.isEmpty(optionC)||TextUtils.isEmpty(optionD)||answerInt==0) {
            Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<Paper> query= new BmobQuery<>();
        query.getObject(paperObjectId, new QueryListener<Paper>() {
            @Override
            public void done(Paper paper, BmobException e) {
                if(e==null){
                    Question question=new Question();
                    question.setTitle(title);
                    question.setOptions(options);
                    question.setAnswer(answerInt);
                    question.setAnalysis(analysis);
                    question.setQuestionNum(questionNum);
                    question.setPaper(paper);//最后提交时要更新后台paper的questionCount字段
                    Gson gson=new Gson();
                    String questionJsonStr=gson.toJson(question);//先存到Json里
                    String questionJsonKey=paperObjectId+questionNum;
                    mCache.put(questionJsonKey,questionJsonStr);//存储到Cache里
                }else{
                    Toast.makeText(NewQuestionActivity.this,
                            "查询Paper("+paperObjectId+")失败,错误信息： "+
                                    e.getMessage()+"错误码： "+e.getErrorCode(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_pre_que:
                preQuestion();
                break;
            case R.id.btn_next_que:
                nextQuestion();
                break;
            case R.id.btn_commit:
                commitQuestions();
                break;
                default:
        }
    }

    /**
     * 显示当前题目的内容
     */
    public void showQuestionFromCache(){
        String questionJsonKey=paperObjectId+questionNum;
        String jsonData=readCache(questionJsonKey);
        if(jsonData!=null){
            Gson gson=new Gson();
            Question question=gson.fromJson(jsonData,Question.class);
            if(question!=null){
                if(question.getTitle()!=null) et_title.setText(question.getTitle());
                if(question.getAnalysis()!=null) et_analysis.setText(question.getAnalysis());
                if(question.getOptions()[0]!=null) et_option_a.setText(question.getOptions()[0]);
                if(question.getOptions()[1]!=null) et_option_b.setText(question.getOptions()[1]);
                if(question.getOptions()[2]!=null) et_option_c.setText(question.getOptions()[2]);
                if(question.getOptions()[3]!=null) et_option_d.setText(question.getOptions()[3]);
                setCheckedAccordAnswer(question.getAnswer());

            }
        }
    }

    /**
     * 根据answer设置四个CheckBox状态
     *
     * @param currentAnswer answer
     */
    public  void setCheckedAccordAnswer(int currentAnswer){
        switch (currentAnswer){
            case 1:
                setCheckBoxes(false,false,false,true);
                break;
            case 2:
                setCheckBoxes(false,false,true,false);
                break;
            case 3:
                setCheckBoxes(false,false,true,true);
                break;
            case 4:
                setCheckBoxes(false,true,false,false);
                break;
            case 5:
                setCheckBoxes(false,true,false,true);
                break;
            case 6:
                setCheckBoxes(false,true,true,false);
                break;
            case 7:
                setCheckBoxes(false,true,true,true);
                break;
            case 8:
                setCheckBoxes(true,false,false,false);
                break;
            case 9:
                setCheckBoxes(true,false,false,true);
                break;
            case 10:
                setCheckBoxes(true,false,true,false);
                break;
            case 11:
                setCheckBoxes(true,false,true,true);
                break;
            case 12:
                setCheckBoxes(true,true,false,false);
                break;
            case 13:
                setCheckBoxes(true,true,false,true);
                break;
            case 14:
                setCheckBoxes(true,true,true,false);
                break;
            case 15:
                setCheckBoxes(true,true,true,true);
                break;
                default:
                    setCheckBoxes(false,false,false,false);
        }
    }

    public  void setCheckBoxes(boolean a,boolean b ,boolean c,boolean d){
        cb_a.setChecked(a);
        cb_b.setChecked(b);
        cb_c.setChecked(c);
        cb_d.setChecked(d);
    }

    /**
     * 缓存当前题，编辑上一题
     */
    public void preQuestion(){//查询修改缓存
        questionSave2Json2Cache();

        questionNum--;
        clearEditArea();
        showQuestionFromCache();
    }

    /**
     * 缓存当前题，添加下一题
     */
    public void nextQuestion(){
        questionSave2Json2Cache();

        questionNum++;
        clearEditArea();
        showQuestionFromCache();
    }

    public void commitQuestions(){//更新paper题目数，上传数据库

    }

    public void clearEditArea(){
        if(et_title.getText()!=null) et_title.getText().clear();
        if(et_option_a.getText()!=null) et_option_a.getText().clear();
        if(et_option_b.getText()!=null)et_option_b.getText().clear();
        if(et_option_c.getText()!=null)et_option_c.getText().clear();
        if(et_option_d.getText()!=null)et_option_d.getText().clear();
        if(et_analysis.getText()!=null)et_analysis.getText().clear();
        if(cb_a.isChecked())cb_a.setChecked(false);
        if(cb_b.isChecked())cb_b.setChecked(false);
        if(cb_c.isChecked())cb_c.setChecked(false);
        if(cb_d.isChecked())cb_d.setChecked(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.cb_option_a:
                answer[0]=isChecked;
                break;
            case R.id.cb_option_b:
                answer[1]=isChecked;
                break;
            case R.id.cb_option_c:
                answer[2]=isChecked;
                break;
            case R.id.cb_option_d:
                answer[3]=isChecked;
                break;
                default:
        }
    }

    /**
     *更新answerInt，为了方便处理，将answer转为int类型
     */
    public int getAnswer(){
        return booleanArrayToInt(answer);
    }

    /**
     * booleanArrayToInt
     *
     * @param input 输入的数组
     * @return 输出的int
     */
    public int booleanArrayToInt(boolean []input){
        int output=0;
        if(input[0]){
            output+=8;
        }
        if(input[1]){
            output+=4;
        }
        if(input[2]){
            output+=2;
        }
        if(input[3]){
            output+=1;
        }
        return output;
    }
}
