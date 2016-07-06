package com.qcsh.fuxiang.bean.look;

import com.qcsh.fuxiang.bean.BaseEntity;

/**
 * Created by WWW on 2015/10/10.
 */
public class ZanEntity extends BaseEntity {
    private String id;
    private String relation_id;
    private String userid;
    private String type;
    private String praise_time;
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelation_id() {
        return relation_id;
    }

    public void setRelation_id(String relation_id) {
        this.relation_id = relation_id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPraise_time() {
        return praise_time;
    }

    public void setPraise_time(String praise_time) {
        this.praise_time = praise_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
