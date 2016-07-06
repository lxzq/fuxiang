package com.qcsh.fuxiang.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.widget.MoviePlayView;

import java.util.List;

/**
 * Created by WWW on 2015/8/27.(使用通用的Adapter代替了)
 */
public class GrowthTreeAdapter extends BaseAdapter {

    private List<Object> list;
    private Context mContext;
    private LayoutInflater inflater;
    private static final int TYPE_Text = 0;//心情类型
    private static final int TYPE_PICTURE = 1;//图片类型
    private static final int TYPE_AUDIO = 2;//语音类型
    private static final int TYPE_VIDEO = 3;//视频类型

    public GrowthTreeAdapter(Context mContext, List<Object> list) {
        this.list = list;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
//        return list.size();
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
            convertView = inflater.inflate(R.layout.activity_growthtreeitem, null);
            //vh.typeImgSmall = (ImageView) convertView.findViewById(R.id.type_img);
            // vh.feelingText = (TextView) convertView.findViewById(R.id.feeling_text);
            //vh.timeText = (TextView) convertView.findViewById(R.id.gtitem_time);
            vh.userText = (TextView) convertView.findViewById(R.id.gtitem_user);
            vh.dateText = (TextView) convertView.findViewById(R.id.gtitem_date);
            vh.ageText = (TextView) convertView.findViewById(R.id.gtitem_childage);
            vh.picFrameLayout = (FrameLayout) convertView.findViewById(R.id.gtitem_piclayout);
            //vh.typeImg = (ImageView) convertView.findViewById(R.id.type_content_img);
            //vh.contentText = (TextView) convertView.findViewById(R.id.gtitem_content);
            //vh.likeText = (TextView) convertView.findViewById(R.id.gtitem_likecount);
            vh.addrexxText = (TextView) convertView.findViewById(R.id.gtitem_address);
//            vh.morebtn = (TextView) convertView.findViewById(R.id.gtitem_morebtn);
            // vh.commentText = (TextView) convertView.findViewById(R.id.gtitem_commentcount);
            vh.commentLayout = (LinearLayout) convertView.findViewById(R.id.layout_comment);
            //vh.playerl = (MoviePlayView) convertView.findViewById(R.id.movie_player);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final TextView morebtn = vh.morebtn;
        morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(morebtn);
            }
        });
        //GrowthTreeEntity entity = (GrowthTreeEntity) list.get(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AppIntentManager.startGrowthTreeDetailActivity(mContext,"http://218.244.129.92:8080/244.mp4");

            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView typeImgSmall;
        TextView feelingText;
        TextView timeText;
        TextView userText;
        TextView dateText;
        TextView ageText;
        FrameLayout picFrameLayout;
        ImageView typeImg;
        TextView contentText;
        TextView likeText;
        TextView addrexxText;
        TextView morebtn;
        TextView commentText;
        LinearLayout commentLayout;
        MoviePlayView playerl;
    }

    private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.gtitem_pop_window, null);
        // 设置按钮的点击事件
        //ImageView btnDownload = (ImageView) contentView.findViewById(R.id.download_btn);
        //ImageView btnShare = (ImageView) contentView.findViewById(R.id.share_btn);
        //ImageView btnDelete = (ImageView) contentView.findViewById(R.id.delete_btn);
//        btnDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        btnShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OneKeyShare oks = new OneKeyShare(mContext);
//                oks.setShareContent();
//                oks.addSharePlatforms();
//            }
//        });
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(
                R.color.white));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);

    }

}
