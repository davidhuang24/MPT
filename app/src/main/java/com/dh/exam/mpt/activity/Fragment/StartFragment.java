package com.dh.exam.mpt.activity.Fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dh.exam.mpt.activity.MainActivity;
import com.dh.exam.mpt.R;
import com.dh.exam.mpt.activity.StartActivity;

/**
 * 启动碎片，用于展示广告或者APP的LOGO,非第一次启动调用
 **/

public class StartFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "StartFragment";
    private Button skipADButton;
    private LinearLayout linearLayout;
    private int remainTime=3;
    private final int UPDATE_REMAIN_TIME=1;
    private StartActivity startActivity;
    private boolean isJumpped=false;//解决两次跳转MainActivity的问题（小概率），不知道是否解决

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_start,container,false);
        linearLayout=(LinearLayout) view.findViewById(R.id.skip_ll);
        skipADButton=(Button) linearLayout.findViewById(R.id.ad_skip_btn);
        skipADButton.setOnClickListener(this);
        startActivity=(StartActivity)getActivity();
        Message msg = Message.obtain();
        msg.what = UPDATE_REMAIN_TIME;
        handler.sendMessage(msg);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ad_skip_btn:
                if(!isJumpped){
                    jumpToMain();
                    startActivity.onBackPressed();
                }

                break;
            default:
        }
    }

    private void jumpToMain(){
        MainActivity.activityStart(getContext(),MainActivity.class,null,null,null);
        if(!isJumpped) isJumpped=true;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_REMAIN_TIME:
                    if(remainTime > 0) {
                        skipADButton.setText(remainTime + "S  跳过");
                        handler.sendEmptyMessageDelayed(UPDATE_REMAIN_TIME, 1000);
                        remainTime--;
                    } else {
                        if(!isJumpped){
                            jumpToMain();
                            startActivity.onBackPressed();
                        }

                    }
                    break;
                default:
            }
        }
    };

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        super.onDestroy();
    }


}
