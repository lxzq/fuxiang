package com.qcsh.fuxiang.bean.look;

import com.qcsh.fuxiang.bean.BaseEntity;

/**
 *
 * Created by Administrator on 2015/9/17.
 */
public class LookChildEntity extends BaseEntity {

    private String userId;
    private String nickname;
    private String face;
    private String relation;//1 爸爸  2 妈妈 3 爷爷 4奶奶
    private String isNurse;//是否当前看护人 0 是 1 否
    private String child_id;

    public String getChild_id() {
        return child_id;
    }

    public void setChild_id(String child_id) {
        this.child_id = child_id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getIsNurse() {
        return isNurse;
    }

    public void setIsNurse(String isNurse) {
        this.isNurse = isNurse;
    }
}
