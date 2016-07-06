package com.qcsh.fuxiang;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.util.OSSToolKit;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.common.AppCache;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.yixia.weibo.sdk.VCamera;
import com.yixia.weibo.sdk.util.DeviceUtils;

import java.io.File;

public class AppContext extends Application {

    private boolean login;    //登录状态
    private double latitude;
    private double longitude;
    private String address;

    private User user;
    static final String accessKey = "ukZxh4uCD6VWvwd2"; // 测试代码没有考虑AK/SK的安全性
    static final String screctKey = "H6NsydEk5SJUXb3pWYyj48etiWUgeq";


    @Override
    public void onCreate() {
        super.onCreate();
        // Crashlytics.start(this);
        //注册App异常崩溃处理器
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        initImageLoader(this);
        intOSS(this);
        initVideoCachePath();

    }

    //设置拍摄视频缓存路径
    private void initVideoCachePath() {
        if (DeviceUtils.isZte()) {
            if (Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).exists()) {
                VCamera.setVideoCachePath(
                        Environment.getExternalStorageDirectory() + File.separator + AppConfig.CACHE_VIDEO);
            } else {
                VCamera.setVideoCachePath(
                        Environment.getExternalStorageDirectory().getPath().replace(
                                "/sdcard/", "/sdcard-ext/") + File.separator + AppConfig.CACHE_VIDEO);
            }
        } else {
            VCamera.setVideoCachePath(
                    Environment.getExternalStorageDirectory() + File.separator + AppConfig.CACHE_VIDEO);
            /*VCamera.setVideoCachePath(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM) + "/JJBcacheVideo/");*/
        }
        //开启log 输出,ffmpeg 输出到logcat
        VCamera.setDebugMode(true);
        //初始化拍摄SDK，必须
        VCamera.initialize(this);
    }

    /**
     * 获取App安装包信息
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null) info = new PackageInfo();
        return info;
    }

    /**
     * 用户是否登录
     */
    public boolean isLogin() {
        return login;
    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    public User getCurUser() {
        return this.user;
    }

    // 获取应用的版本号
    public int getCurVersion() {
        PackageManager packageManager = getPackageManager();
        int curVersionCode = 0;
        try {
            assert packageManager != null;
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            curVersionCode = packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return curVersionCode;
    }

    // 获取应用的版本号
    public String getCurVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            assert packageManager != null;
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }


    /**
     * 读取缓存用户信息
     *
     * @return
     */
    public User getCacheLoginInfo() {
        return (User) AppCache.readObject(this, AppConfig.CACHE_LOGINUSER);
    }

    /**
     * 登录成功后缓存用户信息
     *
     * @param user
     */
    public void cacheUserInfo(User user) {
        this.login = true;
        this.user = user;
        AppCache.saveObject(this, user, AppConfig.CACHE_LOGINUSER);
    }


    public void intOSS(AppContext customApplication) {
        OSSClient.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
            @Override
            public String generateToken(String httpMethod, String md5, String type, String date,
                                        String ossHeaders, String resource) {

                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                        + resource;

                return OSSToolKit.generateToken(accessKey, screctKey, content);
            }
        });
        OSSClient.setGlobalDefaultHostId("oss-cn-hangzhou.aliyuncs.com"); // 设置全局默认数据中心域名
        OSSClient.setGlobalDefaultACL(AccessControlList.PRIVATE); // 设置全局默认bucket访问权限
        OSSClient.setApplicationContext(getApplicationContext()); // 传入应用程序context
    }

    private void initImageLoader(Context context) {

        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), AppConfig.CACHE_IMAGES);
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 16) * 1024 * 1024; // 1/8 of app
            // memory limit
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                /*.showImageOnLoading(R.drawable.default_loading_100x100)
                .showImageForEmptyUri(R.drawable.default_loading_100x100)
                .showImageOnFail(R.drawable.default_loading_100x100)*/
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .memoryCacheSize(memoryCacheSize)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .defaultDisplayImageOptions(defaultDisplayImageOptions)
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
