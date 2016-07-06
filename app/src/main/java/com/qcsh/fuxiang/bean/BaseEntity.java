package com.qcsh.fuxiang.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by liu on 13-9-28.
 * 所有实体的基类
 */
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 5478269796773170329L;

    public void initWithJson(JSONObject jsonObject){
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for(Field field : fields){
                String fieldName = field.getName();
                if(fieldName.contains("this"))continue;
                String val = jsonObject.optString(fieldName);
                setter(this,toFirstUpperCase(fieldName), val, String.class);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void dataHandler(Object object,Field[] fields,JSONObject json) {

        for(Field field : fields){
            String fieldName = field.getName();
            if(fieldName.contains("this"))continue;
            String val = null;
            try {
                val = json.getString(fieldName);
                setter(object,toFirstUpperCase(fieldName), val, String.class);
            } catch (JSONException e) {
            }
        }
    }

    protected void setter(Object obj,String attr,Object value,Class<?> type) {
        Method method;
        try {
            method = obj.getClass().getMethod("set" + attr, type);
            method.invoke(obj, value);
        } catch (Exception e) {
           // e.printStackTrace();
        }

    }

    protected String toFirstUpperCase(String name){
        String temp = "";
        if(name.length() > 1){
            temp = name.substring(1);
        }
        return name.toUpperCase().substring(0, 1) + temp;
    }
}
