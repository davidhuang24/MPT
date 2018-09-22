package com.dh.exam.mpt.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.entity.Paper;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class PaperInfoActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView tv_paper_name;
    private TextView tv_paper_kind;
    private TextView tv_paper_author;
    private TextView tv_question_count;
    private Button begin_test;
    private String paperObjectId;
    private Paper currentPaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_info);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init(){
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){//设置导航按钮，打开滑动菜单
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tv_paper_name=(TextView) findViewById(R.id.tv_paper_name);
        tv_paper_kind=(TextView) findViewById(R.id.tv_paper_kind);
        tv_paper_author=(TextView) findViewById(R.id.tv_paper_author);
        tv_question_count=(TextView) findViewById(R.id.tv_question_count);
        begin_test=(Button) findViewById(R.id.btn_begin_test);
        begin_test.setOnClickListener(this);

        paperObjectId=getIntent().getStringExtra("param1");
        BmobQuery<Paper> query= new BmobQuery<>();
        query.include("paperAuthor");
        query.getObject(paperObjectId, new QueryListener<Paper>() {
            @Override
            public void done(Paper paper, BmobException e) {
                if (e == null) {
                    currentPaper=paper;//获取当前paper
                    tv_paper_name.setText(currentPaper.getPaperName());
                    tv_paper_kind.setText("试卷类型: "+currentPaper.getPaperKind());
                    tv_paper_author.setText("作者: "+currentPaper.getPaperAuthor().getUsername());
                    tv_question_count.setText("题目数: "+currentPaper.getQuestionCount());
                }else{
                    Toast.makeText(PaperInfoActivity.this,
                            "查询Paper("+paperObjectId+")失败,错误信息： "+ e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_begin_test:
                if(currentPaper.getQuestionCount()!=0){
                    TestActivity.activityStart(PaperInfoActivity.this,TestActivity.class,
                            paperObjectId,null,null);
                }else {
                    Toast.makeText(this, getString(R.string.question_num_zero), Toast.LENGTH_SHORT).show();
                }
                break;
                default:
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//Toolbar上的Action按钮是菜单Item
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }
}
