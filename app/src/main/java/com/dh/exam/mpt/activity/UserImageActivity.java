package com.dh.exam.mpt.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dh.exam.mpt.CustomView.SheetDialog;
import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.ConStant;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;

/**
 *1显示头像；
 *2调用图片选择；
 *3选择图片后调用图片裁剪API UCrop进行图片裁剪
 *
 *@author DavidHuang  at 下午3:57 18-5-31
 */
public class UserImageActivity extends BaseActivity implements UCropFragmentCallback {

    private static final String TAG = "UserImageActivity";
    private Toolbar toolbar;

    private static final int REQUEST_SELECT_PICTURE_FOR_FRAGMENT = 0x02;
    private static  String croppedImgName ;
    private int requestMode = 1;

    private boolean mShowLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_image);
        init();
    }

    public void init(){
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){//设置导航按钮，打开滑动菜单
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initUserHeadImg(getIntent().getData());
    }

    /**
     * 显示用户头像
     *
     * @param imgUri 图片来源
     */
    public void initUserHeadImg(Uri imgUri){
        if (imgUri != null) {
            try {
                UCropView uCropView = findViewById(R.id.user_head_img);
                uCropView.getCropImageView().setImageUri(imgUri, null);
                uCropView.getOverlayView().setShowCropFrame(false);
                uCropView.getOverlayView().setShowCropGrid(false);
                uCropView.getOverlayView().setDimmedColor(Color.TRANSPARENT);
            } catch (Exception e) {
                Log.e(TAG, "setImageUri", e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void showSheetDialog(){
        new SheetDialog.Builder(UserImageActivity.this).setTitle(getString(R.string.sheet_dialog_title))
                .addMenu(getString(R.string.sheet_dialog_item), (dialog, which) -> {
                    dialog.dismiss();
                    pickFromGallery();
                })
                .addMenu(getString(R.string.sheet_dialog_save), (dialog, which) -> {
                    //模拟保存，图片裁剪后（startCrop中）就已保存
                    Toast.makeText(this, "图片已保存", Toast.LENGTH_SHORT).show();
                }).create().show();
    }

    /**
     * 从相册选择图片
     */
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), requestMode);
    }

    /**
     * startActivityForResult回调函数，返回数据
     *
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == requestMode) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCrop(selectedUri);
                } else {
                    Toast.makeText(UserImageActivity.this, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    /**
     * 裁剪图片
     *
     * @param uri 图片uri
     */
    private void startCrop(@NonNull Uri uri) {
        croppedImgName= ConStant.CROP_CACHE_NAME;
        String destinationFileName = croppedImgName;
        destinationFileName += ".png";
        File file=new File(getCacheDir(), destinationFileName);
        if(file.exists()){//保证缓存中只有一张裁剪结果(用户头像)
            file.delete();
        }
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(file));
        uCrop = uCropConfig(uCrop);

        if (requestMode == REQUEST_SELECT_PICTURE_FOR_FRAGMENT) {       //if build variant = fragment

        } else {                                                        // else start uCrop Activity
            uCrop.start(UserImageActivity.this);
        }

    }

    /**
     * uCrop相关配置
     *
     * @param uCrop  uCrop builder instance
     * @return uCrop builder instance
     */
    private UCrop uCropConfig(@NonNull UCrop uCrop) {
        uCrop = uCrop.withAspectRatio(1, 1);

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionQuality(90);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        options.setCircleDimmedLayer(true);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));
        return uCrop.withOptions(options);
    }

    /**
     * 跳转到显示裁剪结果照片，携带裁剪结果Uri
     *
     * @param result 裁剪后的数据
     */
    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            CropResultActivity.activityStart(UserImageActivity.this,CropResultActivity.class,
                    null,null,resultUri);
        } else {
            Toast.makeText(UserImageActivity.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 裁剪异常处理
     *
     * @param result
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "裁剪异常 ：", cropError);
            Toast.makeText(UserImageActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(UserImageActivity.this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 是否显示裁剪进度条
     *
     * @param showLoader
     */
    @Override
    public void loadingProgress(boolean showLoader) {
        mShowLoader = showLoader;
        supportInvalidateOptionsMenu();
    }
    /**
     * 裁剪回调函数-裁剪完成
     *
     * @param result
     */
    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {
        switch (result.mResultCode) {
            case RESULT_OK:
                handleCropResult(result.mResultData);
                break;
            case UCrop.RESULT_ERROR:
                handleCropError(result.mResultData);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_img_activity_toolbar,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//Toolbar上的Action按钮是菜单Item
        switch(item.getItemId()){
            case R.id.custom_menu:
                showSheetDialog();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_MENU:
                showSheetDialog();//按实体菜单键也显示SheetDialog
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
