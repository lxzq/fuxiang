package com.qcsh.fuxiang.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.bean.Home.ChildDetailEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.bean.look.GrowthTreeEntity;
import com.qcsh.fuxiang.common.CuttingBitmap;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.ui.look.GrowthTreeActivity;
import com.qcsh.fuxiang.ui.media.AudioUtils;
import com.qcsh.fuxiang.widget.MoviePlayView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wo on 15/9/23.
 */
public class HomeBabyDetailActivity extends BaseActivity {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private ImageView babayImg;
    private ImageView sexImg;
    private TextView nameText;
    private TextView birthdayText;

    private String childId;
    private User user;

    private CommonAdapter<GrowthTreeEntity> gtAdapter;
    private ListView listView;
    private ArrayList<Object> mlist;
    private LayoutInflater inflater;
    private View headView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_babydetail);
        inflater = LayoutInflater.from(this);
        headView = inflater.inflate(R.layout.home_babydetail_head, null);
        childId = getIntent().getStringExtra("childId");
        user = getCurrentUser();
        mlist = new ArrayList<Object>();
        initToolBar();
        initView();
        loadChildData();
        initData();
    }

    private void initToolBar() {
        leftBtn = (ImageButton) findViewById(R.id.action_bar_back);
        title = (TextView) findViewById(R.id.action_bar_title);
        rightBtn = (Button) findViewById(R.id.action_bar_action);
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("添加");
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChild();
            }
        });

        title.setText("宝宝名片");
    }

    private void initView() {
        babayImg = (ImageView) headView.findViewById(R.id.iv_baby);
        sexImg = (ImageView) headView.findViewById(R.id.ic_sex);
        nameText = (TextView) headView.findViewById(R.id.ic_name);
        birthdayText = (TextView) headView.findViewById(R.id.ic_birthday);
        listView = (ListView) findViewById(R.id.list_view);
        listView.addHeaderView(headView);
        initAdapter();
    }


    private void initAdapter() {

        gtAdapter = new CommonAdapter<GrowthTreeEntity>(this, mlist, R.layout.activity_growthtreeitem) {
            @Override
            public void convert(final ViewHolder holder, final GrowthTreeEntity growthTreeEntity) {
                holder.setTextColorRes(R.id.item_content, R.color.text_dark);
                TextView contentText = (TextView) holder.getView(R.id.item_content);
                contentText.setPadding(0, Utils.dip2px(HomeBabyDetailActivity.this, 20), 0, 0);
                String content = growthTreeEntity.getContent();
                if (!StringUtils.isEmpty(content)) {
                    holder.setVisible(R.id.item_content, true);
                    holder.setText(R.id.item_content, content);
                } else {
                    holder.setVisible(R.id.item_content, false);
                }
                holder.setText(R.id.gtitem_date, growthTreeEntity.getRelease_time());
                String address = growthTreeEntity.getAddress();
                String shortAddress = "";
                if (address.contains("路")) {
                    shortAddress = address.split("路")[1];
                } else if (address.contains("县")) {
                    shortAddress = address.split("县")[1];
                } else if (address.contains("区")) {
                    String a[] = address.split("区");
                    for (int i = 1; i < a.length; i++) {
                        shortAddress += a[i];
                    }
                } else {
                    shortAddress = address;
                }
                if (shortAddress.contains("(")) {
                    int index = shortAddress.indexOf("(");
                    shortAddress = shortAddress.substring(0, index);
                }
                holder.setText(R.id.gtitem_address, shortAddress);
                String tags = growthTreeEntity.getTags();
                String[] tagsArray = tags.split(",");
                String first = null;
                String mood = tagsArray[0];
                if (tagsArray.length > 1)
                    first = tagsArray[1];

                if (!StringUtils.isEmpty(mood)) {
                    holder.setVisible(R.id.gtitem_mood, true);
                    holder.setText(R.id.gtitem_mood, mood);
                } else {
                    holder.setVisible(R.id.gtitem_mood, false);
                }
                if (!StringUtils.isEmpty(first)) {
                    holder.setVisible(R.id.first_icon, true);
                    holder.setVisible(R.id.gtitem_first, true);
                    holder.setText(R.id.gtitem_first, "第一次" + first);
                } else {
                    holder.setVisible(R.id.first_icon, false);
                    holder.setVisible(R.id.gtitem_first, false);
                }

                String user = growthTreeEntity.getUser();
                if (!StringUtils.isEmpty(user)) {
                    try {
                        JSONObject userObject = new JSONObject(user);
                        int uid = userObject.getInt("user_id");
                        String unickname = userObject.getString("nickname");
                        holder.setText(R.id.gtitem_user, unickname);
                        if (uid == Integer.valueOf(getCurrentUser().id)) {
                            holder.setVisible(R.id.layout_delete, true);
                        } else {
                            holder.setVisible(R.id.layout_delete, false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                String shareType = growthTreeEntity.getType();
                holder.setVisible(R.id.gtitem_piclayout, true);
                holder.setBackgroundRes(R.id.item_content, R.color.transparent);
                String videoPath = growthTreeEntity.getVideo_url();
                final String audioPath = growthTreeEntity.getVoice_url();

                if (!StringUtils.isEmpty(videoPath)) {

                    final String path = AppConfig.FILE_SERVER + videoPath;
                    holder.setVisible(R.id.image, false);
                    holder.setVisible(R.id.type_audio_img, false);
                    holder.setVisible(R.id.videoplayer, true);
                    holder.setVisible(R.id.movie_play, false);

                    final ImageView image_video = holder.getView(R.id.image_video);
                    ImageButton playButton = holder.getView(R.id.play_button);
                    final LinearLayout movie_play = holder.getView(R.id.movie_play);
                    final FrameLayout videoPlayer = holder.getView(R.id.videoplayer);
                    image_video.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.scan_mask));

                    String images = growthTreeEntity.getImages();
                    if (!StringUtils.isEmpty(images)) {
                        try {
                            JSONArray imgsArray = new JSONArray(images);
                            int arrLength = imgsArray.length();
                            if (arrLength > 0) {
                                String imgPath = imgsArray.getJSONObject(0).getString("imageName");
                                ImageLoader.getInstance().loadImage(AppConfig.getMessageImage(imgPath), new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        image_video.setImageBitmap(loadedImage);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    playButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            movie_play.removeAllViews();
                            movie_play.setVisibility(View.VISIBLE);
                            playVideo(path, videoPlayer, movie_play);
                        }
                    });

                } else {
                    holder.setVisible(R.id.videoplayer, false);
                }

                if (!StringUtils.isEmpty(audioPath)) {
                    holder.setVisible(R.id.videoplayer, false);
                    holder.setVisible(R.id.type_audio_img, true);
                    holder.setOnClickListener(R.id.type_audio_img, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            holder.setVisible(R.id.type_audio_img, false);
                            ImageView playIng = holder.getView(R.id.audio_playing);
                            playIng.setVisibility(View.VISIBLE);
                            AnimationDrawable spinner = (AnimationDrawable) playIng.getBackground();
                            spinner.start();

                            AudioUtils mAudio = AudioUtils.getInstance();
                            setGrowthTreeAudioListener(mAudio);

                            mAudio.startPlay(AppConfig.FILE_SERVER + audioPath);
                            mAudio.setAudioStopListener(new AudioUtils.AudioStopListener() {
                                @Override
                                public void stopPlay() {

                                    holder.setVisible(R.id.type_audio_img, true);
                                    holder.setVisible(R.id.audio_playing, false);
                                }
                            });
                        }
                    });
                } else {
                    holder.setVisible(R.id.type_audio_img, false);

                }
                String images = growthTreeEntity.getImages();
                holder.setVisible(R.id.images_count, false);
                if (!StringUtils.isEmpty(images)) {
                    try {
                        JSONArray imgsArray = new JSONArray(images);
                        int arrLength = imgsArray.length();
                        if (arrLength > 0) {
                            if (AppConfig.VIDEOTYPE == Integer.valueOf(shareType)) {
                                holder.setVisible(R.id.videoplayer, true);
                                holder.setVisible(R.id.images_count, false);
                                holder.setVisible(R.id.image, false);
                            } else {
                                holder.setVisible(R.id.image, true);
                                holder.setVisible(R.id.videoplayer, false);
                                holder.setVisible(R.id.images_count, true);
                                holder.setText(R.id.images_count, arrLength + "");
                                String imgPath = imgsArray.getJSONObject(0).getString("imageName");
                                ImageLoader.getInstance().displayImage(AppConfig.getMessageImage(imgPath), (ImageView) holder.getView(R.id.image));
                            }
                        } else if (StringUtils.isEmpty(videoPath) && StringUtils.isEmpty(audioPath)) {
                            holder.setVisible(R.id.gtitem_piclayout, false);
                            holder.setBackgroundRes(R.id.item_content, R.color.green_light);
                            holder.setTextColorRes(R.id.item_content, R.color.white);
                            contentText.setPadding(Utils.dip2px(HomeBabyDetailActivity.this, 20), Utils.dip2px(HomeBabyDetailActivity.this, 20), Utils.dip2px(HomeBabyDetailActivity.this, 20), 0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                holder.setVisible(R.id.gtitem_morebtn, false);
                holder.setVisible(R.id.layout_delete, false);
            }
        };
        listView.setAdapter(gtAdapter);

    }

    private GrowthTreeActivity.GrowthTreeListener growthTreeListener;
    private GrowthTreeActivity.GrowthTreeAudioListener growthTreeAudioListener;

    private void playVideo(String path, final FrameLayout videoPlayer, final LinearLayout movie_play) {
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
    }

    private void setGrowthTreeListener(GrowthTreeActivity.GrowthTreeListener growthTreeListener) {
        this.growthTreeListener = growthTreeListener;
    }

    private void setGrowthTreeAudioListener(GrowthTreeActivity.GrowthTreeAudioListener growthTreeAudioListener) {
        this.growthTreeAudioListener = growthTreeAudioListener;
    }

    private void stopVideo() {
        if (null != growthTreeListener) {
            growthTreeListener.stop();
        }
        if (null != growthTreeAudioListener) {
            growthTreeAudioListener.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopVideo();
    }

    private void loadChildData() {
        showProgress();
        ApiClient.post(this, ApiConfig.CHILD_DETAIL, new ApiResponseHandler<ChildDetailEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                ChildDetailEntity childDetailEntity = (ChildDetailEntity) entity.data.get(0);
                setViewData(childDetailEntity);
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(HomeBabyDetailActivity.this, errorInfo.getMessage());
            }
        }, "childId", childId);
    }

    private void initData() {
        showProgress();
        ApiClient.get(this, ApiConfig.GROWTHTREE_LIST, new ApiResponseHandler<GrowthTreeEntity>() {

            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();

                List<Object> list = entity.data;
                if (null != list && list.size() > 0) {
                    mlist.addAll(list);
                }
                gtAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(HomeBabyDetailActivity.this, errorInfo.getMessage());
            }

        }, "userId", getCurrentUser().getId(), "childId", childId, "page", "1");
    }


    private void setViewData(ChildDetailEntity childDetailEntity) {

        if (!TextUtils.isEmpty(childDetailEntity.getFace()) && childDetailEntity.getFace().trim().length() > 5) {
            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_xx(childDetailEntity.getFace()), babayImg,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            if (null != loadedImage) {
                                Bitmap roundBitmap = CuttingBitmap
                                        .toRoundBitmap(loadedImage);
                                ((ImageView) view).setImageBitmap(roundBitmap);

                            }
                        }
                    });
        }

        nameText.setText(childDetailEntity.getNickname());

        String birthDay = childDetailEntity.getBirthday();
        String days = StringUtils.getAgeWithYearDay(birthDay);
        String[] mm = birthDay.substring(0, 10).split("\\-");
        String format = mm[0] + "年" + mm[1] + "月" + mm[2] + "日(" + days + ")";
        birthdayText.setText(format);
        if ("0".equals(childDetailEntity.getSex())) {
            sexImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_wojia_nv2));
        } else {
            sexImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_wojia_nan2));
        }

    }

    private void addChild() {

        String childname = null;
        String childface = null;
        try {
            JSONArray childList = new JSONArray(getCurrentUser().getChild_info());
            for (int i = 0; i < childList.length(); i++) {
                JSONObject childInfo = childList.getJSONObject(i);
                String childid = childInfo.getString("child_id");
                if (getCurrentUser().getChildId().equals(childid)) {
                    childname = childInfo.getString("nick_name");
                    childface = childInfo.getString("face");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgress();
        ApiClient.post(this, ApiConfig.ADD_CHILD_FRIEND, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                UIHelper.ToastMessage(HomeBabyDetailActivity.this, "发送加好友请求成功.");
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(HomeBabyDetailActivity.this, errorInfo.getMessage());
            }
        }, "userId", user.getId(), "childId", user.getChildId(), "friendId", childId, "nickname", childname, "face", childface);
    }
}
