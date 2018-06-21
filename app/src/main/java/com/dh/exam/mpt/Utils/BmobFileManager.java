package com.dh.exam.mpt.Utils;

import android.app.ProgressDialog;
import android.widget.Toast;

import com.dh.exam.mpt.MPTApplication;
import com.dh.exam.mpt.avtivity.MainActivity;
import com.dh.exam.mpt.entity.MPTUser;

import java.io.File;
import java.util.List;


import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteBatchListener;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 *Bmob文件管理，包含任意类型文件的 上传、下载、删除
 *
 * @author DavidHuang  at 下午9:28 18-5-31
 */
public class BmobFileManager extends BmobFile{

    private static final String TAG = "BmobFileManager";


    /**
     * 上传头像并更新数据库
     * <p>
     * 流程：上传新头像（from app目录）-删除旧头像-更新数据库
     * <p>
     */
    public static void updateUserHeadImg(final ProgressDialog progressDialog){
        MPTUser currentUser = BmobUser.getCurrentUser(MPTUser.class);
        if(currentUser!=null){
            String fileName= ConStant.HEAD_IMG_NAME_Header+currentUser.getObjectId()+".png";
            File file=new File(CacheManager.DirsExistedOrCreat(ConStant.APP_Public_Dir_ROOT+"/HeadImages"),
                    fileName);
            if(file.exists()){
                uploadDeleteUpdateFile(file.getPath(),progressDialog);
            }else {
                Toast.makeText(MPTApplication.getContext(), "裁剪结果复制失败，请重新更换头像", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                MainActivity.activityStart(MPTApplication.getContext(),MainActivity.class,
                        null,null,null);
            }
        }else{
            Toast.makeText(MPTApplication.getContext(), "请登陆", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            MainActivity.activityStart(MPTApplication.getContext(),MainActivity.class,
                    null,null,null);
        }
    }
    /**
     * 单一文件上传并更新数据库
     *
     * @param filePath 待上传文件路径
     */
    public static void uploadDeleteUpdateFile(String filePath, final ProgressDialog progressDialog){

        MPTUser currentUser = BmobUser.getCurrentUser(MPTUser.class);
        BmobFile bmobFile=new BmobFile(new File(filePath));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){

                    //删除旧头像
                    if(currentUser.getHeadImg()!=null){
                        //非第一次更新头像，上传成功后，先删除旧头像再更新数据库
                        deleteFileAndUpdateDb(currentUser.getHeadImg().getFileUrl(),bmobFile,progressDialog);
                    }else{
                        //更新数据库
                        currentUser.setHeadImg(bmobFile);
                        currentUser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                progressDialog.dismiss();
                                MainActivity.activityStart(MPTApplication.getContext(),MainActivity.class,
                                        null,null,null);
                                if (e == null) {

                                } else {
                                    Toast.makeText(MPTApplication.getContext(),
                                            "更新头像失败！错误码:" + e.getErrorCode() + ";错误描述："
                                                    + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    MainActivity.activityStart(MPTApplication.getContext(),MainActivity.class,
                                            null,null,null);
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(MPTApplication.getContext(),"文件上传失败："+e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    MainActivity.activityStart(MPTApplication.getContext(),MainActivity.class,
                            null,null,null);
                }
            }
            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
            }
        });
    }

    /**
     * 文件批量上传
     *
     * @param filePaths 待上传文件的路径
     */
    public static void uploadFileBatch(String []filePaths){
        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
                //2、urls-上传文件的完整url地址
                if(list1.size()==filePaths.length){//如果数量相等，则代表文件全部上传完成
                    //dosmothing
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {
                //1、curIndex--表示当前第几个文件正在上传
                //2、curPercent--表示当前上传文件的进度值（百分比）
                //3、total--表示总的上传文件数
                //4、totalPercent--表示总的上传进度（百分比）
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MPTApplication.getContext(), "错误码:"+i+"错误描述:"+s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     *下载文件
     *调用此方法之前先获取BmobFile对象实例，可以是查询数据时
     * 返回的BmobFile，也可以通过url自行构建BmobFile对象
     * Uri可以是bmob服务器的url，也可以是任意url
     *
     * @param bmobFile
     */
    public static void downloadFile(BmobFile bmobFile,final WriteCacheListener listener){
        File saveFile=new File(
                CacheManager.DirsExistedOrCreat(ConStant.APP_Public_Dir_ROOT+"/HeadImages"),
                bmobFile.getFilename());

        bmobFile.download(saveFile, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    if(listener!=null){
                        listener.done();
                    }
                }else {
                    if(listener!=null){
                        listener.onError(e);
                    }
                    Toast.makeText(MPTApplication.getContext(),
                            "下载失败，错误码："+e.getErrorCode()+";错误描述： "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {
                //integer下载进度;l下载速度
            }
        });
    }

    /**
     *删除文件然后更新数据库
     *
     * @param url 待删除文件的url,通过bmobFile.getFileUrl()获取
     * @param uploadedSuccessFile 上传成功的文件
     */
    public static void deleteFileAndUpdateDb(String url, BmobFile uploadedSuccessFile,
                                             final ProgressDialog progressDialog){
        MPTUser currentUser = BmobUser.getCurrentUser(MPTUser.class);
        BmobFile bmobFile=new BmobFile();
        bmobFile.setUrl(url);
        bmobFile.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //更新数据库
                    currentUser.setHeadImg(uploadedSuccessFile);

                    currentUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            progressDialog.dismiss();
                            MainActivity.activityStart(MPTApplication.getContext(),MainActivity.class,
                                    null,null,null);
                            if (e == null) {

                            } else {
                                progressDialog.dismiss();
                                MainActivity.activityStart(MPTApplication.getContext(),MainActivity.class,
                                        null,null,null);
                                Toast.makeText(MPTApplication.getContext(),
                                        "更新头像失败！错误码:" + e.getErrorCode() + ";错误描述："
                                                + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    MainActivity.activityStart(MPTApplication.getContext(),MainActivity.class,
                            null,null,null);
                    Toast.makeText(MPTApplication.getContext(),
                            "删除失败，错误码："+e.getErrorCode()+";错误描述： "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 批量删除文件
     *
     * @param urls 待删除文件的url
     */
    public static void deleteFileBatch(String []urls){
        BmobFile.deleteBatch(urls, new DeleteBatchListener() {
            @Override
            public void done(String[] strings, BmobException e) {//strings是删除失败的url
                if(e==null){
                    Toast.makeText(MPTApplication.getContext(), "全部删除成功", Toast.LENGTH_SHORT).show();
                }else {
                    if(strings!=null){
                        Toast.makeText(MPTApplication.getContext(), "删除失败文件个数为"+
                                strings.length+","+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MPTApplication.getContext(), "全部删除失败，"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

}
