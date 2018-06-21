package com.dh.exam.mpt.avtivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.InputLeagalCheck;
import com.dh.exam.mpt.entity.MPTUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private EditText et_username;
    private EditText et_password;
    private EditText et_repeat_pwd;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

    }

    public void init(){
        et_username=(EditText) findViewById(R.id.et_username);
        et_password=(EditText) findViewById(R.id.et_pwd);
        et_repeat_pwd=(EditText) findViewById(R.id.et_repeat_pwd);
        btn_register=(Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_register:
                registerUser();
                break;
            default:
        }
    }

    /**
     * 注册逻辑
     */
    private void registerUser(){
        String userName = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String pwd_repeated = et_repeat_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!InputLeagalCheck.isPassword(password)) {//输入合法性检测
            Toast.makeText(this, "请输入6-16位字母或者数字，但不能是纯数字或纯密码", Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(pwd_repeated)) {
            Toast.makeText(this, "两次密码不一样", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        final MPTUser user = new MPTUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.signUp(new SaveListener<MPTUser>() {
            @Override
            public void done(MPTUser user, BmobException ex) {
                progress.dismiss();
                if(ex==null){
                    Toast.makeText(RegisterActivity.this, "注册成功",
                            Toast.LENGTH_SHORT).show();
                    LoginActivity.activityStart(RegisterActivity.this,LoginActivity.class,null,null,null);
                    finish();
                }else{
                    Toast.makeText(RegisterActivity.this,
                            "注册失败: code="+ ex.getErrorCode()+" , 错误描述： "+ ex.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}
