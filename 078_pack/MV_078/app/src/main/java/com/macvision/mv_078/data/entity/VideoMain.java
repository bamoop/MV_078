package com.macvision.mv_078.data.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LiangXiong on 2016/7/28 0028.
 */
public class VideoMain extends Soul implements Cloneable,Serializable{

    /**
     * firstFrameBase64 : /9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcU
     FhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgo
     KCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAFoAeIDASIA
     AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA
     AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3
     ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm
     p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA
     AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx
     BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK
     U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3
     uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD5wUZs
     M+5rG/i/GtxB/wAS4fU1h/x/8CrKn1N6nQ2dAihkaRJlG0tgt3X6etS62I3YmOPOMKNpODz15qlo
     sji8WKPhnOOvX2q1rENxbP5d2shlb5iR0HTioa/eGsX+62Mi8XZcMu0LgDgHOOKgqxejFwRt28D5
     T24qvW62OWW7CiiimSFFFFABRRRQAUUUUAFFFFABRRS0AAoNLRQOwlJS0CgQCinIrMwVQSx4AHc1
     Z1SyfT9QuLSYYlgcxuPRh1H50rq9ilF2uU6KdSqpY4Ap3FYZRS0UCEopaApwT6UAJRS4pKACiilo
     ASiiloAKSloOMDFAD4RmVB71tWIEM7sUYEfMu5uCay7ZcyQgjqa0reBpJyCrIAwJ3HI69ayqM6aK
     sWtWc3QDXbAqsY27GzzVPR/+PsnOenP4Vf1a2SONnVhGhHy8Y3H1xVLQwftWD7fyrNNcmhtJP2iu
     aWqjAXv1rBUD7TJkZ46V0mpp8y47CsIKBey5GQBmlTegVVqQGXy5CsQKEZ53Y49KjEwkYeaN69+x
     /A1fksJHuZY40Ztw8wYH8OM5rMeNomO4EexrWLTMZJovwxrFP5a8gfxeoPINULgEyOR0BxWhYt5k
     iZycKq/lVKUkGQdic0R3CSuiaBf9FU+/9DVnTx/ojH2NPW2I02An+JS36VZtbVk07JUj5Sc/hUSk
     i4Renoc/J/qx/vGmjhalnGIIz6s39KiLZ61stjB6MaTTaceKM1RmxtFLiigVgpKd34oOaB2EpKWi
     gQlFFFAG+n/IP69zWJ/y0/4FWpZSSTRLErx8nG3HNb2vaVFB4VtpYo1Dxyjcw+8c+v6VgpKDs+p0
     uLmrroY2jBAkuFjEobIduTwewq5rcjbLjzZhK8rb8ovT6exqtoCpMk8Zdg5YEfLlevf0q1rkH7xv
     LkY/KFb5cc56VDt7Q2jf2Whzt+MXBGCvyjg9RxVerWpKVu2UgggAc9elVa6Y7HFP4mFFFFMkKKKK
     ACiiigAooooAKKKKAFpaKciliABSKSEAoYVeSyfYGKmmSW5z0NTzI05NCnQBmpJUKHBFMXrVEWsz
     0n4G+HE174haHDPGHhjuVmkBHVV+b+laf7RPhrT9B8eXUWlNIx
     * firstFrameLocation : default.jpg
     * userId : 7000001
     * videoCaption : 这是视频说明说明说明
     * videoCommentNumber : 0
     * videoCreateTime : 2016-08-01 02:33:41
     * videoId : 100003
     * videoLikesNumber : 0
     * videoLocation : zm.mp4
     * videoReleaseAddress : 深圳，福田区
     * videoTitle : 测试视频-标题03
     * videoType : 4
     * videoViewNumber : 10
     * videoWebViewAddress : html/zm.mp4
     */


        private String firstFrameBase64;
        private String firstFrameLocation;
        private int userId;
        private String videoCaption;
        private String videoCommentNumber;
        private String videoCreateTime;
        private int videoId;
        private int videoLikesNumber;
        private String videoLocation;
        private String videoReleaseAddress;
        private String videoTitle;
        private int videoType;
        private String videoViewNumber;
        private String videoWebViewAddress;

        public String getFirstFrameBase64() {
            return firstFrameBase64;
        }

        public void setFirstFrameBase64(String firstFrameBase64) {
            this.firstFrameBase64 = firstFrameBase64;
        }

        public String getFirstFrameLocation() {
            return firstFrameLocation;
        }

        public void setFirstFrameLocation(String firstFrameLocation) {
            this.firstFrameLocation = firstFrameLocation;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
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

        public String getVideoCreateTime() {
            return videoCreateTime;
        }

        public void setVideoCreateTime(String videoCreateTime) {
            this.videoCreateTime = videoCreateTime;
        }

        public int getVideoId() {
            return videoId;
        }

        public void setVideoId(int videoId) {
            this.videoId = videoId;
        }

        public int getVideoLikesNumber() {
            return videoLikesNumber;
        }

        public void setVideoLikesNumber(int videoLikesNumber) {
            this.videoLikesNumber = videoLikesNumber;
        }

        public String getVideoLocation() {
            return videoLocation;
        }

        public void setVideoLocation(String videoLocation) {
            this.videoLocation = videoLocation;
        }

        public String getVideoReleaseAddress() {
            return videoReleaseAddress;
        }

        public void setVideoReleaseAddress(String videoReleaseAddress) {
            this.videoReleaseAddress = videoReleaseAddress;
        }

        public String getVideoTitle() {
            return videoTitle;
        }

        public void setVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
        }

        public int getVideoType() {
            return videoType;
        }

        public void setVideoType(int videoType) {
            this.videoType = videoType;
        }

        public String getVideoViewNumber() {
            return videoViewNumber;
        }

        public void setVideoViewNumber(String videoViewNumber) {
            this.videoViewNumber = videoViewNumber;
        }

        public String getVideoWebViewAddress() {
            return videoWebViewAddress;
        }

        public void setVideoWebViewAddress(String videoWebViewAddress) {
            this.videoWebViewAddress = videoWebViewAddress;
        }
    }

