package com.qcsh.fuxiang.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qcsh.fuxiang.R;

import java.util.List;

/**
 * Created by WWW on 2015/9/7.(使用通用的Adapter代替了)
 */
public class SquareDetailAdapter extends RecyclerView.Adapter<SquareDetailAdapter.ViewHolder> {

    private List mlist;

    public SquareDetailAdapter(List list) {
        this.mlist = list;
    }

    @Override
    public SquareDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_squaredetail_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 9;
//        return mlist.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImg;
        public ViewHolder(View itemView) {
            super(itemView);
            mImg = (ImageView)itemView.findViewById(R.id.sditem_img);
        }
    }
}
