package com.qcsh.fuxiang.ui.bang;

import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.bean.bang.BangInfo;
import com.qcsh.fuxiang.bean.look.CommentEntity;
import com.qcsh.fuxiang.bean.look.ZanEntity;
import com.qcsh.fuxiang.common.OneKeyShare;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 帮详情
 * Created by Administrator on 2015/9/7.
 */
public class BangDetailActivity extends BaseActivity implements XListView.IXListViewListener {


    private ImageButton backButton, shareButton, conButton;
    private TextView toolTitle;
    private Button toolRight;

    private TextView titleView, contentView, dateView, nicknameView;

    private ImageView faceView, imageView1, imageView2, imageView3;
    private TextView hitNumView, bbNumView, commentNumView, commentNumTextView;

    private XListView listView;

    private BangInfo bangDetailInfo;
    private User user;
    private OneKeyShare oneKeyShare;

    private int nextCursor = 1;
    private ArrayList<Object> dadaList;
    private CommonAdapter<CommentEntity> adapter;
    private View root;
    private String userId;
    private int pageCount = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bang_detail_list);
        root = LayoutInflater.from(this).inflate(R.layout.bang_detail, null);
        dadaList = new ArrayList<Object>();
        bangDetailInfo = (BangInfo) getIntent().getSerializableExtra("bangDetailInfo");
        user = getCurrentUser();
        if (null != user) userId = user.id;
        oneKeyShare = new OneKeyShare(this);
        initView();
        loadData();
    }

    private void initView() {

        backButton = (ImageButton) findViewById(R.id.action_bar_back);
        shareButton = (ImageButton) findViewById(R.id.action_bar_image_action);
        conButton = (ImageButton) findViewById(R.id.action_bar_image_action1);

        toolTitle = (TextView) findViewById(R.id.action_bar_title);
        toolRight = (Button) findViewById(R.id.action_bar_action);
        toolRight.setText("");
        toolRight.setVisibility(View.GONE);
        toolTitle.setText("帮帮详情");
        backButton.setVisibility(View.VISIBLE);

        shareButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.btn_fenxiang2));
        shareButton.setVisibility(View.VISIBLE);

        conButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.btn_bb_shoucang));
        conButton.setVisibility(View.VISIBLE);


        titleView = (TextView) root.findViewById(R.id.title);
        faceView = (ImageView) root.findViewById(R.id.face);
        nicknameView = (TextView) root.findViewById(R.id.nickname);
        contentView = (TextView) root.findViewById(R.id.content);
        dateView = (TextView) root.findViewById(R.id.date);

        imageView1 = (ImageView) root.findViewById(R.id.image_1);
        imageView2 = (ImageView) root.findViewById(R.id.image_2);
        imageView3 = (ImageView) root.findViewById(R.id.image_3);

        hitNumView = (TextView) root.findViewById(R.id.hit_num);
        bbNumView = (TextView) root.findViewById(R.id.bb_num);
        commentNumView = (TextView) root.findViewById(R.id.comment_num);
        commentNumTextView = (TextView) root.findViewById(R.id.comment_num_text);

        adapter = new CommonAdapter<CommentEntity>(BangDetailActivity.this, dadaList, R.layout.layout_commentitem) {
            @Override
            public void convert(ViewHolder holder, CommentEntity comment) {

                holder.setText(R.id.comment_user, comment.getUsername() + "：");
                TextView tv = holder.getView(R.id.comment_content);
                StringUtils.setTextEmoji(BangDetailActivity.this, tv, comment.getComment_content());
                holder.setText(R.id.comment_date, comment.getComment_time());

            }
        };

        listView = (XListView) findViewById(R.id.listView);

        listView.addHeaderView(root);
        listView.setAdapter(adapter);
        listView.setPullLoadEnable(true);
        listView.setPullRefreshEnable(true);
        listView.setXListViewListener(this);

        titleView.setText(bangDetailInfo.getTitle());
        contentView.setText(bangDetailInfo.getContentInfo());
        dateView.setText(bangDetailInfo.getCreatTime());

        String praise = bangDetailInfo.getPraise();
        if (!StringUtils.isEmpty(praise)) {
            try {
                JSONArray collArray = new JSONArray(praise);
                if (collArray != null && collArray.length() > 0) {
                    hitNumView.setText(collArray.length() + "");
                } else {
                    hitNumView.setText("0");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        bbNumView.setText(bangDetailInfo.bbNum);
        commentNumView.setText(bangDetailInfo.getCommentCount());
        commentNumTextView.setText("全部评论" + bangDetailInfo.getCommentCount() + "条");

        String user = bangDetailInfo.getUser();
        if (!StringUtils.isEmpty(user)) {
            try {
                JSONObject userObj = new JSONObject(user);
                String nickname = userObj.getString("nickname");
                String face = userObj.getString("userface");

                nicknameView.setText(nickname);
                if (!StringUtils.isEmpty(face)) {
                    ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(face), faceView);
                } else {
                    faceView.setImageResource(R.mipmap.defalut_user_face);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String images = bangDetailInfo.getImages();
        if (!StringUtils.isEmpty(images)) {
            try {
                JSONArray imgArray = new JSONArray(images);
                if (imgArray.length() > 0) {
                    for (int i = 0; i < imgArray.length(); i++) {
                        String imgUrl = imgArray.getJSONObject(i).getString("imageName");
                        switch (i) {
                            case 0:
                                ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(imgUrl), imageView1);
                                imageView1.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(imgUrl), imageView2);
                                imageView2.setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(imgUrl), imageView3);
                                imageView3.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetUrl = "";
                oneKeyShare.setShareContent(bangDetailInfo.getTitle(), bangDetailInfo.getContentInfo(), targetUrl, null, null, null);
                oneKeyShare.addSharePlatforms();
            }
        });

        conButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collBang();
            }
        });

        hitNumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zan();
            }
        });

        bbNumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bangMoneyWindow();
            }
        });

        commentNumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commWindow();
            }
        });
    }

    @Override
    public void onRefresh() {
        nextCursor = 1;
        dadaList.clear();
        loadData();
    }

    @Override
    public void onLoadMore() {
        loadData();
    }

    private void loadData() {
        if (nextCursor > pageCount) {
            stopLoading(listView);
            return;
        }

        showProgress();
        ApiClient.post(this, ApiConfig.BANG_COMMENT, new ApiResponseHandler<CommentEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                stopLoading(listView);
                List<Object> data = entity.data;
                if (null != data) {
                    dadaList.addAll(data);
                    pageCount = entity.pageCount;
                    nextCursor++;
                }
                commentNumView.setText(dadaList.size()+"");
                commentNumTextView.setText("全部评论" + dadaList.size() + "条");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                stopLoading(listView);
                UIHelper.ToastMessage(BangDetailActivity.this, errorInfo.getMessage());
            }
        }, "page", nextCursor + "", "contentId", bangDetailInfo.getId());

    }

    /**
     * 收藏
     */
    private void collBang() {
        showProgress();
        ApiClient.post(BangDetailActivity.this, ApiConfig.GROWTHTREE_SHOUCANG, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(BangDetailActivity.this, errorInfo.getMessage());
            }
        }, "objectId", bangDetailInfo.getId(), "type", "3", "userId", getCurrentUser().getId());

    }

    /**
     * 点赞
     */
    private void zan() {
        showProgress();
        ApiClient.post(BangDetailActivity.this, ApiConfig.GROWTHTREE_ZAN, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(BangDetailActivity.this, errorInfo.getMessage());
                loadZanList(bangDetailInfo.getId());
            }
        }, "userId", getCurrentUser().getId(), "relationId", bangDetailInfo.getId(), "type", "3");
    }

    private void bangMoneyWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.bang_money, null);
        final PopupWindow mPopupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 加上下面两行可以用back键关闭popupwindow，否则必须调用dismiss();
        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.transparent));
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.update();
        mPopupWindow.setAnimationStyle(R.style.PopupAnimationToast);
        mPopupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, -30); // 添加popwindow显示的位置

        final EditText editText = (EditText) view.findViewById(R.id.input_message);
        Button button = (Button) view.findViewById(R.id.submit);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = editText.getText().toString();
                if (!TextUtils.isEmpty(money)) {
                    int m = Integer.parseInt(money);
                    payMoney(m);
                    mPopupWindow.dismiss();
                } else {
                    UIHelper.ToastMessage(BangDetailActivity.this, "请输入金额");
                }
            }
        });
    }

    /**
     * 打赏
     *
     * @param m
     */
    private void payMoney(final int m) {
        showProgress();
        ApiClient.post(this, ApiConfig.BANG_PAY_MONEY, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
               /* int bbNum = Integer.parseInt(bangDetailInfo.bbNum);
                bbNum += m;
                bangDetailInfo.bbNum = bbNum + "";
                hitNumView.setText(bangDetailInfo.bbNum);
                UIHelper.ToastMessage(BangDetailActivity.this, "打赏成功");*/
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(BangDetailActivity.this, errorInfo.getMessage());
            }
        }, "userId", userId, "id", bangDetailInfo.getId(), "money", m + "");

    }

    private void commWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.look_notes, null);
        final PopupWindow mPopupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 加上下面两行可以用back键关闭popupwindow，否则必须调用dismiss();
        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.transparent));
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.update();
        mPopupWindow.setAnimationStyle(R.style.PopupAnimationToast);
        mPopupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, -30); // 添加popwindow显示的位置

        final LinearLayout menu_panel = (LinearLayout) view.findViewById(R.id.menu_panel);
        final LinearLayout message_panel = (LinearLayout) view.findViewById(R.id.message_panel);
        menu_panel.setVisibility(View.GONE);
        message_panel.setVisibility(View.VISIBLE);

        final EditText contentText = (EditText) view.findViewById(R.id.input_message);
        Button submitB = (Button) view.findViewById(R.id.submit);
        contentText.setHint("请输入评论");
        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentText.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    sendMessage(content);
                    mPopupWindow.dismiss();
                } else {
                    UIHelper.ToastMessage(BangDetailActivity.this, "请输入评论");
                }
            }
        });

    }

    /**
     * 发布评论
     *
     * @param content
     */
    private void sendMessage(String content) {
        ApiClient.post(this, ApiConfig.SEND_BANG_COMMENT, new ApiResponseHandler<CommentEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                onRefresh();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(BangDetailActivity.this, errorInfo.getMessage());
            }
        }, "userId", userId, "contentId", bangDetailInfo.getId(), "content", content);
    }


    private void loadZanList(String relationId) {
        showProgress();
        ApiClient.post(BangDetailActivity.this, ApiConfig.GET_ZAN_LIST, new ApiResponseHandler<ZanEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                List zanList = entity.data;
                if (zanList != null && zanList.size() > 0) {
                    hitNumView.setText(zanList.size() + "");
                } else {
                    hitNumView.setText("0");
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
            }
        }, "relationId", relationId, "type", "3");
    }
}
