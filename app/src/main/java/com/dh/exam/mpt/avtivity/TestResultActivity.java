package com.dh.exam.mpt.avtivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.ACache;
import com.dh.exam.mpt.Utils.NetworkUtil;
import com.dh.exam.mpt.entity.MPTUser;
import com.dh.exam.mpt.entity.Paper;
import com.dh.exam.mpt.entity.Question;
import com.dh.exam.mpt.entity.Result;
import com.dh.exam.mpt.entity.ScoreRecord;
import com.dh.exam.mpt.entity.TestResultAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class TestResultActivity extends BaseActivity {

    private String paperObjectId;
    private ACache mCache;
    private int questionCount=0;
    private int correctCount=0;
    private List<Question> questionList=new ArrayList<>();
    private List<Result> resultList=new ArrayList<>();
    private TestResultAdapter adapter;
    private TextView tv_candidate;
    private TextView tv_score;
    private MPTUser currentUser;
    private Paper currentPaper;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        mCache=ACache.get(this);
        init();
    }

    private void init(){
        paperObjectId=getIntent().getStringExtra("param1");
        tv_candidate=findViewById(R.id.tv_candidate);
        tv_score=findViewById(R.id.tv_score);

        getQuestionData();

        RecyclerView recyclerView= findViewById(R.id.grid_recycler_view);
        GridLayoutManager layoutManager=new GridLayoutManager(TestResultActivity.this,4);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new TestResultAdapter(resultList,questionList);
        recyclerView.setAdapter(adapter);


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
        boolean isCacheExisted=query.hasCachedResult(Question.class);
        if(isCacheExisted){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);//只从缓存获取数据
        }else {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);//只从网络获取数据，同时会在本地缓存数据
        }
        if(!isCacheExisted&&!NetworkUtil.isNetworkAvailable()){
            Toast.makeText(TestResultActivity.this, "网络不可用,请连接网络后返回重进", Toast.LENGTH_SHORT).show();
            return;
        }
        query.findObjects(new FindListener<Question>() {
            @Override
            public void done(List<Question> list, BmobException e) {
                if(e==null){
                    questionList.addAll(list);
                    questionCount=questionList.size();
                    judge();
                }else{
                    Toast.makeText(TestResultActivity.this,
                            "获取题目数据失败,错误码:"+e.getErrorCode()+",错误信息:"+e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 判卷:将从后台数据库获取的标准答案和从缓存获取的用户答案作对比,判卷结果存入一个List
     */
    private void judge(){
        for(Question question:questionList){
            currentUser= BmobUser.getCurrentUser(MPTUser.class);
            String answerKey=null;
            if(currentUser!=null){
                answerKey="user_answer" + currentUser.getObjectId() +
                        paperObjectId + question.getQuestionNum();
            }
            String userAnswerStr=readCache(answerKey);
            Result result=new Result();
            result.setQuestionNum(question.getQuestionNum());
            result.setUserAnswer(Integer.valueOf(userAnswerStr));
            if(question.getAnswer()==Integer.valueOf(userAnswerStr)&&userAnswerStr!=null
                    ||question.getAnswer()==0){//判对
                result.setResult(true);
                correctCount++;
            }else {//判错
                result.setResult(false);
            }
            resultList.add(result);
        }
        adapter.notifyDataSetChanged();//获取数据后更新UI，否则Adapter会无数据
        showTestResult();
        uploadScoreRecord();
    }

    @SuppressLint("SetTextI18n")
    private void showTestResult(){
        currentUser= BmobUser.getCurrentUser(MPTUser.class);
        if(currentUser!=null){
            tv_candidate.setText(getString(R.string.candidateTag)+currentUser.getUsername());
        }
        //百分制分数计算公式:DecimalFormat设置两位double小数,然后向上取整
        score=(int)Math.ceil(Double.valueOf(
                new DecimalFormat("0.00").format(
                        100*correctCount/(double)questionCount)));
        tv_score.setText("分数: "+score);
    }

    private void uploadScoreRecord(){
        BmobQuery<Paper> query= new BmobQuery<>();
        query.getObject(paperObjectId, new QueryListener<Paper>() {
            @Override
            public void done(Paper paper, BmobException e) {
                if (e == null) {
                    currentPaper=paper;//获取当前paper
                    currentUser = BmobUser.getCurrentUser(MPTUser.class);
                    ScoreRecord scoreRecord=new ScoreRecord();
                    scoreRecord.setScore(score);
                    scoreRecord.setPaper(currentPaper);
                    scoreRecord.setUser(currentUser);
                    scoreRecord.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Toast.makeText(TestResultActivity.this, "上传得分记录成功",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(TestResultActivity.this,
                                        "上传得分记录失败，错误信息："+e.getMessage()+"，错误码："
                                                +e.getErrorCode(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(TestResultActivity.this,
                            "查询Paper("+paperObjectId+")失败,错误信息： "+ e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCache.clear();
    }
}
