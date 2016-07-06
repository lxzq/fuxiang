package com.qcsh.fuxiang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lxz on 2015/1/24.
 */
@Deprecated
public class GridViewAdapter extends DragGridBaseAdapter {
    private Context context;
    private List<String> strList;

    public GridViewAdapter(Context context, List<String> strList) {
        super(strList);
        this.context = context;
        this.strList = strList;

    }
    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if(convertView == null) {
            view = new TextView(context);
        }
        else {
            view = (TextView)convertView;
        }
        view.setText(strList.get(position));
        view.setId(position);
        return view;
    }

}
