package com.qcsh.fuxiang.api;
/**
 * HTTP 请求错误类型定义
 * @author Administrator
 *
 */
public class ErrorMassage {

	
	
    public static final int UPDATE_CUSSESS = 201;
	
	public static final String UPDATE_CUSSESS_MSG = "修改成功";
	
	public static final int INSERT_SUCCESS = 202;
	
	public static final String INSERT_SUCCESS_MSG = "保存成功";
	
	public static final int DELETE_SUCCESS = 203;
	
	public static final String DELETE_SUCCESS_MSG = "删除成功";
	
	public static final int HOST_ERROR = 404;
	
	public static final String HOST_ERROR_MSG = "您的网络似乎不给力，请检查网络配置!";
	
    public static final int NET_ERROR = 400;
	
	public static final String NET_ERROR_MSG = "网络错误,请稍后再试!";
	
	public static final int DATA_ERROR = 500;
	
	public static final String DATA_ERROR_MSG = "数据解析格式错误!";
	
	
	
	public static final int LOGIN_ERROR = 10001;
	
	public static final String LOGIN_ERROR_MSG = "登录失败,账号或密码错误!";
	
	public static final int NOT_REG_ERROR = 10002;
	
	public static final String NOT_REG_ERROR_MSG = "手机号未注册!";
	
	public static final int PHONE_IS_NULL = 10003;
	
	public static final String PHONE_IS_NULL_MSG = "手机号是空号!";
	
	
	
	public static final int DATA_IS_NULL = 20000;
	
	public static final String DATA_IS_NULL_MSG = "资源目前没有数据!";
	
	public static final int LAST_PAGE = 20001;
	
	public static final String LAST_PAGE_MSG = "已经是最后一条记录";
	
    public static final int NOT_ENOUGH = 20003;
	
	public static final String NOT_ENOUGH_MSG = "您的金额不足,请充值";
	
    public static final int NO_GOODS = 20004;
	
	public static final String NO_GOODS_MSG = "支付失败,商品数量不足";
	
	
	
	public static final int LOGIN_OUT = 30005;
	
	public static final String LOGIN_OUT_MSG = "登录超时";
	
	public static final int VERIFY = 30006;
	
	public static final String VERIFY_MSG = "邀友验证成功";
	
	
	
}
