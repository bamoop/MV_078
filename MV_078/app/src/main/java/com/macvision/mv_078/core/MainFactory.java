package com.macvision.mv_078.core;



/**
 * Created by bzmoop on 2016/7/28 0028.
 */
public class MainFactory {
    /**
     * 数据主机地址
    * */
    public static final String HOST = "http://192.168.1.124";
//    public static final String HOST = "http://gank.io/api";
    private static VideoListService mVideolistService;
    protected static final Object monitor = new Object();

    public static final VideoListService getVideoInstance(){
        synchronized (monitor){
            if (mVideolistService == null) {
                mVideolistService = new MainRetrofit().getmService();
            }
            return mVideolistService;
        }
    }


}
