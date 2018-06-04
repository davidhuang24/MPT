package com.dh.exam.mpt.avtivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dh.exam.mpt.CustomView.SheetDialog;
import com.dh.exam.mpt.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;

import java.io.File;
/**
 *1显示头像；
 *2调用图片选择；
 *3选择图片后调用图片裁剪API UCrop进行图片裁剪
 *
 *@author DavidHuang  at 下午3:57 18-5-31
 */
public class UserImageActivity extends BaseActivity implements View.OnClickListener,UCropFragmentCallback {

    private static final String TAG = "UserImageActivity";
    private Toolbar toolbar;
    private ImageView iv_user_img;
    private Uri imageUri;

    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    private static final int REQUEST_SELECT_PICTURE_FOR_FRAGMENT = 0x02;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
    private int requestMode = 1;

    private boolean mShowLoader;

    private int mToolbarCropDrawable;
    //自定义裁剪界面颜色
    private int mToolbarColor;
    private int mStatusBarColor;
    private int mToolbarWidgetColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_image);
        init();
    }

    public void init(){
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        iv_user_img =(ImageView) findViewById(R.id.iv_user_img);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){//设置导航按钮，打开滑动菜单
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void showSheetDialog(){
        new SheetDialog.Builder(iv_user_img.getContext()).setTitle(getString(R.string.sheet_dialog_title))
                .addMenu(getString(R.string.sheet_dialog_item), (dialog, which) -> {
                    dialog.dismiss();
                    pickFromGallery();
                })
                .addMenu(getString(R.string.sheet_dialog_save), (dialog, which) -> {
                    Toast.makeText(this, "保存到本地", Toast.LENGTH_SHORT).show();
                }).create().show();
    }

    /**
     * 从相册选择图片
     */
    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                    .setType("image/*")
                    .addCategory(Intent.CATEGORY_OPENABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), requestMode);
        }
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
     * Callback received when a permissions request has been completed.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 裁剪图片
     *
     * @param uri 图片uri
     */
    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;
        destinationFileName += ".png";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
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
     * 跳转到显示裁剪结果照片，携带裁剪后的数据
     *
     * @param result 裁剪后的数据
     */
    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            CropResultActivity.startWithUri(UserImageActivity.this, resultUri);
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

    @Override
    public void onClick(View v) {

    }
}
