package com.dh.exam.mpt.avtivity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.entity.Feedback;
import com.dh.exam.mpt.entity.MPTUser;


import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private EditText et_contact;
    private EditText et_content;
    private Button commit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        init();
    }

    public void init(){
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){//设置导航按钮，打开滑动菜单
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        et_contact=findViewById(R.id.et_feedback_contact);
        et_content=findViewById(R.id.et_feedback_content);
        commit_btn=findViewById(R.id.btn_commit_feedback);
        commit_btn.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_commit_feedback:
            String contact=et_contact.getText().toString().trim();
            String content=et_content.getText().toString().trim();
            saveFeedback(contact,content);
                break;
                default:
        }
    }

    /**
     * 上传反馈信息到后台数据库
     * @param contact
     * @param content
     */
    private void saveFeedback(String contact,String content){
        MPTUser currentUser= BmobUser.getCurrentUser(MPTUser.class);
        if (TextUtils.isEmpty(contact)) {//输入合法性检测,简单处理
            Toast.makeText(this, "联系方式不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(content)) {//输入合法性检测,简单处理
            Toast.makeText(this, "反馈信息不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentUser==null){
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Feedback feedback = new Feedback();
        feedback.setContact(contact);
        feedback.setContent(content);
        feedback.setUser(currentUser);
        feedback.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(FeedbackActivity.this, "感谢您的反馈！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(FeedbackActivity.this,
                            "上传反馈信息失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
