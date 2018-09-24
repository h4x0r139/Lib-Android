package cn.yinxm.pc.iml;

import cn.yinxm.lib.utils.log.LogUtil;
import cn.yinxm.pc.IProducer;

/**
 * Created by yinxuming on 2018/6/12.
 */
public abstract class AbstractProducerRunnable implements IProducer, Runnable {
    @Override
    public void run() {
        try {
            while (produceConditionIsOk() && !Thread.currentThread().isInterrupted()) {
                produce();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e);
        }
    }

    /**
     * 满足生产条件
     *
     * @return
     */
    protected abstract boolean produceConditionIsOk();
}
