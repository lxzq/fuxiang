package com.qcsh.fuxiang.ui.home;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.adapter.BangTabPagerAdapter;
import com.qcsh.fuxiang.ui.BaseActivity;

import java.util.ArrayList;

/**
 * Created by wo on 15/9/21.
 */
public class HomeMySaveActivity extends BaseActivity {

    private ViewPager viewPage;
    private RadioGroup tabs;
    private Button action;
    private ImageButton imageAction;
    private TextView title;
    private ImageButton leftBtn;

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
        setContentView(R.layout.home_save_tab);
        fragments = new ArrayList<Fragment>();

        HomeGrowthTreeFragment tab1 = new HomeGrowthTreeFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("type","1");
//        tab1.setArguments(bundle);
        fragments.add(tab1);

        HomeBangCollectFragment tab2 = new HomeBangCollectFragment();
//        Bundle bundle2 = new Bundle();
//        bundle2.putString("type","2");
//        tab2.setArguments(bundle2);
        fragments.add(tab2);

//        HomeHuoDongFragment tab3 = new HomeHuoDongFragment();
//        Bundle bundle3 = new Bundle();
//        bundle3.putString("type","3");
//        tab3.setArguments(bundle3);
//        fragments.add(tab3);

        HomeMyShaiShaiFragment tab4 = new HomeMyShaiShaiFragment();
//        Bundle bundle4 = new Bundle();
//        bundle4.putString("type", "4");
//        tab4.setArguments(bundle4);
        fragments.add(tab4);


        adapter = new BangTabPagerAdapter(HomeMySaveActivity.this.getSupportFragmentManager(),fragments);

        initImage();
        initView();
    }


    private void initView(){
        viewPage = (ViewPager)findViewById(R.id.view_page);
        viewPage.setAdapter(adapter);
        viewPage.setOnPageChangeListener(new MyOnPageChangeListener());
        tabs = (RadioGroup)findViewById(R.id.tabs);
        title = (TextView)findViewById(R.id.action_bar_title);
        imageAction = (ImageButton)findViewById(R.id.action_bar_image_action);
        title.setText("我的收藏");
        leftBtn = (ImageButton)findViewById(R.id.action_bar_back);
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
//                    case R.id.tab_3:
//                        viewPage.setCurrentItem(2);
//                        break;
                    case R.id.tab_4:
                        viewPage.setCurrentItem(2);
                        break;
                }
            }
        });

        tabs.check(R.id.tab_1);
        action = (Button)findViewById(R.id.action_bar_action);
        action.setVisibility(View.GONE);
    }

    /**
     * 初始化图片的位移像素
     */
    private void initImage(){
        image = (ImageView)findViewById(R.id.cursor);
        bmpW = image.getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        HomeMySaveActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 3 - bmpW )/2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        image.setImageMatrix(matrix);
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        private int one = offset * 2 + bmpW;//两个相邻页面的偏移量

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
//                    type = "1";
                    tabs.check(R.id.tab_1);
                    break;

                case 1 :
//                    type = "2";
                    tabs.check(R.id.tab_2);
                    break;

//                case 2 :
//                    type = "3";
//                    tabs.check(R.id.tab_3);
//                    break;

                case 2 :
//                    type = "4";
                    tabs.check(R.id.tab_4);
                    break;


            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
