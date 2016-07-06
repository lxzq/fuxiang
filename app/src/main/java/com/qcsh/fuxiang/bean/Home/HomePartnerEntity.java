package com.qcsh.fuxiang.bean.Home;

import com.qcsh.fuxiang.bean.BaseEntity;

/**
 * Created by wo on 15/9/22.
 */
public class HomePartnerEntity extends BaseEntity {

    private String child_friend_id;
    private String child_id;
    private String id;
    private String createtime;
    private String childInfo;


    public String getChild_friend_id() {
        return child_friend_id;
    }

    public void setChild_friend_id(String child_friend_id) {
        this.child_friend_id = child_friend_id;
    }

    public String getChild_id() {
        return child_id;
    }

    public void setChild_id(String child_id) {
        this.child_id = child_id;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getChildInfo() {
        return childInfo;
    }

    public void setChildInfo(String childInfo) {
        this.childInfo = childInfo;
    }

}
