package cn.yinxm.pc;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.yinxm.pc.iml.BlockingQueueModel;
import cn.yinxm.pc.iml.WaitNotifyModel;

/**
 * Created by yinxuming on 2018/6/11.
 * 生产消费管理器
 */
public class ProducerConsumerManager {

    private ProcessInfoCallback mProcessInfoCallback;
    private IView mView;

    private Handler mHandler;

    private IModel mModel;
    private List<Thread> producerList = new ArrayList<>();
    private List<Thread> consumerList = new ArrayList<>();

    private ProducerConsumerManager() {
        mHandler = new Handler(Looper.getMainLooper());

        mProcessInfoCallback = new ProcessInfoCallback() {
            @Override
            public void addProcessInfo(final String text) {
                Log.d("yinxm", text);

                if (mView != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mView.addProcessInfo(text);
                        }
                    });
//                    mView.addProcessInfo(text);
                }
            }
        };
    }

    public static ProducerConsumerManager getInstance() {
        return ProducerConsumerManagerHolder.INSTANCE;
    }

    private static final class ProducerConsumerManagerHolder {
        private static ProducerConsumerManager INSTANCE = new ProducerConsumerManager();
    }


    public void stop() {


//        try {
//            for (Thread thread : producerList) {
//                thread.interrupt();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            for (Thread thread : consumerList) {
//                thread.interrupt();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        mModel.stop();
        if (mView != null) {
            mView.addProcessInfo("任务已停止...");
            this.mView = null;
        }

    }


    public void startBlockingQueueModel(int producers, int consumers, int capacity, IView view) {
        this.mView = view;

        mModel = new BlockingQueueModel(capacity, mProcessInfoCallback);
        mProcessInfoCallback.addProcessInfo("容器size=" + capacity + " 初始化成功");
        for (int i = 0; i < consumers; i++) {
            Thread thread = new Thread(mModel.newConsumerRunnable());
            consumerList.add(thread);
            thread.start();
        }
        for (int i = 0; i < producers; i++) {
            Thread thread = new Thread(mModel.newProducerRunnable());
            producerList.add(thread);
            thread.start();
        }
    }

    public void startWaitNotifyModel(int producers, int consumers, int capacity, IView view) {
        this.mView = view;

        mModel = new WaitNotifyModel(capacity, mProcessInfoCallback);
        mProcessInfoCallback.addProcessInfo("容器size=" + capacity + " 初始化成功");
        for (int i = 0; i < consumers; i++) {
            Thread thread = new Thread(mModel.newConsumerRunnable());
            consumerList.add(thread);
            thread.start();
        }
        for (int i = 0; i < producers; i++) {
            Thread thread = new Thread(mModel.newProducerRunnable());
            producerList.add(thread);
            thread.start();
        }
    }

    /**
     * 处理进度回调
     */
    public interface ProcessInfoCallback {
        /**
         * 增加处理进度
         *
         * @param text
         */
        void addProcessInfo(String text);
    }
}
