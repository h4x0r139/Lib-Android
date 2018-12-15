package cn.yinxm.lib.view.recyclerview;

import android.view.View;

/**
 * description : {@code ViewHolder}的工厂类，用于生成{@code ViewHolder}
 */
public abstract class BaseViewHolderFactory<VH> {
    /**
     * 构造{@code ViewHolder}
     *
     * @param itemView 条目的视图
     * @param viewType 条目的布局类型
     * @return {@code ViewHolder}
     */
    public abstract VH createViewHolder(View itemView, int viewType);
}
