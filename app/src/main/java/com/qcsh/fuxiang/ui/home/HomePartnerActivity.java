package com.qcsh.fuxiang.ui.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.qcsh.fuxiang.bean.Home.HomePartnerEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.XListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.qcsh.fuxiang.R.id;
import static com.qcsh.fuxiang.R.layout;

/**
 * 伙伴管理
 * Created by wo on 15/9/22.
 */
public class HomePartnerActivity extends BaseActivity implements XListView.IXListViewListener {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private XListView mListView;
    private CommonAdapter<HomePartnerEntity> adapter;
    private List<Object> mlist = new ArrayList<Object>();
    private int nextCursor = 1;


    private View searchView;
    private EditText mEtSearch = null; //输入搜索内容
    private Button mBtnClearSearchText = null; //清空搜索信息的按钮
    private LinearLayout mLayoutClearSearchText = null;
    private int totalPage;
    private User user;
    private Drawable boyImg;
    private Drawable girlImg;
    private String nickName = "";
    private String Id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_partner);
        user = getCurrentUser();
        initToolBar();
        initView();
        initData();
    }

    private void initToolBar() {
        leftBtn = (ImageButton) findViewById(id.action_bar_back);
        title = (TextView) findViewById(id.action_bar_title);
        rightBtn = (Button) findViewById(id.action_bar_action);

        ImageButton imageButton = (ImageButton) findViewById(id.action_bar_image_action);
        imageButton.setVisibility(View.GONE);
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightBtn.setVisibility(View.GONE);
        title.setText("伙伴管理");
    }

    private void initData() {

        showProgress();
        ApiClient.get(HomePartnerActivity.this, ApiConfig.PARTNER_MANAGER, new ApiResponseHandler<HomePartnerEntity>() {

            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                stopLoading(mListView);
                List<Object> data = entity.data;
                if (data != null && data.size() > 0) {
                    mlist.addAll(data);
                    nextCursor = entity.currentPage + 1;
                    totalPage = entity.pageCount;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                stopLoading(mListView);
                UIHelper.ToastMessage(HomePartnerActivity.this, errorInfo.getMessage());
            }

        }, "childId", getCurrentUser().getChildId(), "nickName", nickName, "page", nextCursor + "");
    }

    private void initView() {

        searchView = LayoutInflater.from(this).inflate(layout.search_layout, null);
        mEtSearch = (EditText) searchView.findViewById(id.et_search);

        float swidthf = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 17, getResources().getDisplayMetrics());
        int swidth = Math.round(swidthf);
        final Drawable searchImg = getResources().getDrawable(R.mipmap.ic_wojia_fangdajing);
        searchImg.setBounds(0, 0, swidth, swidth);
        mEtSearch.setCompoundDrawables(searchImg, null, null, null);

        mBtnClearSearchText = (Button) searchView.findViewById(R.id.btn_clear_search_text);
        mLayoutClearSearchText = (LinearLayout) searchView.findViewById(id.layout_clear_search_text);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                int textLength = mEtSearch.getText().length();
                mEtSearch.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                if (textLength > 0) {

                    mLayoutClearSearchText.setVisibility(View.VISIBLE);

                } else {

                    nickName = mEtSearch.getText().toString();
                    onRefresh();
                    mLayoutClearSearchText.setVisibility(View.GONE);
                }
            }
        });

        mBtnClearSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEtSearch.setText("");
                mEtSearch.setGravity(Gravity.CENTER);
                mLayoutClearSearchText.setVisibility(View.GONE);

            }
        });


        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    nickName = mEtSearch.getText().toString();
                    onRefresh();
                }

                return false;
            }
        });

        mListView = (XListView) findViewById(id.listView);
        mListView.addHeaderView(searchView);
        mListView.setXListViewListener(this);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setDividerHeight(1);
        adapter = new CommonAdapter<HomePartnerEntity>(HomePartnerActivity.this, mlist, R.layout.home_partnermanager_list) {
            @Override
            public void convert(final ViewHolder holder, final HomePartnerEntity homePartnerEntity) {

                String childInfo = homePartnerEntity.getChildInfo();
                Id = homePartnerEntity.getId();

                if (!StringUtils.isEmpty(childInfo)) {
                    try {
                        JSONObject childObject = new JSONObject(childInfo);


                        String nickName = childObject.getString("nickName");
                        holder.setText(id.ic_name, nickName);

                        String face = childObject.getString("face");
                        if (!StringUtils.isEmpty(face)) {
                            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(face), (ImageView) holder.getView(R.id.iv_face));
                        }

                        String sex = childObject.getString("sex");
                        TextView sexView = holder.getView(id.ic_sex);
                        boyImg = getResources().getDrawable(R.mipmap.ic_wojia_nan2);
                        boyImg.setBounds(0, 0, boyImg.getIntrinsicWidth(), boyImg.getIntrinsicHeight());

                        girlImg = getResources().getDrawable(R.mipmap.ic_wojia_nv2);
                        girlImg.setBounds(0, 0, girlImg.getIntrinsicWidth(), girlImg.getIntrinsicHeight());

                        if ("0".equals(sex)) {
                            sexView.setCompoundDrawables(girlImg, null, null, null);
                        } else if ("1".equals(sex)) {
                            sexView.setCompoundDrawables(boyImg, null, null, null);
                        } else {
                            sexView.setCompoundDrawables(null, null, null, null);
                        }

                        String birthDay = childObject.getString("birthday");
                        if (!StringUtils.isEmpty(birthDay)) {
                            String age = StringUtils.getAgeWithYearDay(birthDay);
                            sexView.setText(age);

                        } else {

                            sexView.setText("");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                holder.getConvertView().setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {


                    }
                });

                holder.setOnClickListener(R.id.btn_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deletePartner(homePartnerEntity.getChild_id(),homePartnerEntity.getChild_friend_id());
                    }
                });
            }

        };

        mListView.setAdapter(adapter);

    }

    private void deletePartner(String childId, String friendId) {
        showProgress();
        ApiClient.post(HomePartnerActivity.this, ApiConfig.PARTNER_DELETE, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                UIHelper.ToastMessage(HomePartnerActivity.this, "删除好友成功");
                onRefresh();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(HomePartnerActivity.this, errorInfo.getMessage());
            }
        }, "childId", childId, "friendId", friendId);
    }

    @Override
    public void onRefresh() {
        mlist.clear();
        nextCursor = 1;
        initData();
    }

    @Override
    public void onLoadMore() {
        if (nextCursor <= totalPage) {
            initData();
        } else {
            stopLoading(mListView);
        }
    }
}
