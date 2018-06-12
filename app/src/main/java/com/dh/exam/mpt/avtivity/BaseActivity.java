package com.dh.exam.mpt.avtivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.ActivityCollector;
import com.dh.exam.mpt.Utils.CacheManager;
import com.dh.exam.mpt.Utils.ConStant;
import com.dh.exam.mpt.Utils.WriteCacheListener;
import com.dh.exam.mpt.database.MPTUser;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

/**
 * Activity公共操作，减少代码复写
 *1.动态申请权限；2.授权AlertDialog处理；3.Bmob初始化；4.Activity跳转的方法5.完全退出app
 *
 *@author DavidHuang  at 下午3:40 18-5-31
 */
public class BaseActivity extends AppCompatActivity {

    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Bmob初始化,从StartActivity移到BaseActivity
        Bmob.initialize(this, ConStant.BMOB_APP_KEY);
        ActivityCollector.addActivity(this);
    }

    /**
     *Activity跳转
     *
     * @param context from Activity
     * @param cls to Activity
     * @param data1 携带数据
     * @param data2 携带数据
     * @param uri 携带Uri数据
     */
    public static void activityStart(Context context, Class<?> cls,String data1, String data2, Uri uri){
        Intent intent=new Intent(context,cls);
        intent.putExtra("param1",data1);
        intent.putExtra("param2",data2);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * 授权回调函数
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//授权
                    cacheFiles();
                } else {//拒绝授权
                    Toast.makeText(this, "您拒绝了申请读写手机存储的权限，请到权限中心授权", Toast.LENGTH_SHORT).show();
                }
                break;
//            case 2:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//授权
//
//                } else {//拒绝授权
//                    Toast.makeText(this, "您拒绝了申请读手机状态的权限，请到权限中心授权", Toast.LENGTH_SHORT).show();
//                }
//                break;
            default:
        }
    }

    /**
     * 动态申请权限
     */
    public void requestPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            cacheFiles();
        }

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE
//                ) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{ Manifest.permission.READ_PHONE_STATE}, 2);
//        } else {
//
//        }
    }

    /**
     * StartActivity缓存操作
     *
     */
    public void cacheFiles(){
        File file;
        MPTUser currentUser=BmobUser.getCurrentUser(MPTUser.class);
        int headImgType=0;
        if(currentUser!=null&&currentUser.getHeadImg()!=null){//已登陆并且用户未设置头像，用户头像
            file=new File(CacheManager.DirsExistedOrCreat(ConStant.APP_Public_Dir_ROOT+"/HeadImages"),
                    currentUser.getHeadImg().getFilename());
            headImgType=1;
        }else{//未登陆或者用户头像为空，默认头像
            file=new File(CacheManager.DirsExistedOrCreat(ConStant.APP_Public_Dir_ROOT+"/HeadImages"),
                    ConStant.DEFAULT_HEAD_IMG_NAME);
            headImgType=0;
        }

        if(!file.exists()) {//缓存不存在，做缓存
            CacheManager.writeHeadImgToCache(headImgType,new WriteCacheListener() {
                @Override
                public void done() { }
                @Override
                public void onError(Exception e) { }
            });//缓存用户头像，只写缓存，不读缓存
        }
    }






    /**
     * Hide alert dialog if any.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }



}
