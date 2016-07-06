package com.qcsh.fuxiang.ui.umengmessage;

import org.android.agoo.client.BaseConstants;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


import com.qcsh.fuxiang.AppContext;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.AppStart;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.ui.HomeActivity;
import com.qcsh.fuxiang.ui.home.FriendsVerificationActivity;
import com.qcsh.fuxiang.ui.home.FriendsVerificationResultActivity;
import com.qcsh.fuxiang.ui.look.LookMapFragment;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

/**
 * 友盟消息推送
 * Developer defined push intent service. 
 * Remember to call {@link com.umeng.message.PushAgent#setPushIntentServiceClass(Class)}. 
 * @author lucas
 *
 */
public class MyPushIntentService extends UmengBaseIntentService{
	private static final String TAG = MyPushIntentService.class.getName();

	private AppContext appContext;
	public static final String PUSH_MESSAGE = "push_message";
	public static final String IS_EXIT = "is_exit";

	private static final String OPEN_PUSH = "open_close";//消息推送
	private static final String ADD_FRIEND = "add_friend";//有人加我好友提醒
	public static final String ADD_COMMENT = "add_comment";//评论提醒
	public static final String ADD_ZAN = "add_zan";//有人赞我
	public static final String SR = "no_disturb_mode";//夜间防骚扰

