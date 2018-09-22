package com.dh.exam.mpt.activity.Fragment;


import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.dh.exam.mpt.MPTApplication;
import com.dh.exam.mpt.Utils.BmobFileManager;
import com.dh.exam.mpt.Utils.CacheManager;
import com.dh.exam.mpt.Utils.ConStant;
import com.dh.exam.mpt.Utils.FirstThingListener;
import com.dh.exam.mpt.activity.LoginActivity;
import com.dh.exam.mpt.R;
import com.dh.exam.mpt.activity.MainActivity;
import com.dh.exam.mpt.activity.RegisterActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

/**
 * 介绍碎片,用于介绍APP功能特性，第一次启动APP时调用
 */

public class IntroduceFragment extends Fragment implements View.OnClickListener {

    private SliderLayout sliderLayout;//轮播控件
//    private List<File> pics =new ArrayList<>();
    private int pics[]={R.drawable.bj,R.drawable.sh,R.drawable.wh};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduce, container, false);
        sliderLayout = view.findViewById(R.id.slider_layout);
        init();
        Button btn_register = sliderLayout.findViewById(R.id.btn_register);
        Button btn_login = sliderLayout.findViewById(R.id.btn_login);
        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        sliderLayout.startAutoCycle();
    }

    private void init(){
//        initPics();
        initSliderLayout();
    }

//    private void initPics(){
//        for(int i=0;i<3;i++){
//            initPic(i);
//        }
//    }

//    /**
//     * 初始化要用的图片，后台-缓存-pics;从后台加载图片会导致启动时资源来不及加载而黑屏
//     * @param index
//     */
//    private void initPic(int index){
//        File file=new File(CacheManager.DirsExistedOrCreate(ConStant.APP_Public_Dir_ROOT+"/IntroduceStartImages"),
//                ConStant.INTRODUCE_PIC_NAMES[index]);
//        if(!file.exists()){
//            //写缓存
//            BmobFileManager.downloadFile(new BmobFile(
//                            ConStant.INTRODUCE_PIC_NAMES[index], "", ConStant.INTRODUCE_PIC_URLS[index]),
//                    ConStant.APP_Public_Dir_ROOT+"/IntroduceStartImages",
//                    new FirstThingListener() {
//                        @Override
//                        public void done() {
//                            //读缓存
//                            pics.add(new File(CacheManager.DirsExistedOrCreate(ConStant.APP_Public_Dir_ROOT+"/IntroduceStartImages"),
//                                    ConStant.INTRODUCE_PIC_NAMES[index]));
//                        }
//                        @Override
//                        public void onError(Exception e) {
//                            Toast.makeText(MPTApplication.getContext(), "缓存异常", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }else {//文件存在，读缓存
//            pics.add(new File(CacheManager.DirsExistedOrCreate(ConStant.APP_Public_Dir_ROOT+"/IntroduceStartImages"),
//                    ConStant.INTRODUCE_PIC_NAMES[index]));
//        }
//    }

    private void initSliderLayout() {
        for(int i=0;i<3;i++){
            DefaultSliderView sliderView = new DefaultSliderView(getContext());
            sliderView
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .image(pics[i]);
            sliderLayout.addSlider(sliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);//切换动画
        sliderLayout.setDuration(4000);//自动播放停留时间
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register://注册
                RegisterActivity.activityStart(getContext(),RegisterActivity.class,null,null,null);
                break;
            case R.id.btn_login://登录
                LoginActivity.activityStart(getContext(),LoginActivity.class,null,null,null);
                break;
            default:
        }

    }

    @Override
    public void onStop() {
        sliderLayout.stopAutoCycle();//防止内存泄露,在Activity和Fragment销毁前调用
        super.onStop();
    }
}