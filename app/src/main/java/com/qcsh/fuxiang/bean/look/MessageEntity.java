package com.qcsh.fuxiang.bean.look;

import com.qcsh.fuxiang.bean.BaseEntity;

/**
 * 消息实体类
 * Created by Administrator on 2015/9/25.
 */
public class MessageEntity extends BaseEntity {

    private String user_id;
    private String face;
    private String content;
    private String msgtype;//1文字 2图片 3视频 4语音
    private String sendStatus;// 0 失败 1 成功  2 待发送
    private String createtime;
    private String userInfo;
    private String send;// 0正在发送 1 发送完成 避免重复发送
    private String mTimeMill = "";

    public String getmTimeMill() {
        return mTimeMill;
    }

    public void setmTimeMill(String mTimeMill) {
        this.mTimeMill = mTimeMill;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }
}
