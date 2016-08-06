package com.macvision.mv_078.core;

import com.macvision.mv_078.data.VideoData;
import com.macvision.mv_078.data.entity.VideoMain;
import com.squareup.picasso.Request;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by bzmoop on 2016/7/28 0028.
 * 这是一个服务类
 * 创建一个接口来管理GET，POST请求的 URL
 */
public interface VideoListService {
    /**
     * 根据
     * @param start,limit
     * @return Observable
    * */
    @POST("/demo/video/list")
    @FormUrlEncoded
    Observable<VideoData> getVideoListData(
            @Field("start") int start,
            @Field("limit") int limit);

}
