package com.qcsh.fuxiang.ui.look;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;

import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.adapter.MessageAdapter;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.bean.look.MessageEntity;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.ui.umengmessage.MyPushIntentService;
import com.qcsh.fuxiang.widget.AudioView;
import com.qcsh.fuxiang.widget.XListView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 家人聊天
 * Created by Administrator on 2015/9/25.
 */
public class MessageActivity extends BaseActivity implements XListView.IXListViewListener {

    private ImageButton action_bar_back;
    private TextView action_bar_title;
    private Button action_bar_action;

    private XListView xListView;
    private ImageButton fyyButton,moreButton,closeButton;
    private Button sendButton,picButton,videoButton,bqButton;
    private EditText contentText;
    private LinearLayout moreL;
    private GridView gridView;

    private String audioPath;
    private ArrayList<Object> messageList;
    private MessageAdapter adapter;
    private String userId;
    private ArrayList<String> mSelectPath;
    private User user;
    private int picOrVideo;
    private MessageReceiver messageReceiver;
    public static final String MESSAGE = "com.qcsh.fuxiang.ui.look.message";
    private  List<Object> emojis ;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_view);
        messageList = new ArrayList<Object>();
        emojis = new ArrayList<Object>();
        initEmojiList();
        user = getCurrentUser();
        userId = user.id;
        adapter = new MessageAdapter(this,messageList,userId,emojis);

        initBar();
        initView();
        initListener();
        onLoadMore();
        initReceiver();
        initEmoji();
    }

    private void initBar(){
        action_bar_back = (ImageButton)findViewById(R.id.action_bar_back);
        action_bar_title= (TextView)findViewById(R.id.action_bar_title);
        action_bar_action = (Button)findViewById(R.id.action_bar_action);

        action_bar_back.setVisibility(View.VISIBLE);
        action_bar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        action_bar_title.setText("留言板");
        action_bar_action.setVisibility(View.GONE);
    }

    private void initView(){
        xListView= (XListView)findViewById(R.id.listView);
        fyyButton = (ImageButton)findViewById(R.id.message_fyy);
        moreButton = (ImageButton)findViewById(R.id.message_more);
        picButton = (Button)findViewById(R.id.message_pic);
        videoButton = (Button)findViewById(R.id.message_video);
        bqButton = (Button)findViewById(R.id.message_bq);
        contentText = (EditText)findViewById(R.id.message_content);
        sendButton = (Button)findViewById(R.id.message_send);
        moreL = (LinearLayout)findViewById(R.id.more);
        gridView  = (GridView)findViewById(R.id.emoji);
        closeButton = (ImageButton)findViewById(R.id.close);
        xListView.setAdapter(adapter);
        xListView.setXListViewListener(this);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
    }

    private void initListener(){
        fyyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAudioWindow();
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = moreL.getVisibility();
                if (visibility == View.GONE) {
                    moreL.setVisibility(View.VISIBLE);
                } else {
                    moreL.setVisibility(View.GONE);
                }
                gridView.setVisibility(View.GONE);
            }
        });

        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picOrVideo = 0;
                mSelectPath = null;
                AppIntentManager.startImageSelectorForResult(MessageActivity.this, mSelectPath, 1, picOrVideo);
                moreL.setVisibility(View.GONE);
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picOrVideo = 1;
                mSelectPath = null;
                AppIntentManager.startImageSelectorForResult(MessageActivity.this, mSelectPath, 1, picOrVideo);
                moreL.setVisibility(View.GONE);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    UIHelper.ToastMessage(MessageActivity.this, "请输入发送内容");
                } else {
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setMsgtype(MessageAdapter.MESSAGE_TYPE.text.toString());
                    messageEntity.setContent(content);
                    sendMessage(messageEntity);
                    contentText.setText("");
                }
            }
        });
        bqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreL.setVisibility(View.GONE);
                int visibility = gridView.getVisibility();
                if (visibility == View.GONE) {
                    gridView.setVisibility(View.VISIBLE);
                    closeButton.setVisibility(View.VISIBLE);
                } else {
                    gridView.setVisibility(View.GONE);
                    closeButton.setVisibility(View.GONE);
                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.setVisibility(View.GONE);
                closeButton.setVisibility(View.GONE);
            }
        });

    }

    private void initReceiver(){
        messageReceiver = new MessageReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE);
        registerReceiver(messageReceiver, intentFilter);
    }


    /**
     * 初始化表情包
     */
    private void initEmoji(){

        CommonAdapter commonAdapter = new CommonAdapter<EmojiObject>(this,emojis,R.layout.message_item_time) {
            @Override
            public void convert(ViewHolder holder, final EmojiObject o) {
                ImageView imageView =  holder.getView(R.id.msg_bq);
                imageView.setImageBitmap(o.bitmap);
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap bitmap = o.bitmap;
                        ImageSpan imageSpan = new ImageSpan(MessageActivity.this, bitmap);
                        SpannableString spannableString = new SpannableString("|" + o.id + "|");   //“emoji”是图片名称的前缀
                        spannableString.setSpan(imageSpan, 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        contentText.append(spannableString);
                        moreL.setVisibility(View.GONE);
                    }
                });
            }
        };

        gridView.setAdapter(commonAdapter);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppConfig.REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setContent(mSelectPath.get(0));
                if(picOrVideo == 0)
                messageEntity.setMsgtype(MessageAdapter.MESSAGE_TYPE.pic.toString());
                else
                messageEntity.setMsgtype(MessageAdapter.MESSAGE_TYPE.video.toString());
                sendMessage(messageEntity);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageReceiver);
    }

    private void showAudioWindow(){
        View view = LayoutInflater.from(MessageActivity.this).inflate(R.layout.layout_submit_popwindow, null);
        FrameLayout audioLayout = (FrameLayout) view.findViewById(R.id.layout_audio);
        LinearLayout moodFirstLayout = (LinearLayout) view.findViewById(R.id.layout_moodandfirst);
        final AudioView mAudioView = (AudioView) view.findViewById(R.id.audio_view);
        final Button cancelBtn = (Button) view.findViewById(R.id.audio_cacel);
        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(MessageActivity.this, 300), true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        audioLayout.setVisibility(View.VISIBLE);
        moodFirstLayout.setVisibility(View.GONE);

        mAudioView.start(new AudioView.OnAudioListener() {
            @Override
            public void onStartRecord() {
                cancelBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onRecording(int time) {
            }

            @Override
            public void onRecordFinish() {
                audioPath = mAudioView.getAudioPath();
                int mTimeMill = mAudioView.getDuration();
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setContent(audioPath);
                messageEntity.setmTimeMill(mTimeMill + "");
                messageEntity.setMsgtype(MessageAdapter.MESSAGE_TYPE.audio.toString());
                sendMessage(messageEntity);
                popupWindow.dismiss();
            }

            @Override
            public void onPlaying() {

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioView.cancelRecord();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mAudioView.stopRecord();
                mAudioView.stopPlay();
            }
        });
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        popupWindow.showAtLocation(MessageActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, -30);
     }




    private void sendMessage(MessageEntity messageEntity){
        messageEntity.setSendStatus(MessageAdapter.SEND_STATUS.LOAD_ING.toString());
        messageEntity.setFace(user.getUserface());
        long time = new Date().getTime() / 1000 ;
        messageEntity.setCreatetime(Utils.getStandardDate(time));
        messageEntity.setUser_id(userId);
        messageEntity.setSend("0");
        messageList.add(messageEntity);
        adapter.notifyDataSetChanged();
        xListView.setSelection(messageList.size() - 1);
    }

    private int pageCount;
    private int currentPage = 1;

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onLoadMore() {
        currentPage = 1;
        messageList.clear();
        loadData();
    }

    private void loadData(){

        if(pageCount > 0 && currentPage > pageCount){
            UIHelper.ToastMessage(MessageActivity.this,"已经是最后记录");
            stopLoading(xListView);
            return;
        }

        ApiClient.get(this, ApiConfig.MESSAGE_LIST, new ApiResponseHandler<MessageEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                stopLoading(xListView);

                List<Object> da = entity.data;
                if (null != da) {


                    ArrayList<Object> temps = null;
                    if (!messageList.isEmpty()) {
                        temps = new ArrayList<Object>();
                        temps.addAll(messageList);
                        messageList.clear();
                    }

                    for (int i = da.size() - 1; i >= 0; i--) {
                        MessageEntity messageEntity = (MessageEntity) da.get(i);

                        try {
                            JSONObject jsonObject = new JSONObject(messageEntity.getUserInfo());
                            String face = jsonObject.getString("face");
                            messageEntity.setFace(face);
                            String createTime = messageEntity.getCreatetime();
                            Date date = StringUtils.toDate(createTime);
                            long time = date.getTime() / 1000 ;
                            createTime = Utils.getStandardDate(time);

                            messageEntity.setCreatetime(createTime);
                        } catch (Exception e) {

                        }
                        messageList.add(messageEntity);
                    }

                    if (null != temps) {
                        messageList.addAll(temps);
                    }
                    adapter.notifyDataSetChanged();
                    if (currentPage == 1)
                        xListView.setSelection(messageList.size() - 1);
                    pageCount = entity.pageCount;
                    currentPage++;
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                stopLoading(xListView);
                UIHelper.ToastMessage(MessageActivity.this, errorInfo.getMessage());
            }
        }, "userId", userId, "page", currentPage + "", "per-page", "1");
    }


    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra(MyPushIntentService.PUSH_MESSAGE);
            try{
                JSONObject jsonObject = new JSONObject(message);
                String content = jsonObject.getString("content");
                String type = jsonObject.getString("msgType");
                String face = jsonObject.getString("face");
                String createTime = jsonObject.getString("createtime");
                String userId = jsonObject.getString("userId");
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setFace(face);
                messageEntity.setContent(content);
                messageEntity.setCreatetime(createTime);
                messageEntity.setMsgtype(type);
                messageEntity.setUser_id(userId);
                messageEntity.setSendStatus(MessageAdapter.SEND_STATUS.SUCCESS.toString());
                messageList.add(messageEntity);
                adapter.notifyDataSetChanged();
                xListView.setSelection(messageList.size() - 1);

            }catch (Exception e){

            }
        }
    }


    private void initEmojiList(){
        Resources resources = getResources();

        EmojiObject emoji_baoquan = new EmojiObject();
        emoji_baoquan.id = "emoji_01";
        emoji_baoquan.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_baoquan);
        emojis.add(emoji_baoquan);

        EmojiObject emoji_biezui = new EmojiObject();
        emoji_biezui.id = "emoji_02";
        emoji_biezui.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_biezui);
        emojis.add(emoji_biezui);

        EmojiObject emoji_bishi = new EmojiObject();
        emoji_bishi.id = "emoji_03";
        emoji_bishi.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_bishi);
        emojis.add(emoji_bishi);

        EmojiObject emoji_chijing = new EmojiObject();
        emoji_chijing.id = "emoji_04";
        emoji_chijing.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_chijing);
        emojis.add(emoji_chijing);

        EmojiObject emoji_ciya = new EmojiObject();
        emoji_ciya.id = "emoji_05";
        emoji_ciya.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_ciya);
        emojis.add(emoji_ciya);

        EmojiObject emoji_dajing = new EmojiObject();
        emoji_dajing.id = "emoji_06";
        emoji_dajing.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_dajing);
        emojis.add(emoji_dajing);

        EmojiObject emoji_daku = new EmojiObject();
        emoji_daku.id = "emoji_07";
        emoji_daku.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_daku);
        emojis.add(emoji_daku);

        EmojiObject emoji_dengyan = new EmojiObject();
        emoji_dengyan.id = "emoji_08";
        emoji_dengyan.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_dengyan);
        emojis.add(emoji_dengyan);

        EmojiObject emoji_fahuo = new EmojiObject();
        emoji_fahuo.id = "emoji_09";
        emoji_fahuo.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_fahuo);
        emojis.add(emoji_fahuo);

        EmojiObject emoji_fanu = new EmojiObject();
        emoji_fanu.id = "emoji_10";
        emoji_fanu.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_fanu);
        emojis.add(emoji_fanu);

        EmojiObject emoji_guzhang = new EmojiObject();
        emoji_guzhang.id = "emoji_11";
        emoji_guzhang.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_guzhang);
        emojis.add(emoji_guzhang);

        EmojiObject emoji_haha = new EmojiObject();
        emoji_haha.id = "emoji_12";
        emoji_haha.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_haha);
        emojis.add(emoji_haha);

        EmojiObject emoji_haixiu = new EmojiObject();
        emoji_haixiu.id = "emoji_13";
        emoji_haixiu.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_haixiu);
        emojis.add(emoji_haixiu);

        EmojiObject emoji_han = new EmojiObject();
        emoji_han.id = "emoji_14";
        emoji_han.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_han);
        emojis.add(emoji_han);

        EmojiObject emoji_haqian = new EmojiObject();
        emoji_haqian.id = "emoji_15";
        emoji_haqian.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_haqian);
        emojis.add(emoji_haqian);

        EmojiObject emoji_kelian = new EmojiObject();
        emoji_kelian.id = "emoji_16";
        emoji_kelian.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_kelian);
        emojis.add(emoji_kelian);

        EmojiObject emoji_koubi = new EmojiObject();
        emoji_koubi.id = "emoji_17";
        emoji_koubi.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_koubi);
        emojis.add(emoji_koubi);

        EmojiObject emoji_nanguo = new EmojiObject();
        emoji_nanguo.id = "emoji_18";
        emoji_nanguo.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_nanguo);
        emojis.add(emoji_nanguo);

        EmojiObject emoji_tiaomei = new EmojiObject();
        emoji_tiaomei.id = "emoji_19";
        emoji_tiaomei.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_tiaomei);
        emojis.add(emoji_tiaomei);

        EmojiObject emoji_tiaopi = new EmojiObject();
        emoji_tiaopi.id = "emoji_20";
        emoji_tiaopi.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_tiaopi);
        emojis.add(emoji_tiaopi);

        EmojiObject emoji_touxiao = new EmojiObject();
        emoji_touxiao.id = "emoji_21";
        emoji_touxiao.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_touxiao);
        emojis.add(emoji_touxiao);

        EmojiObject emoji_wenhao = new EmojiObject();
        emoji_wenhao.id = "emoji_22";
        emoji_wenhao.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_wenhao);
        emojis.add(emoji_wenhao);

        EmojiObject emoji_woyun = new EmojiObject();
        emoji_woyun.id = "emoji_23";
        emoji_woyun.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_woyun);
        emojis.add(emoji_woyun);
    }

    public class EmojiObject {
       public String id;
       public Bitmap bitmap;
    }


}
