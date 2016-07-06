package com.qcsh.fuxiang.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.bang.BangInfo;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseFragment;
import com.qcsh.fuxiang.widget.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wo on 15/9/21.
 */
public class HomeHuoDongFragment extends BaseFragment implements XListView.IXListViewListener {

    private View root;
    private XListView xListView;
    private ArrayList<Object> dataList = new ArrayList<>();
    private int pageCount = 1;
    private int currentPage = 1;
    private CommonAdapter<BangInfo> adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.layout_listview, container, false);
        //initData();
        BangInfo bi = new BangInfo();
        dataList.add(bi);
        dataList.add(bi);
        dataList.add(bi);
        dataList.add(bi);
        dataList.add(bi);
        dataList.add(bi);
        dataList.add(bi);
        dataList.add(bi);
        dataList.add(bi);
        dataList.add(bi);
        dataList.add(bi);
        dataList.add(bi);
        adapter = new CommonAdapter<BangInfo>(getActivity(), dataList, R.layout.home_huodong_list) {
            @Override
            public void convert(ViewHolder holder, BangInfo bangInfo) {

            }
        };
        xListView = (XListView) root.findViewById(R.id.listView);
        xListView.setXListViewListener(this);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        xListView.setAdapter(adapter);
        return root;
    }

    private void initData() {
        if (pageCount > 0 && currentPage > pageCount) {
            stopLoading(xListView);
            return;
        }
        showProgress();
        ApiClient.get(this.getActivity(), ApiConfig.HOME_MY_COLLECT, new ApiResponseHandler<BangInfo>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                stopLoading(xListView);
                List<Object> objectList = entity.data;
                if (null != objectList && !objectList.isEmpty()) {
                    dataList.addAll(objectList);
                    adapter.notifyDataSetChanged();
                    pageCount = entity.pageCount;
                    currentPage++;
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                stopLoading(xListView);
                UIHelper.ToastMessage(getActivity(), errorInfo.getMessage());
            }
        }, "page", currentPage + "");
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        dataList.clear();
        initData();
    }

    @Override
    public void onLoadMore() {
        initData();
    }
}
