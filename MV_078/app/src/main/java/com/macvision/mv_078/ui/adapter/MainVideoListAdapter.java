package com.macvision.mv_078.ui.adapter;/**
 * Created by bzmoop on 2016/8/4 0004.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.macvision.mv_078.Constant;
import com.macvision.mv_078.R;
import com.macvision.mv_078.model.entity.VideoEntity;
import com.macvision.mv_078.util.ImageFromFileCache;
import com.macvision.mv_078.util.ScreenUtils;
import com.macvision.mv_078.util.TaskUtils;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.LruCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.videopls.venvy.e.a.T;

/**
 * 作者：LiangXiong on 2016/8/4 0004 21:49
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class MainVideoListAdapter extends RecyclerView.Adapter<MainVideoListAdapter.ViewHolderItem>{
    private List<VideoEntity.VideolistEntity> mVideoList;
    private Context mContext;
    private static IClickMainItem mIClickItem;
    private String imageCache = null;
    private LruCache lruCache;

    public MainVideoListAdapter(Context mContext,List<VideoEntity.VideolistEntity> mVideoList){
        this.mContext = mContext;
        mVideoList = new ArrayList<>();
        this.mVideoList=mVideoList;
        lruCache = new LruCache(mContext);
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
    }

    public void update(List<VideoEntity.VideolistEntity> data) {
        mVideoList.addAll(data);
        Logger.i( "列表现有: "+mVideoList.size()+"条");
        notifyDataSetChanged();
    }

    public void updateWithClear(List<VideoEntity.VideolistEntity> data){
        mVideoList.clear();
        update(data);
    }

    @Override
    public MainVideoListAdapter.ViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.mainvideolist_item,parent,false);
        return new ViewHolderItemVideo(view)    ;
    }

    @Override
    public void onBindViewHolder(MainVideoListAdapter.ViewHolderItem holder, int position) {
        holder.bindItem(mContext,mVideoList.get(position));

    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    abstract  class ViewHolderItem extends RecyclerView.ViewHolder{
        public ViewHolderItem(View itemView) {
            super(itemView);
        }
        abstract void bindItem(Context context, VideoEntity.VideolistEntity videolistEntity);
    }

    class ViewHolderItemVideo extends ViewHolderItem {
        @Bind(R.id.tv_videoid)
        TextView tv_videoid;
        @Bind(R.id.btn_zan)
        TextView btn_zan;
        @Bind(R.id.linear_parent)
        LinearLayout FrameLayout;
        @Bind(R.id.image_thumb)
        ImageView image_thumb;

        public ViewHolderItemVideo(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }


        @Override
        void bindItem(Context context, VideoEntity.VideolistEntity videoEntity) {
            tv_videoid.setText(videoEntity.getVideoId());

            btn_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_zan.setEnabled(true);
                    if (btn_zan.isEnabled())
                        btn_zan.setEnabled(false);
                    else
                        btn_zan.setEnabled(true);
                }
            });
            FrameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIClickItem.onClickItemVideo(v,videoEntity);
                }
            });
            Bitmap bitmap=null;
            Picasso.with(mContext)
                    .load("http://192.168.1.124/default.jpg")
//                    .placeholder(R.mipmap.zan_normal)
                    .resize(ScreenUtils.getScreenWidth(mContext),ScreenUtils.getScreenHeight(mContext)/3)
                    .error(R.mipmap.zan_pressed)
                    .into(image_thumb);
            //显示图片来源标记
            Picasso.with(mContext). setIndicatorsEnabled(true);
//            Picasso.with(mContext).load("http://192.168.1.124/aaaa/zm.mp4").into(image_thumb);
//            Picasso.with(mContext).load(String.valueOf(createVideoThumbnail("http://192.168.1.124/aaaa/zm.mp4",60,60))).into(image_thumb);
//            cacheImage(Constant.BaseVideoListUrl+videoEntity.getVideoLocation(),image_thumb);
        }

    }

    public void setClickItem(IClickMainItem IClickItem){
        mIClickItem = IClickItem;
    }
    public interface IClickMainItem{
        void onClickItemVideo(View view, VideoEntity.VideolistEntity videoEntity);
    }
    private void test(ImageView view){
        Picasso.with(mContext).load("http://192.168.1.124/aaaa/zm.mp4").into(view);
    }

    private void cacheImage(final  String url,ImageView image){
        TaskUtils.executeAsyncTask(new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap  doInBackground(String... params) {
                Bitmap bmp = ImageFromFileCache.getImage(url);
                if (bmp == null) {
                    ImageFromFileCache.createVideoThumbnail(url,80,80);
                }
                if (bmp == null) {
                    return bmp;
                }
                return bmp;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (result != null) {
                    image.setImageBitmap(result);
                }
            }
        });
    }




}
