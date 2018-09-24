package cn.yinxm.pc.iml;

import cn.yinxm.lib.utils.log.LogUtil;
import cn.yinxm.pc.IConsumer;

/**
 * Created by yinxuming on 2018/6/12.
 */
public abstract class AbstractConsumerRunnable implements IConsumer, Runnable {

    @Override
    public void run() {
        try {
            while (consumeConditionIsOk() && !Thread.currentThread().isInterrupted()) {
                consume();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e);
        }
    }

    /**
     * 满足运行条件
     *
     * @return
     */
    protected abstract boolean consumeConditionIsOk();
}
