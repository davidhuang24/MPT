package com.dh.exam.mpt.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.ACache;
import com.dh.exam.mpt.Utils.FirstThingListener;
import com.dh.exam.mpt.Utils.NetworkUtil;
import com.dh.exam.mpt.entity.MPTUser;
import com.dh.exam.mpt.entity.Paper;
import com.dh.exam.mpt.entity.Question;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;


public class NewQuestionActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private static final String TAG = "NewQuestionActivity";
    private Toolbar toolbar;
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

    private MPTUser currentUser;
    private Paper currentPaper;
    private ACache mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        mCache=ACache.get(this);
        init();
    }

    public void init(){
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){//设置导航按钮，打开滑动菜单
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        paperObjectId=getIntent().getStringExtra("param1");
        BmobQuery<Paper> query= new BmobQuery<>();
        query.getObject(paperObjectId, new QueryListener<Paper>() {
                    @Override
                    public void done(Paper paper, BmobException e) {
                        if (e == null) {
                            currentPaper=paper;//获取当前paper
                        }else{
                            Toast.makeText(NewQuestionActivity.this,
                                    "查询Paper("+paperObjectId+")失败！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        et_title= findViewById(R.id.et_question_title);
        et_option_a= findViewById(R.id.et_option_a);
        et_option_b= findViewById(R.id.et_option_b);
        et_option_c= findViewById(R.id.et_option_c);
        et_option_d= findViewById(R.id.et_option_d);
        et_analysis= findViewById(R.id.et_question_analysis);
        btn_pre= findViewById(R.id.btn_pre_que);
        btn_next= findViewById(R.id.btn_next_que);
        btn_commit= findViewById(R.id.btn_commit);
        cb_a= findViewById(R.id.cb_option_a);
        cb_b= findViewById(R.id.cb_option_b);
        cb_c= findViewById(R.id.cb_option_c);
        cb_d= findViewById(R.id.cb_option_d);
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
        mCache.clear();
    }

    /**
     *将question对象先存到Json里然后存储到一条Cache里
     */
    public void questionSave2Json2Cache(boolean isPreQuestion,FirstThingListener listener){
        final String title=et_title.getText().toString().trim();
        final String optionA=et_option_a.getText().toString().trim();
        final String optionB=et_option_b.getText().toString().trim();
        final String optionC=et_option_c.getText().toString().trim();
        final String optionD=et_option_d.getText().toString().trim();
        final String analysis=et_analysis.getText().toString().trim();
        String options[]={optionA,optionB,optionC,optionD};

        answerInt=getAnswer();
        if (TextUtils.isEmpty(title)&&TextUtils.isEmpty(optionA)&&TextUtils.isEmpty(optionB)&&
                TextUtils.isEmpty(optionC)&&TextUtils.isEmpty(optionD)&&TextUtils.isEmpty(analysis)
                &&answerInt==0) {
            if(isPreQuestion){//编辑区全空时，若是点击上一题，直接跳到上一题，不保存
                listener.done();
                return;
            }else{//编辑区全空时，若是点击下一题，不缓存，不跳到下一题
                Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        currentUser= BmobUser.getCurrentUser(MPTUser.class);
        Question question=new Question();
        question.setTitle(title);
        question.setOptions(options);
        question.setAnswer(answerInt);
        question.setAnalysis(analysis);
        question.setQuestionNum(questionNum);
        question.setPaper(currentPaper);//添加一对多关系，一张试卷多个题目
        Gson gson=new Gson();
        String questionJsonStr=gson.toJson(question);//先存到Json里
        if(currentUser!=null) {
            String questionJsonKey = "correct_answer" + currentUser.getObjectId() + paperObjectId + questionNum;
            mCache.put(questionJsonKey, questionJsonStr, 2 * ACache.TIME_DAY);//存储到Cache里
        }
        listener.done();
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
                if(!NetworkUtil.isNetworkAvailable()){
                    Toast.makeText(NewQuestionActivity.this, "网络不可用,请连接网络后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    commitQuestions();
                }
                break;
                default:
        }
    }

    /**
     * 显示当前题目的内容
     */
    public void showQuestionFromCache(){
        String questionJsonKey = null;
        if(currentUser!=null) {
            questionJsonKey = "correct_answer" + currentUser.getObjectId() + paperObjectId + questionNum;
        }

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
     * @param answer answer
     */
    public  void setCheckedAccordAnswer(int answer){
        switch (answer){
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
     * 先缓存当前题，后编辑上一题
     */
    public void preQuestion(){//查询修改缓存
        questionSave2Json2Cache(true,new FirstThingListener(){
            @Override
            public void done() {
                if(questionNum>=1){
                    questionNum--;
                    clearEditArea();
                    showQuestionFromCache();
                }else {
                    Toast.makeText(NewQuestionActivity.this, "没有上一题了", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {}

        });
    }

    /**
     * 先缓存当前题，后添加下一题
     */
    public void nextQuestion(){
        questionSave2Json2Cache(false,new FirstThingListener() {
            @Override
            public void done() {
                questionNum++;
                clearEditArea();
                showQuestionFromCache();
            }
            @Override
            public void onError(Exception e) {}
        });
    }

    /**
     * 先缓存当前题，后将题目缓存上传到数据库，更新paper题目数
     */
    public void commitQuestions(){
        questionSave2Json2Cache(false,new FirstThingListener() {//缓存当前题
            @Override
            public void done() {
                List<BmobObject> questions = new ArrayList<>();
                questionNum++;
                int i=0;
                //一条一条读缓存Cache->Json->question
                String questionJsonKey=null;
                if(currentUser!=null) {
                    questionJsonKey = "correct_answer" + currentUser.getObjectId() + paperObjectId +i;
                    String jsonData=readCache(questionJsonKey);
                    while (jsonData!=null){
                        Gson gson=new Gson();
                        Question question=gson.fromJson(jsonData,Question.class);
                        if(question!=null){
                            questions.add(question);
                        }
                        i++;
                        questionJsonKey="correct_answer" + currentUser.getObjectId() + paperObjectId +i;
                        jsonData=readCache(questionJsonKey);
                    }
                }

                int questionCount=i;
                //批量添加题目数据到数据库
                new BmobBatch().insertBatch(questions).doBatch(new QueryListListener<BatchResult>() {
                    @Override
                    public void done(List<BatchResult> list, BmobException e) {
                        if(e==null){
                            int count=questionCount;
                            for(int i=0;i<list.size();i++){
                                BatchResult result = list.get(i);
                                BmobException ex =result.getError();
                                if(ex==null){
                                }else{
                                    Toast.makeText(NewQuestionActivity.this,
                                            "第"+i+"个数据批量添加失败！", Toast.LENGTH_SHORT).show();
                                    count--;
                                }
                            }
                            //更新当前paper的题目数
                            currentPaper.setQuestionCount(count);
                            currentPaper.update(paperObjectId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Toast.makeText(NewQuestionActivity.this,
                                                "新增试卷成功", Toast.LENGTH_SHORT).show();
                                        MainActivity.activityStart(NewQuestionActivity.this,
                                                MainActivity.class,null,null,null);
                                        finish();
                                    }else{
                                        Toast.makeText(NewQuestionActivity.this,
                                                "questionCount更新失败！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(NewQuestionActivity.this,
                                    "批量添加题目数据失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onError(Exception e) {}
        });
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//Toolbar上的Action按钮是菜单Item
        switch(item.getItemId()){
            case android.R.id.home:
                showBackAlertDialog(R.string.new_question_back_alert_content);
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                showBackAlertDialog(R.string.new_question_back_alert_content);
                break;
                default:
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
