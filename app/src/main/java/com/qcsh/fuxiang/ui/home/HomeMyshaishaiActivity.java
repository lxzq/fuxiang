package com.qcsh.fuxiang.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.adapter.MyBangBiAdapter;
import com.qcsh.fuxiang.adapter.MyShaiShaiAdapter;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.bang.BangInfo;
import com.qcsh.fuxiang.bean.look.GrowthTreeEntity;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.XListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by wo on 15/9/18.
 */
public class HomeMyshaishaiActivity extends BaseActivity implements XListView.IXListViewListener {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;
    private int pageCount = 1;
    private int curPage = 1;
    private ArrayList<Object> dataList = new ArrayList<>();
    private XListView xListView;
    private CommonAdapter<GrowthTreeEntity> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_myshaishai);
        initToolBar();
        initView();
        loadData();
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
        title.setText("我的晒晒");
    }

    private void initView() {

        adapter = new CommonAdapter<GrowthTreeEntity>(HomeMyshaishaiActivity.this, dataList, R.layout.home_myshaishai_list) {
            @Override
            public void convert(ViewHolder holder, final GrowthTreeEntity entity) {
                String time = StringUtils.friendly_time(entity.getRelease_time());
                String year = entity.getRelease_time().substring(0, 4);
                if (Integer.valueOf(year) < Integer.valueOf(StringUtils.getYearMonthDay().substring(0,4))) {
                    holder.setVisible(R.id.ic_year, true);
                    holder.setText(R.id.ic_year, year);
                } else {
                    holder.setVisible(R.id.ic_year, false);
                }

                if (time.length() > 4) {
                    time = time.substring(5, 10);
                }

                holder.setText(R.id.ic_date, time);
                holder.setText(R.id.ic_content, entity.getContent());


                String shareType = entity.getType();
                switch (Integer.valueOf(shareType)) {
                    case AppConfig.VIDEOTYPE:
                        holder.setVisible(R.id.iv_btn, true);
                        holder.setImageResource(R.id.iv_btn, R.mipmap.btn_kanzhe_bofang);
                        break;
                    case AppConfig.AUDIOTYPE:
                        holder.setVisible(R.id.iv_btn, true);
                        holder.setImageResource(R.id.iv_btn, R.mipmap.btn_kanzhe_yuyin_normal4);
                        break;
                    case AppConfig.PICTURETYPE:
                        holder.setVisible(R.id.iv_btn, false);
                        break;
                    case AppConfig.MOODTYPE:
                        holder.setVisible(R.id.iv_btn, false);
                        break;
                }
                String images = entity.getImages();
                if (!StringUtils.isEmpty(images)) {
                    try {
                        JSONArray array = new JSONArray(images);
                        if (array.length() > 0 || Integer.valueOf(shareType) == AppConfig.AUDIOTYPE) {
                            holder.setVisible(R.id.image_layout, true);
                            holder.setBackgroundRes(R.id.content_layout, R.color.white);
                            holder.setTextColor(R.id.ic_content, getResources().getColor(R.color.text_dark));
                            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(array.getJSONObject(0).getString("imageName")), (ImageView) holder.getView(R.id.iv_face));
                        } else {
                            holder.setVisible(R.id.image_layout, false);
                            holder.setTextColor(R.id.ic_content, getResources().getColor(R.color.white));
                            holder.setBackgroundRes(R.id.content_layout, R.color.green_light);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppIntentManager.startGrowthTreeDetailActivity(HomeMyshaishaiActivity.this, entity, "2");
                    }
                });
            }
        };

        xListView = (XListView) findViewById(R.id.listView);
        xListView.setXListViewListener(this);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        xListView.setAdapter(adapter);
    }


    private void loadData() {
        if (curPage > pageCount) {
            stopLoading(xListView);
            return;
        }
        showProgress();
        ApiClient.get(HomeMyshaishaiActivity.this, ApiConfig.HOME_MY_SHAISHAI, new ApiResponseHandler<GrowthTreeEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                stopLoading(xListView);
                List<Object> objectList = entity.data;
                if (null != objectList && !objectList.isEmpty()) {
                    dataList.addAll(objectList);
                    adapter.notifyDataSetChanged();
                    pageCount = entity.pageCount;
                    curPage++;
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                stopLoading(xListView);
                UIHelper.ToastMessage(HomeMyshaishaiActivity.this, errorInfo.getMessage());
            }
        }, "page", curPage + "", "userId", getCurrentUser().getId());

    }

    @Override
    public void onRefresh() {
        curPage = 1;
        dataList.clear();
        loadData();
    }

    @Override
    public void onLoadMore() {
        loadData();
    }

}
