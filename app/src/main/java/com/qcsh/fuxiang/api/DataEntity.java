package com.qcsh.fuxiang.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;


import com.qcsh.fuxiang.bean.BaseEntity;

/**
 * Created by liu on 13-9-28.
 * 所有实体的基类
 */
public class DataEntity<T extends BaseEntity> implements Serializable {
    public int totalCount;
    public int pageCount;
    public List<Object> data;
    public Class<T> type;
    public int currentPage;
    public int perPage;


    protected void initWithJson(JSONObject jsonObject) throws IllegalAccessException, InstantiationException {

        JSONObject meta=jsonObject.optJSONObject("_meta");
        if (null!=meta){
            totalCount=meta.optInt("totalCount");
            pageCount=meta.optInt("pageCount");
            currentPage=meta.optInt("currentPage");
            perPage=meta.optInt("perPage");

            JSONArray jsonArray=  jsonObject.optJSONArray("items");
            if (null!=jsonArray&&jsonArray.length()>0) {
                data = new ArrayList<>();
                for(int i = 0 ; i < jsonArray.length() ; i++) {
                    try{
                        JSONObject dataJSON = jsonArray.getJSONObject(i);
                        BaseEntity entity = type.newInstance();
                        entity.initWithJson(dataJSON);
                        data.add(entity);
                    }catch (Exception e){

                    }

                }
            }
        }
        else {
            data = new ArrayList<>();
            BaseEntity entity = type.newInstance();
            entity.initWithJson(jsonObject);
            data.add(entity);
        }
    }

}
