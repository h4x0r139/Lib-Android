package cn.yinxm.pc.iml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import cn.yinxm.pc.CustomMessage;
import cn.yinxm.pc.IModel;
import cn.yinxm.pc.ProducerConsumerManager;

/**
 * Created by yinxuming on 2018/6/12.
 * 实现2：利用wait、notify来取代缓冲区，实现并发和容量控制，控制生产消费的进行
 *
 */
public class WaitNotifyModel implements IModel {
    private final Object BUFFER_LOCK = new Object();
    private final Queue<CustomMessage> mQueue = new LinkedList<>();
    private final int cap;
    private final AtomicInteger increTaskNo = new AtomicInteger(0);

    ProducerConsumerManager.ProcessInfoCallback callback;
    private boolean isCanceled = false;
    private DateFormat mDateFormat;



    public WaitNotifyModel(int cap, ProducerConsumerManager.ProcessInfoCallback callback) {
        this.cap = cap;
        this.callback = callback;
        mDateFormat = new SimpleDateFormat("ss:SSS ");
    }

    @Override
    public Runnable newProducerRunnable() {
        return new ProducerIml();
    }

    @Override
    public Runnable newConsumerRunnable() {
        return new ConsumerIml();
    }

    @Override
    public void stop() {
        isCanceled = true;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public Queue<CustomMessage> getQueue() {
        return mQueue;
    }

    private class ProducerIml extends AbstractProducerRunnable {

        @Override
        protected boolean produceConditionIsOk() {
            // 外部条件是否满足运行，比如时间、数量、时序
            return !isCanceled();
        }

        @Override
        public void produce() throws Exception {

            // 不定期生产，模拟随机的用户请求
            Thread.sleep((long) (Math.random() * 1000));
            synchronized (BUFFER_LOCK) {
                callback.addProcessInfo(mDateFormat.format(new Date()).toString() + Thread.currentThread().getName() + "......offer prepare wait, size=" + getQueue().size() +"\n");
                while (mQueue.size() == cap) {
                    BUFFER_LOCK.wait();
                }
                CustomMessage message = new CustomMessage(increTaskNo.getAndIncrement());
                getQueue().offer(message);
                callback.addProcessInfo(mDateFormat.format(new Date()).toString() + Thread.currentThread().getName() + "......offer done, size=" + getQueue().size() + ", " + message + "\n");
                BUFFER_LOCK.notifyAll();
            }

        }
    }

    private class ConsumerIml extends AbstractConsumerRunnable {

        @Override
        protected boolean consumeConditionIsOk() {
            // 外部条件是否满足运行，比如时间、数量、时序
            return !isCanceled();
        }

        @Override
        public void consume() throws Exception {
            synchronized (BUFFER_LOCK) {
                callback.addProcessInfo("\n"+mDateFormat.format(new Date()).toString() + Thread.currentThread().getName() + "......poll prepare wait, size=" + getQueue().size() + "\n");

                // 没有可消费的产品，则等待
                while (mQueue.size() == 0) {
                    BUFFER_LOCK.wait();
                }

                // 有可消费的产品，开始出队处理
                CustomMessage message = mQueue.poll();

                if (message != null) {
                    callback.addProcessInfo(mDateFormat.format(new Date()).toString() + Thread.currentThread().getName() + "......poll done, size=" + getQueue().size() + ", " + message + "\n");
                    // 固定时间范围的消费，模拟相对稳定的服务器处理过程
                    Thread.sleep(500 + (long) (Math.random() * 500));
                    BUFFER_LOCK.notifyAll();
                }

            }
        }
    }

//    public static void main(String[] args) {
//        IModel model = new WaitNotifyModel(3);
//        for (int i = 0; i < 2; i++) {
//            new Thread(model.newConsumerRunnable()).start();
//        }
//        for (int i = 0; i < 5; i++) {
//            new Thread(model.newProducerRunnable()).start();
//        }
//    }
}
