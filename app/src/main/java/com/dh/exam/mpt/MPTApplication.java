package com.dh.exam.mpt;

import android.app.Application;
import android.content.Context;

import com.dh.exam.mpt.Utils.ConStant;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * APP启动时初始化，在此可
 * 以完成一些预操作，全局操作，初始化操作
 * <p>
 *1.全局获取Context;
 *2.Bmob配置和初始化;
 *
 * @author DavidHuang  at 上午10:15 18-6-1
 */
public class MPTApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //Bmob配置和初始化
        BmobConfig bmobConfig=new BmobConfig.Builder(this)
                .setApplicationId(ConStant.BMOB_APP_KEY)
                .setConnectTimeout(15)//请求超时时间（单位为s）：不设置默认15s
                .setUploadBlockSize(512*1024)//文件分片上传时每片的大小（单位B），默认512*1024
                .build();
        Bmob.initialize(bmobConfig);

    }
    /**
     * 全局获取上下文
     *
     * @return 上下文
     */
    public static Context getContext(){
        return context;
    }
}
