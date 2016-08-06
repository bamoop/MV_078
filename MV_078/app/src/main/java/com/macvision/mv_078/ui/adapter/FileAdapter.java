package com.macvision.mv_078.ui.adapter;/**
 * Created by bzmoop on 2016/8/14 0014.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.macvision.mv_078.R;
import com.macvision.mv_078.model.entity.FileEntity;
import com.macvision.mv_078.util.ImageFromFileCache;
import com.macvision.mv_078.util.SlideRelativeLayout;
import com.macvision.mv_078.util.TaskUtils;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者：LiangXiong on 2016/8/14 0014 20:44
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolderItem> {
    public List<FileEntity> mFileList;
    private Context mContext;
    public static final int NORMAL = 1000;
    public static final int SLIDE = 2000;
    private int mState = NORMAL;
    private List<ViewHolderItemVideo> mViewHolderItemVideos = new ArrayList<>();

    public FileAdapter(Context context, List<FileEntity> mFileList) {
        this.mContext = context;
        this.mFileList = mFileList;
    }

    public void update(List<FileEntity> data) {
        mFileList.addAll(data);
        Logger.i("列表现有: " + mFileList.size() + "条");
        notifyDataSetChanged();
    }

    public void updateWithClear(List<FileEntity> data) {
        mFileList.clear();
        update(data);
    }

    public void openItemAnimation() {
        mState = SLIDE;
        for (ViewHolderItemVideo holder : mViewHolderItemVideos) {
            holder.openItemAnimation();
        }
    }

    public void closeItemAnimation() {
        mState = NORMAL;
        for (ViewHolderItemVideo holder : mViewHolderItemVideos) {
            holder.closeItemAnimation();
        }
    }

    @Override
    public FileAdapter.ViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.filelist_item, parent, false);

        return new ViewHolderItemVideo(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderItem holder, int position) {
        holder.bindItem(mContext, mFileList.get(position));
    }


    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    abstract class ViewHolderItem extends RecyclerView.ViewHolder {
        public ViewHolderItem(View itemView) {
            super(itemView);
        }

        abstract void bindItem(Context context, FileEntity fileEntity);
    }

    class ViewHolderItemVideo extends ViewHolderItem implements View.OnClickListener {
        @Bind(R.id.image_fileThumb)
        ImageView image_thumb;
        @Bind(R.id.tv_fileName)
        TextView tv_name;
        private SlideRelativeLayout mSlideRelativeLayout;
        private CheckBox mCheckBox;


        public ViewHolderItemVideo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mSlideRelativeLayout = (SlideRelativeLayout) itemView.findViewById(R.id.item_root);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
            itemView.setOnClickListener(this);
        }

        @Override
        void bindItem(Context context, FileEntity fileEntity) {
            tv_name.setText(fileEntity.getNaame());
            String path = ImageFromFileCache.getPath(fileEntity.getPath());
            if (path != null) {
                Picasso.with(mContext).load(new File(path)).resize(60, 60).centerCrop().into(image_thumb);
            } else
                cacheImage(fileEntity.getPath(), image_thumb);

            mCheckBox.setChecked(fileEntity.isChecked());
            switch (mState) {
                case NORMAL:
                    mSlideRelativeLayout.close();
                    break;

                case SLIDE:
                    mSlideRelativeLayout.open();
                    break;
            }
        }

        public void openItemAnimation() {
            mSlideRelativeLayout.openAnimation();
        }

        public void closeItemAnimation() {
            mSlideRelativeLayout.closeAnimation();
        }

        public void setCheckBox() {
            mCheckBox.setChecked(!mCheckBox.isChecked());
//            mItemBean.setChecked(mCheckBox.isChecked());
        }

        @Override
        public void onClick(View v) {
            setCheckBox();
        }
    }

    private void cacheImage(final String url, ImageView image) {
        TaskUtils.executeAsyncTask(new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bmp = ImageFromFileCache.createVideoThumbnail(url, 60, 60);
                return bmp;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (result != null) {
//                    image.setImageBitmap(result);
                    Picasso.with(mContext).load(new File(ImageFromFileCache.getPath(url))).resize(80, 80).centerCrop().into(image);

                }
            }
        });
    }

}
