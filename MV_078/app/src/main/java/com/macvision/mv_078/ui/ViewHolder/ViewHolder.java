package com.macvision.mv_078.ui.ViewHolder;/**
 * Created by bzmoop on 2016/8/6 0006.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者：LiangXiong on 2016/8/6 0006 16:37
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class ViewHolder extends RecyclerView.ViewHolder{
    private SparseArray<View> mViews;
    private View mConvertView;
    private Context mContext;

    public ViewHolder(Context mContext, View itemView, ViewGroup parent) {
        super(itemView);
        this.mContext = mContext;
        this.mConvertView = itemView;
        mViews = new SparseArray<View>();
    }
    public static ViewHolder get(Context context, ViewGroup parent, int layoutId){
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(context, itemView ,parent);
        return holder;
    }

    public <T extends View> T getView(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
}
