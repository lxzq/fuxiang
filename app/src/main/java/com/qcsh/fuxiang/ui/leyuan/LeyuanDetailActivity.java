package com.qcsh.fuxiang.ui.leyuan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
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
import com.qcsh.fuxiang.bean.leyuan.LeyuanLiveEntity;
import com.qcsh.fuxiang.bean.leyuan.LeyuanTab2Entity;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目详情
 * Created by Administrator on 2015/11/4.
 */
public class LeyuanDetailActivity extends BaseActivity {


    private List<Object> list;
    private CommonAdapter<LeyuanLiveEntity> commonAdapter;
    private LeyuanTab2Entity leyuanTab2Entity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leyuan_detail);
        initBar();
        leyuanTab2Entity = (LeyuanTab2Entity) getIntent().getSerializableExtra("leyuan");
        initAdapter();
        initView();
        initData();
    }

    private void initBar(){
        ImageButton imageButton = (ImageButton)findViewById(R.id.action_bar_back);
        TextView textView = (TextView)findViewById(R.id.action_bar_title);
        Button button = (Button)findViewById(R.id.action_bar_action);
        button.setVisibility(View.GONE);
        imageButton.setVisibility(View.VISIBLE);
        textView.setText("项目详情");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){
        ImageView imageView = (ImageView)findViewById(R.id.pic);

        TextView nameView = (TextView)findViewById(R.id.name);
        TextView priView = (TextView)findViewById(R.id.price);
        TextView contentView = (TextView)findViewById(R.id.content);
        String pic = leyuanTab2Entity.getPic();

        if(!TextUtils.isEmpty(pic) && pic.trim().length() > 5){
            ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(pic),imageView);
        }else{
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.scan_mask);
            imageView.setImageBitmap(bitmap);
        }
        nameView.setText(leyuanTab2Entity.getName());
        priView.setText(leyuanTab2Entity.getPrice());
        contentView.setText(leyuanTab2Entity.getNotes());

        GridView gridView = (GridView)findViewById(R.id.lives);
        gridView.setAdapter(commonAdapter);
    }

    private void initAdapter(){
        list = new ArrayList<>();
        commonAdapter = new CommonAdapter<LeyuanLiveEntity>(this,list,R.layout.leyuan_tab_1_item) {
            @Override
            public void convert(ViewHolder holder,final LeyuanLiveEntity leyuanTab1Entity) {

                ImageView liveView = holder.getView(R.id.live_pic);
                TextView titleView = holder.getView(R.id.live_title);

                String pic = leyuanTab1Entity.getPic();
                if(!TextUtils.isEmpty(pic) && pic.trim().length() > 5 )
                    ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(pic), liveView);
                else{
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.scan_mask);
                    liveView.setImageBitmap(bitmap);
                }
                titleView.setText(leyuanTab1Entity.getName());

                holder.setOnClickListener(R.id.live_pic, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppIntentManager.startLeyuanLive(LeyuanDetailActivity.this, leyuanTab1Entity.getPath(), leyuanTab1Entity.getName(), leyuanTab1Entity.getId());
                    }
                });
            }
        };
    }

    private void initData(){
        ApiClient.get(this, ApiConfig.LEYUAN_DETAIL_CAMERA, new ApiResponseHandler<LeyuanLiveEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                List<Object> da = entity.data;
                if (null != da) {
                    list.addAll(da);
                    commonAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(ErrorEntity errorInfo) {
                UIHelper.ToastMessage(getApplication(), errorInfo.getMessage());
            }
        }, "id",leyuanTab2Entity.getId());
    }
}
