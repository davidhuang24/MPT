package com.dh.exam.mpt.activity;

import android.app.ProgressDialog;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.InputLeagalCheck;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener{


    private MyCountTimer timer;

    private EditText et_phone;
    private EditText et_code;
    private EditText et_new_pwd;
    private Button btn_reset;
    private TextView tv_request_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        init();
    }

    public void init(){
        et_phone=(EditText) findViewById(R.id.et_phone);
        et_code=(EditText) findViewById(R.id.et_code);
        et_new_pwd=(EditText) findViewById(R.id.et_new_pwd);
        tv_request_code=(TextView) findViewById(R.id.tv_request_code);
        btn_reset=(Button) findViewById(R.id.btn_reset_pwd);
        tv_request_code.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_request_code:
                requestSMSCode();
                break;
            case R.id.btn_reset_pwd:
                resetPasswordByPhoneNum();
                break;
            default:
        }
    }

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            tv_request_code.setText((millisUntilFinished / 1000) +"秒后重发");
        }
        @Override
        public void onFinish() {
            tv_request_code.setText("重新发送验证码");
        }
    }

    /**
     *请求验证码
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
                        Toast.makeText(ResetPasswordActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ResetPasswordActivity.this,
                                "验证码发送失败：code="+ex.getErrorCode()+"，错误描述："+ex.getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                        timer.cancel();
                    }
                }
            });
        } else {
            Toast.makeText(this, "请输入正确格式的手机号", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 重置密码
     */
    private void resetPasswordByPhoneNum() {
        final String code = et_code.getText().toString().trim();
        final String newPwd = et_new_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!InputLeagalCheck.isPassword(newPwd)) {
            Toast.makeText(this, "请输入6-16位字母或者数字，但不能是纯数字或纯密码", Toast.LENGTH_LONG).show();
            return;
        }
        final ProgressDialog progress = new ProgressDialog(ResetPasswordActivity.this);
        progress.setMessage("正在重置密码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobUser.resetPasswordBySMSCode(code, newPwd, new UpdateListener() {
            @Override
            public void done(BmobException ex) {
                progress.dismiss();
                if(ex==null){
                    Toast.makeText(ResetPasswordActivity.this,
                            "密码重置成功", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(ResetPasswordActivity.this,
                            "密码重置失败：code="+ex.getErrorCode()+"，错误描述："+ex.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
    }
}
