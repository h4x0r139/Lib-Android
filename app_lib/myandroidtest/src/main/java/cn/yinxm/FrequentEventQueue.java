package cn.yinxm;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import cn.yinxm.lib.utils.log.LogUtil;


/**
 * 解决频繁触发回调，更新UI导致的ANR等问题
 * <p>
 * 频繁触发事件，用队列按最小处理时间间隔 去触发 丢弃部分事件
 * 注意：只会丢弃中间事件，最终结果会保持和实际一致
 * 例如，规定最多1s内触发1个事件，实际用户在10s内触发了20个事件，本应用只会返回10个触发事件
 * 最大延迟 < MinInterval
 * <p>
 *
 * @author yinxuming
 * @date 2018/11/1
 */
public class FrequentEventQueue<T> {
    private static final String TAG = "FrequentEventQueue";


    private static final int EVENT_COME = 1;
    private static final int EVENT_LAST = 2;

    /**
     * 最小时间间隔
     */
    private int mMinInterval;

    /**
     * 是否在子线程执行任务
     */
    private boolean mIsRunWorkThread;

    /**
     * 事件待触发的任务
     */
    private EventTrigger mEventTrigger;

    private long mLastProcessedTime = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Handler mWorkHandler;

    public FrequentEventQueue(int minInterval, EventTrigger eventTrigger, boolean isRunWorkThread) {
        mMinInterval = minInterval;
        mEventTrigger = eventTrigger;
        mIsRunWorkThread = isRunWorkThread;
    }

    /**
     * 在子线程中调用该方法，启动处理
     */
    public void start() {
        if (mWorkHandler == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    initWorkHanlder();
                    Looper.loop();
                }
            }).start();
        }
    }

    private void initWorkHanlder() {
        mWorkHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    long currentTime = System.currentTimeMillis();
//                    LogUtil.d(TAG, "msg.what=" + msg.what + ", index=" + msg.obj + ",  currentTime=" + currentTime + ",  last="
//                            + mLastProcessedTime + ", 差值=" + (currentTime - mLastProcessedTime) + ", threadId=" + Thread.currentThread().getId());
                    switch (msg.what) {
                        case EVENT_LAST:
//                            if (hasMessages(EVENT_COME)) {
//                                LogUtil.e(TAG, msg.what + ", index=" + msg.obj + " not last ...");
//                                break;
//                            }
                        case EVENT_COME:

                            if ((currentTime - mLastProcessedTime) < mMinInterval) {
//                                LogUtil.e(TAG, "Ignore calls that are too frequent ... " + msg.what + ", index=" + msg.obj);
                                Message newMsg = obtainMessage(EVENT_LAST, msg.obj);
                                removeMessages(EVENT_LAST);
                                sendMessageDelayed(newMsg, mMinInterval);

                                return;
                            }
                            mLastProcessedTime = currentTime;
                            if (mEventTrigger == null) {
                                return;
                            }
                            final Object obj = msg.obj;
                            if (!mIsRunWorkThread) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mEventTrigger.onEvent(obj);
                                    }
                                });
                            } else {
                                mEventTrigger.onEvent(obj);
//                                LogUtil.d(TAG, "onEvent " + msg.what + ",  index=" + msg.obj);
                            }
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    LogUtil.e(e);
                }
            }
        };
    }


    /**
     * 新事件触发
     */
    public void onNewEvent(T t) {
        if (mWorkHandler != null) {
            Message message = mWorkHandler.obtainMessage(EVENT_COME, t);
            mWorkHandler.removeMessages(EVENT_LAST);
            mWorkHandler.removeMessages(EVENT_COME);
//            LogUtil.e(TAG, "\nonNewEvent index=" + t);
            mWorkHandler.sendMessage(message);
        }
    }


    public void stopAndDestroyAll() {
        if (mWorkHandler != null) {
            mWorkHandler.removeCallbacksAndMessages(null);
            mWorkHandler = null;
        }
    }

    public interface EventTrigger<T> {
        void onEvent(T t);
    }
}