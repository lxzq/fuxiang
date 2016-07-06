package com.qcsh.fuxiang.ui.share;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;

/**
 * Created by WWW on 2015/9/14.
 */
public class PagerItemFragment extends Fragment {
    int mIndex;

    public PagerItemFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_squaredetail_item, container, false);
        if (savedInstanceState == null) {
            mIndex = getArguments().getInt("index");
        } else {
            mIndex = savedInstanceState.getInt("index");
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AppIntentManager.startGrowthTreeDetailActivity(getActivity());
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", mIndex);
        //Toast.makeText(getActivity(), "call onSaveInstanceState:" + mIndex, Toast.LENGTH_SHORT).show();
    }
}
