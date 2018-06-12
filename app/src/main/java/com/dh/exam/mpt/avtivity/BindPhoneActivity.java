package com.dh.exam.mpt.avtivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
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
import com.dh.exam.mpt.database.MPTUser;

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
        if(handleType.equals("1")){
            btn_bind.setText(getResources().getText(R.string.bind_phone));
        }else if(handleType.equals("2")){
            btn_bind.setText(getResources().getText(R.string.change_phone_num));
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

    /**
    *@Author: DavidHuang
    *@Time: 18-5-14 下午7:42
    *@return:
    *@params: [context, type, data2] type表示操作类型，1绑定新手机号，2修改手机号
    *@Descrption:
    */
    @Override
    public void onClick(View v) {
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
                            "验证失败：code="+ex.getErrorCode()+"，错误描述："+ex.getLocalizedMessage(),
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
                    }else {
                        Toast.makeText(BindPhoneActivity.this,
                                "手机号绑定失败，code="+ex.getErrorCode()+",错误原因："+ex.getLocalizedMessage(),
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
