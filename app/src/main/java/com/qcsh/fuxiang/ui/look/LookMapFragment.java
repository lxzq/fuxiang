package com.qcsh.fuxiang.ui.look;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;

import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppContext;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.bean.look.ChildEntity;
import com.qcsh.fuxiang.bean.look.LookListEntity;
import com.qcsh.fuxiang.common.CuttingBitmap;


import com.qcsh.fuxiang.common.ImageUtils;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseFragment;
import com.qcsh.fuxiang.ui.HomeActivity;
import com.qcsh.fuxiang.ui.umengmessage.MyPushIntentService;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;


/**
 * 看着地图
 * Created by Administrator on 2015/8/27.
 */
public class LookMapFragment extends BaseFragment implements HomeActivity.IHomeTabListener {

    private View root;
    private RadioGroup menu;
    private Button action;
    private ImageButton smCode;
    private TextView titleV;
    private TextView messageNumV;

    //高德地图
    private MapView mapView;
    private AMap aMap;
    private int minTime = -1;
    private float minDistance = 10f;
    private LayoutInflater inflater;
    private Bundle savedInstanceState;

    //高德定位
    private LocationManagerProxy mLocationManagerProxy;
    private MyAMapLocationListener myAMapLocationListener;
    private LocationSource.OnLocationChangedListener mListener;
    private MyLocationBroadcastReceiver myLocationBroadcastReceiver;
    //我的位置标记信息
    private MarkerInfo myMarkerInfo;

    private Point leftTopPoint;
    private Point rightBottomPoint;

    LatLng leftTopLatLng;//左上角经纬度
    LatLng rightBottomLatLng;//右下角经纬度
    float zoom;//缩放级别
    int menuId;

    private User user;
    private String userId = "1";
    private String childId = "3";
    private List<String> stringList;
    /**
     * 处理接受到定位地点的信息
     */
    public static final String LOOKMAP_LOCATION_INFO = "com.qcsh.fuxiang.ui.look.location";


    //穿递地理位置信息
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String NAME = "name";
    public static final String CONTENT = "content";

    private ArrayList<String> mSelectPath;
    private LinkedList<String> pushMsgList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pushMessage = getArguments().getString(MyPushIntentService.PUSH_MESSAGE);
        stringList = new LinkedList<String>();
        pushMsgList = new LinkedList<String>();
        if (!TextUtils.isEmpty(pushMessage))
            pushMsgList.add(pushMessage);

