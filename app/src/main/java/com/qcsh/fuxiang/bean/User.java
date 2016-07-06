package com.qcsh.fuxiang.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录用户实体类
 *
 * @author lxz
 * @version 1.0
 * @created 2015-5-19
 */
public class User extends BaseEntity implements Serializable {

    public String id;//用户ID
    public String username;//
    public String password;
    public String nickname;//
    public String userface;//
    public String email;//
    public String auth_key;
    public String childId;
    private String child_info;
    private String weibo_uid;
    private String qq_uid;
    private String openid;
    private String phone;
    private String sex;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWeibo_uid() {
        return weibo_uid;
    }

    public void setWeibo_uid(String weibo_uid) {
        this.weibo_uid = weibo_uid;
    }

    public String getQq_uid() {
        return qq_uid;
    }

    public void setQq_uid(String qq_uid) {
        this.qq_uid = qq_uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getChild_info() {
        return child_info;
    }

    public void setChild_info(String child_info) {
        this.child_info = child_info;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getUserface() {
        return userface;
    }

    public void setUserface(String userface) {
        this.userface = userface;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuth_key() {
        return auth_key;
    }

    public void setAuth_key(String auth_key) {
        this.auth_key = auth_key;
    }
}
