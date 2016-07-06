package com.qcsh.fuxiang.bean.Home;

import com.qcsh.fuxiang.bean.BaseEntity;

/**
 * Created by WWW on 2015/9/29.
 */
public class FamilyUserEntity extends BaseEntity {

    private String id;
    private String userface;
    private String nickname;
    private String relation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserface() {
        return userface;
    }

    public void setUserface(String userface) {
        this.userface = userface;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
