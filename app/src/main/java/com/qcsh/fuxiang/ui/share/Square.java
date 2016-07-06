package com.qcsh.fuxiang.ui.share;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.share.ShareListEntity;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseFragment;
import com.qcsh.fuxiang.widget.NoScrollGridView;
import com.qcsh.fuxiang.widget.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WWW on 2015/9/6.
 */
public class Square extends BaseFragment implements View.OnClickListener, XListView.IXListViewListener {

    private View mBaseView;
    private FrameLayout layout0;
    private FrameLayout layout1;
    private FrameLayout layout2;
    private FrameLayout layout3;
    private FrameLayout layout4;
    private NoScrollGridView mGridView;
    private List<Object> mlist = new ArrayList<Object>();
    private boolean isRefresh;
    private String cacaheKey;
    private XListView mListView;
    private CommonAdapter<ShareListEntity> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.layout_listview, container, false);
        initView();
        initData();
        return mBaseView;
    }

    private void initView() {
        mListView = (XListView)mBaseView.findViewById(R.id.listView);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(this);
        mlist.add(new ShareListEntity());
        adapter = new CommonAdapter<ShareListEntity>(getActivity(),mlist,R.layout.fragment_square){
            @Override
            public void convert(ViewHolder holder, ShareListEntity shareEntity) {

                holder.setOnClickListener(R.id.layout0,Square.this);
                holder.setOnClickListener(R.id.layout1,Square.this);
                holder.setOnClickListener(R.id.layout2,Square.this);
                holder.setOnClickListener(R.id.layout3,Square.this);
                holder.setOnClickListener(R.id.layout4,Square.this);

                ((NoScrollGridView)holder.getView(R.id.squre_gridview)).setAdapter(new MyGridAdapter());
            }

        };
        mListView.setAdapter(adapter);
//        layout0 = (FrameLayout) headView.findViewById(R.id.layout0);
//        layout1 = (FrameLayout) headView.findViewById(R.id.layout1);
//        layout2 = (FrameLayout) headView.findViewById(R.id.layout2);
//        layout3 = (FrameLayout) headView.findViewById(R.id.layout3);
//        layout4 = (FrameLayout) headView.findViewById(R.id.layout4);

//        layout0.setOnClickListener(this);
//        layout1.setOnClickListener(this);
//        layout2.setOnClickListener(this);
//        layout3.setOnClickListener(this);
//        layout4.setOnClickListener(this);

//        mGridView = (NoScrollGridView) headView.findViewById(R.id.squre_gridview);
//        mGridView.setAdapter(new MyGridAdapter());
    }

    private void initData() {
        showProgress();
        ApiClient.get(getActivity(), ApiConfig.SQUARE_CIRCLE,  new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                stopLoading(mListView);
            }
            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                stopLoading(mListView);
                UIHelper.ToastMessage(getActivity(), errorInfo.getMessage());
            }
        },"");
    }

    @Override
    public void onRefresh() {

        mlist.clear();
        initData();
    }

    @Override
    public void onLoadMore() {
        mlist.clear();
        initData();
    }


    private class MyGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 9;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.square_gridview_item, null);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppIntentManager.startSquareDetailActivity(getActivity());
                }
            });
            return convertView;
        }

        class ViewHolder {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout0:
                AppIntentManager.startSquareDetailActivity(getActivity());
                break;
            case R.id.layout1:
                AppIntentManager.startSquareDetailActivity(getActivity());
                break;
            case R.id.layout2:
                AppIntentManager.startSquareDetailActivity(getActivity());
                break;
            case R.id.layout3:
                AppIntentManager.startSquareDetailActivity(getActivity());
                break;
            case R.id.layout4:
                AppIntentManager.startSquareDetailActivity(getActivity());
                break;
        }
    }
}
