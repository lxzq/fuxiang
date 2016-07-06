package com.qcsh.fuxiang.ui.leyuan;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.adapter.BangTabPagerAdapter;

import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.leyuan.LeyuanAdaressEntity;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseFragment;



import java.util.ArrayList;
import java.util.List;

/**
 * 智慧乐园
 * Created by Administrator on 2015/10/15.
 */
public class LeyuanFragment extends BaseFragment {

    private View root;
    private ViewPager viewPage;
    private RadioGroup tabs;
    private Button addressButton;

    private ArrayList<Fragment> fragments;
    private BangTabPagerAdapter adapter;
    private List<Object> list ;
    private CommonAdapter<LeyuanAdaressEntity> addressCommonAdapter;
    private LeyuanAdaressEntity currentAddress;

    private LeyuanTab1 testFragment1;
    private LeyuanTab2 testFragment2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<Object>();
        fragments = new ArrayList<Fragment>();

        testFragment1 = new LeyuanTab1();
        testFragment2 = new LeyuanTab2();
        LeyuanTab3 testFragment3 = new LeyuanTab3();

        fragments.add(testFragment1);
        fragments.add(testFragment2);
        fragments.add(testFragment3);

        adapter = new BangTabPagerAdapter(getChildFragmentManager(),fragments);

        initAdapter();
        initAddressList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.leyuan_tab,container,false);
        initView();
        initAddressView(inflater);
        return root;
    }

    private void initView(){
        viewPage = (ViewPager)root.findViewById(R.id.view_page);
        addressButton = (Button)root.findViewById(R.id.select_address);
        viewPage.setAdapter(adapter);
        viewPage.setOnPageChangeListener(new MyOnPageChangeListener());
        tabs = (RadioGroup)root.findViewById(R.id.tabs);

        addressButton.setText("全部");

        tabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.tab_1:
                        viewPage.setCurrentItem(0);
                        break;
                    case R.id.tab_2:
                        viewPage.setCurrentItem(1);
                        break;
                    case R.id.tab_3:
                        viewPage.setCurrentItem(2);
                        break;
                }
            }
        });
        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });
        tabs.check(R.id.tab_1);
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

            switch (position){
                case 0 :
                    tabs.check(R.id.tab_1);
                    break;

                case 1 :
                    tabs.check(R.id.tab_2);
                    break;

                case 2 :
                    tabs.check(R.id.tab_3);
                    break;

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private View addressView;
    private PopupWindow mPopupWindow;


    private void initAddressView(LayoutInflater inflater){
        addressView = inflater.inflate(R.layout.leyuan_address, null);
        ListView listView = (ListView) addressView.findViewById(R.id.list_view);
        listView.setAlpha(0.9f);
        listView.setAdapter(addressCommonAdapter);
    }

    private void initAdapter(){
        addressCommonAdapter = new CommonAdapter<LeyuanAdaressEntity>(getActivity(), list, R.layout.look_change_child_item) {
            @Override
            public void convert(ViewHolder holder, final LeyuanAdaressEntity address) {
                RadioButton nickname = holder.getView(R.id.child);
                ImageView faceView = holder.getView(R.id.face);
                faceView.setVisibility(View.GONE);
                nickname.setChecked(false);
                if(null != currentAddress && currentAddress.equals(address))
                nickname.setChecked(true);

                nickname.setText(address.getName());
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentAddress = address;
                        addressButton.setText(currentAddress.getName());
                        leyuanRefresh();
                        mPopupWindow.dismiss();
                    }
                });

                nickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentAddress = address;
                        addressButton.setText(currentAddress.getName());
                        leyuanRefresh();
                        mPopupWindow.dismiss();
                    }
                });
            }
        };
    }

    private void initPopupWindow(){

        if(null == mPopupWindow){
            mPopupWindow = new PopupWindow(addressView,
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.transparent));
            mPopupWindow.setBackgroundDrawable(dw);
            mPopupWindow.setAnimationStyle(R.style.PopupAnimationToast);
        }
        int x = Utils.dip2int(this.getActivity(), -30);
        mPopupWindow.showAsDropDown(addressButton, x, 0);
    }

    private void initAddressList() {
        LeyuanAdaressEntity all = new LeyuanAdaressEntity();
        all.setId("");
        all.setName("全部");
        list.add(all);
        currentAddress = all;
        addressCommonAdapter.notifyDataSetChanged();

        ApiClient.get(getActivity(), ApiConfig.LEYUAN_ADDRESS, new ApiResponseHandler<LeyuanAdaressEntity>() {
            @Override
            public void onFailure(ErrorEntity errorInfo) {

            }
            @Override
            public void onSuccess(DataEntity entity) {
                List<Object> data = entity.data;
                if (null != data) {
                    list.addAll(data);
                    addressCommonAdapter.notifyDataSetChanged();
                }
            }
        },"");
    }

    private void leyuanRefresh(){
        LeyuanAreaRefreshListener leyuantab1 = testFragment1;
        leyuantab1.onAreaRefresh(currentAddress.getId());

        LeyuanAreaRefreshListener leyuantab2 = testFragment2;
        leyuantab2.onAreaRefresh(currentAddress.getId());
    }

    public interface LeyuanAreaRefreshListener {
        void onAreaRefresh(String areaId);
    }
}
