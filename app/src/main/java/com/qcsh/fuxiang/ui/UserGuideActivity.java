package com.qcsh.fuxiang.ui;

import java.util.ArrayList;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.ui.login.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class UserGuideActivity extends Activity {

    private ViewPager pager;
    private Button btn;
    private RadioGroup radioGroup;
    private int[] images = {R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_guide);

        btn = (Button) findViewById(R.id.button1);
        btn.setVisibility(View.GONE);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        pager = (ViewPager) findViewById(R.id.pager_guide);
        pager.setAdapter(new MyPagerAdapter());
        pager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int positon) {

                if (positon == 3) {

                    btn.setVisibility(View.VISIBLE);

                    btn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(UserGuideActivity.this, LoginActivity.class);
                            startActivity(intent);
                           /*CustomApplication customApplication = (CustomApplication) getApplication();
                           customApplication.startHomeActivity(UserGuideActivity.this);*/
                            finish();
                        }
                    });
                } else {

                    btn.setVisibility(View.GONE);
                }

                RadioButton radioBtn = (RadioButton) radioGroup.getChildAt(positon);
                radioBtn.setChecked(true);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    class MyPagerAdapter extends PagerAdapter {

        ArrayList<ImageView> views = new ArrayList<ImageView>();

        public MyPagerAdapter() {
            for (int i = 0; i < images.length; i++) {
                ImageView view = new ImageView(UserGuideActivity.this);
                view.setScaleType(ScaleType.FIT_XY);
                view.setImageResource(images[i]);
                views.add(view);
            }
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = views.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

}
