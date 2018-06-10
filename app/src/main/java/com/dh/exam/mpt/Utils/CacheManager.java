package com.dh.exam.mpt.Utils;



import com.dh.exam.mpt.database.MPTUser;

import java.io.File;


import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 *缓存管理
 *
 * @author DavidHuang  at 下午9:20 18-6-7
 */
public class CacheManager {

    /**
     * 将用户头像缓存到应用目录中
     */
    public static void writeHeadImgToCache(int fileType,final WriteCacheListener listener){
        if(fileType==1){//缓存用户头像
            BmobFileManager.downloadFile
                    (BmobUser.getCurrentUser(MPTUser.class).getHeadImg(),listener);
        }else {//缓存默认头像
            BmobFileManager.downloadFile(new BmobFile(
                    ConStant.DEFAULT_HEAD_IMG_NAME,"",ConStant.DEFAULT_HEAD_IMG_URL),listener);
        }
    }

    /**
     * 从应用目录用户获取头像,必须在writeHeadImgToCache()后执行
     */
    public static File getHeadImgFromCache(){
        File file;
        MPTUser currentUser=BmobUser.getCurrentUser(MPTUser.class);
        if(currentUser!=null&&currentUser.getHeadImg()!=null){//用户头像
            file=new File(CacheManager.DirsExistedOrCreat(ConStant.APP_Public_Dir_ROOT+"/HeadImages"),
                    BmobUser.getCurrentUser(MPTUser.class).getHeadImg().getFilename());
        }else {//默认头像
            file=new File(CacheManager.DirsExistedOrCreat(ConStant.APP_Public_Dir_ROOT+"/HeadImages"),
                    ConStant.DEFAULT_HEAD_IMG_NAME);
        }
        return file;
    }

    /**
     * 判断多级目录是否存在，不存在则创建
     *
     * @param dirsPath 目录路径
     * @return 目录
     */
    public static File DirsExistedOrCreat(String dirsPath) {
        File file = new File(dirsPath);
        try {
            if (!file.exists()) {
                if (file.mkdirs()) {
                    return file;
                } else {
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return file;
    }


}