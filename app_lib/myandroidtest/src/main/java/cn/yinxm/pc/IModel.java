package cn.yinxm.pc;

/**
 * Created by yinxuming on 2018/6/12.
 * 不同模型具体生产消费实现不同，抽象出公共接口
 */
public interface IModel {
    /**
     *  新的生产任务
     * @return
     */
    Runnable newProducerRunnable();

    /**
     * 新的消费任务
     * @return
     */
    Runnable newConsumerRunnable();

    /**
     * 停止运行
     */
    void stop();
}
