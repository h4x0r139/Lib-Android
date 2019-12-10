package cn.yinxm.lib.utils.event;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import cn.yinxm.lib.utils.log.LogUtil;


/**
 * 解决频繁触发回调，更新UI导致的ANR等问题
 * <p>
 * 频繁触发事件，用队列按最小处理时间间隔 去触发 丢弃部分事件
 * 注意：只会丢弃中间事件，最终结果会保持和实际一致
 * 例如，规定最多1s内触发1个事件，实际用户在10s内触发了20个事件，本应用只会返回10个触发事件
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
     * 是否在新的子线程处理事件， 默认是，会新建一个线程
     */
    private boolean mIsProcessOnNewThread = true;

    /**
     * 是否在子线程执行最终回调任务
     */
    private boolean mIsCallbackOnWorkThread;


    /**
     * 事件待触发的任务
     */
    private EventTrigger mEventTrigger;

    private long mLastProcessedTime = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Handler mWorkHandler;

    public FrequentEventQueue(int minInterval, EventTrigger eventTrigger, boolean isCallbackOnWorkThread) {
        this(minInterval, eventTrigger, isCallbackOnWorkThread, true);
    }

    /**
     * @param minInterval            处理最小间隔
     * @param eventTrigger           处理回调
     * @param isCallbackOnWorkThread 结果回调是否在子线程执行
     * @param isProcessOnNewThread   队列处理是否在子线程执行
     */
    public FrequentEventQueue(int minInterval, EventTrigger eventTrigger, boolean isCallbackOnWorkThread, boolean isProcessOnNewThread) {
        mMinInterval = minInterval;
        mEventTrigger = eventTrigger;
        mIsCallbackOnWorkThread = isCallbackOnWorkThread;
        mIsProcessOnNewThread = isProcessOnNewThread;
    }

    /**
     * 在子线程中调用该方法，启动处理
     */
    public void start() {
        if (mWorkHandler == null) {
            if (mIsProcessOnNewThread) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        initWorkHanlder();
                        Looper.loop();
                    }
                }).start();
            }
        } else {
            initWorkHanlder();
        }

    }

    private void initWorkHanlder() {
        mWorkHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    switch (msg.what) {
                        case EVENT_LAST:
                        case EVENT_COME:
                            long currentTime = SystemClock.uptimeMillis();  //
                            // 不用currentTimeMillis，防止系统时间调整影响队列运行
                            if ((currentTime - mLastProcessedTime) < mMinInterval) {
//                                LogUtil.e(TAG,"Ignore calls that are too frequent ... ");
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
                            if (!mIsCallbackOnWorkThread) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mEventTrigger.onEvent(obj);
                                    }
                                });
                            } else {
                                mEventTrigger.onEvent(obj);
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