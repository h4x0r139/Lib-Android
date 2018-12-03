package cn.yinxm.pc;

/**
 * Created by yinxuming on 2018/6/12.
 */
public interface IView {
    /**
     * 清空处理进度信息
     */
    void clearProcessInfo();

    /**
     * 添加处理进度信息
     * @param text
     */
    void addProcessInfo(String text);
}
