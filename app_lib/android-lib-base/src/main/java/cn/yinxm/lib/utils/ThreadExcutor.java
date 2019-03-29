package cn.yinxm.lib.utils;

import android.arch.core.executor.TaskExecutor;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池
 * <p>
 *
 * @author yinxuming
 * @date 2019/3/27
 */
public class ThreadExcutor {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    /**
     * IO读写线程池
     */
    private static volatile ExecutorService ioPool;

    /**
     * 全局cachePool,适用于AsyncHttpClient等不
     * 限制任务数的请求
     */
    private static volatile ExecutorService cachePool;

    /**
     * 串行线程池
     */
    private static volatile ExecutorService serialPool;

    /**
     * 主线程handler，用于将任务提交至主线程执行
     */
    private static volatile Handler uiHandler;

    private ThreadExcutor() {
        throw new UnsupportedOperationException();
    }

    public static void ui(Runnable task) {
        getHandler().post(task);
    }

    public static void ui(Runnable task, long delayMills) {
        getHandler().postDelayed(task, delayMills);
    }

    public static void io(Runnable task) {
        getIOPool().execute(task);
    }


    public static void enqueue(Runnable task) {
        getSerialPool().execute(task);
    }

    public static void infinite(Runnable task) {
        getCachePool().execute(task);
    }

    private static Handler getHandler() {
        if (uiHandler == null) {
            synchronized (TaskExecutor.class) {
                if (uiHandler == null) {
                    uiHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return uiHandler;
    }

    private static ExecutorService getIOPool() {
        if (ioPool == null) {
            synchronized (TaskExecutor.class) {
                if (ioPool == null) {
                    ioPool = new ThreadPoolExecutor(CORE_POOL_SIZE, 15, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("io-pool"));
                }
            }
        }
        return ioPool;
    }

    private static ExecutorService getSerialPool() {
        if (serialPool == null) {
            synchronized (TaskExecutor.class) {
                if (serialPool == null) {
                    serialPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("serial-pool"));
                }
            }
        }
        return serialPool;
    }

    private static ExecutorService getCachePool() {
        if (cachePool == null) {
            synchronized (TaskExecutor.class) {
                if (cachePool == null) {
                    cachePool = new ThreadPoolExecutor(0, MAXIMUM_POOL_SIZE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory("cache-pool"));
                }
            }
        }
        return cachePool;
    }

    private static class NamedThreadFactory implements ThreadFactory {

        private final String mThreadName;
        private final AtomicInteger mCount;

        NamedThreadFactory(String threadName) {
            mThreadName = threadName;
            mCount = new AtomicInteger(1);
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, mThreadName + "#" + mCount.getAndIncrement());
        }
    }

}
