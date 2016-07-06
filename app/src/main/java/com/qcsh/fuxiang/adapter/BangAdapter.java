package com.qcsh.fuxiang.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.bean.bang.BangInfo;

import java.util.List;

/**
 * Created by Administrator on 2015/9/6.
 */
public class BangAdapter extends BaseAdapter {

    private List<Object> dataList;
    private Activity activity;
    private LayoutInflater inflater;

    public BangAdapter(List<Object> dataList,Activity activity){
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        this.dataList = dataList;
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderView holderView = null;
        BangInfo bangInfo = (BangInfo)dataList.get(position);
        if(null == convertView){
            holderView = new HolderView();
            convertView = inflater.inflate(R.layout.bang_list_item,null);
            holderView.titleV = (TextView)convertView.findViewById(R.id.title);
            holderView.hitNumV = (TextView)convertView.findViewById(R.id.hit_num);
            holderView.shareNumV = (TextView)convertView.findViewById(R.id.share_num);
            holderView.commentNum = (TextView)convertView.findViewById(R.id.comment_num);
            holderView.faceV = (ImageView)convertView.findViewById(R.id.face);
            holderView.nicknameV = (TextView)convertView.findViewById(R.id.nickname);
            holderView.dateV = (TextView)convertView.findViewById(R.id.date);

            holderView.imageView1 = (ImageView)convertView.findViewById(R.id.image_1);
            holderView.imageView2 = (ImageView)convertView.findViewById(R.id.image_2);
            holderView.imageView3 = (ImageView)convertView.findViewById(R.id.image_3);
            convertView.setTag(holderView);
        }else {
            holderView = (HolderView) convertView.getTag();
        }

       /* holderView.faceV.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.defalut_user_face));
        if(!TextUtils.isEmpty(bangInfo.getFace())){
            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(bangInfo.getFace()),holderView.faceV);
        }
        holderView.nicknameV.setText(bangInfo.getNickname());
        holderView.dateV.setText(bangInfo.getDate());
        holderView.titleV.setText(bangInfo.getTitle());

        holderView.hitNumV.setText(bangInfo.getHitNum());

        holderView.shareNumV.setText(bangInfo.getShareNum());
        holderView.commentNum.setText(bangInfo.getCommentNum());



        if(!TextUtils.isEmpty(bangInfo.getImages())){
            holderView.imageView1.setVisibility(View.INVISIBLE);
            holderView.imageView2.setVisibility(View.INVISIBLE);
            holderView.imageView3.setVisibility(View.INVISIBLE);
            String[] images = bangInfo.getImages().split(",");
            for(int i = 0 ; i < images.length ; i++){
                String image = images[i];
                if(!TextUtils.isEmpty(image)){
                    switch (i){
                        case 0 :
                            ImageLoader.getInstance().displayImage(AppConfig.getGroupLogo(image), holderView.imageView1);
                            holderView.imageView1.setVisibility(View.VISIBLE);
                            break;
                        case 1 :
                            ImageLoader.getInstance().displayImage(AppConfig.getGroupLogo(image), holderView.imageView2);
                            holderView.imageView2.setVisibility(View.VISIBLE);
                            break;
                        case 2 :
                            ImageLoader.getInstance().displayImage(AppConfig.getGroupLogo(image), holderView.imageView3);
                            holderView.imageView3.setVisibility(View.VISIBLE);
                            break;
                    }

                }
            }
        }else {
            holderView.imageView1.setVisibility(View.GONE);
            holderView.imageView2.setVisibility(View.GONE);
            holderView.imageView3.setVisibility(View.GONE);
        }


        final BangDetailInfo bangDetailInfo = new BangDetailInfo();
        bangDetailInfo.title = bangInfo.getTitle();
        bangDetailInfo.content = bangInfo.getContent();
        bangDetailInfo.date = bangInfo.getDate();
        bangDetailInfo.face = bangInfo.getFace();
        bangDetailInfo.uid = bangInfo.getUserId();
        bangDetailInfo.hitNum = bangInfo.getHitNum();
        bangDetailInfo.commentNum = bangInfo.getCommentNum();
        bangDetailInfo.images = bangInfo.getImages();
        bangDetailInfo.shareNum = bangInfo.getShareNum();
        bangDetailInfo.id = bangInfo.getId();
        bangDetailInfo.nickname = bangInfo.getNickname();
        bangDetailInfo.bbNum = bangInfo.getBbNum();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AppIntentManager.startBangDetailActivity(activity,bangDetailInfo);
            }
        });
*/
        return convertView;
    }

    private static class HolderView {
        ImageView faceV;
        TextView nicknameV;
        TextView dateV;
        TextView titleV;
        TextView hitNumV;
        TextView shareNumV;
        TextView commentNum;
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;
    }
}
