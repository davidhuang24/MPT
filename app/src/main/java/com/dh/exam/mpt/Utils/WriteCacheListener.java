package com.dh.exam.mpt.Utils;

/**
 * 写缓存回调接口，解决了“先写缓存，后读缓存”问题
 *
 * @author DavidHuang  at 下午2:06 18-6-10
 */
public interface WriteCacheListener {
    void done();//缓存完成回调函数
    void onError(Exception e);//写缓存异常
}
