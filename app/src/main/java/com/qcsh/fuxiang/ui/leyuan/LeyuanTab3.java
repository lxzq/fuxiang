package com.qcsh.fuxiang.ui.leyuan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.bean.leyuan.LeyuanTab1Entity;
import com.qcsh.fuxiang.bean.leyuan.LeyuanTab2Entity;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseFragment;
import com.qcsh.fuxiang.widget.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/15.
 */
public class LeyuanTab3 extends BaseFragment implements XListView.IXListViewListener{

    private View root;
    private XListView xListView;

    private int pageCount = 1;
    private int currentPage = 1;

    private List<Object> list;
//    private List<Object> leyuanList;
    private CommonAdapter<LeyuanTab1Entity> commonAdapter;
    LayoutInflater inflater;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<Object>();
//        leyuanList = new ArrayList<Object>();
        user = getCurrentUser();
        initAdapter();
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        root = inflater.inflate(R.layout.layout_listview,container,false);
        xListView = (XListView) root.findViewById(R.id.listView);
        xListView.setXListViewListener(this);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
        xListView.setAdapter(commonAdapter);
//        initLeYuanData();
        return root;
    }

    private void initData(){

        list.clear();
        LeyuanTab1Entity video = new LeyuanTab1Entity();
        video.setFace("http://wx.qlogo.cn/mmopen/U8kZXZNIQiaEgKhCavpmpAPYmtD58wCG7Km4Ls4ibwbDy4ppIMQ617YlsNb8wHdKiaTl50F2icZYveDdYR6LN1LicGGibDjSYwmVFA/0");
        video.setContent("发视频玩啊");
        video.setCreateTime("2015-10-13 10:00:00");
        video.setImages("images/2015/9/1443431051586.png");
        video.setPath("/videos/2015/9/1443431036380.mp4");
        video.setNickname("刘向中");
        video.setType("3");
        list.add(video);
        list.add(video);

        LeyuanTab1Entity live = new LeyuanTab1Entity();
        live.setFace("http://wx.qlogo.cn/mmopen/U8kZXZNIQiaEgKhCavpmpAPYmtD58wCG7Km4Ls4ibwbDy4ppIMQ617YlsNb8wHdKiaTl50F2icZYveDdYR6LN1LicGGibDjSYwmVFA/0");
        live.setType("1");
        live.setNickname("刘向中");
        live.setPath("");
        live.setContent("我也开始直播啦");
        live.setCreateTime("2015-10-15 10:00:00");
        list.add(live);
        list.add(live);
        list.add(live);

        stopLoading(xListView);
        commonAdapter.notifyDataSetChanged();


       if(pageCount > 0 && currentPage > pageCount){
            //UIHelper.ToastMessage(this.getActivity(), "已经是最后记录");
            stopLoading(xListView);
            return;
        }
        ApiClient.get(this.getActivity(), ApiConfig.LE_YUAN_TAB1, new ApiResponseHandler<LeyuanTab1Entity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                stopLoading(xListView);
                List<Object> da = entity.data;
                if (null != da) {
                    pageCount = entity.pageCount;
                    currentPage++;
                    list.addAll(da);
                    commonAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                stopLoading(xListView);
                UIHelper.ToastMessage(getActivity(), errorInfo.getMessage());
            }
        }, "page", currentPage + "");
    }

   /* private void initLeYuanData(){
        leyuanList.clear();
        LeyuanTab2Entity entity = new LeyuanTab2Entity();
        entity.setId("1");
        entity.setName("儿童手绘坊");
        entity.setNotes("开儿童乐园，厂家直营儿童乐园品牌，集儿童娱乐，健身，益智为一体。开儿童乐园，首创整店输出模式，负责安装设备，全程指导");
        entity.setPic("/images/2015/9/1443490805958.png");
        entity.setPrice("¥5/小时");
        leyuanList.add(entity);
        leyuanList.add(entity);
        leyuanList.add(entity);

        addHeadView();
    }*/

