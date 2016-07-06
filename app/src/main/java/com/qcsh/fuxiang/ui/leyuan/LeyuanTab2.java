package com.qcsh.fuxiang.ui.leyuan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;

import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.leyuan.LeyuanTab2Entity;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseFragment;
import com.qcsh.fuxiang.widget.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/15.
 */
public class LeyuanTab2 extends BaseFragment implements XListView.IXListViewListener ,LeyuanFragment.LeyuanAreaRefreshListener {

    private View root;
    private XListView xListView;

    private int pageCount;
    private int currentPage = 1;
    private String areaId = "";

    private List<Object> list;
    private CommonAdapter<LeyuanTab2Entity> commonAdapter;
    LayoutInflater inflater;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<Object>();
        initAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        root = inflater.inflate(R.layout.leyuan_tab_2,container,false);
        xListView = (XListView) root.findViewById(R.id.list_view);
        xListView.setXListViewListener(this);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
        xListView.setAdapter(commonAdapter);
        onRefresh();
        return root;

    }

    private void initAdapter(){
        commonAdapter = new CommonAdapter<LeyuanTab2Entity>(this.getActivity(),list,R.layout.leyuan_tab_2_item) {
            @Override
            public void convert(ViewHolder holder,final LeyuanTab2Entity leyuanTab1Entity) {
                String pic = leyuanTab1Entity.getPic();
                ImageView picView = holder.getView(R.id.pic);
                if(!TextUtils.isEmpty(pic) && pic.trim().length() > 5){
                    ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_xx(pic),picView);
                }else{
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.scan_mask);
                    picView.setImageBitmap(bitmap);
                }

                holder.setText(R.id.name,leyuanTab1Entity.getName());
                holder.setText(R.id.notes,leyuanTab1Entity.getNotes());
                holder.setText(R.id.price,leyuanTab1Entity.getPrice());
                holder.setOnClickListener(R.id.button, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppIntentManager.startLeyuanDetailActivity(getActivity(),leyuanTab1Entity);
                    }
                });
            }
        };
    }

    private void initData(){


        if(pageCount > 0 && currentPage > pageCount){
            UIHelper.ToastMessage(this.getActivity(), "已经是最后记录");
            stopLoading(xListView);
            return;
        }
        ApiClient.get(this.getActivity(), ApiConfig.LE_YUAN_TAB2, new ApiResponseHandler<LeyuanTab2Entity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                stopLoading(xListView);
                List<Object> da = entity.data;
                if (null != da) {
                    pageCount = entity.pageCount;
                    currentPage++;
                    list.addAll(da);
                    commonAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                stopLoading(xListView);
                UIHelper.ToastMessage(LeyuanTab2.this.getActivity(), errorInfo.getMessage());
            }
        }, "page", currentPage + "","areaId",areaId);
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        list.clear();
        initData();
    }

    @Override
    public void onLoadMore() {
        initData();
    }

    @Override
    public void onAreaRefresh(String areaId) {
        this.areaId = areaId;
        onRefresh();
    }
}
