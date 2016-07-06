package com.qcsh.fuxiang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcsh.fuxiang.R;

/**
 * Created by wo on 15/9/21.
 */
public class HomeHuoDongAdapter extends BaseAdapter{
    private Context mContext;
    private LayoutInflater inflater;

    public HomeHuoDongAdapter(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {

        return 5;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final  ViewHolder holder;
        if (null == convertView){

            convertView = inflater.inflate(R.layout.home_huodong_list,null);
            holder = new ViewHolder();

            holder.ivImage = (ImageView)convertView.findViewById(R.id.iv_image);
            holder.titleText = (TextView)convertView.findViewById(R.id.ic_title);
            convertView.setTag(holder);
        }
        else {

            holder = (ViewHolder)convertView.getTag();
        }

        return convertView;
    }

    class ViewHolder{

        ImageView ivImage;
        TextView titleText;
    }
}
