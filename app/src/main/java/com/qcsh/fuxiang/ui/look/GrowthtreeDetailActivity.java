package com.qcsh.fuxiang.ui.look;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.qcsh.fuxiang.bean.look.ChildEntity;
import com.qcsh.fuxiang.bean.look.CollectEntity;
import com.qcsh.fuxiang.bean.look.CommentEntity;
import com.qcsh.fuxiang.bean.look.GrowthTreeEntity;
import com.qcsh.fuxiang.bean.look.ZanEntity;
import com.qcsh.fuxiang.common.ImageUtils;
import com.qcsh.fuxiang.common.OneKeyShare;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.ui.PhotoViewActivity;
import com.qcsh.fuxiang.ui.media.AudioUtils;
import com.qcsh.fuxiang.widget.EmojiView;
import com.qcsh.fuxiang.widget.MoviePlayView;
import com.qcsh.fuxiang.widget.MyConfirmDialog;
import com.qcsh.fuxiang.widget.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GrowthtreeDetailActivity extends BaseActivity implements View.OnClickListener, XListView.IXListViewListener {
    private TextView textLikeCount;
    private TextView textAddress;
    private ImageView btnDelete;
    private TextView textCommentCount;
    private XListView mListView;
    private CommonAdapter adapter;
    private List<Object> mlist = new ArrayList<Object>();
    private EditText editText;
    private Button btnSend;
    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;
    private TextView textShareCount;
    private OneKeyShare onKeyShare;
    private ImageView audioPlay;
    private ImageView audioPlayIng;
    private ImageButton rightImgBtn;
    private RelativeLayout editlayout;
    private ImageButton rightImgBtn1;
    private GrowthTreeEntity gtDetailData;
    private TextView gtdetailContent;
    private ImageView gtdetailFace;
    private TextView gtdetailUser;
    private TextView gtdetailTime;
    private ImageView image;
    private TextView imageCount;
    private FrameLayout layoutMedia;
    private TextView pinlunCount;
    private List<Object> zanList = new ArrayList<Object>();
    private TextView textZanDetail;
    private int currentPage = 1;
    private int pageCount;
    private int totalCount;

    private FrameLayout videoplayer;
    private LinearLayout movie_play;
    private LayoutInflater inflater;
    private ImageView image_video;
    private ImageButton play_button;
    private int icon60dp;
    private String flag = "0";//1:成长树详情  2：晒晒详情
    private List<Object> emojis;
    private ImageView emojiBtn;
    private EmojiView emojiView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growthtree_detail);
        icon60dp = Utils.dip2px(GrowthtreeDetailActivity.this, 60);
        emojis = new ArrayList<Object>();
        gtDetailData = (GrowthTreeEntity) getIntent().getSerializableExtra("gtEntity");
        flag = getIntent().getStringExtra("flag");
        inflater = LayoutInflater.from(this);
        initToolBar();
        initChild(gtDetailData.getChildren_id());
        initView();
        setData();
        loadZanList(gtDetailData.getContent_id());
        loadCollectList();
        loadCommentList();
    }

    private void initToolBar() {
        leftBtn = (ImageButton) findViewById(R.id.action_bar_back);
        title = (TextView) findViewById(R.id.action_bar_title);
        rightBtn = (Button) findViewById(R.id.action_bar_action);
        rightImgBtn = (ImageButton) findViewById(R.id.action_bar_image_action);
        rightImgBtn1 = (ImageButton) findViewById(R.id.action_bar_image_action1);
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightBtn.setVisibility(View.GONE);
        title.setText("张星星");
        if (getCurrentUser().getId().equals(gtDetailData.getUser_id())) {
            rightImgBtn.setVisibility(View.VISIBLE);
            rightImgBtn.setImageResource(R.mipmap.btn_kanzhe_bianji);
            rightImgBtn.setOnClickListener(this);
        }
        rightImgBtn1.setVisibility(View.VISIBLE);
        rightImgBtn1.setImageResource(R.mipmap.btn_fenxiang2);
        rightImgBtn1.setOnClickListener(this);

    }

    private void initView() {
        editText = (EditText) findViewById(R.id.editText);
        btnSend = (Button) findViewById(R.id.sendBtn);
        editlayout = (RelativeLayout) findViewById(R.id.edit_layout);
        emojiBtn = (ImageView) findViewById(R.id.emoji_btn);
        emojiView = (EmojiView) findViewById(R.id.emoji_view);
        emojiView.setVisibility(View.GONE);
        emojiView.setOnItemEmojiClickListener(new EmojiView.OnItemEmojiClickListener() {
            @Override
            public void onItemEmojiClick(SpannableString spannableString) {
                editText.append(spannableString);
            }
        });
        editlayout.setVisibility(View.GONE);

        View headView = LayoutInflater.from(GrowthtreeDetailActivity.this).inflate(R.layout.layout_gtdetail_head, null);
        gtdetailContent = (TextView) headView.findViewById(R.id.gtdetail_content);
        textLikeCount = (TextView) headView.findViewById(R.id.gtdetail_likecount);
        textZanDetail = (TextView) headView.findViewById(R.id.gtdetail_zan);
        textCommentCount = (TextView) headView.findViewById(R.id.gtdetail_commentcount);
        textShareCount = (TextView) headView.findViewById(R.id.gtdetail_sharecount);
        textAddress = (TextView) headView.findViewById(R.id.gtdetail_address);
        gtdetailUser = (TextView) headView.findViewById(R.id.gtdetail_user);
        gtdetailTime = (TextView) headView.findViewById(R.id.gtdetail_time);
        imageCount = (TextView) headView.findViewById(R.id.images_count);
        pinlunCount = (TextView) headView.findViewById(R.id.pinlun_count);
        btnDelete = (ImageView) headView.findViewById(R.id.gtdetail_morebtn);
        gtdetailFace = (ImageView) headView.findViewById(R.id.gtdetail_face);
        videoplayer = (FrameLayout) headView.findViewById(R.id.videoplayer);
        audioPlay = (ImageView) headView.findViewById(R.id.type_audio_img);
        audioPlayIng = (ImageView) headView.findViewById(R.id.audio_playing);
        image = (ImageView) headView.findViewById(R.id.image);
        layoutMedia = (FrameLayout) headView.findViewById(R.id.layout_media);

        movie_play = (LinearLayout) headView.findViewById(R.id.movie_play);
        image_video = (ImageView) headView.findViewById(R.id.image_video);
        play_button = (ImageButton) headView.findViewById(R.id.play_button);


        mListView = (XListView) findViewById(R.id.listView);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.addHeaderView(headView);
        mListView.setXListViewListener(this);

        adapter = new CommonAdapter<CommentEntity>(GrowthtreeDetailActivity.this, mlist, R.layout.layout_commentitem) {
            @Override
            public void convert(ViewHolder holder, CommentEntity comment) {

                holder.setText(R.id.comment_user, comment.getUsername() + "：");
                TextView tv = holder.getView(R.id.comment_content);
                StringUtils.setTextEmoji(GrowthtreeDetailActivity.this, tv, comment.getComment_content());
                holder.setText(R.id.comment_date, comment.getComment_time());

            }
        };
        mListView.setAdapter(adapter);
        btnDelete.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        textShareCount.setOnClickListener(this);
        audioPlay.setOnClickListener(this);
        textLikeCount.setOnClickListener(this);
        textCommentCount.setOnClickListener(this);
        emojiBtn.setOnClickListener(this);
    }

    private void setData() {
        String content = gtDetailData.getContent();
        if (!StringUtils.isEmpty(content)) {
            gtdetailContent.setText(content);
        } else {
            gtdetailContent.setText("");
        }
        String user = gtDetailData.getUser();
        if (!StringUtils.isEmpty(user)) {
            try {
                JSONObject userObject = new JSONObject(user);
                int uid = userObject.getInt("user_id");
                String unickname = userObject.getString("nickname");
                String uface = userObject.getString("userface");

                if (!StringUtils.isEmpty(unickname)) {
                    gtdetailUser.setText(unickname);
                } else {
                    gtdetailUser.setText("");
                }
                if (!StringUtils.isEmpty(uface)) {
                    ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(uface), gtdetailFace);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (getCurrentUser().id.equals(gtDetailData.getUser_id())) {
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.INVISIBLE);
        }
        String childInfo = gtDetailData.getChildInfo();
        if (!StringUtils.isEmpty(childInfo)) {
            try {
                JSONObject userObject = new JSONObject(childInfo);
                String uface = userObject.getString("face");
                String unickname = userObject.getString("nick_name");

                if (!StringUtils.isEmpty(unickname)) {
                    gtdetailUser.setText(unickname);
                } else {
                    gtdetailUser.setText("");
                }

                if (!StringUtils.isEmpty(uface)) {
                    ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(uface), gtdetailFace);
                }else{
                    gtdetailFace.setImageResource(R.mipmap.ic_kanzhe_zt_normal);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        gtdetailTime.setText(gtDetailData.getRelease_time().substring(0,16));
        String address = gtDetailData.getAddress();

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
        textAddress.setText(shortAddress);

        String shareType = gtDetailData.getType();
        String videoPath = gtDetailData.getVideo_url();
        final String audioPath = gtDetailData.getVoice_url();
        String images = gtDetailData.getImages();

        imageCount.setVisibility(View.GONE);
        videoplayer.setVisibility(View.GONE);
        image.setVisibility(View.GONE);
        audioPlay.setVisibility(View.GONE);

        if (!StringUtils.isEmpty(videoPath)) {
            final String path = AppConfig.FILE_SERVER + videoPath;
            videoplayer.setVisibility(View.VISIBLE);
            play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playVideo(path, videoplayer, movie_play);
                }
            });
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

        } else if (!StringUtils.isEmpty(audioPath)) {
            audioPlay.setVisibility(View.VISIBLE);
            //设置音频默认图片
            image.setVisibility(View.VISIBLE);
            image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.scan_mask));
            audioPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    audioPlay.setVisibility(View.GONE);
                    audioPlayIng.setVisibility(View.VISIBLE);
                    AnimationDrawable spinner = (AnimationDrawable) audioPlayIng.getBackground();
                    spinner.start();

                    AudioUtils mAudio = AudioUtils.getInstance();
                    setGrowthTreeAudioListener(mAudio);
                    mAudio.startPlay(AppConfig.FILE_SERVER + audioPath);

                    mAudio.setAudioStopListener(new AudioUtils.AudioStopListener() {
                        @Override
                        public void stopPlay() {
                            audioPlay.setVisibility(View.VISIBLE);
                            audioPlayIng.setVisibility(View.GONE);

                        }
                    });
                }
            });
        }

        if (!StringUtils.isEmpty(images)) {
            try {
                JSONArray imgsArray = new JSONArray(images);
                int arrLength = imgsArray.length();
                if (arrLength > 0) {

                    if (AppConfig.VIDEOTYPE == Integer.valueOf(shareType)) {
                        imageCount.setVisibility(View.GONE);
                        image.setVisibility(View.GONE);
                    } else {
                        imageCount.setVisibility(View.VISIBLE);
                        imageCount.setText(arrLength + "");
                        image.setVisibility(View.VISIBLE);
                        final String imgPath = imgsArray.getJSONObject(0).getString("imageName");
                        ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(imgPath), image);
                        final ArrayList<String> picList = new ArrayList<String>();
                        for (int i = 0; i < arrLength; i++) {
                            String img = imgsArray.getJSONObject(i).getString("imageName");
                            picList.add(img);
                        }
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppIntentManager.startPhotoViewActivity(GrowthtreeDetailActivity.this, imgPath, picList, PhotoViewActivity.HTTPIMG);
                            }
                        });
                    }
                } else if (StringUtils.isEmpty(videoPath) && StringUtils.isEmpty(audioPath)) {
                    layoutMedia.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        textLikeCount.setText("0");
        textZanDetail.setText("");

        textShareCount.setText("0");

        textCommentCount.setText("0");

        pinlunCount.setText("全部评论0条");

    }

    private void playVideo(String path, final FrameLayout videoPlayer, final LinearLayout movie_play) {
        videoPlayer.setVisibility(View.GONE);
        movie_play.setVisibility(View.VISIBLE);
        View view = inflater.inflate(R.layout.movie_play_view, null);
        MoviePlayView moviePlayView = (MoviePlayView) view.findViewById(R.id.video_player);
        setGrowthtreeDetailListener(moviePlayView);
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

    private void initChild(String childId) {
        showProgress();
        ApiClient.get(GrowthtreeDetailActivity.this, ApiConfig.GROWTHTREE_CHILD, new ApiResponseHandler<ChildEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                List<Object> list = entity.data;
                if (list != null && list.size() > 0) {
                    ChildEntity childInfo = (ChildEntity) list.get(0);
                    title.setText(childInfo.getNick_name());
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
            }
        }, "id", childId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_image_action:
                AppIntentManager.startSubmitActivity(GrowthtreeDetailActivity.this, null, null, Integer.valueOf(gtDetailData.getType()), gtDetailData, flag);
                finish();
                break;
            case R.id.action_bar_image_action1:
                onKeyShare = new OneKeyShare(GrowthtreeDetailActivity.this);
                String images = gtDetailData.getImages();
                String imgPath = null;
                String targetUrl = "";
                if (!StringUtils.isEmpty(images)) {
                    try {
                        JSONArray imgsArray = new JSONArray(images);
                        int arrLength = imgsArray.length();
                        if (arrLength > 0) {
                            imgPath = imgsArray.getJSONObject(0).getString("imageName");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onKeyShare.setShareContent("分享", gtDetailData.getContent(), targetUrl, imgPath, gtDetailData.getVoice_url(), gtDetailData.getVideo_url());
                if ("1".equals(flag)&&gtDetailData.getChildren_id().equals(getCurrentUser().getChildId())) {
                    onKeyShare.addCustomShareBoard(gtDetailData.getContent_id());
                } else {
                    onKeyShare.addSharePlatforms();
                }
                break;
            case R.id.gtdetail_morebtn:
                new MyConfirmDialog(this, "删除确认", "我们将删除该条记录",
                        new MyConfirmDialog.OnCancelDialogListener() {
                            @Override
                            public void onCancel() {
                            }
                        },
                        new MyConfirmDialog.OnConfirmDialogListener() {
                            @Override
                            public void onConfirm() {
                                deleteItem(gtDetailData.getContent_id());
                            }
                        }
                ).show();
                break;
            case R.id.sendBtn:
                submitComment();
                break;
            case R.id.gtdetail_sharecount:
                shouCang();
                break;
            case R.id.gtdetail_likecount:
                dianZan(gtDetailData.getContent_id());
                break;
            case R.id.gtdetail_commentcount:
                editlayout.setVisibility(View.VISIBLE);
                break;
            case R.id.type_audio_img:
                AudioUtils mAudio = AudioUtils.getInstance();
                if (!mAudio.isPlaying()) {
                    mAudio.startPlay("");
                } else {
                    mAudio.stopPlay();
                }
                break;
            case R.id.emoji_btn:
                int visibility = emojiView.getVisibility();
                if (visibility == View.GONE) {
                    emojiView.setVisibility(View.VISIBLE);
                } else {
                    emojiView.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void submitComment() {
        String content = editText.getText().toString();
        if (StringUtils.isEmpty(content)) {
            UIHelper.ToastMessage(GrowthtreeDetailActivity.this, "请输入评论内容！");
            return;
        }
        showProgress();
        String url = "";
        if ("1".equals(flag)) {
            url = ApiConfig.GROWTHTREE_SENDCOMMENT;
        } else {
            url = ApiConfig.SS_SEND_COMMENT;
        }
        ApiClient.post(GrowthtreeDetailActivity.this, url, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                //UIHelper.ToastMessage(GrowthtreeDetailActivity.this, errorInfo.getMessage());
                editText.setText("");
                if (currentPage > 2) {
                    currentPage--;
                    mlist = mlist.subList(currentPage - 1, mlist.size() - 1);
                } else {
                    currentPage = 1;
                    mlist.clear();
                }
                loadCommentList();
            }
        }, "contentId", gtDetailData.getContent_id(), "content", content, "userId", getCurrentUser().getId());
    }

    private void loadCommentList() {
        showProgress();
        String url = "";
        if ("1".equals(flag)) {
            url = ApiConfig.GET_COMMENT_LIST;
        } else {
            url = ApiConfig.SS_COMMENT;
        }
        ApiClient.post(GrowthtreeDetailActivity.this, url, new ApiResponseHandler<CommentEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                stopLoading(mListView);
                List<Object> list = entity.data;
                if (list != null && list.size() > 0) {
                    mlist.addAll(list);
                    currentPage = entity.currentPage + 1;
                    pageCount = entity.pageCount;
                    totalCount = entity.totalCount;
                    textCommentCount.setText(totalCount + "");
                    pinlunCount.setText("全部评论" + totalCount + "条");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                stopLoading(mListView);
                UIHelper.ToastMessage(GrowthtreeDetailActivity.this, errorInfo.getMessage());
            }
        }, "page", currentPage + "", "contentId", gtDetailData.getContent_id());

    }

    private void deleteItem(String contentId) {
        showProgress();
        String url = "";
        if ("1".equals(flag)) {
            url = ApiConfig.GROWTHTREE_DELETE;
        } else {
            url = ApiConfig.SQUARE_CIRCLE_DELETE;
        }
        ApiClient.post(GrowthtreeDetailActivity.this, url, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(GrowthtreeDetailActivity.this, errorInfo.getMessage());
                if (10000 == Integer.valueOf(errorInfo.getCode())) {
                    finish();
                }
            }
        }, "contentId", contentId);
    }

    private void dianZan(String relationId) {
        showProgress();
        ApiClient.post(GrowthtreeDetailActivity.this, ApiConfig.GROWTHTREE_ZAN, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(GrowthtreeDetailActivity.this, errorInfo.getMessage());
                if (10081 == Integer.valueOf(errorInfo.getCode())) {
//                    Drawable img = getResources().getDrawable(R.mipmap.btn_zan_selected);
//                    img.setBounds(0, 0, icon60dp, icon60dp);
//                    textLikeCount.setCompoundDrawables(null, img, null, null);
                } else if (10082 == Integer.valueOf(errorInfo.getCode())) {
//                    Drawable img = getResources().getDrawable(R.mipmap.btn_zan);
//                    img.setBounds(0, 0, icon60dp, icon60dp);
//                    textLikeCount.setCompoundDrawables(null, img, null, null);
                }
                loadZanList(gtDetailData.getContent_id());
            }
        }, "userId", getCurrentUser().getId(), "relationId", relationId, "type", flag);
    }

    private void loadZanList(String relationId) {
        showProgress();
        ApiClient.post(GrowthtreeDetailActivity.this, ApiConfig.GET_ZAN_LIST, new ApiResponseHandler<ZanEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                zanList = entity.data;
                boolean hasZan = false;
                if (zanList != null && zanList.size() > 0) {
                    textLikeCount.setText(zanList.size() + "");
                    StringBuffer sbf = new StringBuffer();
                    for (int i = 0; i < zanList.size(); i++) {
                        ZanEntity zanentity = (ZanEntity) zanList.get(i);
                        String name = zanentity.getUsername();
                        if (i == zanList.size() - 1) {
                            sbf.append(name + " " + zanList.size() + "人赞了一下");
                        } else {
                            sbf.append(name + "、");
                        }
                        if (getCurrentUser().getId().equals(zanentity.getUserid())) {
                            hasZan = true;
                        }
                    }
                    textZanDetail.setText(sbf);
                    if (hasZan) {
                        Drawable img = getResources().getDrawable(R.mipmap.btn_zan_selected);
                        img.setBounds(0, 0, icon60dp, icon60dp);
                        textLikeCount.setCompoundDrawables(null, img, null, null);
                    } else {
                        Drawable img = getResources().getDrawable(R.mipmap.btn_zan);
                        img.setBounds(0, 0, icon60dp, icon60dp);
                        textLikeCount.setCompoundDrawables(null, img, null, null);
                    }
                } else {
                    textLikeCount.setText("0");
                    textZanDetail.setText("");
                    Drawable img = getResources().getDrawable(R.mipmap.btn_zan);
                    img.setBounds(0, 0, icon60dp, icon60dp);
                    textLikeCount.setCompoundDrawables(null, img, null, null);
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
            }
        }, "relationId", relationId, "type", flag);
    }

    private void shouCang() {
        showProgress();
        ApiClient.post(GrowthtreeDetailActivity.this, ApiConfig.GROWTHTREE_SHOUCANG, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(GrowthtreeDetailActivity.this, errorInfo.getMessage());
                if (10081 == Integer.valueOf(errorInfo.getCode())) {
//                    Drawable img = getResources().getDrawable(R.mipmap.btn_shoucang_selected);
//                    img.setBounds(0, 0, icon60dp, icon60dp);
//                    textShareCount.setCompoundDrawables(null, img, null, null);
                } else if (10082 == Integer.valueOf(errorInfo.getCode())) {
//                    Drawable img = getResources().getDrawable(R.mipmap.btn_shoucang);
//                    img.setBounds(0, 0, icon60dp, icon60dp);
//                    textShareCount.setCompoundDrawables(null, img, null, null);
                }
                loadCollectList();
            }
        }, "objectId", gtDetailData.getContent_id(), "type", flag, "userId", getCurrentUser().getId());
    }

    private void loadCollectList() {
        showProgress();
        ApiClient.post(GrowthtreeDetailActivity.this, ApiConfig.GET_COLLECT_LIST, new ApiResponseHandler<CollectEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                List<Object> list = entity.data;
                if (list != null && list.size() > 0) {
                    textShareCount.setText((list.size() + ""));

                    boolean hasShoucang = false;
                    for (int i = 0; i < list.size(); i++) {
                        CollectEntity collectEntity = (CollectEntity) list.get(i);
                        if (getCurrentUser().getId().equals(collectEntity.getUserId())) {
                            hasShoucang = true;
                        }
                    }

                    if (hasShoucang) {
                        Drawable img = getResources().getDrawable(R.mipmap.btn_shoucang_selected);
                        img.setBounds(0, 0, icon60dp, icon60dp);
                        textShareCount.setCompoundDrawables(null, img, null, null);
                    } else {
                        Drawable img = getResources().getDrawable(R.mipmap.btn_shoucang);
                        img.setBounds(0, 0, icon60dp, icon60dp);
                        textShareCount.setCompoundDrawables(null, img, null, null);
                    }

                } else {
                    textShareCount.setText(("0"));
                    Drawable img = getResources().getDrawable(R.mipmap.btn_shoucang);
                    img.setBounds(0, 0, icon60dp, icon60dp);
                    textShareCount.setCompoundDrawables(null, img, null, null);
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(GrowthtreeDetailActivity.this, errorInfo.getMessage());
            }
        }, "objectId", gtDetailData.getContent_id(), "type", flag);
    }


    @Override
    public void onRefresh() {
        mlist.clear();
        currentPage = 1;
        loadZanList(gtDetailData.getContent_id());
        loadCollectList();
        loadCommentList();
        stopPlay();
    }

    @Override
    public void onLoadMore() {
        if (currentPage <= pageCount) {
            loadCommentList();
        } else {
            stopLoading(mListView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlay();
    }

    private void stopPlay() {
        if (null != growthtreeDetailListener) {
            growthtreeDetailListener.stop();
        }
        if (null != growthTreeAudioListener) {
            growthTreeAudioListener.stop();
        }
    }

    private GrowthTreeActivity.GrowthTreeListener growthtreeDetailListener;
    private GrowthTreeActivity.GrowthTreeAudioListener growthTreeAudioListener;

    public void setGrowthtreeDetailListener(GrowthTreeActivity.GrowthTreeListener growthtreeDetailListener) {
        this.growthtreeDetailListener = growthtreeDetailListener;
    }

    public void setGrowthTreeAudioListener(GrowthTreeActivity.GrowthTreeAudioListener growthTreeAudioListener) {
        this.growthTreeAudioListener = growthTreeAudioListener;
    }

}
