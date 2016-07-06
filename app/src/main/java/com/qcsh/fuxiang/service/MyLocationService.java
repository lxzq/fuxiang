package com.qcsh.fuxiang.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.qcsh.fuxiang.AppContext;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定位服务 用于向后台发送定位数据
 * Created by Administrator on 2015/8/26.
 */
public class MyLocationService extends Service {

    private static final String TAG = MyLocationService.class.getName();
    //高德定位
    private LocationManagerProxy mLocationManagerProxy;
    //定位通知器
    private MyAMapLocationListener myAMapLocationListener;
    // 家家帮定位服务
    public static final String JJB_LOCATION_SERVICE = "com.qcsh.fuxiang.service.MyLocationService";
    //5 分钟定位一次
    private static final int TIME = 5 * 60 * 1000;
    //位置变化通知距离，单位为米
    private static final float MIN_DISTANCE = 10F;
    //经度
    private Double geoLat;
    //纬度
    private Double geoLng;

   // private String userId = "1";

   // private String childId = "1";

    //定时任务执行请求定位
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private final int REQUEST_CODE = 0;

    private UpdateLocationReceiver updateLocationReceiver;
    private static final String UPDATE_LOCATION = "com.qcsh.fuxiang.service.updateLocationReceiver";
    private ExecutorService singleThreadExecutor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate ===================");
        /*AppContext appContext = (AppContext)getApplication();
        User u = appContext.getCurUser();
        if(null != u){
            userId = u.id;
            childId = u.childId;
        }*/

       singleThreadExecutor = Executors.newSingleThreadExecutor();

       updateLocationReceiver = new UpdateLocationReceiver();
       IntentFilter intentFilter = new IntentFilter();
       intentFilter.addAction(UPDATE_LOCATION);
       registerReceiver(updateLocationReceiver, intentFilter);



       alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
       Intent intent = new Intent();
       intent.setAction(UPDATE_LOCATION);
       pendingIntent = PendingIntent.getBroadcast(this,REQUEST_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT);
       long triggerAtTime = SystemClock.elapsedRealtime();
       alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, TIME, pendingIntent);
       startGDLoc();

    }

    /**
     * 高德定位监听器
     */
    private class MyAMapLocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){
                //获取位置信息
                 geoLat = amapLocation.getLatitude();
                 geoLng = amapLocation.getLongitude();
                 String address = amapLocation.getAddress();
                 String cityCode = amapLocation.getCityCode();
                 String city = amapLocation.getCity();
                 AppContext context = (AppContext) getApplication();
                 context.setAddress(address);
                 context.setLatitude(geoLat);
                 context.setLongitude(geoLng);

                 Log.d(TAG, "定位信息：Latitude=" + geoLat + ",Longitude=" + geoLng+",地址="+address + ",city :" +city);
                 singleThreadExecutor.execute(new SubmitLocationData(geoLat,geoLng,address,cityCode,city));
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onLocationChanged(Location location) {}
    }

    private class SubmitLocationData implements Runnable {

        //经度
        private double geoLat;
        //纬度
        private double geoLng;
        //地址信息
        private String address;

        private String cityCode;

        private String city;

       private SubmitLocationData(double geoLat,double geoLng,String address,String cityCode,String city){
            this.address = address;
            this.geoLng = geoLng;
            this.geoLat = geoLat;
            this.city = city;
            this.cityCode = cityCode;
        }

        @Override
        public void run() {
            Log.d(TAG,"正在发送地理位置.................");
            AppContext appContext = (AppContext)getApplication();
            User u = appContext.getCacheLoginInfo();
            String userId = u.id;
            String childInfo = u.getChild_info();
            try{
                JSONArray jsonArray = new JSONArray(childInfo);
                for(int i = 0 ; i < jsonArray.length() ; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String childId = jsonObject.getString("child_id");
                    ApiClient.post(MyLocationService.this.getApplication(), ApiConfig.GAODE_LOCATION, new ApiResponseHandler<BaseEntity>() {
                        @Override
                        public void onSuccess(DataEntity entity) {
                            Log.d(TAG, "发送地理位置成功...");
                        }
                        @Override
                        public void onFailure(ErrorEntity errorInfo) {
                            Log.e(TAG,"发送地理位置失败..." + errorInfo.getMessage());
                        }
                    }, "geoLat", geoLat + "", "geoLng", geoLng + "", "address", address, "userId", userId, "childId", childId,"city",city,"cityCode",cityCode);
                }
            }catch (Exception e){

            }
        }
    }
    /**
     * 定时注册广播
     */
    private class UpdateLocationReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            startGDLoc();
        }
    }
    /**
     * 发起定位请求
     */
    private void startGDLoc(){
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        myAMapLocationListener =  new MyAMapLocationListener();
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, MIN_DISTANCE, myAMapLocationListener);
        mLocationManagerProxy.setGpsEnable(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        alarmManager.cancel(pendingIntent);
        stopLocation();
        unregisterReceiver(updateLocationReceiver);
        Log.e(TAG, "onDestroy ===================");
    }
    /**
     * 释放高德定位资源
     */
    private void stopLocation() {
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(myAMapLocationListener);
            mLocationManagerProxy.destroy();
        }
        mLocationManagerProxy = null;

    }
}
