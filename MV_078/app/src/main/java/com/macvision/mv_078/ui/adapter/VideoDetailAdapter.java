package com.macvision.mv_078.ui.adapter;/**
 * Created by bzmoop on 2016/8/16 0016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.macvision.mv_078.R;
import com.macvision.mv_078.model.entity.CommentEntity;
import com.macvision.mv_078.model.entity.VideoEntity;


import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.video.venvy.param.JjVideoView;
import cn.com.videopls.venvy.e.a.T;
import cn.com.videopls.venvy.view.C;

/**
 * 作者：LiangXiong on 2016/8/16 0016 13:54
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class VideoDetailAdapter extends RecyclerView.Adapter<VideoDetailAdapter.ViewHolderItem> {
    private Map<String, List> map;
    List<CommentEntity.DataBean> mCommentEntity;
    VideoEntity.VideolistEntity mVideoList;

    private Context context;
    int viewTyPe;
    public static int position;

    public VideoDetailAdapter(Context context, List<CommentEntity.DataBean> mCommentEntity, VideoEntity.VideolistEntity mVideoList) {
        this.mCommentEntity = mCommentEntity;
        this.context = context;
        this.mVideoList = mVideoList;
    }

    public enum EItemType {
        ITEM_TYPE_HEAD,
        ITEM_TYPE_COMMENT;
    }

    @Override
    public int getItemViewType(int position) {
        viewTyPe = position;
        if (position == 0) {
            return position;
        } else
            return position;
    }

    @Override
    public ViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.videodetails_header_item, null);
            return new ViewHolderItemDetailHead(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_videodetail_comment, null);
            return new ViewHolderItemComment(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolderItem holder, int position) {
        this.position = position;
        if (viewTyPe == 0)
            holder.bindItem(context, null, mVideoList);
        else
            holder.bindItem(context, mCommentEntity.get(position - 1),null);
    }

    @Override
    public int getItemCount() {
        return mCommentEntity.size() + 1;
    }

    abstract static class ViewHolderItem extends RecyclerView.ViewHolder {
        public ViewHolderItem(View itemView) {
            super(itemView);
        }
        abstract void bindItem(Context context, CommentEntity.DataBean commentEntity,VideoEntity.VideolistEntity mVideoList);
    }

    static class ViewHolderItemDetailHead extends ViewHolderItem {
        @Bind(R.id.btn_ping_detail)
        Button btn_ping;
        @Bind(R.id.tv_pingCount_detail)
        TextView tv_pingCount;
        @Bind(R.id.btn_zan_detail)
        Button btn_zan;
        @Bind(R.id.tv_zanCount_detail)
        TextView tv_zanCount;
        @Bind(R.id.btn_fen_detail)
        Button btn_fen;
        @Bind(R.id.tv_videoState_detail)
        TextView tv_videoState;
        @Bind(R.id.tv_pingCount2_detail)
        TextView tv_pingCount2;
        @Bind(R.id.tv_type_detail)
        TextView tv_type;

        public ViewHolderItemDetailHead(View itemView) {
            super(itemView);
        }
        @Override
        void bindItem(Context context, CommentEntity.DataBean commentEntity,VideoEntity.VideolistEntity mVideoList) {
            ButterKnife.bind(this, itemView);
            tv_pingCount.setText(mVideoList.getVideoCommentNumber());
            tv_zanCount.setText(mVideoList.getVideoLikesNumber());
            tv_videoState.setText(mVideoList.getVideoCaption());
            tv_pingCount2.setText(mVideoList.getVideoCommentNumber());
            tv_type.setText(mVideoList.getVideoType());
        }
    }

    static class ViewHolderItemComment extends ViewHolderItem {
        @Bind(R.id.tv_name)
        TextView tv_name;
        @Bind(R.id.tv_comment)
        TextView tv_comment;

        public ViewHolderItemComment(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindItem(Context context, CommentEntity.DataBean commentEntity,VideoEntity.VideolistEntity mVideoList) {
            tv_comment.setText(commentEntity.getCmContent());
        }
    }
}
