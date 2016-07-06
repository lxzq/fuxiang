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
public class MyShaiShaiAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;

    public MyShaiShaiAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {

        return 8;
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
        if (null == convertView) {

            convertView = inflater.inflate(R.layout.home_myshaishai_list, null);
            holder = new ViewHolder();

            holder.dateText = (TextView)convertView.findViewById(R.id.ic_date);
            holder.contentText = (TextView)convertView.findViewById(R.id.ic_content);
            holder.faceimg = (ImageView)convertView.findViewById(R.id.iv_face);

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

        TextView contentText;
        TextView dateText;
        ImageView faceimg;
    }
}
