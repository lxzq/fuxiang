package com.qcsh.fuxiang.ui.share;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.ui.BaseFragment;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 晒晒
 */
public class ShareFragment extends BaseFragment implements ViewPager.OnPageChangeListener {


    private View mBaseView;
    private Toolbar mToolBar;
    private RadioGroup tabGroup;
    private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    private ViewPager mViewPager;
    private RadioButton tab1;
    private RadioButton tab0;
    private LinearLayout rightLayout;
    private Square square;
    private SharePublishFragment sharePublishFragment;
    private SharePublishFragment share2PublishFragment;
    private FriendCircle friendCircle;
    private MyPagerAdapter mPagerAdapter;
    private ArrayList<String> mSelectPath;
    private RadioGroup tabLineGroup;
    private RadioButton tabLine0;
    private RadioButton tabLine1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (fragmentList != null && fragmentList.size() > 0) {
            fragmentList.clear();
        }
        Bundle bundle = new Bundle();
        bundle.putInt("visibleAnge", 2);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("visibleAnge", 1);
        sharePublishFragment = new SharePublishFragment();
        sharePublishFragment.setArguments(bundle);
        share2PublishFragment = new SharePublishFragment();
        share2PublishFragment.setArguments(bundle2);
        fragmentList.add(sharePublishFragment);
        fragmentList.add(share2PublishFragment);
        mPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_share, container, false);
        initToolBar();
        mViewPager = (ViewPager) mBaseView
                .findViewById(R.id.share_viewpager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
        return mBaseView;
    }

    private void initToolBar() {
        mToolBar = (Toolbar) mBaseView.findViewById(R.id.layout_toolbar_tab);
        tabGroup = (RadioGroup) mToolBar.findViewById(R.id.toolbar_radiogroup);
        tabLineGroup = (RadioGroup) mToolBar.findViewById(R.id.toolbar_line_radiogroup);
        tab1 = (RadioButton) tabGroup.getChildAt(1);
        tab0 = (RadioButton) tabGroup.getChildAt(0);
        tab0.setTextColor(getResources().getColor(R.color.front_color));
        rightLayout = (LinearLayout) mToolBar.findViewById(R.id.toolbar_rightlayout);
        rightLayout.setVisibility(View.INVISIBLE);
        tab0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTab(0);
                mViewPager.setCurrentItem(0);

            }
        });
        tab1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkTab(1);
                mViewPager.setCurrentItem(1);

            }
        });

        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGrowTreeTypeWindow();
            }
        });
    }

    private void checkTab(int i) {
        if (i == 0) {
            rightLayout.setVisibility(View.VISIBLE);
            tab0.setTextColor(getResources().getColor(R.color.front_color));
            tab1.setTextColor(getResources().getColor(R.color.text_dark));
        } else {
            rightLayout.setVisibility(View.VISIBLE);
            tab1.setTextColor(getResources().getColor(R.color.front_color));
            tab0.setTextColor(getResources().getColor(R.color.text_dark));
        }
        ((RadioButton) tabLineGroup.getChildAt(i)).setChecked(true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                checkTab(0);
                rightLayout.setVisibility(View.INVISIBLE);
                break;
            case 1:
                checkTab(1);
                rightLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }

    private void showGrowTreeTypeWindow() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.select_grow_tree_type, null);
        final PopupWindow mPopupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        // 加上下面两行可以用back键关闭popupwindow，否则必须调用dismiss();
        ColorDrawable dw = new ColorDrawable(00000);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.update();
        mPopupWindow.setAnimationStyle(R.style.PopupAnimationToast);
        mPopupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, -30); // 添加popwindow显示的位置


        //发视频
        view.findViewById(R.id.send_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppIntentManager.startRecordVideoActivity(getActivity(), null, "2");
                mPopupWindow.dismiss();
            }
        });

        //发照片
        view.findViewById(R.id.send_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startImageSelectorForResult(getActivity(), mSelectPath, 9, 0);
                mPopupWindow.dismiss();
            }
        });

        //发心情
        view.findViewById(R.id.send_mood).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startSubmitActivity(getActivity(), null, null, AppConfig.MOODTYPE, null, "2");
                mPopupWindow.dismiss();
            }
        });

        //发语音
        view.findViewById(R.id.send_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startSubmitActivity(getActivity(), null, null, AppConfig.AUDIOTYPE, null, "2");
                mPopupWindow.dismiss();
            }
        });
        //关闭
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPopupWindow.dismiss();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选择图片后处理结果
        if (requestCode == AppConfig.REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                AppIntentManager.startSubmitActivity(getActivity(), null, mSelectPath, AppConfig.PICTURETYPE, null, "2");
            }
        }
    }
}
