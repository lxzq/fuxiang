package com.qcsh.fuxiang.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.LocationManagerProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.ui.PhotoViewActivity;
import com.qcsh.fuxiang.ui.SlidingMenuFragment;
import com.qcsh.fuxiang.ui.media.RecordVideoActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.util.ArrayList;
import java.util.Map;

/**
 * 登录界面
 * Created by Administrator on 2015/8/12.
 */
@Deprecated
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private TextView sinaLogin;
    private TextView weixinLogin;
    private TextView qqLogin;

    private Button btn_register;
    private Button btn_username;

    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");



    private TextView shareView;


    private TextView rcdVideoView;
    private Toolbar toolbar;

    //图片选择器
    ArrayList<String> mSelectPath;
    ImageView selectImage;
    ImageView selectImage2;
    ImageView selectImage3;

    //高德定位
    private LocationManagerProxy mLocationManagerProxy;
    //private MyAMapLocationListener myAMapLocationListener;
    private LocationSource.OnLocationChangedListener mListener;

    //高德地图
    private MapView mapView;
    private AMap aMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //高德地图
//        mapView = (MapView) findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState);// 必须要写
//
//        aMap = mapView.getMap();
//
//        aMap.setLocationSource(new MyLocationSource());// 设置定位监听
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
//        aMap.getUiSettings().setScaleControlsEnabled(true);//设置比例尺
//        aMap.getUiSettings().setCompassEnabled(true);//设置指南针
//        aMap.setMyLocationEnabled(true);//开启定位层
//        aMap.setInfoWindowAdapter(new MyInfoWindowAdapter());//设置标记适配器
//
//
//        //创建地图图标
//        MarkerOptions options2 = new MarkerOptions();
//        LatLng lat2 = new LatLng(32.818884,118.221945);
//        options2.position(lat2);
//        options2.title("万达广场");
//        options2.snippet("安徽合肥市天鹅湖万达广场");
//        options2.visible(true);
//        options2.draggable(false);
//        aMap.addMarker(options2);
//
//
//        MarkerOptions options = new MarkerOptions();
//        LatLng lat = new LatLng(31.818884,117.221945);
//        options.position(lat);
//        options.title("万达广场22");
//        options.snippet("合肥万达广场2号写字楼：31.818884,117.221945");
//        options.visible(true);
//        options.draggable(false);
//        options.icon(BitmapDescriptorFactory.fromView(getLayoutInflater().inflate(R.layout.start, null)));
//        aMap.addMarker(options).showInfoWindow();

//        toolbar = (Toolbar) findViewById(R.id.demo_toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
//        applyKitKatTranslucency(R.color.blue);
        //toolbar.setBackgroundResource(R.color.blue);
        //OauthHelper. isAuthenticatedAndTokenNotExpired(this, SHARE_MEDIA.SINA);
        /*MyConfirmDialog dialog = new MyConfirmDialog(this, "确认手机号", "我们将发生手机号码",

                new MyConfirmDialog.OnCancelDialogListener() {
                    @Override
                    public void onCancel() {

                    }
                },

                new MyConfirmDialog.OnConfirmDialogListener() {

                    @Override
                    public void onConfirm() {

                        AppIntentManager.startCaptureActivity(LoginActivity.this);
                    }
                }
        );

        dialog.show();*/
        //setSlidingMenu();
        addPlatform();
        initView();
        initListenter();


       // initGDLoc();

    }

    private void initView() {
        /*sinaLogin = (TextView) findViewById(R.id.sinaLogin);
        sinaLogin.setOnClickListener(this);

        weixinLogin = (TextView) findViewById(R.id.weixinLogin);
        qqLogin = (TextView) findViewById(R.id.qqLogin);*/
//        shareView = (TextView) findViewById(R.id.onekeyshare);
//        rcdVideoView = (TextView) findViewById(R.id.recordvideo);

        //选择图片
//        selectImage = (ImageView)findViewById(R.id.select_image);
//        selectImage2 = (ImageView)findViewById(R.id.select_image2);
//        selectImage3 = (ImageView)findViewById(R.id.select_image3);
//         findViewById(R.id.select_button).setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 //AppIntentManager.startImageSelectorForResult(LoginActivity.this, mSelectPath, 3, MultiImageSelectorActivity.VIDEO_FILE);
//                 AppIntentManager.startCaptureActivity(LoginActivity.this);
//             }
//         });



        //已有账号登陆
        btn_username = (Button)findViewById(R.id.btn_login);
        btn_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // aMap.getMapScreenShot(new MyMapScreenShotListener());//设置截图
            }
        });

        //手机号注册
        btn_register = (Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // AppIntentManager.startRegisterActivity(LoginActivity.this, tel, psw, key);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        //stopLocation();
    }

