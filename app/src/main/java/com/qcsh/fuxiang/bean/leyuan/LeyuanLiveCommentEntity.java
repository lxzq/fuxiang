package com.qcsh.fuxiang.bean.leyuan;

import com.qcsh.fuxiang.bean.BaseEntity;

/**
 * Created by Administrator on 2015/11/4.
 */
public class LeyuanLiveCommentEntity extends BaseEntity {

    private String face;
    private String nickname;
    private String time;
    private String content;

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
