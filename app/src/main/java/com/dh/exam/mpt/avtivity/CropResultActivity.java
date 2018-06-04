package com.dh.exam.mpt.avtivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.BmobFileManager;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;

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

    /**
     * 界面跳转
     *
     * @param context 上下文
     * @param uri 传过来的uri
     */
    public static void startWithUri(@NonNull Context context, @NonNull Uri uri) {
        Intent intent = new Intent(context, CropResultActivity.class);
        intent.setData(uri);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_use_save) {//使用并且上传图片到服务器
            useAndUploadImg(imgUri);
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean useAndUploadImg(Uri uri){
        MainActivity.activityStart(CropResultActivity.this,MainActivity.class,null,null,uri);

        BmobFileManager.uploadFile(uri.getPath());



        return true;
    }


//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_STORAGE_WRITE_ACCESS_PERMISSION:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                    saveCroppedImage();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//    private void saveCroppedImage() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    getString(R.string.permission_write_storage_rationale),
//                    REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
//        } else {
//            Uri imageUri = getIntent().getData();
//            if (imageUri != null && imageUri.getScheme().equals("file")) {
//                try {
//                    copyFileToDownloads(getIntent().getData());
//                } catch (Exception e) {
//                    Toast.makeText(CropResultActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, imageUri.toString(), e);
//                }
//            } else {
//                Toast.makeText(CropResultActivity.this, getString(R.string.toast_unexpected_error), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    private void copyFileToDownloads(Uri croppedFileUri) throws Exception {
//        String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//        String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), croppedFileUri.getLastPathSegment());
//
//        File saveFile = new File(downloadsDirectoryPath, filename);
//
//        FileInputStream inStream = new FileInputStream(new File(croppedFileUri.getPath()));
//        FileOutputStream outStream = new FileOutputStream(saveFile);
//        FileChannel inChannel = inStream.getChannel();
//        FileChannel outChannel = outStream.getChannel();
//        inChannel.transferTo(0, inChannel.size(), outChannel);
//        inStream.close();
//        outStream.close();
//
//        showNotification(saveFile);
//        Toast.makeText(this, R.string.notification_image_saved, Toast.LENGTH_SHORT).show();
//        finish();
//    }
//    private void showNotification(@NonNull File file) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri fileUri = FileProvider.getUriForFile(
//                this,
//                getString(R.string.file_provider_authorities),
//                file);
//
//        intent.setDataAndType(fileUri, "image/*");
//
//        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(
//                intent,
//                PackageManager.MATCH_DEFAULT_ONLY);
//        for (ResolveInfo info : resInfoList) {
//            grantUriPermission(
//                    info.activityInfo.packageName,
//                    fileUri, FLAG_GRANT_WRITE_URI_PERMISSION | FLAG_GRANT_READ_URI_PERMISSION);
//        }
//
//        NotificationCompat.Builder notificationBuilder;
//        NotificationManager notificationManager = (NotificationManager) this
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(createChannel());
//            }
//            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
//        } else {
//            notificationBuilder = new NotificationCompat.Builder(this);
//        }
//
//        notificationBuilder
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText(getString(R.string.notification_image_saved_click_to_preview))
//                .setTicker(getString(R.string.notification_image_saved))
//                .setSmallIcon(R.drawable.ic_done)
//                .setOngoing(false)
//                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
//                .setAutoCancel(true);
//        if (notificationManager != null) {
//            notificationManager.notify(DOWNLOAD_NOTIFICATION_ID_DONE, notificationBuilder.build());
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.O)
//    public NotificationChannel createChannel() {
//        int importance = NotificationManager.IMPORTANCE_LOW;
//        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), importance);
//        channel.setDescription(getString(R.string.channel_description));
//        channel.enableLights(true);
//        channel.setLightColor(Color.YELLOW);
//        return channel;
//    }
}