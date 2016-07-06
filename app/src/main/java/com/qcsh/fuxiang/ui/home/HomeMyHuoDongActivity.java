package com.qcsh.fuxiang.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.share.FriendCircleListEntity;
import com.qcsh.fuxiang.bean.share.ShareListEntity;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.NoScrollGridView;
import com.qcsh.fuxiang.widget.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wo on 15/9/21.
 */
public class HomeMyHuoDongActivity extends BaseActivity implements XListView.IXListViewListener{

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;
    private XListView mListView;
    private List<Object> mlist = new ArrayList<Object>();
    private CommonAdapter<FriendCircleListEntity> adapter;
    private int nextCursor = 0;
    private TextView distanceText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_myhuodong);
        initToolBar();
        initView();
        initData();

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
        title.setText("我的活动");
    }

    private void initView(){
        mListView = (XListView)findViewById(R.id.listView);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setDividerHeight(2);
        mListView.setXListViewListener(this);
        mlist.add(new FriendCircleListEntity());
        mlist.add(new FriendCircleListEntity());
        mlist.add(new FriendCircleListEntity());
        mlist.add(new FriendCircleListEntity());
        adapter = new CommonAdapter<FriendCircleListEntity>(HomeMyHuoDongActivity.this, mlist, R.layout.home_huodong_list) {
            @Override
            public void convert(ViewHolder holder, final FriendCircleListEntity friendCircleEntity) {

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        mListView.setAdapter(adapter);

    }

    private void initData() {
        showProgress();
        ApiClient.get(HomeMyHuoDongActivity.this, ApiConfig.SQUARE_CIRCLE, new ApiResponseHandler<FriendCircleListEntity>() {

            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                stopLoading(mListView);
                List<Object> data = entity.data;
                if (data != null && data.size() > 0) {
                    mlist.addAll(data);
                    nextCursor = entity.currentPage;
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                stopLoading(mListView);
                UIHelper.ToastMessage(HomeMyHuoDongActivity.this, errorInfo.getMessage());
            }

        });
    }

    @Override
    public void onRefresh() {
        mlist.clear();
        nextCursor = 0;
        initData();
    }

    @Override
    public void onLoadMore() {
        initData();
    }
}
