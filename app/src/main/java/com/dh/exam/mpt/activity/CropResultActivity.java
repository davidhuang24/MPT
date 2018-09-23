package com.dh.exam.mpt.activity;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.BmobFileManager;
import com.dh.exam.mpt.Utils.CacheManager;
import com.dh.exam.mpt.Utils.ConStant;
import com.dh.exam.mpt.Utils.NetworkUtil;
import com.dh.exam.mpt.entity.MPTUser;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;
import java.io.IOException;

import cn.bmob.v3.BmobUser;

/**
 *裁剪结果展示界面,并且保存图片
 *
 *@author DavidHuang  at 下午4:36 18-5-31
 */
public class CropResultActivity extends BaseActivity {


    private static final String TAG = "ResultActivity";
    private static final String CHANNEL_ID = "3000";
    private static final int DOWNLOAD_NOTIFICATION_ID_DONE = 911;
    private Uri imgUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_result);
        imgUri = getIntent().getData();
        if (imgUri != null) {
            try {
                UCropView uCropView = findViewById(R.id.ucrop);
                uCropView.getCropImageView().setImageUri(imgUri, null);
                uCropView.getOverlayView().setShowCropFrame(false);
                uCropView.getOverlayView().setShowCropGrid(false);
                uCropView.getOverlayView().setDimmedColor(Color.TRANSPARENT);
            } catch (Exception e) {
                Log.e(TAG, "setImageUri", e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(getIntent().getData().getPath()).getAbsolutePath(), options);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.format_crop_result_d_d, options.outWidth, options.outHeight));
        }
    }



    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_use_save) {
            if(!NetworkUtil.isNetworkAvailable()){
                Toast.makeText(CropResultActivity.this, "网络不可用,请连接网络后再操作！", Toast.LENGTH_SHORT).show();
                return true;
            }else{
                SaveUploadUseImg(imgUri);
            }
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存、上传、使用用户头像
     *
     * @param uri 裁剪结果Uri
     */
    public void SaveUploadUseImg(Uri uri){
        //上传ProgressDialog
        final ProgressDialog progress = new ProgressDialog(CropResultActivity.this);
        progress.setMessage("头像上传中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        saveCropResult(uri);
        BmobFileManager.updateUserHeadImg(progress);
    }

    /**
     * 将裁剪结果从应用cache目录重命名后复制到公共应用目录
     *
     * @param uri 裁剪结果Uri
     */
    public void saveCropResult(Uri uri){
        MPTUser currentUser= BmobUser.getCurrentUser(MPTUser.class);
        String fileName;
        if(currentUser!=null){
            fileName= ConStant.HEAD_IMG_NAME_Header+currentUser.getObjectId()+".png";
        }else{//未登陆时的文件名
            fileName=ConStant.CROP_CACHE_NAME+".png";
        }
        File file=new File(CacheManager.DirsExistedOrCreate(ConStant.APP_Public_Dir_ROOT+"/HeadImages"),
                fileName);
        if(file.exists()){
            file.delete();
        }
        try {
            //将裁剪结果从cache复制到APP公共目录
            CacheManager.copyFile(new File(uri.getPath()),file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
