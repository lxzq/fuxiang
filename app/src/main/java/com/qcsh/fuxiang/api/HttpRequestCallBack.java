package com.qcsh.fuxiang.api;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * 文件上传 回调函数
 * Created by Administrator on 2015/9/1.
 */
public abstract class HttpRequestCallBack<T> extends RequestCallBack<T> {

    /**
     * 文件上传结果回调方法
     * @param resultCode
     */
    protected abstract void onHandleMessage(int resultCode,String msg);

    /**
     * 加载进度
     * @param total  总进度
     * @param current 当前进度
     * @param isUploading 是否加载中
     */
    protected abstract void onHandleLoading(long total, long current, boolean isUploading);

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onLoading(long total, long current, boolean isUploading) {
        super.onLoading(total, current, isUploading);
        onHandleLoading(total, current, isUploading);
    }

    @Override
    public void onSuccess(ResponseInfo<T> responseInfo) {
        T t = responseInfo.result;
        if(t instanceof  String){
            onHandleMessage(ErrorMassage.INSERT_SUCCESS,responseInfo.result.toString());
        }else if(t instanceof File){
            File file = (File)t;
            onHandleMessage(ErrorMassage.INSERT_SUCCESS,file.getPath());
        }

    }

    @Override
    public void onFailure(HttpException e, String s) {
        onHandleMessage(ErrorMassage.DATA_ERROR,s);
    }
}
