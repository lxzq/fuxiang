package com.qcsh.fuxiang;

import android.annotation.SuppressLint;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 *
 * @author owen
 * @version 1.0
 * @created 2015--5-6
 */
@SuppressLint("NewApi")
public class AppConfig {


    public final static String CACHE_LOGINUSER = "CACHE_LOGINUSER";
    public final static String CACHE_IMAGES = "JJB/Images";
    public static final String CACHE_VIDEO = "JJB/Video/";
    public static final String CACHE_AUDIO = "JJB/Audio/";
    public final static String CACHE_SYSCONFIG = "CACHE_SYSCONFIG";

    public final static String QQ_APPID = "1104739703";
    public final static String QQ_APPKEY = "FbQ4GZTBukI4ZPmC";
    public final static String WEIXIN_APPID = "wx8775f0d9d378c50e";
    public final static String WEXIN_APPSECRET = "edb04ffda7bf0eccd2c2c61b79fee1e3";


    public final static String HOST_URL = "http://115.29.224.17:8080/";
   // public final static String HOST_URL = "http://172.16.5.31:8080/";

//    public final static String HOST_URL = "http://115.29.224.17:8080/";
//    public final static String HOST_URL = "http://172.16.5.53:8080/";

    public final static String BASE_URL = HOST_URL + "" ;
    //public final static String PHOTO_SERVER = "http://image.jjb777.com/";
    public final static String PHOTO_SERVER = "http://image.wevaluing.com/";

    public final static String FILE_SERVER = "http://file.wevaluing.com";
    /**
     * 图片缩放宽度
     */
    public static final int IMAGE_WIDTH = 150;
    /**
     * 图片缩放高度
     */
    public static final int IMAGE_HEIGHT = 150;

    /**
     * 选择图片后返回的requestCode
     */
    public static final int REQUEST_IMAGE = 2;
    /**
     * 发布类型
     */
    public static final int MOODTYPE = 4;
    public static final int VIDEOTYPE = 2;
    public static final int AUDIOTYPE = 3;
    public static final int PICTURETYPE = 1;

    /**
     * 视频资源类型
     */
    public enum VIDEO {
        /**
         * 本地视频
         */
        LOCAL_VIDEO,
        /**
         * 网络视频
         */
        HTTP_VIDEO
    }

    /**
     * OSS 上传文件
     */
    public enum OSS_UPLOAD {
        /**
         * 图片文件夹
         */
        images,
        /**
         * 视频文件夹
         */
        videos,
        /**
         * 音频文件夹
         */
        audios
    }


    // 指定长宽，返回固定大小
    public static String getImageURLfitXY(String strName, int w, int h) {
        return PHOTO_SERVER + strName + "@1e_" + w + "w_" + h
                + "h_1c_0i_1o_90Q_1x";
    }

    public static String getUserPhoto_x(String strName) {
        if(strName.startsWith("http")){
            return strName;
        }
        return PHOTO_SERVER + strName + "@1e_117w_117h_1c_0i_1o_90Q_1x.jpg";
    }

    public static String getUserPhoto_xx(String strName) {
        if(strName.startsWith("http")){
            return strName;
        }
        return PHOTO_SERVER + strName + "@1e_150w_150h_1c_0i_1o_90Q_1x.jpg";
    }

    public static String getGroupLogo(String strName) {
        return PHOTO_SERVER + strName + "@1e_150w_150h_1c_0i_1o_90Q_1x.jpg";
    }

    public static String getUserPhoto_xxx(String strName) {
        return PHOTO_SERVER + strName + "@1e_270w_270h_1c_0i_1o_90Q_1x.jpg";
    }

    public static String getMessageImage(String strName) {
        return PHOTO_SERVER + strName + "@1e_234w_234h_1c_0i_1o_90Q_1x.jpg";
    }

    public static String getActivityLogo(String strName) {
        return PHOTO_SERVER + strName + "@1e_468w_216h_1c_0i_1o_90Q_1x.jpg";
    }

    public static String getShareLogo(String strName) {
        return PHOTO_SERVER + strName + "@1e_1080w_540h_1c_0i_1o_90Q_1x.jpg";
    }

    public static String getOriginalImage(String strName) {
        return PHOTO_SERVER + strName;
    }

}