        this.savedInstanceState = savedInstanceState;
        myLocationBroadcastReceiver = new MyLocationBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(LOOKMAP_LOCATION_INFO);
        getActivity().registerReceiver(myLocationBroadcastReceiver, filter);
        loadUser();
        Log.i("LookMapFragment", "onCreate=====================");
    }

    private void loadUser() {
        user = getCurrentUser();
        if (null != user) {
            userId = user.id;
            childId = user.childId;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        root = inflater.inflate(R.layout.look_map, container, false);
        initView();
        initListener();
        initGDLoc();
        Log.i("LookMapFragment", "onCreateView=====================");
        return root;
    }

    private void initView() {
        //高德地图
        mapView = (MapView) root.findViewById(R.id.map);
        menu = (RadioGroup) root.findViewById(R.id.menu);
        action = (Button) root.findViewById(R.id.action_bar_action);
        titleV = (TextView) root.findViewById(R.id.action_bar_title);
        smCode = (ImageButton) root.findViewById(R.id.action_bar_image_action);
        smCode.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.btn_kanzhe_dingwei));

        Drawable addImg = getResources().getDrawable(R.mipmap.btn_kanzhe_arrow);
        int swidth55 = Utils.dip2int(this.getActivity(), 20);
        int shight55 = Utils.dip2int(this.getActivity(), 10);
        addImg.setBounds(0, 0, swidth55, shight55);
        titleV.setText("看着");
        titleV.setCompoundDrawablePadding(5);
        titleV.setCompoundDrawables(null, null, addImg, null);

        titleV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChangeWindow();
            }
        });


        action.setVisibility(View.GONE);
        smCode.setVisibility(View.VISIBLE);
        messageNumV = (TextView) root.findViewById(R.id.message_num);
        mapView.onCreate(this.savedInstanceState);// 必须要写
        aMap = mapView.getMap();

        leftTopPoint = new Point(0, 0);
        rightBottomPoint = new Point(Utils.getScreenWidth(this.getActivity()), Utils.getScreenHeight(this.getActivity()));


        aMap.moveCamera(CameraUpdateFactory.zoomTo(13.0f));
        aMap.setLocationSource(new MyLocationSource());// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setScaleControlsEnabled(true);//设置比例尺
        aMap.getUiSettings().setCompassEnabled(false);//设置指南针
        aMap.getUiSettings().setZoomControlsEnabled(true);
        aMap.setMyLocationEnabled(true);//开启定位层
        aMap.setOnMarkerClickListener(new MyOnMarkerClickListener());
        aMap.setOnInfoWindowClickListener(new MyInfoWindowClickListener());
        aMap.setOnCameraChangeListener(new MyCameraChangeListener());

        messageNumV.setVisibility(View.GONE);

    }


    private void initListener() {
        menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.look || checkedId == R.id.look_friend || checkedId == R.id.notes) {
                    menuId = checkedId;
                }
                group.check(menuId);
            }
        });

        root.findViewById(R.id.look).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postLookData(R.id.look);
            }
        });

        root.findViewById(R.id.look_child).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startHomeFamilyMangerActivity(getActivity());
            }
        });

        root.findViewById(R.id.look_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lookFriend();
            }
        });

        smCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startCaptureActivity(LookMapFragment.this.getActivity(), "7");
            }
        });


        root.findViewById(R.id.notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pushMsgList.isEmpty()) {
                    messageNumV.setVisibility(View.GONE);
                    for (String pushMessage : pushMsgList) {
                        showPushMessage(pushMessage);
                    }
                    pushMsgList.clear();
                } else {
                    loadNotesData();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Log.i("LookMapFragment", "onResume=====================");
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        Log.i("LookMapFragment", "onPause=====================");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        stopLocation();
        getActivity().unregisterReceiver(myLocationBroadcastReceiver);
        Log.i("LookMapFragment", "onDestroy=====================");
    }

    @Override
    public void onAction() {
        showPopupWindow();
    }

    public void onPushMessage(String pushMessage) {
        pushMsgList.add(pushMessage);
        messageNumV.setText(pushMsgList.size() + "");
        messageNumV.setVisibility(View.VISIBLE);
    }

    /**
     * 显示推送留言
     *
     * @param pushMessage
     */
    private void showPushMessage(String pushMessage) {
        try {
            JSONObject jsonObject = new JSONObject(pushMessage);
            MarkerInfo markerInfo = new MarkerInfo();
            markerInfo.latitude = jsonObject.getDouble(LATITUDE);
            markerInfo.longitude = jsonObject.getDouble(LONGITUDE);
            String content = jsonObject.getString("content");
            int type = jsonObject.getInt("msgType");
            String face = jsonObject.getString("face");
            markerInfo.face = face;
            markerInfo.isFamily = 0;
            markerInfo.number = "0";
            markerInfo.content = formatContent(content, type);
            markerInfo.isShowMessage = 1;
            addMapMarker(markerInfo);
        } catch (JSONException e) {
        }

    }

    /**
     * 初始化高德定位
     */
    private void initGDLoc() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(LookMapFragment.this.getActivity());
        myAMapLocationListener = new MyAMapLocationListener();
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, minTime, minDistance, myAMapLocationListener);
        mLocationManagerProxy.setGpsEnable(true);
    }

    /**
     * 添加地图标记
     */

    private void addMapMarker(final MarkerInfo markerInfo) {
        final View rootView;
        if (markerInfo.isFamily == 0) {
            //0 表示一家  否则不属于一家庭成员
            rootView = inflater.inflate(R.layout.look_map_marker_family, null);
            TextView contentView = (TextView) rootView.findViewById(R.id.message);
            if (markerInfo.isShowMessage == 1){
                contentView.setText(markerInfo.content);
                contentView.setVisibility(View.VISIBLE);
            }else{
                contentView.setVisibility(View.GONE);
                int width = Utils.dip2int(this.getActivity(), 65);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width,ViewGroup.LayoutParams.WRAP_CONTENT);
                rootView.setLayoutParams(layoutParams);
            }
        } else {
            rootView = inflater.inflate(R.layout.look_map_marker, null);
        }
        ImageView faceView = (ImageView) rootView.findViewById(R.id.face);
        TextView numberView = (TextView) rootView.findViewById(R.id.pro_num);

        numberView.setText(markerInfo.number);
        numberView.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(markerInfo.number) && !"0".equals(markerInfo.number)) {
            numberView.setVisibility(View.VISIBLE);
        }
        if (markerInfo.isShowMessage == 1) {
            numberView.setVisibility(View.GONE);
        }
        //创建地图图标
        final MarkerOptions options = new MarkerOptions();
        LatLng lat = new LatLng(markerInfo.latitude, markerInfo.longitude);
        options.position(lat);
        options.title("");
        options.snippet("");
        options.visible(true);
        options.draggable(false);

        if (null != markerInfo.face && markerInfo.face.trim().length() > 5) {
            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(markerInfo.face), faceView, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (!stringList.contains(markerInfo.userId)) {
                        stringList.add(markerInfo.userId);
                        if (null != loadedImage) {
                            Bitmap roundBitmap = CuttingBitmap
                                    .toRoundBitmap(loadedImage);
                            ((ImageView) view).setImageBitmap(roundBitmap);

                        } else {
                            ((ImageView) view).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_kz_addpicwo));
                        }
                        options.icon(BitmapDescriptorFactory.fromView(rootView));
                        aMap.addMarker(options).setObject(markerInfo.childId);

                    }
                }
            });
        } else {
            options.icon(BitmapDescriptorFactory.fromView(rootView));
            aMap.addMarker(options).setObject(markerInfo.childId);
        }
    }

    private void clearMarker() {
        aMap.clear();
        stringList.clear();
    }

    /**
     * 请求看着数据
     */
    private void postLookData(final int id) {
        loadUser();
        myMarkerInfo.isShowMessage = 0;
        showProgress();
        ApiClient.post(this.getActivity(), ApiConfig.LOOK, new ApiResponseHandler<LookListEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                List<Object> list = entity.data;
                clearMarker();
                if (null != list) {

                    for (Object o : list) {
                        LookListEntity lookEntity = (LookListEntity) o;
                        MarkerInfo markerInfo = new MarkerInfo();
                        markerInfo.childId = lookEntity.getChildId();
                        if (!TextUtils.isEmpty(lookEntity.getLongitude()) && !"null".equals(lookEntity.getLongitude()))
                            markerInfo.longitude = Double.parseDouble(lookEntity.getLongitude());
                        else
                            markerInfo.longitude = myMarkerInfo.longitude;
                        if (!TextUtils.isEmpty(lookEntity.getLatitude()) && !"null".equals(lookEntity.getLatitude()))
                            markerInfo.latitude = Double.parseDouble(lookEntity.getLatitude());
                        else
                            markerInfo.latitude = myMarkerInfo.latitude;

                        markerInfo.face = lookEntity.getFace();
                        markerInfo.userId = lookEntity.getUserId();
                        markerInfo.isFamily = Integer.parseInt(lookEntity.getIsFamilyChild());
                        markerInfo.number = "0";
                        markerInfo.isShowMessage = 0;
                        addMapMarker(markerInfo);
                    }
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                clearMarker();
                UIHelper.ToastMessage(LookMapFragment.this.getActivity(), errorInfo.getMessage());
            }
        }, "userId", userId, "childId", childId);
    }

    /**
     * 加载留言板信息
     */
    private void loadNotesData() {
        myMarkerInfo.isShowMessage = 1;
        showProgress();
        ApiClient.post(this.getActivity(), ApiConfig.MESSAGE_BOARD, new ApiResponseHandler<LookListEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                List<Object> list = entity.data;
                clearMarker();
                if (null != list) {

                    for (Object o : list) {
                        LookListEntity lookEntity = (LookListEntity) o;
                        String content = lookEntity.getContent();
                        int type = Integer.parseInt(lookEntity.getMsgtype());
                        MarkerInfo markerInfo = new MarkerInfo();
                        markerInfo.longitude = Double.parseDouble(lookEntity.getLongitude());
                        markerInfo.latitude = Double.parseDouble(lookEntity.getLatitude());
                        markerInfo.face = lookEntity.getFace();
                        markerInfo.content = formatContent(content, type);
                        markerInfo.isFamily = 0;
                        markerInfo.number = "0";
                        markerInfo.isShowMessage = 1;
                        addMapMarker(markerInfo);
                    }
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                clearMarker();
                UIHelper.ToastMessage(LookMapFragment.this.getActivity(), errorInfo.getMessage());
            }
        }, "userId", userId, "childId", childId);
    }

    private String formatContent(String content, int type) {
        // 1文字 2图片 3视频 4语音
        if (type == 1) {
            if (content.indexOf("|") != -1) {
                StringBuffer stringBuffer = new StringBuffer();
                String[] strings = content.split("\\|");
                for (String id : strings) {
                    if (TextUtils.isEmpty(id)) continue;
                    if (id.startsWith("emoji")) {
                        stringBuffer.append("[表情]");
                    } else {
                        stringBuffer.append(id);
                    }
                }
                content = stringBuffer.toString();
            }
        } else if (type == 2) {
            content = "[图片]";
        } else if (type == 3) {
            content = "[视频]";
        } else if (type == 4) {
            content = "[语音]";
        }
        return content;
    }

    /**
     * 找伙伴数据
     */
    private void lookFriend() {
        loadUser();
        String url = ApiConfig.LOOK_FRIEND;
        ApiClient.post(this.getActivity(), url, new ApiResponseHandler<LookListEntity>() {

                    @Override
                    public void onSuccess(DataEntity entity) {
                        List<Object> list = entity.data;
                        if (null != list) {
                            aMap.clear();
                            stringList.clear();
                            List<Object> tempData = new ArrayList<Object>();
                            if (zoom == 20) {
                                tempData.addAll(list);
                            } else {
                                searchFriendJL(list, tempData);
                            }

                            for (Object o : tempData) {
                                LookListEntity lookEntity = (LookListEntity) o;
                                MarkerInfo markerInfo = new MarkerInfo();
                                markerInfo.address = lookEntity.getAddress();
                                markerInfo.face = lookEntity.getFace();
                                markerInfo.isShowMessage = 0;
                                markerInfo.number = lookEntity.getCountPeople();
                                markerInfo.content = "";
                                markerInfo.latitude = Double.parseDouble(lookEntity.getLatitude());
                                markerInfo.longitude = Double.parseDouble(lookEntity.getLongitude());
                                markerInfo.name = lookEntity.getNickname();
                                markerInfo.userId = lookEntity.getUserId();
                                markerInfo.childId = lookEntity.getChildId();
                                addMapMarker(markerInfo);
                            }

                        }
                    }

                    @Override
                    public void onFailure(ErrorEntity errorInfo) {
                        clearMarker();
                        UIHelper.ToastMessage(LookMapFragment.this.getActivity(), errorInfo.getMessage());
                    }
                }, "leftTopLatLng_latitude", leftTopLatLng.latitude + "",
                "leftTopLatLng_longitude", leftTopLatLng.longitude + ""
                , "rightBottomLatLng_latitude", rightBottomLatLng.latitude + ""
                , "rightBottomLatLng_longitude", rightBottomLatLng.longitude + ""
                , "zoom",
                "" + zoom,
                "childId",
                childId,
                "userId",
                userId
        );
    }


    /**
     * 计算地图放大缩小 的 显示用户数量合并
     *
     * @param list
     * @param tempData
     */
    private void searchFriendJL(List<Object> list, List<Object> tempData) {
        int m = zoomToM();
        if (null == list || list.isEmpty()) return;
        List<Object> listD = new ArrayList<Object>();
        for (int i = 0; i < list.size(); i++) {
            LookListEntity startLookEntity = (LookListEntity) list.get(i);
            double Latitude = Double.parseDouble(startLookEntity.getLatitude());
            double Longitude = Double.parseDouble(startLookEntity.getLongitude());
            LatLng startLatlng = new LatLng(Latitude, Longitude);
            int size = 1;
            StringBuffer ids = new StringBuffer();
            ids.append(startLookEntity.getChildId());
            for (int j = list.size() - 1; j > i; j--) {
                LookListEntity endLookEntity = (LookListEntity) list.get(j);
                double endLatitude = Double.parseDouble(endLookEntity.getLatitude());
                double endLongitude = Double.parseDouble(endLookEntity.getLongitude());
                LatLng endLatlng = new LatLng(endLatitude, endLongitude);
                float lines = AMapUtils.calculateLineDistance(startLatlng, endLatlng);//计算距离多少米
                if (lines <= m) {

                    size++;
                    ids.append(",");
                    ids.append(endLookEntity.getChildId());
                    if (size > 99) {
                        startLookEntity.setCountPeople("99+");
                    } else {
                        startLookEntity.setCountPeople(size + "");
                    }
                    startLookEntity.setChildId(ids.toString());
                    if (!tempData.contains(startLookEntity)) {
                        tempData.add(startLookEntity);
                    }
                } else {
                    listD.add(endLookEntity);
                }
            }
            if (listD.size() == list.size() - 1) {
                tempData.add(startLookEntity);
            }
            break;
        }
        searchFriendJL(listD, tempData);
    }
    //3=1000KM 4=500KM 5=200KM 6=100KM 7=50KM 8=30KM 9=20KM 10=10KM 11=5KM 12=2KM
    //13=1KM 14=500M 15=200M 16=100M 17=50M 18=25M 19=10M 20=5M

    /**
     * 缩放级别转换 距离 M
     *
     * @return
     */
    private int zoomToM() {
        int m = 1000;
        int zoomInt = (int) zoom;
        switch (zoomInt) {
            case 3:
                m = 1000000;
                break;
            case 4:
                m = 500000;
                break;
            case 5:
                m = 200000;
                break;
            case 6:
                m = 100000;
                break;
            case 7:
                m = 50000;
                break;
            case 8:
                m = 30000;
                break;
            case 9:
                m = 20000;
                break;
            case 10:
                m = 10000;
                break;
            case 11:
                m = 5000;
                break;
            case 12:
                m = 2000;
                break;
            case 13:
                m = 1000;
                break;
            case 14:
                m = 500;
                break;
            case 15:
                m = 200;
                break;
            case 16:
                m = 100;
                break;
            case 17:
                m = 50;
                break;
            case 18:
                m = 25;
                break;
            case 19:
                m = 10;
                break;
            case 20:
                m = 5;
                break;
        }
        return m;
    }

    /**
     * 高德定位监听器 定位我的位置
     */
    private class MyAMapLocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (mListener != null && amapLocation != null) {
                if (amapLocation.getAMapException().getErrorCode() == 0) {
                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                    MarkerInfo markerInfo = new MarkerInfo();
                    markerInfo.longitude = amapLocation.getLongitude();
                    markerInfo.latitude = amapLocation.getLatitude();
                    markerInfo.face = user.getUserface();
                    markerInfo.isFamily = 0;
                    markerInfo.number = "0";
                    markerInfo.isShowMessage = 0;
                    myMarkerInfo = markerInfo;

                    if (!pushMsgList.isEmpty()) {
                        menu.check(R.id.notes);
                        root.findViewById(R.id.notes).callOnClick();
                    } else {
                        menu.check(R.id.look);
                        postLookData(R.id.look);
                    }
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
        }
    }

    private class MyLocationSource implements LocationSource {

        @Override
        public void activate(OnLocationChangedListener listener) {
            mListener = listener;
            if (mLocationManagerProxy == null) {
                mLocationManagerProxy = LocationManagerProxy.getInstance(LookMapFragment.this.getActivity());
                mLocationManagerProxy.requestLocationData(
                        LocationProviderProxy.AMapNetwork, minTime, minDistance, myAMapLocationListener);
            }
        }

        @Override
        public void deactivate() {
            mListener = null;
            stopLocation();
        }
    }

    /**
     * 弹出文字框点击事件
     */
    private class MyInfoWindowClickListener implements AMap.OnInfoWindowClickListener {
        @Override
        public void onInfoWindowClick(Marker marker) {
            // marker.hideInfoWindow();
        }
    }

    /**
     * 标记点 点击事件
     */
    private class MyOnMarkerClickListener implements AMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            String id = (String) marker.getObject();
            if (menuId == R.id.look) {
                AppIntentManager.startGrowthTreeActivity(getActivity(), id);
            } else if (menuId == R.id.look_friend) {
                if (id.indexOf(",") >= 0) {
                    AppIntentManager.startFriendListActivity(getActivity(), id);
                } else {
                    AppIntentManager.startHomeBabyDetailActivity(LookMapFragment.this.getActivity(), id);
                }
            } else if (menuId == R.id.notes) {
                AppIntentManager.startMessageActivity(LookMapFragment.this.getActivity());
            }
            return true;
        }
    }


    private class MyCameraChangeListener implements AMap.OnCameraChangeListener {

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
        }

        @Override
        public void onCameraChangeFinish(CameraPosition cameraPosition) {
            LatLng latLng = cameraPosition.target;
            zoom = cameraPosition.zoom;
            leftTopLatLng = aMap.getProjection().fromScreenLocation(leftTopPoint);
            rightBottomLatLng = aMap.getProjection().fromScreenLocation(rightBottomPoint);
            if (menuId == R.id.look_friend) {
                lookFriend();
            }
        }
    }

    /**
     * 处理定位显示在地图中的广播
     */
    private class MyLocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String pushMessage = intent.getStringExtra(MyPushIntentService.PUSH_MESSAGE);
            if (action.equals(LOOKMAP_LOCATION_INFO)) {
                onPushMessage(pushMessage);
            }
        }
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


    private void changeChangeWindow() {
        loadUser();
        View view = inflater.inflate(R.layout.look_change_child, null);
        final PopupWindow mPopupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.transparent));
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setAnimationStyle(R.style.PopupAnimationToast);
        int swidth55 = Utils.dip2int(this.getActivity(), 75);

        mPopupWindow.showAtLocation(titleV, Gravity.TOP, 0, swidth55);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        List<Object> list = childInfoList();
        CommonAdapter<ChildEntity> adapter = new CommonAdapter<ChildEntity>(getActivity(), list, R.layout.look_change_child_item) {

            @Override
            public void convert(ViewHolder holder, final ChildEntity childEntity) {
                RadioButton nickname = holder.getView(R.id.child);
                ImageView faceView = holder.getView(R.id.face);
                nickname.setChecked(false);
                if (childId.equals(childEntity.getId()))
                    nickname.setChecked(true);

                ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(childEntity.getFace()), faceView, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        Bitmap roundBitmap = CuttingBitmap.toRoundBitmap(loadedImage);
                        ImageView imageView = (ImageView) view;
                        imageView.setImageBitmap(roundBitmap);
                    }
                });
                nickname.setText(childEntity.getNick_name());
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeChildData(childEntity.getId());
                        mPopupWindow.dismiss();
                    }
                });

                nickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeChildData(childEntity.getId());
                        mPopupWindow.dismiss();
                    }
                });


            }
        };
        listView.setAlpha(0.9f);
        listView.setAdapter(adapter);
    }

    private List<Object> childInfoList() {
        String childInfo = getCurrentUser().getChild_info();
        List<Object> list = new ArrayList<Object>();
        try {
            JSONArray array = new JSONArray(childInfo);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String childId = obj.getString("child_id");
                String childFace = obj.getString("face");
                String childName = obj.getString("nick_name");
                ChildEntity childEntity = new ChildEntity();
                childEntity.setId(childId);
                childEntity.setFace(childFace);
                childEntity.setNick_name(childName);
                list.add(childEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void changeChildData(String childId) {
        User user = getCurrentUser();
        user.setChildId(childId);
        AppContext context = (AppContext) this.getActivity().getApplication();
        context.cacheUserInfo(user);
        menu.check(R.id.look);
        postLookData(R.id.look);
    }


    private void showPopupWindow() {
        View view = inflater.inflate(R.layout.look_notes, null);
        final PopupWindow mPopupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                Utils.dip2px(this.getActivity(), 240), true);

        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.transparent));
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setAnimationStyle(R.style.PopupAnimationToast);
        mPopupWindow.showAtLocation(LookMapFragment.this.getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, -30); // 添加popwindow显示的位置

        Button menu_message = (Button) view.findViewById(R.id.menu_message);
        Button menu_send = (Button) view.findViewById(R.id.menu_send);

        menu_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                AppIntentManager.startMessageActivity(LookMapFragment.this.getActivity());
            }
        });

        menu_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                showGrowTreeTypeWindow();
            }
        });
    }

    private void showGrowTreeTypeWindow() {
        View view = inflater.inflate(R.layout.select_grow_tree_type, null);
        final PopupWindow mPopupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        // 加上下面两行可以用back键关闭popupwindow，否则必须调用dismiss();
        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.white));
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.update();
        mPopupWindow.setAnimationStyle(R.style.PopupAnimationToast);
        mPopupWindow.showAtLocation(LookMapFragment.this.getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, -30); // 添加popwindow显示的位置
        //发视频
        view.findViewById(R.id.send_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startRecordVideoActivity(getActivity(), null, "1");
                mPopupWindow.dismiss();
            }
        });

        //发照片
        view.findViewById(R.id.send_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startImageSelectorForResult(getActivity(), mSelectPath, 9, 0);
                mPopupWindow.dismiss();
            }
        });

        //发心情
        view.findViewById(R.id.send_mood).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startSubmitActivity(getActivity(), null, null, AppConfig.MOODTYPE, null, "1");
                mPopupWindow.dismiss();
            }
        });

        //发语音
        view.findViewById(R.id.send_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startSubmitActivity(getActivity(), null, null, AppConfig.AUDIOTYPE, null, "1");
                mPopupWindow.dismiss();
            }
        });
        //关闭
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPopupWindow.dismiss();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选择图片后处理结果
        if (requestCode == AppConfig.REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                AppIntentManager.startSubmitActivity(getActivity(), null, mSelectPath, AppConfig.PICTURETYPE, null, "1");
            }
        }
    }

    /**
     * 标记信息
     */
    public class MarkerInfo {
        public double latitude;
        public double longitude;
        public String name;
        public String content;
        public String face;
        public String userId;
        public String childId;
        /**
         * 是否是一家人 0 是 1 否
         */
        public int isFamily = 1;
        public String number;
        public String address;
        public int isShowMessage;
    }


}
