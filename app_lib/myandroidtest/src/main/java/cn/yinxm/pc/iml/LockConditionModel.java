package cn.yinxm.pc.iml;

import cn.yinxm.pc.IModel;

/**
 * Created by yinxuming on 2018/6/12.
 * 实现3：通过concurrent里面的lock和Condition实现并发和容量控制
 */
public class LockConditionModel implements IModel {
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
