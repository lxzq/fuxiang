package com.qcsh.fuxiang.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.bean.Home.FamilyUserEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.bean.look.LookChildEntity;
import com.qcsh.fuxiang.common.ImageUtils;
import com.qcsh.fuxiang.common.OneKeyShare;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.ui.look.LookMapFragment;
import com.qcsh.fuxiang.widget.MyConfirmDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 家人管理
 * Created by wo on 15/9/22.
 */
public class HomeFamilyManagerActivity extends BaseActivity implements OnClickListener {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private TextView yeyeText;
    private TextView nainaiText;
    private TextView waipuoText;
    private TextView waigonText;
    private TextView babaText;
    private TextView mamaText;
    //private TextView babyText;
    private TextView addText;
    private List mlist = new ArrayList();
    private int swidth55;
    private int swidth70;
    private String childName;


    private LinearLayout childList;
    private ImageView faceHost;
    private TextView textHost;
    private String child = "3";
    private String userId;
    public static final String CHILD_INFO = "com.qcsh.fuxiang.ui.home.HomeFamilyManagerActivity.childInfo";
    private ChildInfocastReceiver childInfocastReceiver;
    LayoutInflater inflater;
    private OneKeyShare oks;
    private List userInfoList = new ArrayList();
    private CommonAdapter<User> userAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_familymanger);
        User user = getCurrentUser();
        inflater = LayoutInflater.from(this);
        if (null != user) {
            userId = user.id;
            child = user.childId;
        }
        oks = new OneKeyShare(HomeFamilyManagerActivity.this);
        childInfocastReceiver = new ChildInfocastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CHILD_INFO);
        registerReceiver(childInfocastReceiver, intentFilter);

        initToolBar();
        initView();
        initData();
        lookChildData();
    }

    private void initToolBar() {
        leftBtn = (ImageButton) findViewById(R.id.action_bar_back);
        title = (TextView) findViewById(R.id.action_bar_title);
        rightBtn = (Button) findViewById(R.id.action_bar_action);
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightBtn.setVisibility(View.INVISIBLE);
        title.setText("家人管理");
    }

    private void initView() {

        //Dip -> Int
        swidth55 = Utils.dip2int(HomeFamilyManagerActivity.this, 55);
        swidth70 = Utils.dip2int(HomeFamilyManagerActivity.this, 70);

        //55dip Image
        Drawable nanImg = getResources().getDrawable(R.mipmap.btn_wojia_nan60dp);
        nanImg.setBounds(0, 0, swidth55, swidth55);

        Drawable nvImg = getResources().getDrawable(R.mipmap.btn_wojia_nv60dp);
        nvImg.setBounds(0, 0, swidth55, swidth55);

        //70dip Image
        Drawable nanImg2 = getResources().getDrawable(R.mipmap.btn_wojia_nan70dp);
        nanImg2.setBounds(0, 0, swidth70, swidth70);

        Drawable nvImg2 = getResources().getDrawable(R.mipmap.btn_wojia_nv70dp);
        nvImg2.setBounds(0, 0, swidth70, swidth70);

        Drawable nvImg3 = getResources().getDrawable(R.mipmap.btn_wojia_nv70dp02);
        nvImg3.setBounds(0, 0, swidth70, swidth70);

        Drawable addImg = getResources().getDrawable(R.mipmap.btn_wojia_jiahao);
        addImg.setBounds(0, 0, swidth70, swidth70);

        yeyeText = (TextView) findViewById(R.id.iv_yeye);
        yeyeText.setCompoundDrawables(null, nanImg, null, null);
        yeyeText.setOnClickListener(this);

        nainaiText = (TextView) findViewById(R.id.iv_nainai);
        nainaiText.setCompoundDrawables(null, nvImg, null, null);
        nainaiText.setOnClickListener(this);

        waigonText = (TextView) findViewById(R.id.iv_waigon);
        waigonText.setCompoundDrawables(null, nanImg, null, null);
        waigonText.setOnClickListener(this);

        waipuoText = (TextView) findViewById(R.id.iv_waipuo);
        waipuoText.setCompoundDrawables(null, nvImg, null, null);
        waipuoText.setOnClickListener(this);

        babaText = (TextView) findViewById(R.id.iv_baba);
        babaText.setCompoundDrawables(null, nanImg2, null, null);
        babaText.setOnClickListener(this);

        mamaText = (TextView) findViewById(R.id.iv_mama);
        mamaText.setCompoundDrawables(null, nvImg2, null, null);
        mamaText.setOnClickListener(this);

        addText = (TextView) findViewById(R.id.iv_addbaby);
        addText.setCompoundDrawables(null, addImg, null, null);
        addText.setOnClickListener(this);

        faceHost = (ImageView) findViewById(R.id.face_host);
        textHost = (TextView) findViewById(R.id.text_host);
        childList = (LinearLayout) findViewById(R.id.child_list);

        childInfo();

    }

    private void childInfo() {
        String childInfo = getCurrentUser().getChild_info();
        childList.removeAllViews();
        addText.setVisibility(View.VISIBLE);
        try {
            JSONArray array = new JSONArray(childInfo);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                View view = inflater.inflate(R.layout.home_familymanger_child, null);
                TextView babyText = (TextView) view.findViewById(R.id.iv_wo);
                String childId = obj.getString("child_id");
                if (this.child.equals(childId)) {
                    String childFace = obj.getString("face");
                    childName = obj.getString("nick_name");
                    Drawable face = null;
                    babyText.setText(childName);
                    if (!StringUtils.isEmpty(childFace)) {
                        face = ImageUtils.bitmapToDrawable(ImageLoader.getInstance().loadImageSync(AppConfig.getUserPhoto_x(childFace)));
                    } else {
                        face = getResources().getDrawable(R.mipmap.btn_wojia_nan70dp02);
                    }
                    face.setBounds(0, 0, swidth70, swidth70);
                    babyText.setCompoundDrawables(null, face, null, null);
                    childList.addView(view);
                }
            }
            if (array.length() >= 3)
                addText.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.iv_yeye:
                if (hassFamily(3)) {

                } else {
                    showPopupWindow(3);
                }
                break;
            case R.id.iv_nainai:
                if (hassFamily(4)) {
                } else {
                    showPopupWindow(4);
                }
                break;

            case R.id.iv_waigon:
                if (hassFamily(5)) {
                } else {
                    showPopupWindow(5);
                }
                break;

            case R.id.iv_waipuo:
                if (hassFamily(6)) {
                } else {
                    showPopupWindow(6);
                }
                break;

            case R.id.iv_baba:
                if (hassFamily(1)) {
                    //deleteRelation();
                } else {
                    showPopupWindow(1);
                }
                break;

            case R.id.iv_mama:
                if (hassFamily(2)) {
                } else {
                    showPopupWindow(2);
                }
                break;

            case R.id.iv_wo:
                break;

            case R.id.iv_addbaby:
                AppIntentManager.startRegisterBabyActivity(this, 1);
                break;
        }

    }

    private void initData() {
        showProgress();
        ApiClient.post(HomeFamilyManagerActivity.this, ApiConfig.FAMILY_MAMAGEER, new ApiResponseHandler<FamilyUserEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                mlist.addAll((List)entity.data);
                setView();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(HomeFamilyManagerActivity.this, errorInfo.getMessage());
            }
        }, "childId", child);
    }

    private void lookChildData() {
        showProgress();
        ApiClient.post(this, ApiConfig.LOOK_CHILD, new ApiResponseHandler<LookChildEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                List<Object> datas = entity.data;
                setHostManager(datas);
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(HomeFamilyManagerActivity.this, errorInfo.getMessage());
            }
        }, "chiId", child);
    }

    private void setHostManager(List<Object> datas) {
        for (int i = 0; i < datas.size(); i++) {
            final LookChildEntity lookChildEntity = (LookChildEntity) datas.get(i);
            String relationName = lookChildEntity.getRelation();
            String isNurse = lookChildEntity.getIsNurse();
            if ("1".equals(relationName)) {
                lookChildEntity.setNickname("（爸爸）");
            } else if ("2".equals(relationName)) {
                lookChildEntity.setNickname("（妈妈）");
            } else if ("3".equals(relationName)) {
                lookChildEntity.setNickname("（爷爷）");
            } else if ("4".equals(relationName)) {
                lookChildEntity.setNickname("（奶奶）");
            } else if ("5".equals(relationName)) {
                lookChildEntity.setNickname("（外公）");
            } else if ("6".equals(relationName)) {
                lookChildEntity.setNickname("（外婆）");
            }
            if ("0".equals(isNurse)) {//看护人
                textHost.setText(lookChildEntity.getNickname());
                if (!TextUtils.isEmpty(lookChildEntity.getFace()) && lookChildEntity.getFace().trim().length() > 5) {
                    ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_xx(lookChildEntity.getFace()), faceHost);
                }
                break;
            }
        }
    }

    private void changeChild(String id) {
        showProgress();
        ApiClient.post(this, ApiConfig.CHANGE_CHILD, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                UIHelper.ToastMessage(HomeFamilyManagerActivity.this, "切换看护人完成");
                lookChildData();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(HomeFamilyManagerActivity.this, errorInfo.getMessage());

            }
        }, "userId", id, "chiId", child);
    }


    private void setView() {
        for (int i = 0; i < mlist.size(); i++) {
            FamilyUserEntity entity = (FamilyUserEntity) mlist.get(i);
            final String id = entity.getId();
            String face = entity.getUserface();
            String nickname = entity.getNickname();
            String relation = entity.getRelation();
            Drawable faceDrawable = null;
            if (!StringUtils.isEmpty(relation)) {
                switch (Integer.valueOf(relation)) {
                    case 1:
                        if (!StringUtils.isEmpty(face)) {
                            faceDrawable = ImageUtils.bitmapToDrawable(ImageLoader.getInstance().loadImageSync(AppConfig.getUserPhoto_x(face)));
                        } else {
                            faceDrawable = getResources().getDrawable(R.mipmap.btn_wojia_nan70dp02);
                        }
                        faceDrawable.setBounds(0, 0, swidth70, swidth70);
                        babaText.setCompoundDrawables(null, faceDrawable, null, null);
                        babaText.setText(nickname + "（爸爸）");
                        babaText.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeChild(id);
                            }
                        });
                        break;
                    case 2:
                        if (!StringUtils.isEmpty(face)) {
                            faceDrawable = ImageUtils.bitmapToDrawable(ImageLoader.getInstance().loadImageSync(AppConfig.getUserPhoto_x(face)));
                        } else {
                            faceDrawable = getResources().getDrawable(R.mipmap.btn_wojia_nv70dp02);
                        }
                        faceDrawable.setBounds(0, 0, swidth70, swidth70);
                        // faceDrawable.setBounds(0,0,faceDrawable.getMinimumWidth(),faceDrawable.getMinimumHeight());
                        mamaText.setCompoundDrawables(null, faceDrawable, null, null);
                        mamaText.setText(nickname + "（妈妈）");
                        mamaText.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeChild(id);
                            }
                        });
                        break;
                    case 3:
                        if (!StringUtils.isEmpty(face)) {
                            faceDrawable = ImageUtils.bitmapToDrawable(ImageLoader.getInstance().loadImageSync(AppConfig.getUserPhoto_x(face)));
                        } else {
                            faceDrawable = getResources().getDrawable(R.mipmap.btn_wojia_nan60dp02);
                        }
                        faceDrawable.setBounds(0, 0, swidth55, swidth55);
                        yeyeText.setCompoundDrawables(null, faceDrawable, null, null);
                        yeyeText.setText(nickname + "（爷爷）");
                        yeyeText.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeChild(id);
                            }
                        });
                        break;
                    case 4:
                        if (!StringUtils.isEmpty(face)) {
                            faceDrawable = ImageUtils.bitmapToDrawable(ImageLoader.getInstance().loadImageSync(AppConfig.getUserPhoto_x(face)));
                        } else {
                            faceDrawable = getResources().getDrawable(R.mipmap.btn_wojia_nv60dp02);
                        }
                        faceDrawable.setBounds(0, 0, swidth55, swidth55);
                        nainaiText.setCompoundDrawables(null, faceDrawable, null, null);
                        nainaiText.setText(nickname + "（奶奶）");
                        nainaiText.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeChild(id);
                            }
                        });
                        break;
                    case 5:
                        if (!StringUtils.isEmpty(face)) {
                            faceDrawable = ImageUtils.bitmapToDrawable(ImageLoader.getInstance().loadImageSync(AppConfig.getUserPhoto_x(face)));
                        } else {
                            faceDrawable = getResources().getDrawable(R.mipmap.btn_wojia_nan60dp02);
                        }
                        faceDrawable.setBounds(0, 0, swidth55, swidth55);
                        waigonText.setCompoundDrawables(null, faceDrawable, null, null);
                        waigonText.setText(nickname + "（外公）");
                        waigonText.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeChild(id);
                            }
                        });
                        break;
                    case 6:
                        if (!StringUtils.isEmpty(face)) {
                            faceDrawable = ImageUtils.bitmapToDrawable(ImageLoader.getInstance().loadImageSync(AppConfig.getUserPhoto_x(face)));
                        } else {
                            faceDrawable = getResources().getDrawable(R.mipmap.btn_wojia_nv60dp02);
                        }
                        faceDrawable.setBounds(0, 0, swidth55, swidth55);
                        waipuoText.setCompoundDrawables(null, faceDrawable, null, null);
                        waipuoText.setText(nickname + "（外婆）");
                        waipuoText.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeChild(id);
                            }
                        });
                        break;
                }
            }
        }
    }

    private boolean hassFamily(int r) {

        for (int i = 0; i < mlist.size(); i++) {
            FamilyUserEntity entity = (FamilyUserEntity) mlist.get(i);
            String relation = entity.getRelation();
            if (!StringUtils.isEmpty(relation) && r == Integer.valueOf(relation)) {
                return true;
            }
        }
        return false;
    }

    private void deleteRelation() {
        MyConfirmDialog dialog = new MyConfirmDialog(this, "删除家人", "我们将删除家人关系",
                new MyConfirmDialog.OnCancelDialogListener() {
                    @Override
                    public void onCancel() {
                    }
                },
                new MyConfirmDialog.OnConfirmDialogListener() {
                    @Override
                    public void onConfirm() {

                    }
                }
        );
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(childInfocastReceiver);
    }

    private class ChildInfocastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            childInfo();
        }
    }

    private void showPopupWindow(final int relation) {
        View view = inflater.inflate(R.layout.layout_invitefamily, null);
        final PopupWindow mPopupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,

                Utils.dip2px(HomeFamilyManagerActivity.this, 200), true);

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
        mPopupWindow.showAtLocation(HomeFamilyManagerActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, -30); // 添加popwindow显示的位置

        final LinearLayout inviteLayout = (LinearLayout) view.findViewById(R.id.invite_layout);
        TextView findNumber = (TextView) view.findViewById(R.id.find_number);
        TextView face2face = (TextView) view.findViewById(R.id.face2face);
        TextView newUser = (TextView) view.findViewById(R.id.new_user);

        final LinearLayout findLayout = (LinearLayout) view.findViewById(R.id.find_layout);
        final EditText searchEdit = (EditText) view.findViewById(R.id.search_edit);
        Button searchBtn = (Button) view.findViewById(R.id.search_btn);
        Button backBtn = (Button) view.findViewById(R.id.back_btn);
        ListView userList = (ListView) view.findViewById(R.id.user_list);

        inviteLayout.setVisibility(View.VISIBLE);
        findLayout.setVisibility(View.GONE);

        findNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteLayout.setVisibility(View.GONE);
                findLayout.setVisibility(View.VISIBLE);
            }
        });

        face2face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startCaptureActivity(HomeFamilyManagerActivity.this, relation + "");
                mPopupWindow.dismiss();
            }
        });
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetUrl = AppConfig.BASE_URL + "invitation/invitation?childId=" + child + "&userId=" + getCurrentUser().getId() + "&relation=";
                String rel = "";
                switch (relation) {
                    case 1:
                        rel = "爸爸";
                        targetUrl += 1;
                        break;
                    case 2:
                        rel = "妈妈";
                        targetUrl += 2;
                        break;
                    case 3:
                        rel = "爷爷";
                        targetUrl += 3;
                        break;
                    case 4:
                        rel = "奶奶";
                        targetUrl += 4;
                        break;
                    case 5:
                        rel = "外公";
                        targetUrl += 5;
                        break;
                    case 6:
                        rel = "外婆";
                        targetUrl += 6;
                        break;
                }
                oks.setShareContent("邀请家人", getCurrentUser().getNickname() + "邀请您作为" + childName + "的" + rel, targetUrl, null, null, null);
                oks.addSharePlatforms();
                mPopupWindow.dismiss();
            }
        });

        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteLayout.setVisibility(View.VISIBLE);
                findLayout.setVisibility(View.GONE);
            }
        });
        userAdapter = new CommonAdapter<User>(HomeFamilyManagerActivity.this, userInfoList, R.layout.layout_user_item) {

            @Override
            public void convert(ViewHolder holder, User user) {
                String face = user.getUserface();
                String nickname = user.getNickname();
                final String nurseId = user.getId();

                holder.setText(R.id.ic_name, nickname);

                ImageView faceView = holder.getView(R.id.iv_face);

                Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_wojia_nan60dp02);
                faceView.setImageBitmap(defaultBitmap);
                if (!TextUtils.isEmpty(face) && face.trim().length() > 5)
                    ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(face), faceView);

                holder.setOnClickListener(R.id.btn_invite, new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ApiClient.post(HomeFamilyManagerActivity.this, ApiConfig.ADD_FAMILY, new ApiResponseHandler<BaseEntity>() {
                                    @Override
                                    public void onSuccess(DataEntity entity) {
                                        mPopupWindow.dismiss();
                                    }

                                    @Override
                                    public void onFailure(ErrorEntity errorInfo) {
                                        UIHelper.ToastMessage(HomeFamilyManagerActivity.this, errorInfo.getMessage());
                                        mPopupWindow.dismiss();
                                    }
                                }, "userId", getCurrentUser().getId(), "childId", getCurrentUser().getChildId(),
                                "nuserId", nurseId, "childName", childName, "userName", getCurrentUser().getNickname(),
                                "relation", relation + "", "face", getCurrentUser().getUserface());
                    }
                });
            }
        };
        userList.setAdapter(userAdapter);
        searchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = searchEdit.getText().toString().trim();
                if (StringUtils.isEmpty(str)) {
                    UIHelper.ToastMessage(HomeFamilyManagerActivity.this, "请输入昵称或手机号");
                } else {
                    searchUser(str);
                }

            }
        });
    }

    private void searchUser(String string) {
        ApiClient.post(HomeFamilyManagerActivity.this, ApiConfig.SERCH_USER, new ApiResponseHandler<User>() {
            @Override
            public void onSuccess(DataEntity entity) {
                userInfoList.clear();
                if (null != entity.data) {
                    userInfoList.addAll(entity.data);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                UIHelper.ToastMessage(HomeFamilyManagerActivity.this, errorInfo.getMessage());
            }
        }, "userName", string);
    }
}
