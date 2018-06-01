package com.dh.exam.mpt.avtivity;


import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.dh.exam.mpt.Utils.ConStant;
import com.dh.exam.mpt.avtivity.Fragment.IntroduceFragment;
import com.dh.exam.mpt.avtivity.Fragment.StartFragment;
import com.dh.exam.mpt.R;

import cn.bmob.v3.Bmob;

/**
 *启动界面,不同情况调用不同Fragment:
 * 第一次启动调用IntroduceFragment()；
 * 否则调用StartFragment()
 *
 *@author DavidHuang  at 下午3:55 18-5-31
 */
public class StartActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Bmob.initialize(this, ConStant.BMOB_APP_KEY);
        //全屏设置
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(isFirstStart()){//第一次启动
            replaceFragment(new IntroduceFragment());
        }else {
            replaceFragment(new StartFragment());
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout,fragment);
        transaction.commit();
    }

    /**
     * 使用SharedPreferences判断APP是否是第一次启动
     *
     * @return 是否是第一次启动
     */
    public Boolean isFirstStart(){
        SharedPreferences preferences=
                getSharedPreferences("MTPPreferences",MODE_PRIVATE);
        Boolean isFirst=preferences.getBoolean("isFirst",true);
        if(isFirst){//第一次启动
            preferences.edit().putBoolean("isFirst",false).apply();
            return true;
        }else{
            return false;
        }

    }
}
