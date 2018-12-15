package cn.yinxm.lib.view.recyclerview;

import android.view.ViewGroup;

/**
 * description : 条目点击事件监听器
 */
public interface OnItemClickListener<T> {
    /**
     * 列表条目的点击事件
     *
     * @param parent   条目所在的父布局
     * @param position 点击的Item的位置
     * @param t        点击的item对应的数据
     */
    void onItemClick(ViewGroup parent, int position, T t);
}