	@Override
	protected void onMessage(Context context, Intent intent) {
		super.onMessage(context, intent);
		appContext = (AppContext)context;
		SharedPreferences sharedPreferences = getSharedPreferences(AppStart.SYS_VERSION_PREF, Context.MODE_PRIVATE);
		boolean isExit = sharedPreferences.getBoolean(IS_EXIT,true);
		try {
			String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
			UMessage msg = new UMessage(new JSONObject(message));
			Log.i(TAG, "message=" + message);
			Log.i(TAG, "custom="+msg.custom);
			Log.i(TAG, "收到自定义消息..............");
			String msgType = "1";//消息类型 1 加小伙伴  2 家人留言通知  3 评论通知  4 有人赞我通知 5验证结果通知 6加家人
			JSONObject custom = null;
			try{
				custom = new JSONObject(msg.custom);
				msgType = custom.getString("msgType");
			}catch (Exception e){}

			//加好友验证
			if("1".equals(msgType) || "6".equals(msgType)){
				showAddFriendNotcation(msg.custom,msgType);
			}else if("2".equals(msgType)){//家人聊天

			if(isExit){
				try {
					JSONObject jsonObject = new JSONObject(msg.custom);
					String content = jsonObject.getString(LookMapFragment.NAME) + ":" +
							jsonObject.getString(LookMapFragment.CONTENT);
					showNotificaction(getString(R.string.app_name),content,msg.custom);
				} catch (JSONException e) {}
			}else{
				AppIntentManager.sendHomeTab1TipsBroadcast(appContext);
				AppIntentManager.sendMapLocationBroadcast(appContext, msg.custom);
				AppIntentManager.sendMessageBroadcast(appContext, msg.custom);
			}

			}else if("3".equals(msgType)){//3 评论通知
				boolean isComment = sharedPreferences.getBoolean(ADD_COMMENT,true);
				if(isComment){
					String comment = custom.getString("content");
					showCommentNotification(comment+",评论了我.");
				}

			}else if("4".equals(msgType)){//4 有人赞我通知
				boolean isZan = sharedPreferences.getBoolean(ADD_ZAN,true);
				if(isZan){
					String comment = custom.getString("content");
					showCommentNotification(comment+",赞了我.");
				}
			}
			else if("5".equals(msgType)){//5加小伙伴验证结果通知
				showAddFriendResultNotcation(msg.custom);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}


	/**
	 * 显示加小伙伴通知
	 * @param custom
	 */
	private void showAddFriendNotcation(String custom,String msgType){
		NotificationManager manager = (NotificationManager) this.appContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = createNotification();

		PendingIntent pendingIntent = null;
		pendingIntent = PendingIntent.getActivity(this.appContext, 0,
				new Intent(this.appContext, FriendsVerificationActivity.class).putExtra(PUSH_MESSAGE, custom).putExtra("msgType",msgType),
				PendingIntent.FLAG_UPDATE_CURRENT);
		String contentText = "有人加我成为小伙伴,点击查看";
		if("6".equals(msgType)){
			  contentText = "有人邀请我成为家人,点击查看";
		}
		notification.setLatestEventInfo(appContext, getString(R.string.app_name),contentText ,
				pendingIntent);
		manager.notify(0, notification);

	}

	/**
	 * 显示加小伙伴验证结果通知
	 * @param custom
	 */
	private void showAddFriendResultNotcation(String custom){
		NotificationManager manager = (NotificationManager) this.appContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = createNotification();

		PendingIntent pendingIntent = null;
		pendingIntent = PendingIntent.getActivity(this.appContext, 0,
				new Intent(this.appContext, FriendsVerificationResultActivity.class).putExtra(PUSH_MESSAGE, custom),
				PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(appContext, getString(R.string.app_name), "邀友验证结果,点击查看",
				pendingIntent);
		manager.notify(0, notification);

	}

	/**
	 * 显示家人的留言通知
	 * @param title
	 * @param content
	 * @param custom
	 */
	private void showNotificaction(String title,String content,String custom){
		NotificationManager manager = (NotificationManager) this.appContext
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = createNotification();
		PendingIntent pendingIntent = null;
		pendingIntent = PendingIntent.getActivity(this.appContext, 0,
				new Intent(this.appContext, HomeActivity.class).putExtra(PUSH_MESSAGE, custom),
				PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(appContext, title, content,
				pendingIntent);
		manager.notify(0, notification);
	}

	/**
	 * 显示评论通知 和 点赞 通知
	 * @param content
	 */
	private void showCommentNotification(String content){
		NotificationManager manager = (NotificationManager) this.appContext
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = createNotification();
		notification.setLatestEventInfo(appContext, getString(R.string.app_name), content,
				null);
		manager.notify(0, notification);
	}


	private Notification createNotification(){
		Notification notification = new Notification();
		notification.icon = R.mipmap.logo;
		notification.tickerText = getString(R.string.app_name);
		// 设置点击此通知后自动清除
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.when = System.currentTimeMillis();

		notification.defaults |= Notification.DEFAULT_SOUND;//声音提醒
		notification.defaults |= Notification.DEFAULT_VIBRATE;//振动提醒
	    return notification;
	}


	/**
	 * 推送消息
	 * @param appContext
	 * @param message true 开启推送  false 关闭推送
	 */
	public static void pushMessageSet(AppContext appContext, boolean message){
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(AppStart.SYS_VERSION_PREF, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(OPEN_PUSH,message).commit();
	}

	/**
	 * 有人加我好友
	 * @param appContext
	 * @param message true 开启推送  false 关闭推送
	 */
	public static void pushMessageFriend(AppContext appContext, boolean message){
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(AppStart.SYS_VERSION_PREF, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(ADD_FRIEND,message).commit();
	}

	/**
	 * 评论提醒
	 * @param appContext
	 * @param message true 开启推送  false 关闭推送
	 */
	public static void pushMessageComment(AppContext appContext, boolean message){
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(AppStart.SYS_VERSION_PREF, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(ADD_COMMENT,message).commit();
	}

	/**
	 * 有人赞我提醒
	 * @param appContext
	 * @param message true 开启推送  false 关闭推送
	 */
	public static void pushMessageZan(AppContext appContext, boolean message){
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(AppStart.SYS_VERSION_PREF, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(ADD_ZAN,message).commit();
	}

	/**
	 * 夜间放骚扰模式
	 * @param appContext
	 * @param message
	 */
	public static void pushMessageSR(AppContext appContext, boolean message){
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(AppStart.SYS_VERSION_PREF, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(SR,message).commit();
	}
}
