package com.qcsh.fuxiang.bean.look;

import com.qcsh.fuxiang.bean.BaseEntity;

import java.io.Serializable;

/**
 * Created by WWW on 2015/9/6.
 */
public class GrowthTreeEntity extends BaseEntity implements Serializable{
    private String content_id;
    private String title;
    private String content;
    private String address;
    private String user_id;
    private String release_time;
    private String video_url;
    private String voice_url;
    private String children_id;
    private String visible_ange;
    private String tags;
    private String user;
    private String images;
    private String collect;
    private String praise;
    private String type;
    private String childInfo;

    public String getChildInfo() {
        return childInfo;
    }

    public void setChildInfo(String childInfo) {
        this.childInfo = childInfo;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRelease_time() {
        return release_time;
    }

    public void setRelease_time(String release_time) {
        this.release_time = release_time;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVoice_url() {
        return voice_url;
    }

    public void setVoice_url(String voice_url) {
        this.voice_url = voice_url;
    }

    public String getChildren_id() {
        return children_id;
    }

    public void setChildren_id(String children_id) {
        this.children_id = children_id;
    }

    public String getVisible_ange() {
        return visible_ange;
    }

    public void setVisible_ange(String visible_ange) {
        this.visible_ange = visible_ange;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getCollect() {
        return collect;
    }

    public void setCollect(String collect) {
        this.collect = collect;
    }

    public String getPraise() {
        return praise;
    }

    public void setPraise(String praise) {
        this.praise = praise;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
