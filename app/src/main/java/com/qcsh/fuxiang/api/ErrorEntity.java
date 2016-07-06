package com.qcsh.fuxiang.api;

import android.text.TextUtils;

import com.qcsh.fuxiang.bean.BaseEntity;


/**
 * @author xuanqiang
 */
public class ErrorEntity extends BaseEntity {
    public String name;
    public String message;
    public String code;
    public int status;
    public String type;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code =code;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status =Integer.parseInt(status);
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public boolean hasError() {
        return !TextUtils.isEmpty(code) && !code.equals("200");
    }

}