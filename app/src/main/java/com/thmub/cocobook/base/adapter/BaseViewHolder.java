package com.thmub.cocobook.base.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhouas666 on 18-2-17.
 */

public class BaseViewHolder<T> extends RecyclerView.ViewHolder{
    public IViewHolder<T> holder;

    public BaseViewHolder(View itemView, IViewHolder<T> holder) {
        super(itemView);
        this.holder = holder;
        holder.initView();
    }
}