//    @Override
//    public void onActivityReenter(int resultCode, Intent data) {
//        super.onActivityReenter(resultCode, data);
//    }


    private void setSlidingMenu(){
        SlidingMenuFragment smf = new SlidingMenuFragment();
        FragmentManager fm = getSupportFragmentManager();
        SlidingMenu menu = new SlidingMenu(LoginActivity.this);
//        setBehindContentView(R.layout.slidingmenu_content);
//        SlidingMenu menu = getSlidingMenu();
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//        menu.setShadowWidthRes(30);
//        menu.setShadowDrawable(R.color.blue);

        // 设置滑动菜单视图的宽度
        menu.setBehindWidth(Utils.dip2px(LoginActivity.this, 240));
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        /**
         * SLIDING_WINDOW will include the Title/ActionBar in the content
         * section of the SlidingMenu, while SLIDING_CONTENT does not.
         */
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        menu.setMenu(R.layout.slidingmenu_content);
        fm.beginTransaction().replace(R.id.slidingmenu_content,smf).commit();

    }



    private void initListenter() {

        sinaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(SHARE_MEDIA.SINA);
            }
        });

        qqLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(SHARE_MEDIA.QQ);
            }
        });

        weixinLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(SHARE_MEDIA.WEIXIN);
            }
        });

        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                OneKeyShare share = new OneKeyShare(LoginActivity.this);
