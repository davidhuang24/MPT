package com.dh.exam.mpt.Utils;

import android.Manifest;
import android.os.Environment;

import java.io.File;

/**
 * 常量
 *
 * @author DavidHuang  at 下午7:11 18-6-1
 */
public class ConStant {
    //Bmob Application ID
    public static final String BMOB_APP_KEY="42ab78bb163be6fe44298812dba4d5ce";
    public static final String APP_Public_Dir_ROOT=
            Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"MPT";
    public static final String DEFAULT_HEAD_IMG_NAME="DefaultHeadImg.png";
//    public static final String INTRODUCE_PIC_NAMES[]=
//            {"introduce01.jpg","introduce02.jpg","introduce03.jpg"};
//    public static final String START_PIC_NAME="start.jpg";
    public static final String HEAD_IMG_NAME_Header="MPTHeadImg-";
    public static final String DEFAULT_HEAD_IMG_URL =
            "http://bmob-cdn-19653.b0.upaiyun.com/2018/06/08/7e1624844034744f80aa061bd5354fc7.png";
//    public static final String INTRODUCE_PIC_URLS[]=
//            {"http://bmob-cdn-19653.b0.upaiyun.com/2018/09/22/e44123c3403259f4808c3c9e7a480949.jpg",
//            "http://bmob-cdn-19653.b0.upaiyun.com/2018/09/22/2aec17a840821d3e8089524a01bdf56d.jpg",
//            "http://bmob-cdn-19653.b0.upaiyun.com/2018/09/22/75e186484063389c80f94d7b1bc04d62.jpg"};
//    public static final String START_PIC_URL =
//            "http://bmob-cdn-19653.b0.upaiyun.com/2018/09/22/4105e3c7403874e680686957175a5a7f.jpg";
    public static final String CROP_CACHE_NAME="MPTCropCache";
    public static final String PAPER_LIBRARY_FRAG_TAG="lib_frag";
    public static final String NEW_PAPER_FRAG_TAG="new_frag";
    public final  static String PERMISSIONS_STORAGE[]= {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

}
