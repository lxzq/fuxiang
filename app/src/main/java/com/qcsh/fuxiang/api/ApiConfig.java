package com.qcsh.fuxiang.api;


/**
 * 系统请求地址配置
 * Created by admin on 15/5/15.
 */
public class ApiConfig {

    public static final int TIME_OUT = 30 * 1000;

    public static final int HANDLE_SUCCESS = 200;
    /**
     * 检测新版本
     */
    public static final String CHECK_NEW_VERSION = "user/CheckNewVersion";
    /**
     * 成长树列表
     */
    public static final String GROWTHTREE_LIST = "growthtree/index";
    /**
     * 成长树列表孩子信息
     */
    public static final String GROWTHTREE_CHILD = "child/view";
    /**
     * 成长树详情
     */
    public static final String GROWTHTREE_DETAIL = "";
    /**
     * 成长树评论
     */
    public static final String GROWTHTREE_SENDCOMMENT = "comment/savecomment";
    /**
     * 删除成长树
     */
    public static final String GROWTHTREE_DELETE = "growthtree/delete";
    /**
     * 成长树点赞
     */
    public static final String GROWTHTREE_ZAN = "praise/savepraise";
    /**
     * 成长树收藏
     */
    public static final String GROWTHTREE_SHOUCANG = "collect/savecollect";
    /**
     * 发送我的定位 位置
     */
    public static final String GAODE_LOCATION = "position/send-position";

    /**
     * 看着模块中 留言板发布留言
     */
    public static final String SEND_NOTES = "message/release-message";

    /**
     * 看着模块中 请求看着数据
     */
    public static final String LOOK = "position/positioning-friend";

    /**
     * 看着模块中 请求找伙伴数据
     */
    public static final String LOOK_FRIEND = "position/find-friend";
    /**
     * 获取孩子信息列表
     */
    public static final String GET_CHILDREN_INFO = "children/childlist";

    /**
     * 定位自己孩子的位置 信息
     */
    public static final String LOC_CHILD = "position/acquisition-positioning";

    /**
     * 看着模块中 留言板
     */
    public static final String MESSAGE_BOARD = "message/get-message";

    /**
     * 帮帮列表
     */
    public static final String BANG_LIST = "bb/index";

    /**
     * 帮详情评论
     */
    public static final String BANG_COMMENT = "comment/bbcomment";

    /**
     * 发布帮帮问题
     */
    public static final String PUBLISH_BANG_QUESTION = "bb/releasecontent";

    /**
     * 发布帮评论
     */
    public static final String SEND_BANG_COMMENT = "comment/savebbcomment";

    /**
     * 帮详细打赏
     */
    public static final String BANG_PAY_MONEY = "";
    /**
     * 广场和社区
     */
    public static final String SQUARE_CIRCLE = "ss/index";
    /**
     * 广场和社区删除
     */
    public static final String SQUARE_CIRCLE_DELETE = "ss/delete";
    /**
     * 广场和社区的发布
     */
    public static final String SQUARE_CIRCLE_SUBMIT = "ss/releasecontent";
    /**
     * 广场和社区的评论
     */
    public static final String SS_SEND_COMMENT = "comment/savesscomment";
    /**
     * 广场和社区的评论列表
     */
    public static final String SS_COMMENT = "comment/sscomment";
    /**
     * 登录
     */
    public static final String SYS_LOGIN = "user/login";

    /**
     * 微博登录
     */
    public static final String SYS_WEIBO = "user/loginbyweibo";

    /**
     * QQ登录
     */
    public static final String SYS_QQ = "user/loginbyqq";

    /**
     * 微信登录
     */
    public static final String SYS_WEIXIN = "user/loginbywechat";

    /**
     * 注册1/3
     */
    public static final String SYS_REGISTER = "user/register";
    /**
     * 发送验证码
     */
    public static final String SYS_SENDKEY = "user/sendcode";

