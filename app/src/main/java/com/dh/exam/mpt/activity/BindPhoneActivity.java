package com.dh.exam.mpt.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.exam.mpt.MPTApplication;
import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.InputLeagalCheck;
import com.dh.exam.mpt.Utils.NetworkUtil;
import com.dh.exam.mpt.entity.MPTUser;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 *绑定手机号界面
 *
 *@author DavidHuang  at 下午3:41 18-5-31
 */
public class BindPhoneActivity extends BaseActivity implements View.OnClickListener{

    private MyCountTimer timer;
    private EditText et_phone;
    private EditText et_code;
    private TextView tv_request_code;
    private Button btn_bind;
    private String handleType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        init();
    }

    public void init(){
        et_phone=(EditText) findViewById(R.id.et_phone);
        et_code=(EditText) findViewById(R.id.et_code);
        tv_request_code=(TextView) findViewById(R.id.tv_request_code);
        btn_bind=(Button) findViewById(R.id.btn_bind_phone);
        tv_request_code.setOnClickListener(this);
        btn_bind.setOnClickListener(this);
        Intent intent=getIntent();
        handleType=intent.getStringExtra("param1");
        if("1".equals(handleType)){
            btn_bind.setText(getResources().getText(R.string.bind_phone));
        }else if("2".equals(handleType)){
            btn_bind.setText(getResources().getText(R.string.update_phone_num));
        }
    }

    /**
     *验证码计时器
     *
     *@author DavidHuang  at 下午3:42 18-5-31
     */
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


    @Override
    public void onClick(View v) {
        if(!NetworkUtil.isNetworkAvailable()){
            Toast.makeText(BindPhoneActivity.this, "网络不可用,请连接网络后再操作！", Toast.LENGTH_SHORT).show();
            return;
        }else{
            switch(v.getId()){
                case R.id.tv_request_code:
                    requestSMSCode();
                    break;
                case R.id.btn_bind_phone:
                    verifyCode();
                    break;
                default:
            }
        }

    }

    /**
     * 请求验证码
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
                        Toast.makeText(BindPhoneActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(BindPhoneActivity.this,
                                "验证码发送失败！",
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
     *验证验证码
     */
    private void verifyCode(){
        final String phoneNum = et_phone.getText().toString().trim();
        String code = et_code.getText().toString().trim();
        if (!InputLeagalCheck.isPhoneNum(phoneNum )) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("验证码校验中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobSMS.verifySmsCode(phoneNum, code, new UpdateListener() {
            @Override
            public void done(BmobException ex) {
                progress.dismiss();
                if(ex==null){
                    Toast.makeText(BindPhoneActivity.this, "手机号验证成功", Toast.LENGTH_SHORT).show();
                    bindMobilePhone(phoneNum);
                }else{
                    Toast.makeText(BindPhoneActivity.this,
                            "验证失败！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     *绑定手机号
     */
    private void bindMobilePhone(String phoneNum){
        //绑定手机号时需提交两个字段的值：mobilePhoneNumber、mobilePhoneNumberVerified
        MPTUser user =new MPTUser();
        user.setMobilePhoneNumber(phoneNum);
        user.setMobilePhoneNumberVerified(true);
        MPTUser currentUser = BmobUser.getCurrentUser(MPTUser.class);
        if(currentUser!=null){
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setCanceledOnTouchOutside(false);
            progress.setMessage("手机号绑定中...");
            progress.show();
            user.update(currentUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException ex) {
                    progress.dismiss();
                    if(ex==null){
                        Toast.makeText(BindPhoneActivity.this,
                                "手机号绑定成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(BindPhoneActivity.this,
                                "手机号绑定失败！",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(MPTApplication.getContext(), "请登陆", Toast.LENGTH_SHORT).show();
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
