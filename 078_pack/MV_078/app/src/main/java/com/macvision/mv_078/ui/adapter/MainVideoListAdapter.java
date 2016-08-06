package com.macvision.mv_078.ui.adapter;/**
 * Created by bzmoop on 2016/8/4 0004.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.macvision.mv_078.R;
import com.macvision.mv_078.data.entity.VideoMain;
import com.macvision.mv_078.ui.activity.BaseActivity;
import com.macvision.mv_078.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：LiangXiong on 2016/8/4 0004 21:49
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class MainVideoListAdapter extends RecyclerView.Adapter<MainVideoListAdapter.ViewHolderItem>{
    private List<VideoMain> mVideoList;
    private Context mContext;
    private static IClickMainItem mIClickItem;

    public MainVideoListAdapter(Context context){
        mContext = context;
        mVideoList = new ArrayList<>();

    }

    public void update(List<VideoMain> data) {
        mVideoList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public MainVideoListAdapter.ViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.mainvideolist_item,null);
        return new ViewHolderItemVideo(view)    ;
    }

    @Override
    public void onBindViewHolder(MainVideoListAdapter.ViewHolderItem holder, int position) {
        holder.bindItem(mContext,mVideoList.get(position));
    }

    @Override
    public int getItemCount() {
        Log.i("moop", "getItemCount: "+mVideoList.size());
        return mVideoList.size();
    }

    abstract static class ViewHolderItem extends RecyclerView.ViewHolder{
        public ViewHolderItem(View itemView) {
            super(itemView);
        }
        abstract void bindItem(Context context, VideoMain videoMain);
    }

    static class ViewHolderItemVideo extends ViewHolderItem {
        @Bind(R.id.tv_title)
        TextView tv_title;
        @Bind(R.id.tv_videoCaption)
        TextView tv_videoCaption;
        @Bind(R.id.tv_videoReleaseAddress)
        TextView tv_videoReleaseAddress;
        @Bind(R.id.image_zan)
        ImageView image_zan;
        @Bind(R.id.linear_parent)
        LinearLayout linear_parent;

        public ViewHolderItemVideo(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        void bindItem(Context context, VideoMain videoMain) {
            tv_title.setText(videoMain.getVideoTitle());
            tv_videoCaption.setText(videoMain.getVideoCaption());
            tv_videoReleaseAddress.setText(videoMain.getVideoReleaseAddress());
            image_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image_zan.setEnabled(false);
                }
            });
            linear_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIClickItem.onClickItemVideo(videoMain,v);
                }
            });
        }
    }
    public void setClickItem(IClickMainItem IClickItem){
        mIClickItem = IClickItem;
    }
    public interface IClickMainItem{
        void onClickItemVideo(VideoMain video,View view);
    }

}
