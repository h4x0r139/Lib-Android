package cn.yinxm.pc.iml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import cn.yinxm.lib.utils.log.LogUtil;
import cn.yinxm.pc.CustomMessage;
import cn.yinxm.pc.IModel;
import cn.yinxm.pc.ProducerConsumerManager;

/**
 * Created by yinxuming on 2018/6/12.
 * 实现1：同步阻塞队列作为缓冲区，平衡生产消费的执行
 */
public class BlockingQueueModel implements IModel {

    private AtomicInteger mIndex;
    private BlockingQueue<CustomMessage> mQueue;

    ProducerConsumerManager.ProcessInfoCallback callback;

    private DateFormat mDateFormat;
    private boolean isCanceled = false;


    public BlockingQueueModel(int capacity, ProducerConsumerManager.ProcessInfoCallback callback) {
        mIndex = new AtomicInteger(0);
        // LinkedBlockingQueue 的队列不 init，入队时检查容量；ArrayBlockingQueue 在创建时 init
        mQueue = new LinkedBlockingQueue<>(capacity);
        this.callback = callback;
        mDateFormat = new SimpleDateFormat("ss:SSS ");
    }

//    private static BlockingQueueModel getInstance() {
//        return BlockingQueueModelHolder.INSTANCE;
//    }
//
//    private static class BlockingQueueModelHolder {
//        private static final BlockingQueueModel INSTANCE = new BlockingQueueModel();
//    }

    @Override
    public Runnable newProducerRunnable() {
        return new ProducerIml();
    }

    @Override
    public Runnable newConsumerRunnable() {
        return new ConsumerIml();
    }

    public BlockingQueue<CustomMessage> getQueue() {
        return mQueue;
    }

    public int getNextIndex() {
        return mIndex.getAndIncrement();
    }


    public void start() {

    }

    @Override
    public void stop() {
        isCanceled = true;
    }

    public boolean isCanceled() {
        return isCanceled;
    }


    private class ProducerIml extends AbstractProducerRunnable {

        @Override
        protected boolean produceConditionIsOk() {
            // 外部条件是否满足运行，比如时间、数量、时序
            return !isCanceled();
        }

        @Override
        public void produce() throws Exception {
            CustomMessage message = new CustomMessage(getNextIndex());
//            Log.d("yinxm", "——————》producer index=" + message.index);
            // 如果调用interrupt，这里在睡眠的话，会直接抛出InterruptException异常
            Thread.sleep(1000 + (int) Math.random() * 1000);

            callback.addProcessInfo(mDateFormat.format(new Date()).toString() + Thread.currentThread().getName() + "......put prepare, size=" + getQueue().size() + ", " + message + "\n");
            getQueue().put(message);
            callback.addProcessInfo(mDateFormat.format(new Date()).toString() + Thread.currentThread().getName() + "......put done, size=" + getQueue().size() + ", " + message + "\n");
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
            BlockingQueue<CustomMessage> queue = getQueue();
            callback.addProcessInfo("\n"+mDateFormat.format(new Date()).toString() + Thread.currentThread().getName() + "......take prepare, size=" + getQueue().size() + "\n");
            CustomMessage message = queue.take();
            callback.addProcessInfo(mDateFormat.format(new Date()).toString() + Thread.currentThread().getName() + "......take done, size=" + getQueue().size() + ", " + message + "\n");

            if (message != null) {
                    Thread.sleep(1000 + (int) Math.random() * 1000);
//                Thread.yield();
//                Log.d("yinxm", "——————》consumer index=" + message.index);

            } else {
                LogUtil.e("message is null");
            }
        }
    }
}
