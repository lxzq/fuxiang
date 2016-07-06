package com.qcsh.fuxiang.api;


import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.qcsh.fuxiang.bean.BaseEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;

/**
 * Created by liu on 13-10-8.
 * 网络请求返回处理类
 */
public abstract class  ApiResponseHandler<T extends BaseEntity>  extends AsyncHttpResponseHandler {

    private final String TGA = ApiResponseHandler.class.getName();
    private Class<T> type;
    public ApiResponseHandler() {
        type = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public abstract void onSuccess(DataEntity entity);
    public abstract void onFailure(ErrorEntity errorInfo);

    @Override
    protected void handleFailureMessage(Throwable e, String arg1) {
        ErrorEntity error= new ErrorEntity();
        try {
            JSONObject json = new JSONObject(arg1);
            error.initWithJson(json);
        } catch (JSONException e1) {
            error.name="返回数据格式不正确";
            error.message="返回数据格式不正确";
            error.code="10002";
            error.status=200;
            error.type="Data Foramt Error";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        onFailure(error);
        super.handleFailureMessage(e, arg1);
    }

    @Override
    protected void handleSuccessMessage(int arg0, String arg1) {
        try {
            JSONObject json = new JSONObject(arg1);
            ErrorEntity errorInfo = new ErrorEntity();
            errorInfo.initWithJson(json);
            if (!errorInfo.hasError()) {
                DataEntity entity = new DataEntity();
                entity.type = type;
                entity.initWithJson(json);
                onSuccess(entity);
                Log.i(TGA, "handleSuccessMessage data  success  : " + arg1 + "");
            }
            else
                onFailure(errorInfo);

        } catch (Exception e) {
            ErrorEntity error= new ErrorEntity();
            error.name="返回数据格式不正确";
            error.message="返回数据格式不正确";
            error.code="10002";
            error.status=200;
            error.type="Data Foramt Error";
            onFailure(error);
            Log.e(TGA, "handleSuccessMessage data  error  : " + e.getMessage());
            e.printStackTrace();
        }
        super.handleSuccessMessage(arg0, arg1);
    }
}
