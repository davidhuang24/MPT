package com.dh.exam.mpt.Utils;

/**
 * 同步接口：第一件事做完后，再做第二件事
 * 1.解决了“先写缓存，后读缓存”问题;
 * 2.解决了“”问题；
 *
 * @author DavidHuang  at 下午2:06 18-6-10
 */
public interface FirstThingListener {
    void done();//第一件事完成回调函数
    void onError(Exception e);//第一件事异常
}
