package com.qcsh.fuxiang.ui;


import android.content.Intent;
import android.support.v4.app.Fragment;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppContext;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.widget.Loading;
import com.qcsh.fuxiang.widget.XListView;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by liu on 13-10-15.
 * fragment
 */
public class BaseFragment extends Fragment {
    private Loading mCustomProgress;


    public void showProgress() {
        if (mCustomProgress == null) {
            mCustomProgress = Loading.Show(getActivity(), null, true, null);
        }
    }

    public void showProgress(String str) {
        if (mCustomProgress == null) {
            mCustomProgress = Loading.Show(getActivity(), str, true, null);
        } else {
            mCustomProgress.setMessage(str);
            mCustomProgress.show();
        }
    }

    public void showProgress(int resId) {
        String str = this.getString(resId);
        showProgress(str);
    }

    public void closeProgress() {
        if (mCustomProgress != null) {
            mCustomProgress.dismiss();
            mCustomProgress = null;
        }
    }

    protected void stopLoading(XListView customListView) {
        if (customListView != null) {
            customListView.stopRefresh();
            customListView.stopLoadMore();
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
                    DateFormat.MEDIUM, Locale.CHINA);
            String strDate = df.format(new Date());
            customListView.setRefreshTime(strDate);// 设置更新时间
        }
    }

    protected User getCurrentUser() {
        AppContext appContext = (AppContext) this.getActivity().getApplication();
        return appContext.getCurUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        closeProgress();
        ApiClient.httpClient.cancelRequests(this.getActivity(), true);
        super.onDestroy();
    }
}
