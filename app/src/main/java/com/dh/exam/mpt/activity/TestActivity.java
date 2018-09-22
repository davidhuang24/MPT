package com.dh.exam.mpt.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.ACache;
import com.dh.exam.mpt.Utils.FirstThingListener;
import com.dh.exam.mpt.Utils.NetworkUtil;
import com.dh.exam.mpt.entity.MPTUser;
import com.dh.exam.mpt.entity.Paper;
import com.dh.exam.mpt.entity.Question;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class TestActivity extends BaseActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    private String paperObjectId;
    private int questionCount;
    private int questionNum=0;
    private List<Question> questionList=new ArrayList<>();
    private ACache mCache;
    private MPTUser currentUser;

    private boolean[] answer={false,false,false,false};
    private int answerInt=0;
//    private int testResult[]={0,0};//对题数,错题数


    private Toolbar toolbar;
    private FloatingActionButton fab_commit;
    private TextView tv_question_num;
    private TextView tv_question_title;
    private TextView tv_option_a;
    private TextView tv_option_b;
    private TextView tv_option_c;
    private TextView tv_option_d;
    private CheckBox cb_a;
    private CheckBox cb_b;
    private CheckBox cb_c;
    private CheckBox cb_d;

    private GestureDetector mDetector;//滑动手势
    protected static final float FLIP_DISTANCE = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mCache=ACache.get(this);
        mCache.clear();
        init();
    }

    private void init(){
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){//设置导航按钮，打开滑动菜单
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Toast.makeText(this, "向左滑,下一题;向右滑,上一题", Toast.LENGTH_LONG).show();
        tv_question_title= findViewById(R.id.tv_question_title);
        tv_question_num= findViewById(R.id.tv_question_num);
        tv_option_a= findViewById(R.id.tv_optionA);
        tv_option_b= findViewById(R.id.tv_optionB);
        tv_option_c= findViewById(R.id.tv_optionC);
        tv_option_d= findViewById(R.id.tv_optionD);
        cb_a= findViewById(R.id.cb_option_a);
        cb_b= findViewById(R.id.cb_option_b);
        cb_c= findViewById(R.id.cb_option_c);
        cb_d= findViewById(R.id.cb_option_d);
        cb_a.setOnCheckedChangeListener(this);
        cb_b.setOnCheckedChangeListener(this);
        cb_c.setOnCheckedChangeListener(this);
        cb_d.setOnCheckedChangeListener(this);
        fab_commit= findViewById(R.id.fab);
        fab_commit.setOnClickListener(this);

        mDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // TODO Auto-generated method stub
                return false;
            }
            @Override
            public void onShowPress(MotionEvent e) {
                // TODO Auto-generated method stub

            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // TODO Auto-generated method stub
                return false;
            }
            @Override
            public void onLongPress(MotionEvent e) {
                // TODO Auto-generated method stub

            }
            /**
             * e1 The first down motion event that started the fling. e2 The
             * move motion event that triggered the current onFling.
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > FLIP_DISTANCE) {//左滑
                    Log.i("MYTAG", "向左滑...");
                    slideToLeft();
                    return true;
                }
                if (e2.getX() - e1.getX() > FLIP_DISTANCE) {//右滑
                    Log.i("MYTAG", "向右滑...");
                    slideToRight();
                    return true;
                }
                if (e1.getY() - e2.getY() > FLIP_DISTANCE) {//上滑
                    return true;
                }
                if (e2.getY() - e1.getY() > FLIP_DISTANCE) {//下滑
                    return true;
                }

                Log.d("TAG", e2.getX() + " " + e2.getY());

                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        getQuestionData();

    }

    /**
     * 关联查询,查询是paperObjectId的试卷的所有题目
     */
    private void getQuestionData(){
        paperObjectId=getIntent().getStringExtra("param1");
        BmobQuery<Question> query= new BmobQuery<>();
        query.include("paper");
        Paper paper=new Paper();
        paper.setObjectId(paperObjectId);
        query.addWhereEqualTo("paper",new BmobPointer(paper));
        if(!NetworkUtil.isNetworkAvailable()){
            Toast.makeText(TestActivity.this, "网络不可用,请连接网络后返回重进", Toast.LENGTH_SHORT).show();
            return;
        }
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);//只从网络获取数据，同时会在本地缓存数据
        query.findObjects(new FindListener<Question>() {
            @Override
            public void done(List<Question> list, BmobException e) {
                if(e==null){
                    toolbar.setTitle(list.get(0).getPaper().getPaperName());
                    questionList.addAll(list);
                    questionCount=questionList.size();
                    showQuestion(questionNum);
                }else{
                    Toast.makeText(TestActivity.this,
                            "获取题目数据失败,错误码:"+e.getErrorCode()+",错误信息:"+e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 显示指定题目
     * @param num 题号
     */
    @SuppressLint("SetTextI18n")
    private void showQuestion(int num){
        Question question=questionList.get(num);
        int i=question.getQuestionNum()+1;
        tv_question_num.setText(i+"/"+questionCount);
        tv_question_title.setText(question.getTitle());
        tv_option_a.setText(question.getOptions()[0]);
        tv_option_b.setText(question.getOptions()[1]);
        tv_option_c.setText(question.getOptions()[2]);
        tv_option_d.setText(question.getOptions()[3]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                fabAction();
                break;
            default:
        }
    }

    /**
     * 向左滑,下一题:先判题,再缓存用户答案,最后下一题
     */
    public void slideToLeft(){
        if(questionNum==questionCount-1){
            Toast.makeText(this, "没有下一题了", Toast.LENGTH_SHORT).show();
            return;
        }
        answerInt=getAnswer();
        saveAnswerToCache(new FirstThingListener() {
            @Override
            public void done() {
                clearCheckBox();
                showQuestion(++questionNum);
                showAnswerFromCache();
            }
            @Override
            public void onError(Exception e) {}
        });

    }

    /**
     * 向右滑,上一题:先缓存,后判题,最后上一题
     */
    public void slideToRight(){
        if(questionNum==0){
            Toast.makeText(this, "没有上一题了", Toast.LENGTH_SHORT).show();
            return;
        }
        answerInt=getAnswer();
        saveAnswerToCache(new FirstThingListener() {
            @Override
            public void done() {
                clearCheckBox();
                showQuestion(--questionNum);
                showAnswerFromCache();
            }
            @Override
            public void onError(Exception e) { }
        });
    }

    public void clearCheckBox(){
        if(cb_a.isChecked())cb_a.setChecked(false);
        if(cb_b.isChecked())cb_b.setChecked(false);
        if(cb_c.isChecked())cb_c.setChecked(false);
        if(cb_d.isChecked())cb_d.setChecked(false);
    }

    /**
     * 结束考试,提交:先缓存当前题答案,最后提交
     */
    private void fabAction(){
        answerInt=getAnswer();
        saveAnswerToCache(new FirstThingListener() {

            @Override
            public void done() {
                new AlertDialog.Builder(TestActivity.this)
                        .setIcon(R.drawable.commit_paper)
                        .setTitle("确认交卷")
                        .setMessage("您已答完全部题目,您可以选择确定交卷或者取消检查试卷")
                        .setNegativeButton("取消", (dialog, which) -> {})
                        .setPositiveButton("确定", (dialog, which) -> {
                            TestResultActivity.activityStart(TestActivity.this,TestResultActivity.class,
                                    paperObjectId,null,null);
                            finish();
                        })
                        .create().show();
            }

            @Override
            public void onError(Exception e) {

            }
        });
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
     *更新answerInt，为了方便处理，将boolean类型answer转为int类型
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
     * 将当前题的用户答案保存到缓存
     * @param listener 同步监听器,用于先写缓存后
     */
    public void saveAnswerToCache(FirstThingListener listener){
        currentUser= BmobUser.getCurrentUser(MPTUser.class);
        String answerKey=null;
        if(currentUser!=null){
            answerKey="user_answer" + currentUser.getObjectId() +
                    paperObjectId + questionNum;
        }
        mCache.put(answerKey,String.valueOf(answerInt),2*ACache.TIME_DAY);//存储到Cache里
        listener.done();
    }

    /**
     * 显示当前题目的用户答案
     */
    public void showAnswerFromCache(){
        currentUser= BmobUser.getCurrentUser(MPTUser.class);
        String answerKey=null;
        if(currentUser!=null){
            answerKey="user_answer" + currentUser.getObjectId() +
                    paperObjectId + questionNum;
        }
        String userAnswerStr=readCache(answerKey);
        if(userAnswerStr!=null){//有缓存显示,无缓存不显示用户答案
            int userAnswer=Integer.valueOf(userAnswerStr);
            setCheckedAccordAnswer(userAnswer);
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//Toolbar上的Action按钮是菜单Item
        switch(item.getItemId()){
            case android.R.id.home:
                showBackAlertDialog(R.string.test_back_alert_content);
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                showBackAlertDialog(R.string.test_back_alert_content);
                break;
            default:
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    /**
     * 不继续向下分发TouchEvent，解决mDetector被DrawerLayout手势屏蔽的问题
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mDetector.onTouchEvent(ev)){
            return mDetector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }


}
