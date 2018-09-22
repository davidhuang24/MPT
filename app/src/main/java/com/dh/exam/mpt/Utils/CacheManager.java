package com.dh.exam.mpt.Utils;



import com.dh.exam.mpt.entity.MPTUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


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
    public static void writeHeadImgToCache(int fileType,final FirstThingListener listener){
        if(fileType==1){//缓存用户头像
            BmobFileManager.downloadFile
                    (BmobUser.getCurrentUser(MPTUser.class).getHeadImg(),
                            ConStant.APP_Public_Dir_ROOT+"/HeadImages",listener);
        }else {//缓存默认头像
            BmobFileManager.downloadFile(new BmobFile(
                    ConStant.DEFAULT_HEAD_IMG_NAME,"",ConStant.DEFAULT_HEAD_IMG_URL),
                    ConStant.APP_Public_Dir_ROOT+"/HeadImages",listener);
        }
    }

    /**
     * 从应用目录用户获取头像,必须在writeHeadImgToCache()后执行
     */
    public static File getHeadImgFromCache(){
        File file;
        MPTUser currentUser=BmobUser.getCurrentUser(MPTUser.class);
        if(currentUser!=null&&currentUser.getHeadImg()!=null){//用户头像
            file=new File(CacheManager.DirsExistedOrCreate(ConStant.APP_Public_Dir_ROOT+"/HeadImages"),
                    BmobUser.getCurrentUser(MPTUser.class).getHeadImg().getFilename());
        }else {//默认头像
            file=new File(CacheManager.DirsExistedOrCreate(ConStant.APP_Public_Dir_ROOT+"/HeadImages"),
                    ConStant.DEFAULT_HEAD_IMG_NAME);
        }
        return file;
    }

    /**
     * 判断多级目录是否存在，不存在则创建
     *
     * @param dirPath 目录路径
     * @return 目录
     */
    public static File DirsExistedOrCreate(String dirPath) {
        File file = new File(dirPath);
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

    /**
     * 复制文件
     * @param fromFile
     * @param toFile
     * @throws IOException
     */
    public static void copyFile(File fromFile,File toFile) throws IOException {
        FileInputStream ins = new FileInputStream(fromFile);
        FileOutputStream out = new FileOutputStream(toFile);
        byte[] b = new byte[1024];
        int n=0;
        while((n=ins.read(b))!=-1){
            out.write(b, 0, n);
        }

        ins.close();
        out.close();
    }


}
