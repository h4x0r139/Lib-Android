package cn.yinxm.pc.iml;

import cn.yinxm.pc.IModel;

/**
 * Created by yinxuming on 2018/6/12.
 * 实现4：通过concurrent里面的lock和Condition实现并发和容量控制，性能更高的实现方式
 */
public class LockConditionBetterModel implements IModel {

    @Override
    public Runnable newProducerRunnable() {
        return null;
    }

    @Override
    public Runnable newConsumerRunnable() {
        return null;
    }

    @Override
    public void stop() {

    }
}
