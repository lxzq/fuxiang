package com.qcsh.fuxiang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.widget.MoviePlayView;

import java.util.List;

/**
 * Created by WWW on 2015/9/7.(使用通用的Adapter代替了)
 */
public class FriendCircleAdapter extends BaseAdapter {

    private Context context;
    private List mlist;

    public FriendCircleAdapter(Context context, List list) {
        this.context = context;
        this.mlist = list;
    }

    @Override
    public int getCount() {
       // return mlist.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.friendcircle_item, null);
            vh.face = (ImageView) convertView.findViewById(R.id.userface);
            vh.user = (TextView) convertView.findViewById(R.id.user);
            vh.time = (TextView) convertView.findViewById(R.id.create_time);
            vh.location = (TextView) convertView.findViewById(R.id.location);
            vh.distance = (TextView) convertView.findViewById(R.id.distance);
            vh.img = (ImageView) convertView.findViewById(R.id.image);
            vh.audiobtn = (ImageView) convertView.findViewById(R.id.type_audio_img);
           // vh.player = (MoviePlayView) convertView.findViewById(R.id.videoplayer);
            vh.content = (TextView) convertView.findViewById(R.id.item_content);
            vh.zan = (TextView) convertView.findViewById(R.id.zan);
            vh.comment = (TextView) convertView.findViewById(R.id.comment);
            vh.share = (TextView) convertView.findViewById(R.id.share);
            vh.contentLayout = (LinearLayout) convertView.findViewById(R.id.layout_comment);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        //mlist.get(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // AppIntentManager.startGrowthTreeDetailActivity(context);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView face;
        TextView user;
        TextView time;
        TextView location;
        TextView distance;
        ImageView img;
        MoviePlayView player;
        ImageView audiobtn;
        TextView content;
        TextView zan;
        TextView comment;
        TextView share;
        LinearLayout contentLayout;
    }
}
