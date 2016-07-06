package com.qcsh.fuxiang.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.ui.HomeActivity;

import java.util.List;

/**
 * Created by lxz on 2015/9/8.
 */
public class FragmentTabAdapter implements RadioGroup.OnCheckedChangeListener {
    private final int length25;
    private final int length22;
    private final Drawable img1;
    private final Drawable img2;
    private final Drawable img2ed;
    private final Drawable img3;
    private final Drawable img3ed;
    private final Drawable img4;
    private final Drawable img4ed;
    private final RadioButton host2;
    private final RadioButton host3;
    private final RadioButton host4;
    private List<Fragment> fragments; // 一个tab页面对应一个Fragment
    private RadioGroup rgs; // 用于切换tab
    private FragmentActivity fragmentActivity; // Fragment所属的Activity
    private int fragmentContentId; // Activity中所要被替换的区域的id
    private int currentTab; // 当前Tab页面索引
    private OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener; // 用于让调用者在切换tab时候增加新的功能

    public FragmentTabAdapter(FragmentActivity fragmentActivity, List<Fragment> fragments, int fragmentContentId, RadioGroup rgs) {
        this.fragments = fragments;
        this.rgs = rgs;
        this.fragmentActivity = fragmentActivity;
        this.fragmentContentId = fragmentContentId;

        // 默认显示第一页
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.add(fragmentContentId, fragments.get(0));
        ft.commit();
        rgs.setOnCheckedChangeListener(this);

        length25 = Utils.dip2int(fragmentActivity, 25);
        length22 = Utils.dip2int(fragmentActivity, 22);

        img1 = fragmentActivity.getResources().getDrawable(R.mipmap.tabbar_btn_kanzhe_normal);
        img1.setBounds(0, 0, length25, length22);

        img2 = fragmentActivity.getResources().getDrawable(R.mipmap.tabbar_btn_bang_normal);
        img2.setBounds(0, 0, length25, length22);
        img2ed = fragmentActivity.getResources().getDrawable(R.mipmap.tabbar_btn_bang_selected);
        img2ed.setBounds(0, 0, length25, length22);

        img3 = fragmentActivity.getResources().getDrawable(R.mipmap.tabbar_btn_shai_normal);
        img3.setBounds(0, 0, length25, length22);
        img3ed = fragmentActivity.getResources().getDrawable(R.mipmap.tabbar_btn_shai_selected);
        img3ed.setBounds(0, 0, length25, length22);

        img4 = fragmentActivity.getResources().getDrawable(R.mipmap.tabbar_btn_wojia_normal);
        img4.setBounds(0, 0, length25, length22);
        img4ed = fragmentActivity.getResources().getDrawable(R.mipmap.tabbar_btn_wojia_selected);
        img4ed.setBounds(0, 0, length25, length22);

        host2 = (RadioButton) rgs.findViewById(R.id.host2);
        host3 = (RadioButton) rgs.findViewById(R.id.host3);
        host4 = (RadioButton) rgs.findViewById(R.id.host4);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        int index = 0;
        switch (checkedId){
            case R.id.host1 :
                index = 0;
                break;
            case R.id.host1_add :
                index = 0;
                break;
            case R.id.host2 :
                index = 1;
                host2.setCompoundDrawables(null, img2ed, null, null);
                host3.setCompoundDrawables(null, img3, null, null);
                host4.setCompoundDrawables(null, img4, null, null);
                break;
            case R.id.host3 :
                index = 2;
                host3.setCompoundDrawables(null, img3ed, null, null);
                host2.setCompoundDrawables(null, img2, null, null);
                host4.setCompoundDrawables(null, img4, null, null);
                break;
            case R.id.host4 :
                index = 3;
                host4.setCompoundDrawables(null, img4ed, null, null);
                host2.setCompoundDrawables(null, img2, null, null);
                host3.setCompoundDrawables(null, img3, null, null);
                break;
            case R.id.host5 :
                index = 4;
                host2.setCompoundDrawables(null, img2, null, null);
                host3.setCompoundDrawables(null, img3, null, null);
                host4.setCompoundDrawables(null, img4, null, null);
                break;
        }
        Fragment fragment = fragments.get(index);
        FragmentTransaction ft = obtainFragmentTransaction(index);
        getCurrentFragment().onPause(); // 暂停当前tab
//      getCurrentFragment().onStop(); // 暂停当前tab
        if (fragment.isAdded()) {
//          fragment.onStart(); // 启动目标tab的onStart()
            fragment.onResume(); // 启动目标tab的onResume()
        } else {
            ft.add(fragmentContentId, fragment);
        }
        showTab(index); // 显示目标tab
        ft.commit();
        // 如果设置了切换tab额外功能功能接口
        if (null != onRgsExtraCheckedChangedListener) {
            onRgsExtraCheckedChangedListener.OnRgsExtraCheckedChanged(radioGroup,checkedId,index);
        }
    }

    /**
     * 切换tab
     *
     * @param idx
     */
    private void showTab(int idx){
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = obtainFragmentTransaction(idx);
            if (idx == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commit();
        }
        currentTab = idx; // 更新目标tab为当前tab
    }

    /**
     * 获取一个带动画的FragmentTransaction
     *
     * @param index
     * @return
     */
    private FragmentTransaction obtainFragmentTransaction(int index) {
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        return ft;
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public Fragment getCurrentFragment() {
        return fragments.get(currentTab);
    }

    public OnRgsExtraCheckedChangedListener getOnRgsExtraCheckedChangedListener() {
        return onRgsExtraCheckedChangedListener;
    }

    public void setOnRgsExtraCheckedChangedListener(OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener) {
        this.onRgsExtraCheckedChangedListener = onRgsExtraCheckedChangedListener;
    }

    /**
     * 切换tab额外功能功能接口
     */
   public static class OnRgsExtraCheckedChangedListener {
        public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {

        }
    }

}
