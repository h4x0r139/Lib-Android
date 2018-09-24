package cn.yinxm.design.object;

import android.support.v4.util.Pools.*;

/**
 * Created by yinxuming on 2018/7/19.
 */
public class MyPooledClass {

    private static final SynchronizedPool<MyPooledClass> sPool =
            new SynchronizedPool<MyPooledClass>(10);

    public static MyPooledClass obtain() {
        MyPooledClass instance = sPool.acquire();
        return (instance != null) ? instance : new MyPooledClass();
    }

    /**
     * 重复调用会抛出异常
     */
    public void recycle() {
        // Clear state if needed.
        sPool.release(this);
    }

}
