package com.qcsh.fuxiang.bean.look;

import com.qcsh.fuxiang.bean.BaseEntity;

/**
 * Created by WWW on 2015/10/12.
 */
public class CommentEntity extends BaseEntity {
    private String comment_id;
    private String content_id;
    private String comment_content;
    private String comment_time;
    private String status;
    private String comment_userId;
    private String username;

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment_userId() {
        return comment_userId;
    }

    public void setComment_userId(String comment_userId) {
        this.comment_userId = comment_userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
