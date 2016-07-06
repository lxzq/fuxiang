package com.qcsh.fuxiang.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcsh.fuxiang.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wo on 15/8/31.
 */
public class InviteFriendAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private  static final String[] COUNTRIES={"短信邀请","QQ邀请","微信邀请","微博邀请","扫二维码邀请","复制链接"};
    private static final  String[] Images={};

    public InviteFriendAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {

        return COUNTRIES.length;
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

            convertView = inflater.inflate(R.layout.look_list,null);
            holder = new ViewHolder();

            holder.ivImage = (ImageView)convertView.findViewById(R.id.image);
            holder.txtType = (TextView)convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        }
        else {

            holder = (ViewHolder)convertView.getTag();
        }

        String className = COUNTRIES[position];
        holder.txtType.setText(className);

//        String fileName = Images[position];
//        Bitmap bm = BitmapFactory.decodeFile(fileName);
//        holder.ivImage.setImageBitmap(bm);

        return convertView;
    }

    class ViewHolder{

        ImageView ivImage;
        TextView txtType;
    }
}
