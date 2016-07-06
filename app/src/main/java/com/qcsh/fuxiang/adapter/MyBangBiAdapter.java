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
 * Created by wo on 15/9/20.
 */
public class MyBangBiAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;

    public MyBangBiAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {

        return 4;
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
        final ViewHolder holder;
        if (null == convertView){

            convertView = inflater.inflate(R.layout.home_mybangbi_list,null);
            holder = new ViewHolder();

            holder.descripeText = (TextView)convertView.findViewById(R.id.ic_descripe);
            holder.dateText = (TextView)convertView.findViewById(R.id.ic_date);
            holder.bangbiCountText = (TextView)convertView.findViewById(R.id.ic_bangbilist);
            convertView.setTag(holder);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }else
            holder = (ViewHolder)convertView.getTag();

        return convertView;
    }

    class ViewHolder{

        TextView descripeText;
        TextView dateText;
        TextView bangbiCountText;
    }
}
