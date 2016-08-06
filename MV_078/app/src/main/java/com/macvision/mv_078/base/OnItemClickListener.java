package com.macvision.mv_078.base;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bzmoop on 2016/8/11 0011.
 */
public interface OnItemClickListener<T> {
    void onItemClick(ViewGroup parent, View view, T t, int position);
    boolean onItemLongClick(ViewGroup parent, View view, T t, int position);


}
