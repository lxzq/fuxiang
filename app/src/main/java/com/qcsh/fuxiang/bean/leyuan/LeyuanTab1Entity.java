package com.qcsh.fuxiang.bean.leyuan;

import com.qcsh.fuxiang.bean.BaseEntity;

/**
 * Created by Administrator on 2015/10/15.
 */
public class LeyuanTab1Entity extends BaseEntity {

    private String id;
    private String face;
    private String nickname;
    private String createTime;
    private String content;
    private String images;
    private String type;//1直播 2 图文 3 视频 4音频
    private String path;//直播地址 视频地址

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
