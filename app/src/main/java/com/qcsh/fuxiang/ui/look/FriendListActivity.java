package com.qcsh.fuxiang.ui.look;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.qcsh.fuxiang.bean.look.ChildEntity;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.XListView;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends BaseActivity implements XListView.IXListViewListener {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;
    private ArrayList<Object> mlist = new ArrayList<Object>();
    private XListView mListView;
    private CommonAdapter<ChildEntity> adapter;
    //    private float zoom;
//    private LatLng leftTopLatLng;//左上角经纬度
//    private LatLng rightBottomLatLng;//右下角经纬度
    private Drawable boyImg;
    private Drawable girlImg;
    private String childIds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        leftTopLatLng = getIntent().getParcelableExtra("leftTopLatLng");
//        rightBottomLatLng = getIntent().getParcelableExtra("rightBottomLatLng");
//        zoom = getIntent().getFloatExtra("zoom", 13f);
        childIds = getIntent().getStringExtra("childIds");
        setContentView(R.layout.home_partner);
        initToolBar();
        lookFriend();
        initView();
    }

    private void initToolBar() {
        leftBtn = (ImageButton) findViewById(R.id.action_bar_back);
        title = (TextView) findViewById(R.id.action_bar_title);
        rightBtn = (Button) findViewById(R.id.action_bar_action);
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightBtn.setVisibility(View.INVISIBLE);
        title.setText("伙伴列表");
    }

    private void initView() {
        mListView = (XListView) findViewById(R.id.listView);
        mListView.setXListViewListener(this);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        adapter = new CommonAdapter<ChildEntity>(FriendListActivity.this, mlist, R.layout.home_partnermanager_list) {
            @Override
            public void convert(ViewHolder holder, final ChildEntity lookListEntity) {
                String face = lookListEntity.getFace();
                if (!StringUtils.isEmpty(face)) {
                    ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(face), (ImageView) holder.getView(R.id.iv_face));
                }

                TextView sexAge = holder.getView(R.id.ic_sex);
                holder.setText(R.id.ic_name, lookListEntity.getNick_name());
                String sex = lookListEntity.getSex();
                boyImg = getResources().getDrawable(R.mipmap.ic_wojia_nan2);
                boyImg.setBounds(0, 0, boyImg.getIntrinsicWidth(), boyImg.getIntrinsicHeight());

                girlImg = getResources().getDrawable(R.mipmap.ic_wojia_nv2);
                girlImg.setBounds(0, 0, girlImg.getIntrinsicWidth(), girlImg.getIntrinsicHeight());
                if ("0".equals(sex)) {
                    sexAge.setCompoundDrawables(girlImg, null, null, null);
                } else if ("1".equals(sex)) {
                    sexAge.setCompoundDrawables(boyImg, null, null, null);
                } else {
                    sexAge.setCompoundDrawables(null, null, null, null);
                }

                String birthday = lookListEntity.getBirthday();
                if (!StringUtils.isEmpty(birthday)) {
                    String age = StringUtils.getAgeWithYearDay(birthday);
                    sexAge.setText(age);
                } else {
                    sexAge.setText("");
                }
                holder.setVisible(R.id.btn_delete, false);
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppIntentManager.startHomeBabyDetailActivity(FriendListActivity.this, lookListEntity.getId());
                    }
                });
            }
        };
        mListView.setAdapter(adapter);
        mListView.setDivider(getResources().getDrawable(R.color.dividingline));
        mListView.setDividerHeight(1);
    }

    /**
     * 找伙伴数据
     */
    private void lookFriend() {
        showProgress();
        String url = ApiConfig.GET_CHILDREN_INFO;
        ApiClient.post(this, url, new ApiResponseHandler<ChildEntity>() {

            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                stopLoading(mListView);
                List<Object> list = entity.data;
                if (list != null && list.size() > 0)
                    mlist.addAll(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                stopLoading(mListView);
                UIHelper.ToastMessage(FriendListActivity.this, errorInfo.getMessage());
            }
        }, "childId", childIds);
    }

    @Override
    public void onRefresh() {
        mlist.clear();
        lookFriend();
    }

    @Override
    public void onLoadMore() {
        stopLoading(mListView);
    }
}
