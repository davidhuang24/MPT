package com.dh.exam.mpt.avtivity;

import android.app.ProgressDialog;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.exam.mpt.entity.MPTUser;
import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.InputLeagalCheck;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 *帐号密码登录
 *
 *@author DavidHuang  at 下午5:12 18-6-14
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final int ACCOUNT_LOGIN=0;
    private static final int PHONE_LOGIN=1;
    private int loginMode;
    private EditText et_account;
    private EditText et_password;
    private EditText et_phone;
    private EditText et_code;
    private TextView tv_forget_pwd;
    private TextView tv_request_code;
    private TextView tv_login_mode_switch;
    private TextView tv_register;
    private Button btn_login;
    private LinearLayout account_login_layout;
    private LinearLayout phone_login_layout;

    private MyCountTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    public void init(){
        et_account=(EditText) findViewById(R.id.et_account);
        et_password=(EditText) findViewById(R.id.et_password);
        et_phone=(EditText) findViewById(R.id.et_phone);
        et_code=(EditText) findViewById(R.id.et_code);
        tv_forget_pwd=(TextView ) findViewById(R.id.tv_forget_pwd);
        tv_request_code=(TextView ) findViewById(R.id.tv_request_code);
        tv_login_mode_switch=(TextView ) findViewById(R.id.tv_login_mode_switch);
        tv_register=(TextView ) findViewById(R.id.tv_register);
        btn_login=(Button) findViewById(R.id.btn_login);
        account_login_layout=(LinearLayout)findViewById(R.id.layout_account_login);
        phone_login_layout=(LinearLayout) findViewById(R.id.layout_phone_login);
        tv_forget_pwd.setOnClickListener(this);
        tv_request_code.setOnClickListener(this);
        tv_login_mode_switch.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        loginMode=ACCOUNT_LOGIN;
        setLoginLayout();
    }
    /**
     * 设置登陆布局
     */
    private void setLoginLayout(){
        if(loginMode==ACCOUNT_LOGIN){
            account_login_layout.setVisibility(View.VISIBLE);
            phone_login_layout.setVisibility(View.GONE);
        }else if(loginMode==PHONE_LOGIN){
            account_login_layout.setVisibility(View.GONE);
            phone_login_layout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_forget_pwd://忘记密码
                ResetPasswordActivity.activityStart(LoginActivity.this,ResetPasswordActivity.class,null,null,null);
                break;
            case R.id.tv_request_code://请求验证码
                requestSMSCode();
                break;
            case R.id.tv_login_mode_switch://切换登陆模式
                switchLoginMode();
                break;
            case R.id.tv_register://注册
                RegisterActivity.activityStart(LoginActivity.this,RegisterActivity.class,null,null,null);
                break;
            case R.id.btn_login://登陆
                if(loginMode==ACCOUNT_LOGIN){
                    loginByAccount();
                }else if(loginMode==PHONE_LOGIN){
                    loginOrRegisterByphone();
                }
                break;
            default:
        }
    }
    /**
     * 切换登陆模式：账户登陆或者手机号登陆
     */
    public void switchLoginMode(){
        if(loginMode==ACCOUNT_LOGIN){
            loginMode=PHONE_LOGIN;
            tv_login_mode_switch.setText(getResources().getString(R.string.account_login));
        }else if(loginMode==PHONE_LOGIN){
            loginMode=ACCOUNT_LOGIN;
            tv_login_mode_switch.setText(getResources().getString(R.string.phone_one_key_login));
        }
        setLoginLayout();
    }

    /**
     * 账户登陆：账户可以是userName,PhoneNumber
     */
    public void loginByAccount(){
        final String account = et_account.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {//输入合法性检测,简单处理
            Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
        progress.setMessage("正在登录中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //V3.3.9提供的新的登录方式，可传用户名/邮箱/手机号码
        BmobUser.loginByAccount(account, password, new LogInListener<MPTUser>() {

            @Override
            public void done(MPTUser user, BmobException ex) {
                // TODO Auto-generated method stub
                progress.dismiss();
                MPTUser currentUser = BmobUser.getCurrentUser(MPTUser.class);
                if(ex==null){//登陆成功
                    if(account.equals(currentUser.getMobilePhoneNumber())&&!currentUser.getMobilePhoneNumberVerified()){//解决“手机号未验证却可以登入”的问题
                        clearPhoneNumberOfUser(currentUser.getObjectId());
                        BmobUser.logOut();
                        Toast.makeText(LoginActivity.this, "您的手机号未验证，请重新绑定手机号", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MainActivity.activityStart(LoginActivity.this,MainActivity.class,"","",null);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,
                            "登录失败：code="+ex.getErrorCode()+"，错误描述："+ex.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 手机号一键注册登陆：第一次一键先注册后自动登陆，以后手机号登陆
     */
    public void loginOrRegisterByphone(){
        final String phone = et_phone.getText().toString().trim();
        final String code = et_code.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
        progress.setMessage("验证中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        // V3.3.9提供的一键注册或登录方式，可传手机号码和验证码
        BmobUser.signOrLoginByMobilePhone(phone, code, new LogInListener<MPTUser>() {

            @Override
            public void done(MPTUser user, BmobException ex) {
                // TODO Auto-generated method stub
                progress.dismiss();
                if(ex==null){
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    MainActivity.activityStart(LoginActivity.this,MainActivity.class,"","",null);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,
                            "登录失败：code="+ex.getErrorCode()+"，错误描述："+ex.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     *清除指定记录的mobilePhoneNumber字段
     */
    void clearPhoneNumberOfUser(String userId){
        MPTUser user=new MPTUser();
        user.setObjectId(userId);
        user.remove("mobilePhoneNumber");
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException ex) {
                if(ex!=null){
                    Toast.makeText(LoginActivity.this, "清除当前用户的手机号失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     *请求验证码计时器
     */
    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            tv_request_code.setText((millisUntilFinished / 1000) +"s后重发");
        }
        @Override
        public void onFinish() {
            tv_request_code.setText("重新发送验证码");
        }
    }


    /**
     *请求服务器发送验证码
     */
    private void requestSMSCode() {
        String phoneNum = et_phone.getText().toString().trim();
        if (InputLeagalCheck.isPhoneNum(phoneNum)) {
            timer = new MyCountTimer(60000, 1000);
            timer.start();
            BmobSMS.requestSMSCode(phoneNum,getResources().getString(R.string.SMS_template), new QueryListener<Integer>(){

                @Override
                public void done(Integer smsId, BmobException ex) {
                    if(ex==null){
                        Toast.makeText(LoginActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                    }else{
                        timer.cancel();
                        Toast.makeText(LoginActivity.this, "验证码发送失败：code="+ex.getErrorCode()+"，错误描述："+ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            Toast.makeText(this, "请输入正确格式的手机号", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
    }
}
