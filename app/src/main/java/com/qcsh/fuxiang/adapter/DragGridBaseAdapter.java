package com.qcsh.fuxiang.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 拖动 GridView Adapter 适配器基类
 * Created by Administrator on 2015/9/9.
 */
public abstract class DragGridBaseAdapter extends BaseAdapter {

    private List dataList;
    private int hidePosition = AdapterView.INVALID_POSITION;

    protected DragGridBaseAdapter(List dataList){
        this.dataList = dataList;
    }
    protected abstract View getItemView(int position, View convertView, ViewGroup parent);

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = getItemView(position,convertView,parent);
        if(position != hidePosition) {
            convertView.setVisibility(View.VISIBLE);
        }
        else {
            convertView.setVisibility(View.INVISIBLE);
        }
        convertView.setId(position);
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    public void hideView(int pos) {
        hidePosition = pos;
        notifyDataSetChanged();
    }

    public void showHideView() {
        hidePosition = AdapterView.INVALID_POSITION;
        notifyDataSetChanged();
    }

    public void removeView(int pos) {
        dataList.remove(pos);
        notifyDataSetChanged();
    }

    //更新拖动时的gridView
    public void swapView(int draggedPos, int destPos) {
        //从前向后拖动，其他item依次前移
        if(draggedPos < destPos) {
            dataList.add(destPos+1, getItem(draggedPos));
            dataList.remove(draggedPos);
        }
        //从后向前拖动，其他item依次后移
        else if(draggedPos > destPos) {
            dataList.add(destPos, getItem(draggedPos));
            dataList.remove(draggedPos+1);
        }
        hidePosition = destPos;
        notifyDataSetChanged();
    }
}
