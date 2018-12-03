package cn.yinxm.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.yinxm.FrequentEventQueue;
import cn.yinxm.test.R;


/**
 * Created by yinxuming on 2018/6/8. 频繁点击拖动问题解决
 * 1、耗时操作方子线程执行
 * 2、事件消息到来，放入队列前，先清空队列里面未处理的消息
 */
public class FrequentClicksFragment extends Fragment {

    private Button btnManyClicks;
    private TextView tvResult;

    private int index = 0;

    DateFormat mDateFormat = new SimpleDateFormat("hh:mm:ss");

    ExecutorService mSingleThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());

    private Handler mHandler, mWorkHandler;
    private static final int MSG_CLICK = 1;

    FrequentEventQueue mFrequentEventQueue;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frequent_clicks, container, false);
        btnManyClicks = (Button) view.findViewById(R.id.btnManyClicks);
        tvResult = (TextView) view.findViewById(R.id.tvResult);

        initData();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_CLICK:
                        Log.d("yinxm", "3, " + Thread.currentThread().getName() + ", hash=" + msg.hashCode() + ", " + msg);
                        tvResult.append("\n" + mDateFormat.format(new Date()).toString() + ", result=" + msg.arg1);
                        break;
                }
            }
        };


        mFrequentEventQueue = new FrequentEventQueue(1000, new FrequentEventQueue.EventTrigger<Integer>() {
            @Override
            public void onEvent(final Integer integer) {
                Log.d("yinxm", "onEvent " + Thread.currentThread().getName() + ", " + integer);
//                requestNeeTime(integer);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvResult.append("\n" + mDateFormat.format(new Date()).toString() + ", result=" + integer);
                    }
                });

            }
        }, true);

        mFrequentEventQueue.start();

        return view;
    }

    private void initData() {

        btnManyClicks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                tvResult.append("\n" + mDateFormat.format(new Date()).toString() + " " + index);
                // 问题：UI 无响应
//                clickRequestDirect();
                // 问题：延迟严重
//                clickRequestThread();

//                clickRequestDiscardData();

                // 频繁处理接口封装
                mFrequentEventQueue.onNewEvent(index);

            }
        });

        Log.d("yinxm", "0, " + Thread.currentThread().getName());

        mSingleThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                Log.d("yinxm", "1, " + Thread.currentThread().getName());
                Looper.prepare();
                mWorkHandler = new Handler() {
                    @Override
                    public void handleMessage(final Message msg) {
                        switch (msg.what) {
                            case MSG_CLICK:
                                // 子线程
                                Log.d("yinxm", "2, " + Thread.currentThread().getName() + ", index=" + index + ", " + msg + "\narg1=" + msg.arg1 + ", hash=" + msg.hashCode());

                                // 收到消息，执行耗时操作
                                requestNeeTime(msg.arg1);

                                // 回到主线程更新UI，打印出来的msg.arg1始终为0，不能这么做
//                                mHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Log.d("yinxm", "3, " + Thread.currentThread().getName()+", hash="+msg.hashCode()+", "+msg);
//                                        tvResult.append("\n" + mDateFormat.format(new Date()).toString() + ", result=" + msg.arg1);
//                                    }
//                                });
//                                mHandler.sendMessage(Message.obtain(msg));
                                Message message = new Message();
                                message.what = MSG_CLICK;
                                message.arg1 = msg.arg1;
                                mHandler.sendMessage(message);
                                break;
                            default:
                                break;
                        }
                    }
                };
                Looper.loop();
            }
        });
    }

    /**
     * 方案三：点击时将消息放入队列前，先清空队列里面未处理的消息
     */
    private void clickRequestDiscardData() {
        Message message = Message.obtain();
        message.what = MSG_CLICK;
        message.arg1 = ++index;
        mWorkHandler.removeMessages(MSG_CLICK);
        mWorkHandler.sendMessage(message);
    }

    /**
     * 方案二：子线程执行耗时操作，延迟严重
     */
    private void clickRequestThread() {

        mSingleThreadPool.submit(new Runnable() {
            @Override
            public void run() {

                requestNeeTime(++index);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResult.append("\n" + mDateFormat.format(new Date()).toString() + ", result=" + index);
                    }
                });
            }
        });
    }

    /**
     * 方案一：直接点击，卡UI，延迟
     */
    private void clickRequestDirect() {
        requestNeeTime(++index);
        tvResult.append("\n" + mDateFormat.format(new Date()).toString() + ", result=" + index);
    }

    /**
     * 模拟耗时操作
     */
    private void requestNeeTime(int index) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
