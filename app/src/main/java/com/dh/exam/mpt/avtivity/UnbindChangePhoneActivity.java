package com.dh.exam.mpt.avtivity;

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
import com.dh.exam.mpt.entity.MPTUser;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 *handleType
 *1.handleType为”1“解绑手机号：先验证手机号，后解绑手机号
 *2.handleType为”2“修改手机号：先验证手机号，后跳转到BindPhoneActivity界面绑定新手机号
 *
 *@author DavidHuang  at 下午7:37 18-6-4
 */
public class UnbindChangePhoneActivity extends BaseActivity implements View.OnClickListener{

    private EditText et_code;
    private Button btn_unbind_phone;
    private Button btn_change_phone_number;
    private TextView tv_request_code;
    private MPTUser currentUser;

    private String handleType;
    private MyCountTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbind_change_phone);
        init();
    }

    public void init(){
        et_code=(EditText) findViewById(R.id.et_code);
        btn_unbind_phone=(Button) findViewById(R.id.btn_unbind_phone);
        btn_change_phone_number =(Button) findViewById(R.id.btn_next_change_phone);
        tv_request_code=(TextView) findViewById(R.id.tv_request_code);
        btn_change_phone_number.setOnClickListener(this);
        btn_unbind_phone.setOnClickListener(this);
        tv_request_code.setOnClickListener(this);
        currentUser=BmobUser.getCurrentUser(MPTUser.class);
        Intent intent=getIntent();
        handleType=intent.getStringExtra("param1");
        if(handleType.equals("1")){
            btn_unbind_phone.setVisibility(View.VISIBLE);
            btn_change_phone_number.setVisibility(View.GONE);
        }else if(handleType.equals("2")){
            btn_change_phone_number.setVisibility(View.VISIBLE);
            btn_unbind_phone.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_request_code:
                requestSMSCode();
                break;
            case R.id.btn_unbind_phone: //验证手机号成功后解绑
                verifyCode(1);
                break;
            case R.id.btn_next_change_phone: //验证旧手机号成功后绑定新手机号
                verifyCode(2);
                break;
            default:
        }
    }

    /**
     * 请求验证码
     */
    private void requestSMSCode() {
        currentUser=BmobUser.getCurrentUser(MPTUser.class);
        String phoneNum = currentUser.getMobilePhoneNumber();
        if (InputLeagalCheck.isPhoneNum(phoneNum)) {
            timer = new MyCountTimer(60000, 1000);
            timer.start();
            BmobSMS.requestSMSCode(phoneNum,getResources().getString(R.string.SMS_template), new QueryListener<Integer>(){

                @Override
                public void done(Integer smsId, BmobException ex) {
                    if(ex==null){
                        Toast.makeText(UnbindChangePhoneActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(UnbindChangePhoneActivity.this,
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
     * 验证验证码
     *
     * @param type 操做类型 ：1表示解绑，2表示修改手机号
     */
    private void verifyCode(final int type){
        currentUser=BmobUser.getCurrentUser(MPTUser.class);
        final String phoneNum = currentUser.getMobilePhoneNumber();
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
                    Toast.makeText(UnbindChangePhoneActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                    if(type==1){//解绑
                        unBindPhone(phoneNum);
                    }else if(type==2){//修改手机号
                        BindPhoneActivity.activityStart(UnbindChangePhoneActivity.this,BindPhoneActivity.class,"2",null,null);
                    }
                }else{
                    Toast.makeText(UnbindChangePhoneActivity.this,
                            "验证失败：code="+ex.getErrorCode()+"，错误描述："+ex.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 解绑手机号
     *
     * @param phoneNum 手机号
     */
    private void unBindPhone(String phoneNum){
        //解绑手机号时需remove两个字段的值：mobilePhoneNumber、mobilePhoneNumberVerified
        currentUser=BmobUser.getCurrentUser(MPTUser.class);
        MPTUser user =new MPTUser();
        if(currentUser!=null){
            user.setObjectId(currentUser.getObjectId());
            user.remove("mobilePhoneNumber");
            user.remove("mobilePhoneNumberVerified");


            final ProgressDialog progress = new ProgressDialog(this);
            progress.setCanceledOnTouchOutside(false);
            progress.setMessage("手机号解绑中...");
            progress.show();
            user.update(new UpdateListener() {
                @Override
                public void done(BmobException ex) {
                    progress.dismiss();
                    if(ex==null){
                        Toast.makeText(UnbindChangePhoneActivity.this,
                                "手机号解绑成功", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(UnbindChangePhoneActivity.this,
                                "手机号解绑失败，code="+ex.getErrorCode()+",错误原因："+ex.getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(MPTApplication.getContext(), "请登陆", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }

    }



}
