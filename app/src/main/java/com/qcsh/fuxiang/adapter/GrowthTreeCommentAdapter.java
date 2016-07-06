package com.qcsh.fuxiang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qcsh.fuxiang.R;

import java.util.List;

/**
 * Created by WWW on 2015/8/28.(使用通用的Adapter代替了)
 */
public class GrowthTreeCommentAdapter extends BaseAdapter {

    private List<Object> list;
    private LayoutInflater inflater;

    public GrowthTreeCommentAdapter(Context context, List<Object> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            convertView = inflater.inflate(R.layout.layout_commentitem, null);
            vh.user = (TextView) convertView.findViewById(R.id.comment_user);
            vh.content = (TextView) convertView.findViewById(R.id.comment_content);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private class ViewHolder {
        TextView user;
        TextView content;
    }
}
