package com.macvision.mv_078.model.entity;/**
 * Created by bzmoop on 2016/8/11 0011.
 */

import java.io.Serializable;

/**
 * 作者：LiangXiong on 2016/8/11 0011 21:46
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class VideoDetailEntity {

    /**
     * errno : 0
     * msg : ok
     * data : {"videoId":100117,"userId":99999999,"videoType":1,"videoLocation":"upload/medias/2016-08-09/1470732776794.MP4","videoLikesNumber":0,"videoTitle":null,"videoReleaseAddress":"深圳南山09","firstFrameLocation":"default.jpg","videoViewNumber":"0","videoCaption":"视频说明jj","videoCommentNumber":"0","videoWebViewAddress":null,"videoCreateTime":1470907711000,"firstFrameBase64":null}
     */

    private String errno;
    private String msg;
    /**
     * videoId : 100117
     * userId : 99999999
     * videoType : 1
     * videoLocation : upload/medias/2016-08-09/1470732776794.MP4
     * videoLikesNumber : 0
     * videoTitle : null
     * videoReleaseAddress : 深圳南山09
     * firstFrameLocation : default.jpg
     * videoViewNumber : 0
     * videoCaption : 视频说明jj
     * videoCommentNumber : 0
     * videoWebViewAddress : null
     * videoCreateTime : 1470907711000
     * firstFrameBase64 : null
     */

    private DataBean data;

    public String getErrno() {
        return errno;
    }

    public void setErrno(String errno) {
        this.errno = errno;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        private int videoId;
        private int userId;
        private int videoType;
        private String videoLocation;
        private int videoLikesNumber;
        private Object videoTitle;
        private String videoReleaseAddress;
        private String firstFrameLocation;
        private String videoViewNumber;
        private String videoCaption;
        private String videoCommentNumber;
        private Object videoWebViewAddress;
        private long videoCreateTime;
        private Object firstFrameBase64;

        public int getVideoId() {
            return videoId;
        }

        public void setVideoId(int videoId) {
            this.videoId = videoId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getVideoType() {
            return videoType;
        }

        public void setVideoType(int videoType) {
            this.videoType = videoType;
        }

        public String getVideoLocation() {
            return videoLocation;
        }

        public void setVideoLocation(String videoLocation) {
            this.videoLocation = videoLocation;
        }

        public int getVideoLikesNumber() {
            return videoLikesNumber;
        }

        public void setVideoLikesNumber(int videoLikesNumber) {
            this.videoLikesNumber = videoLikesNumber;
        }

        public Object getVideoTitle() {
            return videoTitle;
        }

        public void setVideoTitle(Object videoTitle) {
            this.videoTitle = videoTitle;
        }

        public String getVideoReleaseAddress() {
            return videoReleaseAddress;
        }

        public void setVideoReleaseAddress(String videoReleaseAddress) {
            this.videoReleaseAddress = videoReleaseAddress;
        }

        public String getFirstFrameLocation() {
            return firstFrameLocation;
        }

        public void setFirstFrameLocation(String firstFrameLocation) {
            this.firstFrameLocation = firstFrameLocation;
        }

        public String getVideoViewNumber() {
            return videoViewNumber;
        }

        public void setVideoViewNumber(String videoViewNumber) {
            this.videoViewNumber = videoViewNumber;
        }

        public String getVideoCaption() {
            return videoCaption;
        }

        public void setVideoCaption(String videoCaption) {
            this.videoCaption = videoCaption;
        }

        public String getVideoCommentNumber() {
            return videoCommentNumber;
        }

        public void setVideoCommentNumber(String videoCommentNumber) {
            this.videoCommentNumber = videoCommentNumber;
        }

        public Object getVideoWebViewAddress() {
            return videoWebViewAddress;
        }

        public void setVideoWebViewAddress(Object videoWebViewAddress) {
            this.videoWebViewAddress = videoWebViewAddress;
        }

        public long getVideoCreateTime() {
            return videoCreateTime;
        }

        public void setVideoCreateTime(long videoCreateTime) {
            this.videoCreateTime = videoCreateTime;
        }

        public Object getFirstFrameBase64() {
            return firstFrameBase64;
        }

        public void setFirstFrameBase64(Object firstFrameBase64) {
            this.firstFrameBase64 = firstFrameBase64;
        }
    }

}
