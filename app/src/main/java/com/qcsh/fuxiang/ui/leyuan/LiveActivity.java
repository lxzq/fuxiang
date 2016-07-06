package com.qcsh.fuxiang.ui.leyuan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.bean.leyuan.LeyuanLiveCommentEntity;
import com.qcsh.fuxiang.bean.leyuan.LeyuanTab2Entity;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.JJBLiveView;
import com.qcsh.fuxiang.widget.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播界面
 * Created by Administrator on 2015/11/4.
 */
public class LiveActivity extends BaseActivity implements XListView.IXListViewListener{

    private JJBLiveView jjbLiveView;
    private XListView xListView;
    private TextView titleView;

    private List<Object> list;
    private CommonAdapter<LeyuanLiveCommentEntity> commonAdapter;
    private int pageCount;
    private int currentPage = 1;

    private String path;
    private String title;
    private String id;
    private LayoutInflater inflater;
    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leyuan_live);
        inflater = LayoutInflater.from(this);
        userId = getCurrentUser().getId();
        initBar();
        path = getIntent().getStringExtra("path");
        title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        initAdapter();
        initView();
        loadData();
    }

    private void initBar(){
        ImageButton imageButton = (ImageButton)findViewById(R.id.action_bar_back);
        TextView textView = (TextView)findViewById(R.id.action_bar_title);
        Button button = (Button)findViewById(R.id.action_bar_action);
        button.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.VISIBLE);
        textView.setText("直播详情");
        button.setText("评论");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
    }

    private void initView(){
        jjbLiveView = (JJBLiveView)findViewById(R.id.video_view);
        xListView = (XListView)findViewById(R.id.comment_list);
        titleView = (TextView)findViewById(R.id.live_title);

        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        xListView.setXListViewListener(this);
        xListView.setAdapter(commonAdapter);
        titleView.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        jjbLiveView.startPlay(path);
    }

    @Override
    protected void onPause() {
        super.onPause();
        jjbLiveView.stopPlay();
    }

    private void initAdapter(){
        list = new ArrayList<Object>();
        commonAdapter = new CommonAdapter<LeyuanLiveCommentEntity>(this,list,R.layout.leyuan_live_comment) {
            @Override
            public void convert(ViewHolder holder, LeyuanLiveCommentEntity leyuanLiveCommentEntity) {
                String face = leyuanLiveCommentEntity.getFace();
                String nickname = leyuanLiveCommentEntity.getNickname();
                String time = leyuanLiveCommentEntity.getTime();
                String content = leyuanLiveCommentEntity.getContent();
                ImageView faceView = holder.getView(R.id.face);
                TextView nickView = holder.getView(R.id.nickname);
                TextView timeView = holder.getView(R.id.time);
                TextView contentView = holder.getView(R.id.content);

                if(!TextUtils.isEmpty(face) && face.trim().length() > 5 )
                    ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(face), faceView);
                else{
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_wojia_nan70dp02);
                    faceView.setImageBitmap(bitmap);
                }
                nickView.setText(nickname);
                timeView.setText(time);
                contentView.setText(content);
            }
        };
    }

    private void loadData(){
        if(pageCount > 0 && currentPage > pageCount){
            UIHelper.ToastMessage(this, "已经是最后记录");
            stopLoading(xListView);
            return;
        }
        ApiClient.get(this, ApiConfig.LEYUAN_LIVE_COMMENT, new ApiResponseHandler<LeyuanLiveCommentEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                stopLoading(xListView);
                List<Object> da = entity.data;
                if (null != da) {
                    xListView.setVisibility(View.VISIBLE);
                    pageCount = entity.pageCount;
                    currentPage++;
                    list.addAll(da);
                    commonAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                stopLoading(xListView);
                UIHelper.ToastMessage(LiveActivity.this, errorInfo.getMessage());
            }
        }, "page", currentPage + "", "id", id);
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        list.clear();
        loadData();
    }

    @Override
    public void onLoadMore() {
        loadData();
    }


    private PopupWindow mPopupWindow;
    private void showPopupWindow() {

        View view = inflater.inflate(R.layout.leyuan_add_comment, null);
        if(null == mPopupWindow)
        mPopupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                Utils.dip2px(this, 62), true);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
        mPopupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, -30); // 添加popwindow显示的位置

         final EditText editText = (EditText)view.findViewById(R.id.message_content);
        Button sendButton = (Button)view.findViewById(R.id.message_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if(!TextUtils.isEmpty(content)){
                    mPopupWindow.dismiss();
                    sendComment(content);
                }else{
                    UIHelper.ToastMessage(LiveActivity.this,"说点什么吧");
                }
            }
        });
    }

    private void sendComment(String content){
        showProgress();
        ApiClient.post(this, ApiConfig.LEYUAN_ADD_LIVE_COMMENT, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                onRefresh();
            }
            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(LiveActivity.this, errorInfo.getMessage());
            }
        }, "userId", userId, "content", content);
    }
}
