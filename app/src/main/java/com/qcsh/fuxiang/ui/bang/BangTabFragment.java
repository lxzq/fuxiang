package com.qcsh.fuxiang.ui.bang;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.adapter.BangTabPagerAdapter;
import com.qcsh.fuxiang.ui.BaseFragment;

import java.util.ArrayList;

/**
 * 帮主页
 * Created by Administrator on 2015/9/6.
 */
public class BangTabFragment extends BaseFragment {


    private View root;
    private ViewPager viewPage;
    private RadioGroup tabs;
    private Button action;
    private ImageButton imageAction;
    private TextView title;

    private ArrayList<Fragment> fragments;
    private BangTabPagerAdapter adapter;
    private String type = "1";

    private int currIndex;//当前页卡编号
    private int bmpW;//横线图片宽度
    private int offset;//图片移动的偏移量
    private ImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragments = new ArrayList<Fragment>();

        BangFragment tab1 = new BangFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type","1");
        tab1.setArguments(bundle);
        fragments.add(tab1);

        BangFragment tab2 = new BangFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString("type","2");
        tab2.setArguments(bundle2);
        fragments.add(tab2);

        BangFragment tab3 = new BangFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putString("type","3");
        tab3.setArguments(bundle3);
        fragments.add(tab3);

        BangFragment tab4 = new BangFragment();
        Bundle bundle4 = new Bundle();
        bundle4.putString("type","4");
        tab4.setArguments(bundle4);
        fragments.add(tab4);

        BangFragment tab5 = new BangFragment();
        Bundle bundle5 = new Bundle();
        bundle5.putString("type","5");
        tab5.setArguments(bundle5);
        fragments.add(tab5);

        adapter = new BangTabPagerAdapter(getChildFragmentManager(),fragments);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.bang_tab,container,false);
        initImage();
        initView();
        return root;
    }

    private void initView(){
        viewPage = (ViewPager)root.findViewById(R.id.view_page);
        viewPage.setAdapter(adapter);
        viewPage.setOnPageChangeListener(new MyOnPageChangeListener());
        tabs = (RadioGroup)root.findViewById(R.id.tabs);
        title = (TextView)root.findViewById(R.id.action_bar_title);
        imageAction = (ImageButton)root.findViewById(R.id.action_bar_image_action);
        title.setText("帮帮");

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
                    case R.id.tab_4:
                        viewPage.setCurrentItem(3);
                        break;
                    case R.id.tab_5:
                        viewPage.setCurrentItem(4);
                        break;
                }
            }
        });

        tabs.check(R.id.tab_1);
        action = (Button)root.findViewById(R.id.action_bar_action);
        action.setVisibility(View.GONE);
        imageAction.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.btn_bb_fabu));
        imageAction.setVisibility(View.VISIBLE);
        imageAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startBangPublishQuestionActivity(getActivity());
            }
        });
    }

    /**
     * 初始化图片的位移像素
     */
    private void initImage(){
        image = (ImageView)root.findViewById(R.id.cursor);
        bmpW = image.getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 5 - bmpW )/2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        image.setImageMatrix(matrix);
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        private int one = offset * 2 +bmpW;//两个相邻页面的偏移量

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            Animation animation = new TranslateAnimation(currIndex*one,position*one,0,0);
            currIndex = position;
            animation.setFillAfter(true);
            animation.setDuration(300);
            image.startAnimation(animation);


            switch (position){
                case 0 :
                    type = "1";
                    tabs.check(R.id.tab_1);
                    break;

                case 1 :
                    type = "2";
                    tabs.check(R.id.tab_2);
                    break;

                case 2 :
                    type = "3";
                    tabs.check(R.id.tab_3);
                    break;

                case 3 :
                    type = "4";
                    tabs.check(R.id.tab_4);
                    break;

                case 4 :
                    type = "5";
                    tabs.check(R.id.tab_5);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
