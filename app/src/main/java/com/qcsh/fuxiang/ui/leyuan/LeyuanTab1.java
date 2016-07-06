package com.qcsh.fuxiang.ui.leyuan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.qcsh.fuxiang.bean.leyuan.LeyuanLiveEntity;
import com.qcsh.fuxiang.bean.leyuan.LeyuanTab1Entity;
import com.qcsh.fuxiang.common.StringUtils;

import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseFragment;
import com.qcsh.fuxiang.ui.look.GrowthTreeActivity;
import com.qcsh.fuxiang.widget.MoviePlayView;
import com.qcsh.fuxiang.widget.PullToRefreshView;
import com.qcsh.fuxiang.widget.XListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2015/10/15.
 */
public class LeyuanTab1 extends BaseFragment implements LeyuanFragment.LeyuanAreaRefreshListener {

    private View root;
    private GridView gridView;
    private PullToRefreshView pullToRefreshView;

    private int pageCount;
    private int currentPage = 1;
    private String areaId = "";

    private List<Object> list;
    private CommonAdapter<LeyuanLiveEntity> commonAdapter;
    LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList();
        initAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        root = inflater.inflate(R.layout.leyuan_tab_1,container,false);
        gridView = (GridView) root.findViewById(R.id.lives);
        gridView.setAdapter(commonAdapter);
        pullToRefreshView = (PullToRefreshView)root.findViewById(R.id.refresh_view);
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                onRefresh();
            }
        });

        pullToRefreshView.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                onLoadMore();
            }
        });
        onRefresh();
        return root;
    }

    private void initData(){

        LeyuanLiveEntity liveEntity = new LeyuanLiveEntity();
        liveEntity.setName("英语教育中心三班");
        liveEntity.setPic("/images/2015/9/1443435376871.png");
        liveEntity.setId("1");
        liveEntity.setPath(AppConfig.FILE_SERVER + "/videos/2015/9/1443492518656.mp4");
        list.add(liveEntity);
        list.add(liveEntity);
        list.add(liveEntity);
        commonAdapter.notifyDataSetChanged();

       if(pageCount > 0 && currentPage > pageCount){
            UIHelper.ToastMessage(this.getActivity(), "已经是最后记录");
            stopRefresh();
            return;
        }
        ApiClient.get(this.getActivity(), ApiConfig.LE_YUAN_TAB1, new ApiResponseHandler<LeyuanLiveEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                stopRefresh();
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
                stopRefresh();
                UIHelper.ToastMessage(LeyuanTab1.this.getActivity(), errorInfo.getMessage());
            }
        }, "page", currentPage + "","areaId",areaId);
    }

    private void initAdapter(){
        commonAdapter = new CommonAdapter<LeyuanLiveEntity>(this.getActivity(),list,R.layout.leyuan_tab_1_item) {
            @Override
            public void convert(ViewHolder holder,final LeyuanLiveEntity leyuanTab1Entity) {

                ImageView liveView = holder.getView(R.id.live_pic);
                TextView titleView = holder.getView(R.id.live_title);

                String pic = leyuanTab1Entity.getPic();
                if(!TextUtils.isEmpty(pic) && pic.trim().length() > 5 )
                    ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(pic), liveView);
                else{
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.scan_mask);
                    liveView.setImageBitmap(bitmap);
                }
                titleView.setText(leyuanTab1Entity.getName());

                holder.setOnClickListener(R.id.live_pic, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppIntentManager.startLeyuanLive(getActivity(), leyuanTab1Entity.getPath(), leyuanTab1Entity.getName(), leyuanTab1Entity.getId());
                    }
                });
            }
        };
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAreaRefresh(String areaId) {
        this.areaId = areaId;
        onRefresh();
    }

    private void onRefresh(){
        currentPage = 1;
        list.clear();
        initData();
    }

    private void onLoadMore(){
        initData();
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private void stopRefresh(){
        pullToRefreshView.onHeaderRefreshComplete(simpleDateFormat.format(new Date()));
        pullToRefreshView.onFooterRefreshComplete();
    }
}
