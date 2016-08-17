package com.macvision.mv_078.util;/**
 * Created by bzmoop on 2016/8/9 0009.
 */

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * 作者：LiangXiong on 2016/8/9 0009 19:52
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class ImageFromFileCache {
    private static final String CACHDIR = "mimu/ImgCach";
    private static final String WHOLESALE_CONV = ".jpg";

    private static final int MB = 1024 * 1024;
    private static final int CACHE_SIZE = 10;
    private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;

    private static ImageFromFileCache ImageFormFileCacheInstance = null;

    public ImageFromFileCache() {
        //清理文件缓存
        removeCache(getDirectory());
    }

    /**
     * 从缓存中获取图片
     **/
    public static Bitmap getImage(final String url) {
        if (ImageFormFileCacheInstance == null) {
            ImageFormFileCacheInstance = new ImageFromFileCache();
        }

        final String path = ImageFormFileCacheInstance.getDirectory() + "/" + ImageFormFileCacheInstance.convertUrlToFileName(url);
        // Log.i("ImageFileCache", "getImage filepath:" + path);
        // Log.i("ImageFileCache", "getImage url:" + url);
        File file = new File(path);
        if (file.exists()) {
            Log.i("ImageFileCache", "getImage file exists");
            Bitmap bmp = BitmapFactory.decodeFile(path);
            if (bmp == null) {
                file.delete();
            } else {
                ImageFormFileCacheInstance.updateFileTime(path);
                return bmp;
            }
        }
        return null;
    }

    /**
     * 获取缓存中图片的路径
     **/
    public static String getPath(String url) {
        String cachePath = null;
        if (ImageFormFileCacheInstance == null) {
            ImageFormFileCacheInstance = new ImageFromFileCache();
        }
        cachePath = ImageFormFileCacheInstance.getDirectory() + "/" + ImageFormFileCacheInstance.convertUrlToFileName(url);
        File file = new File(cachePath);
        if (file.exists()) {
            Log.i("moop", "getPath: ="+cachePath);

            return cachePath;
        }
        Log.i("moop", "getPath: =null");

        return null;
    }

    /**
     * 将图片存入文件缓存
     **/
    public static void saveBitmap(String url, Bitmap bm) {
        if (bm == null) {
            return;
        }

        if (ImageFormFileCacheInstance == null) {
            ImageFormFileCacheInstance = new ImageFromFileCache();
        }

        //判断sdcard上的空间
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > ImageFormFileCacheInstance.freeSpaceOnSd()) {
            //SD空间不足
            return;
        }
        String filename = ImageFormFileCacheInstance.convertUrlToFileName(url);
        String dir = ImageFormFileCacheInstance.getDirectory();
        File dirFile = new File(dir);
        if (!dirFile.exists())
            dirFile.mkdirs();
        File file = new File(dir + "/" + filename);

        try {
            file.createNewFile();
            OutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        }
    }

    /**
     * 计算存储目录下的文件大小，
     * 当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定
     * 那么删除40%最近没有被使用的文件
     */
    private boolean removeCache(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return true;
        }
        if (!android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return false;
        }

        int dirSize = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains(WHOLESALE_CONV)) {
                dirSize += files[i].length();
            }
        }

        if (dirSize > CACHE_SIZE * MB || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            int removeFactor = (int) ((0.4 * files.length) + 1);
            Arrays.sort(files, new FileLastModifSort());
            for (int i = 0; i < removeFactor; i++) {
                if (files[i].getName().contains(WHOLESALE_CONV)) {
                    files[i].delete();
                }
            }
        }

        if (freeSpaceOnSd() <= CACHE_SIZE) {
            return false;
        }

        return true;
    }

    /**
     * 修改文件的最后修改时间
     **/
    public void updateFileTime(String path) {
        File file = new File(path);
        long newModifiedTime = System.currentTimeMillis();
        file.setLastModified(newModifiedTime);
    }

    /**
     * 计算sdcard上的剩余空间
     **/
    private int freeSpaceOnSd() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
        return (int) sdFreeMB;
    }

    /**
     * 将url转成文件名
     **/
    private String convertUrlToFileName(String url) {
        String[] strs = url.split("/");
        return strs[strs.length - 1] + WHOLESALE_CONV;
    }

    /**
     * 获得缓存目录
     **/
    private String getDirectory() {
        String dir = getSDPath() + "/" + CACHDIR;
        return dir;
    }

    /**
     * 取SD卡路径
     **/
    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();  //获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }

    /**
     * 根据文件的最后修改时间进行排序
     */
    private class FileLastModifSort implements Comparator<File> {
        public int compare(File arg0, File arg1) {
            if (arg0.lastModified() > arg1.lastModified()) {
                return 1;
            } else if (arg0.lastModified() == arg1.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    /**
     * 获取视频文件缩略图
     * 并将获取到的缩略图存入本地缓存
     *
     * @param url,width,height
     * @return bitmp
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        Log.w("ImageFileCache", "bitmap:"+bitmap);
        saveBitmap(url, bitmap);
        return bitmap;
    }
}

