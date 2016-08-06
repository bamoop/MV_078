package com.macvision.mv_078.model.entity;/**
 * Created by bzmoop on 2016/8/16 0016.
 */

import java.util.List;

/**
 * 作者：LiangXiong on 2016/8/16 0016 14:23
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class CommentEntity {

    /**
     * errno : 0
     * msg : ok
     * data : [{"beReplyUserId":0,"beReplyUserName":"","cmContent":"就急急急急急急的大师的发生的发生","cmCreateTime":"2016-08-12 01:53:13.0","cmId":100057,"cmUserId":99999999,"cmVideoId":100039}]
     */

    private String errno;
    private String msg;
    /**
     * beReplyUserId : 0
     * beReplyUserName :
     * cmContent : 就急急急急急急的大师的发生的发生
     * cmCreateTime : 2016-08-12 01:53:13.0
     * cmId : 100057
     * cmUserId : 99999999
     * cmVideoId : 100039
     */

    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private int beReplyUserId;
        private String beReplyUserName;
        private String cmContent;
        private String cmCreateTime;
        private int cmId;
        private int cmUserId;
        private int cmVideoId;

        public int getBeReplyUserId() {
            return beReplyUserId;
        }

        public void setBeReplyUserId(int beReplyUserId) {
            this.beReplyUserId = beReplyUserId;
        }

        public String getBeReplyUserName() {
            return beReplyUserName;
        }

        public void setBeReplyUserName(String beReplyUserName) {
            this.beReplyUserName = beReplyUserName;
        }

        public String getCmContent() {
            return cmContent;
        }

        public void setCmContent(String cmContent) {
            this.cmContent = cmContent;
        }

        public String getCmCreateTime() {
            return cmCreateTime;
        }

        public void setCmCreateTime(String cmCreateTime) {
            this.cmCreateTime = cmCreateTime;
        }

        public int getCmId() {
            return cmId;
        }

        public void setCmId(int cmId) {
            this.cmId = cmId;
        }

        public int getCmUserId() {
            return cmUserId;
        }

        public void setCmUserId(int cmUserId) {
            this.cmUserId = cmUserId;
        }

        public int getCmVideoId() {
            return cmVideoId;
        }

        public void setCmVideoId(int cmVideoId) {
            this.cmVideoId = cmVideoId;
        }
    }
}
