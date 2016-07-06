package com.qcsh.fuxiang.bean.Home;

import com.qcsh.fuxiang.bean.BaseEntity;

/**
 * 宝宝信息
 * Created by Administrator on 2015/9/23.
 */
public class ChildDetailEntity extends BaseEntity {

    private String id;
    private String face;
    private String nickname;
    private String sex;//0 女 1 男
    private String birthday;

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
