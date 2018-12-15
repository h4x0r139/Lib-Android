package cn.yinxm.lib.view.recyclerview.decoration;


public interface DecorStrategy {
    boolean hasDecor(int position, int itemCount, int headerCount, int footerCount);
}