    /**
     * 注册2/3(合并为一步)
     */
    //public static final String SYS_REGISTER_phone = "";

    /**
     * 注册3/3(合并为一步)
     */
    //public static final String SYS_REGISTER_code = "";

    /**
     * 重置密码1/3
     *//*
    public static final String SYS_RESET_PHONE = "";

    *//**
     * 重置密码2/3
     *//*
    public static final String SYS_RESET_IDENTITY = "";*/

    /**
     * 重置密码3/3
     */
    public static final String SYS_RESET = "user/retrievepwd";

    /**
     * 带孩子获取数据
     */
    public static final String LOOK_CHILD = "children/family-list";

    /**
     * 切换带孩子
     */
    public static final String CHANGE_CHILD = "/children/change-nurse";

    /**
     * 成长树的发布
     */
    public static final String SEND_DATA = "growthtree/share";

    /**
     * 添加孩子
     */
    public static final String ADD_CHILD = "children/add-children";

    /**
     * 添加孩子好友
     */
    public static final String ADD_CHILD_FRIEND = "partner/senpartner";
    /**
     * 绑定解绑账号
     */
    public static final String BIND_NUMBER = "user/bind";

    /**
     * 获取宝宝信息
     */
    public static final String CHILD_DETAIL = "children/childreninfo";

    /**
     * 加小伙伴验证
     */
    public static final String FRIENDS_VERICATION = "partner/handlepartner";

    /**
     * 发送家人聊天信息
     */
    public static final String SEND_MESSAGE = "message/sendfamilychat";

    /**
     * 获取家人聊天信息
     */
    public static final String MESSAGE_LIST = "message/familychat";

    /**
     * 伙伴管理列表
     */
    public static final String PARTNER_MANAGER = "partner/partnerlist";
    /**
     * 删除伙伴
     */
    public static final String PARTNER_DELETE = "partner/deletepartner";
    /**
     * 家人管理
     */
    public static final String FAMILY_MAMAGEER = "partner/user-family";
    /**
     * 编辑个人资料
     */
    public static final String EDIT_USER = "user/edituser";

    /**
     * 修改密码
     */
    public static final String EDIT_PASSWORD = "user/editpassword";

    /**
     * 获取赞列表
     */
    public static final String GET_ZAN_LIST = "praise/praiseinfo";
    /**
     * 获取收藏列表
     */
    public static final String GET_COLLECT_LIST = "collect/collectinfo";
    /**
     * 获取评论列表
     */
    public static final String GET_COMMENT_LIST = "comment/commentinfo";
     /**
     * 搜索用户
     */
    public static final String SERCH_USER = "children/userlist";

    /**
     * 添加家人
     */
    public static final String ADD_FAMILY = "children/relation";
    /**
     * 添加家人验证
     */
    public static final String ADD_FAMILY_VERICATION = "children/saverelation";

    /**
     * 智慧乐园TAB1数据
     */
    public static final String LE_YUAN_TAB1 = "";
    /**
     * 智慧乐园TAB2数据
     */
    public static final String LE_YUAN_TAB2 = "";
    /**
     * 成长树分享到晒晒
     */
    public static final String SHARE_TO_SS = "growthtree/ss-share";
    /**
     * 我的收藏-1成长树 2晒晒 3帮帮
     */
    public static final String HOME_MY_COLLECT = "collect/mycollectinfo";
    /**
     * 我的晒晒
     */
    public static final String HOME_MY_SHAISHAI = "ss/myssinfo";

    /**
     * 乐园地址
     */
    public static final String LEYUAN_ADDRESS = "";

    /**
     * 乐园直播评论列表
     */
    public static final String LEYUAN_LIVE_COMMENT = "";

    /**
     * 发送乐园直播评论
     */
    public static final String LEYUAN_ADD_LIVE_COMMENT = "";

    /**
     * 获取乐园项目摄像头列表
     */
    public static final String LEYUAN_DETAIL_CAMERA = "";

}