/*    private void addHeadView(){
        View headView = inflater.inflate(R.layout.leyuan_tab_3,null);
        LinearLayout headList = (LinearLayout) headView.findViewById(R.id.list);

        for(int i = 0 ; i < leyuanList.size() ; i++){
            LeyuanTab2Entity entity = (LeyuanTab2Entity) leyuanList.get(i);
            View view = inflater.inflate(R.layout.leyuan_tab_3_item,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.pic);
            TextView textView = (TextView) view.findViewById(R.id.pic_name);

            String face = entity.getPic();
            if(!TextUtils.isEmpty(face) && face.trim().length() > 5 )
                ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(face), imageView);
            textView.setText(entity.getName());

            headList.addView(view);
        }
        xListView.addHeaderView(headView);
    }*/

    private void initAdapter(){
        commonAdapter = new CommonAdapter<LeyuanTab1Entity>(this.getActivity(),list,R.layout.layout_leyuan_my_item) {
            @Override
            public void convert(ViewHolder holder, LeyuanTab1Entity leyuanTab1Entity) {

                /*TextView nicknameView = holder.getView(R.id.nickname);
                ImageView faceView = holder.getView(R.id.face);
                TextView timeView = holder.getView(R.id.create_time);
                TextView contentView = holder.getView(R.id.content);

                nicknameView.setText(leyuanTab1Entity.getNickname());
                contentView.setText(leyuanTab1Entity.getContent());
                String face = leyuanTab1Entity.getFace();
                if(!TextUtils.isEmpty(face) && face.trim().length() > 5 )
                    ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(face), faceView);

                String createTime = leyuanTab1Entity.getCreateTime();
                Date date = StringUtils.toDate(createTime);
                long time = date.getTime() / 1000 ;
                createTime = Utils.getStandardDate(time);
                timeView.setText(createTime);

                initAdapterView(holder);
                String type = leyuanTab1Entity.getType();////1直播 2 图文 3 视频 4音频

                if("1".equals(type)){
                    liveView(holder,leyuanTab1Entity);
                }else if("2".equals(type)){
                    picView(holder,leyuanTab1Entity);
                }else if("3".equals(type)){
                    videoView(holder,leyuanTab1Entity);
                }*/

                holder.setOnClickListener(R.id.btn_look, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LeyuanTab2Entity entity = new LeyuanTab2Entity();
                        entity.setId("1");
                        entity.setName("儿童手绘坊");
                        entity.setNotes("开儿童乐园，厂家直营儿童乐园品牌，集儿童娱乐，健身，益智为一体。开儿童乐园，首创整店输出模式，负责安装设备，全程指导");
                        entity.setPic("/images/2015/9/1443490805958.png");
                        entity.setPrice("¥5/小时");
                        AppIntentManager.startLeyuanDetailActivity(getActivity(),entity);
                    }
                });

            }
        };
    }


   /* private void initAdapterView(ViewHolder holder){
        holder.setVisible(R.id.image,false);
        holder.setVisible(R.id.video_play,false);
        holder.setVisible(R.id.live,false);
        holder.setVisible(R.id.movie_play,false);
    }*/

/*    private void picView(ViewHolder holder, LeyuanTab1Entity leyuanTab1Entity){
        ImageView imageView = holder.getView(R.id.image);
        String images = leyuanTab1Entity.getImages();
        if(!TextUtils.isEmpty(images) && images.trim().length() > 5 )
            ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(images), imageView);
        else{
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.scan_mask);
            imageView.setImageBitmap(bitmap);
        }
        imageView.setVisibility(View.VISIBLE);
    }
    *//**
     * 直播
     *//*
    private void liveView(ViewHolder holder, LeyuanTab1Entity leyuanTab1Entity){
        ImageView imageView = holder.getView(R.id.image);
        ImageButton imageButton = holder.getView(R.id.video_play);
        TextView textView = holder.getView(R.id.live);

        imageView.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);

        String images = leyuanTab1Entity.getImages();
        if(!TextUtils.isEmpty(images) && images.trim().length() > 5 )
            ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(images), imageView);
        else{
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.scan_mask);
            imageView.setImageBitmap(bitmap);
        }
        final String path = AppConfig.FILE_SERVER + leyuanTab1Entity.getPath();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直播地址跳转
            }
        });
    }

    *//**
     * 视频
     *//*
    private void videoView(ViewHolder holder, LeyuanTab1Entity leyuanTab1Entity){
        ImageView imageView = holder.getView(R.id.image);
        ImageButton imageButton = holder.getView(R.id.video_play);
        final LinearLayout moviePlay = holder.getView(R.id.movie_play);
        final FrameLayout frameLayout = holder.getView(R.id.video_view);

        imageView.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.VISIBLE);

        final String path = AppConfig.FILE_SERVER + leyuanTab1Entity.getPath();

        ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(leyuanTab1Entity.getImages()),imageView);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moviePlay.removeAllViews();
                moviePlay.setVisibility(View.VISIBLE);
                playVideo(path,frameLayout,moviePlay);
            }
        });
    }*/

   /* @Override
    public void onPause() {
        super.onPause();
        if(null != growthTreeListener){
            growthTreeListener.stop();
        }
    }

    private GrowthTreeActivity.GrowthTreeListener growthTreeListener;
    public void setGrowthTreeListener(GrowthTreeActivity.GrowthTreeListener growthTreeListener) {
        this.growthTreeListener = growthTreeListener;
    }*/

   /* private void playVideo(String path, final FrameLayout videoPlayer, final LinearLayout movie_play) {
        videoPlayer.setVisibility(View.GONE);
        View view = inflater.inflate(R.layout.movie_play_view, null);
        MoviePlayView moviePlayView = (MoviePlayView) view.findViewById(R.id.video_player);
        setGrowthTreeListener(moviePlayView);
        moviePlayView.setPath(path);
        moviePlayView.startPlay();

        moviePlayView.setVideoPlayStopListener(new MoviePlayView.VideoPlayStopListener() {
            @Override
            public void playStop() {
                videoPlayer.setVisibility(View.VISIBLE);
                movie_play.setVisibility(View.GONE);
                movie_play.removeAllViews();
            }
        });
        movie_play.addView(view);
    }*/

    @Override
    public void onRefresh() {
        list.clear();
        currentPage = 1;
        initData();
    }

    @Override
    public void onLoadMore() {
        initData();
    }
}
