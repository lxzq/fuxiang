package com.qcsh.fuxiang.ui.share;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.share.FriendCircleListEntity;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseFragment;
import com.qcsh.fuxiang.widget.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WWW on 2015/9/6.
 */
public class FriendCircle extends BaseFragment implements XListView.IXListViewListener {

    private View mBaseView;
    private XListView mListView;
    private String cacheKey;
    private boolean isRefresh;
    private List<Object> mlist = new ArrayList<Object>();
    private CommonAdapter<FriendCircleListEntity> adapter;
    private int nextCursor = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.layout_listview, container, false);
        mListView = (XListView) mBaseView.findViewById(R.id.listView);
        mListView.setXListViewListener(this);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
//        adapter = new FriendCircleAdapter(getActivity(), mlist);
//        mListView.setAdapter(adapter);
        mlist.add(new FriendCircleListEntity());
        mlist.add(new FriendCircleListEntity());
        mlist.add(new FriendCircleListEntity());
        mlist.add(new FriendCircleListEntity());
        adapter = new CommonAdapter<FriendCircleListEntity>(getActivity(), mlist, R.layout.friendcircle_item) {
            @Override
            public void convert(ViewHolder holder, FriendCircleListEntity friendCircleEntity) {

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // AppIntentManager.startGrowthTreeDetailActivity(getActivity());
                    }
                });
            }
        };
        mListView.setAdapter(adapter);
        initData();
        return mBaseView;
    }

    private void initData() {
        showProgress();
        ApiClient.get(getActivity(), ApiConfig.SQUARE_CIRCLE, new ApiResponseHandler<FriendCircleListEntity>() {

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
                UIHelper.ToastMessage(FriendCircle.this.getActivity(), errorInfo.getMessage());
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