//                share.setShareContent();
//                share.addSharePlatforms();
                ArrayList<String> s = new ArrayList<String>();
                s.add("http://img10.3lian.com/c1/newpic/05/32/52.jpg");
                s.add("http://img10.3lian.com/c1/newpic/05/32/52.jpg");
                s.add("http://img10.3lian.com/c1/newpic/05/32/52.jpg");
                AppIntentManager.startPhotoViewActivity(LoginActivity.this, "http://img10.3lian.com/c1/newpic/05/32/52.jpg", s, PhotoViewActivity.HTTPIMG);
            }
        });

        rcdVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RecordVideoActivity.class));
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        Toast.makeText(LoginActivity.this, "action_settings", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    // 如果有使用任一平台的SSO授权, 则必须在对应的activity中实现onActivityResult方法, 并添加如下代码
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //选择图片后处理结果
//        if(requestCode == AppConfig.REQUEST_IMAGE){
//            if(resultCode == RESULT_OK){
//                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//
//                //图片处理
//               /* for(int i = 0 ; i < mSelectPath.size() ; i++){
//                    Bitmap bitmap = ImageUtils.getBitmapByPath(mSelectPath.get(i));
//                    bitmap = ImageUtils.zoomBitmap(bitmap,AppConfig.IMAGE_WIDTH,AppConfig.IMAGE_HEIGHT);
//                    if(i == 0)selectImage.setImageBitmap(bitmap);
//                    else if(i == 1)selectImage2.setImageBitmap(bitmap);
//                    else selectImage3.setImageBitmap(bitmap);
//                }*/
//                //视频处理
//                for(int i = 0 ; i < mSelectPath.size() ; i++){
//                    Bitmap bitmap = ImageUtils.getVideoThumbnail(mSelectPath.get(i), AppConfig.IMAGE_WIDTH,AppConfig.IMAGE_HEIGHT, MediaStore.Video.Thumbnails.MICRO_KIND);
//                    if(i == 0)selectImage.setImageBitmap(bitmap);
//                    else if(i == 1)selectImage2.setImageBitmap(bitmap);
//                    else selectImage3.setImageBitmap(bitmap);
//
//                    selectImage.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            AppIntentManager.startSysVideoPlay(LoginActivity.this,mSelectPath.get(0),AppConfig.VIDEO.LOCAL_VIDEO);
//                        }
//                    });
//                }
//
//            }
//        }

        // 根据requestCode获取对应的SsoHandler
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
                resultCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }


    }

    private void addPlatform() {
        // 添加QQ支持, 并且设置QQ分享内容的target url
        String appId = AppConfig.QQ_APPID;
        String appKey = AppConfig.QQ_APPKEY;
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this, appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com");
        qqSsoHandler.addToSocialSDK();
        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(LoginActivity.this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(LoginActivity.this, AppConfig.WEIXIN_APPID, AppConfig.WEXIN_APPSECRET);
        wxHandler.addToSocialSDK();
        // 设置新浪SSO handler,调用客户端
        //mController.getConfig().setSsoHandler(new SinaSsoHandler());
    }

    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */

    private void login(final SHARE_MEDIA platform) {
        mController.doOauthVerify(LoginActivity.this, platform, new SocializeListeners.UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                Toast.makeText(LoginActivity.this, "授权开始...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                String uid = value.getString("uid");
                Toast.makeText(LoginActivity.this, "uid=" + uid, Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(uid)) {
                    getUserInfo(platform);
                } else {
                    Toast.makeText(LoginActivity.this, "授权失败...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
            }
        });
    }

    /**
     * 获取授权平台的用户信息</br>
     */
    private void getUserInfo(SHARE_MEDIA platform) {
        mController.getPlatformInfo(LoginActivity.this, platform, new SocializeListeners.UMDataListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                // String showText = "";
                // if (status == StatusCode.ST_CODE_SUCCESSED) {
                // showText = "用户名：" + info.get("screen_name").toString();
                // Log.d("#########", "##########" + info.toString());
                // } else {
                // showText = "获取用户信息失败";
                // }
                if (info != null) {
                    Toast.makeText(LoginActivity.this, info.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("userinfo", info.toString());
                }
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    /**
     * 初始化高德定位
     */
//    private void initGDLoc(){
//        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
//        myAMapLocationListener =  new MyAMapLocationListener();
//        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
//        //在定位结束后，在合适的生命周期调用destroy()方法
//        //其中如果间隔时间为-1，则定位只定一次
//        mLocationManagerProxy.requestLocationData(
//                LocationProviderProxy.AMapNetwork, 1000, 15, myAMapLocationListener);
//        mLocationManagerProxy.setGpsEnable(true);
//    }
//
    @Override
    public void onClick(View view) {


//        Intent intent = new Intent(this,ResetByPhoneActivity.class);
//        startActivity(intent);
    }
//
//    /**
//     * 高德定位监听器
//     */
//    private class MyAMapLocationListener implements AMapLocationListener {
//
//        @Override
//        public void onLocationChanged(AMapLocation amapLocation) {
//            if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){
//                //获取位置信息
//                Double geoLat = amapLocation.getLatitude();
//                Double geoLng = amapLocation.getLongitude();
//                String addRess = amapLocation.getAddress();
//
//                /*MarkerOptions options = new MarkerOptions();
//                LatLng lat = new LatLng(geoLat,geoLng);
//                options.position(lat);
//                options.title("万达广场");
//                options.snippet("合肥万达广场2号写字楼：" + geoLat + ", " + geoLng);
//                options.visible(true);
//                options.draggable(false);
//                aMap.addMarker(options);*/
//
//
//
////                username.setText( addRess );
//            }
//
//            if (mListener != null && amapLocation != null) {
//                if (amapLocation.getAMapException().getErrorCode() == 0) {
//                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//                }
//            }
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//
//        @Override
//        public void onLocationChanged(Location location) {
//
//        }
//    }
//
//    private class MyLocationSource implements LocationSource {
//
//        @Override
//        public void activate(OnLocationChangedListener listener) {
//            mListener = listener;
//            if (mLocationManagerProxy == null) {
//                mLocationManagerProxy = LocationManagerProxy.getInstance(LoginActivity.this);
//                //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//                //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
//                //在定位结束后，在合适的生命周期调用destroy()方法
//                //其中如果间隔时间为-1，则定位只定一次
//                mLocationManagerProxy.requestLocationData(
//                        LocationProviderProxy.AMapNetwork, 1*1000, 10, myAMapLocationListener);
//            }
//        }
//
//        @Override
//        public void deactivate() {
//            mListener = null;
//            stopLocation();
//        }
//    }
//
//    /**
//     * 释放高德定位资源
//     */
//    private void stopLocation() {
//        if (mLocationManagerProxy != null) {
//            mLocationManagerProxy.removeUpdates(myAMapLocationListener);
//            mLocationManagerProxy.destroy();
//        }
//        mLocationManagerProxy = null;
//    }
//
//
//    private class MyInfoWindowClickListener implements AMap.OnInfoWindowClickListener{
//        @Override
//        public void onInfoWindowClick(Marker marker) {
//
//        }
//
//    }
//
//    private class MyOnMarkerClickListener implements AMap.OnMarkerClickListener {
//        @Override
//        public boolean onMarkerClick(Marker marker) {
//            return false;
//        }
//    }
//
//    /**
//     * 高德地图标记 自定义布局
//     */
//    private class MyInfoWindowAdapter implements AMap.InfoWindowAdapter {
//
//        @Override
//        public View getInfoContents(Marker marker) {
//            String title = marker.getTitle();
//            View v = null;
//            if("万达广场".equals(title))
//            v = getLayoutInflater().inflate(R.layout.start,null);
//            return v;
//        }
//
//        @Override
//        public View getInfoWindow(Marker marker) {
//            return null;
//        }
//
//
//    }
//
//    /**
//     * 高德地图截图
//     */
//    private class MyMapScreenShotListener implements AMap.OnMapScreenShotListener {
//        @Override
//        public void onMapScreenShot(Bitmap bitmap) {
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//            try {
//                // 保存在SD卡根目录下，图片为png格式。
//                FileOutputStream fos = new FileOutputStream(
//                        Environment.getExternalStorageDirectory() + "/" +AppConfig.CACHE_IMAGES + "/map"
//                                + sdf.format(new Date()) + ".png");
//                boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                try {
//                    fos.flush();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (b){
//                    UIHelper.ToastMessage(LoginActivity.this,"截屏成功");
//                }
//                    //ToastUtil.show(ScreenShotActivity.this, "截屏成功");
//                else {
//                    UIHelper.ToastMessage(LoginActivity.this,"截屏失败");
//                   // ToastUtil.show(ScreenShotActivity.this, "截屏失败");
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}