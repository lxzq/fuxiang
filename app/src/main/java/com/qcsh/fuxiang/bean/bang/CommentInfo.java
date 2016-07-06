package com.qcsh.fuxiang.bean.bang;

import com.qcsh.fuxiang.bean.BaseEntity;

/**
 * 评论实体类
 * Created by Administrator on 2015/9/11.
 */
public class CommentInfo extends BaseEntity {

    private String nickname;
    private String content;
    private String date;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
