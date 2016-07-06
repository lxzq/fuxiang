package com.qcsh.fuxiang.ui.share;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.share.FriendCircleListEntity;
import com.qcsh.fuxiang.bean.share.ShareListEntity;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.ArrowProgressBar;
import com.qcsh.fuxiang.widget.XListView;
import com.qcsh.fuxiang.widget.recyclerviewpager.FragmentStatePagerAdapter;
import com.qcsh.fuxiang.widget.recyclerviewpager.RecyclerViewPager;

import java.util.ArrayList;
import java.util.List;

public class SquareDetailActivity extends BaseActivity {

    private String cacheKey;
    private boolean isRefresh;
    private List<Object> mlist = new ArrayList<Object>();
    private int nextCursor = 0;
    private XListView mListView;
    private CommonAdapter<FriendCircleListEntity> adapter;
    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;
    private RecyclerViewPager mRecyclerView;
    private TextView sdTitle;
    private ArrowProgressBar mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_detail);
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
        title.setText("广场详情");
    }

    private void initView() {
        sdTitle = (TextView) findViewById(R.id.sd_title);
        mProgress = (ArrowProgressBar) findViewById(R.id.progressBar);
        mProgress.setMax(9);
//        mProgress.setProgress(1);
        mRecyclerView = (RecyclerViewPager) findViewById(R.id.recycler_view);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                int currentPosition = mRecyclerView.getCurrentPosition();
                sdTitle.setText(currentPosition + "");
                mProgress.setProgress(currentPosition + 1);
            }
        });
    }

    private void initData() {
        showProgress();
        ApiClient.get(SquareDetailActivity.this, ApiConfig.GROWTHTREE_DETAIL,new ApiResponseHandler<ShareListEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {

            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {

            }
        }, "");
    }

    public class FragmentsAdapter extends FragmentStatePagerAdapter {

        public FragmentsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position, Fragment.SavedState savedState) {
            Fragment f = new PagerItemFragment();
            if (savedState == null) {
                Bundle bundle = new Bundle();
                bundle.putInt("index", position);
                f.setArguments(bundle);
            }
            f.setInitialSavedState(savedState);
            return f;
        }

        @Override
        public void onDestroyItem(int position, Fragment fragment) {

        }

        @Override
        public int getItemCount() {
            return 9;
        }
    }
}
