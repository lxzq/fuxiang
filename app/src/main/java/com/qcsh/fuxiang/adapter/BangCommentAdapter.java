package com.qcsh.fuxiang.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.bean.bang.CommentInfo;

import java.util.List;

/**
 * Created by Administrator on 2015/9/11.
 */
public class BangCommentAdapter extends BaseAdapter {

    private List<Object> dataList;
    private LayoutInflater inflater;
    private Activity activity;

    public BangCommentAdapter(List<Object> dataList,Activity activity){
        this.dataList = dataList;
        this.inflater = LayoutInflater.from(activity);
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HoldView holdView;
        CommentInfo commentInfo = (CommentInfo)dataList.get(position);
        if(null == convertView){
            holdView = new HoldView();
            convertView = inflater.inflate(R.layout.layout_commentitem,null);
            holdView.nickName = (TextView)convertView.findViewById(R.id.comment_user);
            holdView.content = (TextView)convertView.findViewById(R.id.comment_content);
            holdView.date = (TextView)convertView.findViewById(R.id.comment_date);
            convertView.setTag(holdView);
        }else{
            holdView = (HoldView) convertView.getTag();
        }
        holdView.nickName.setText(commentInfo.getNickname()+"：");
        holdView.content.setText(commentInfo.getContent());
        holdView.date.setText(commentInfo.getDate());
        return convertView;
    }

    private static class HoldView {

       public TextView nickName;
       public TextView content;
       public TextView date;
    }
}
