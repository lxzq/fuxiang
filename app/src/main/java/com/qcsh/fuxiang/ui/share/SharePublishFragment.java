package com.qcsh.fuxiang.ui.share;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.qcsh.fuxiang.ui.BaseFragment;
import com.qcsh.fuxiang.ui.look.GrowthTreeActivity;
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

/**
 * Created by wo on 15/10/14.
 */
public class SharePublishFragment extends BaseFragment implements XListView.IXListViewListener {

    private View root;
    private XListView xListView;
    private CommonAdapter<GrowthTreeEntity> gtAdapter;
    private ArrayList<Object> mlist = new ArrayList<Object>();
    private int nextCursor = 1;
    private int totalPage;
    private String childBirthDay = "2010-08-25";
    private ArrayList<String> mSelectPath;
    //private String childId;
    private int visibleAnge;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        visibleAnge = getArguments().getInt("visibleAnge", 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.share_publish, container, false);
        //childId = getCurrentUser().getChildId();
        initView();
        //initData();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    private void initData() {
        showProgress();
        ApiClient.get(getActivity(), ApiConfig.SQUARE_CIRCLE, new ApiResponseHandler<GrowthTreeEntity>() {

            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                stopLoading(xListView);
                List<Object> list = entity.data;
                if (null != list && list.size() > 0) {
                    mlist.addAll(list);
                    nextCursor = entity.currentPage + 1;
                    totalPage = entity.pageCount;
                }
                gtAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                stopLoading(xListView);
                UIHelper.ToastMessage(getActivity(), errorInfo.getMessage());
            }

        }, "visibleAnge", visibleAnge + "", "childId", getCurrentUser().getChildId(), "page", nextCursor + "");
    }

    private void initView() {

        xListView = (XListView) root.findViewById(R.id.listView);
        xListView.setXListViewListener(this);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        gtAdapter = new CommonAdapter<GrowthTreeEntity>(getActivity(), mlist, R.layout.activity_growthtreeitem) {
            @Override
            public void convert(final ViewHolder holder, final GrowthTreeEntity growthTreeEntity) {
                holder.setTextColorRes(R.id.item_content, R.color.text_dark);
                TextView contentText = (TextView) holder.getView(R.id.item_content);
                contentText.setPadding(0, Utils.dip2px(getActivity(), 20), 0, 0);
                String content = growthTreeEntity.getContent();
                if (!StringUtils.isEmpty(content)) {
                    holder.setVisible(R.id.item_content, true);
                    holder.setText(R.id.item_content, content);
                } else {
                    holder.setVisible(R.id.item_content, false);
                }
                String time = growthTreeEntity.getRelease_time();
                holder.setText(R.id.gtitem_date, StringUtils.friendly_time(time)+" "+time.substring(11,16));
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
                holder.setText(R.id.gtitem_childage, shortAddress);
                holder.setVisible(R.id.gtitem_address, false);
                //holder.setText(R.id.gtitem_childage, StringUtils.getAgeWithYearDay(childBirthDay, growthTreeEntity.getRelease_time()));

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

                String childInfo = growthTreeEntity.getChildInfo();
                if (!StringUtils.isEmpty(childInfo)) {
                    try {
                        JSONObject userObject = new JSONObject(childInfo);
                        String uface = userObject.getString("face");
                        String unickname = userObject.getString("nick_name");

                        TextView tvUser = holder.getView(R.id.gtitem_user);
                        tvUser.setText(unickname);
                        Drawable face = null;
                        if (!StringUtils.isEmpty(uface)) {
                            face = ImageUtils.bitmapToDrawable(ImageLoader.getInstance().loadImageSync(AppConfig.getUserPhoto_x(uface)));
                        } else {
                            face = getResources().getDrawable(R.mipmap.ic_kanzhe_zt_normal);
                        }
                        face.setBounds(0, 0, Utils.dip2px(getActivity(),40), Utils.dip2px(getActivity(),40));
                        tvUser.setCompoundDrawables(face, null, null, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (getCurrentUser().id.equals(growthTreeEntity.getUser_id())) {
                    holder.setVisible(R.id.layout_delete, true);
                } else {
                    holder.setVisible(R.id.layout_delete, false);
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
                    //设置音频默认图片
                    ImageView imageView = holder.getView(R.id.image);
                    imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.scan_mask));
                    holder.setVisible(R.id.videoplayer, false);
                    holder.setVisible(R.id.type_audio_img, true);
                    holder.setOnClickListener(R.id.type_audio_img, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.setVisible(R.id.type_audio_img, false);
                            ImageView playIng = holder.getView(R.id.audio_playing);
                            playIng.setVisibility(View.VISIBLE);
                            AnimationDrawable spinner = (AnimationDrawable) playIng.getBackground();
                            assert spinner != null;
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
                    holder.setVisible(R.id.audio_playing, false);
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
                            contentText.setPadding(Utils.dip2px(getActivity(), 27), Utils.dip2px(getActivity(), 27), Utils.dip2px(getActivity(), 27), Utils.dip2px(getActivity(), 27));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                final View view = holder.getView(R.id.gtitem_morebtn);
                final LinearLayout commentLayout = (LinearLayout) holder.getView(R.id.layout_comment);
                holder.setOnClickListener(R.id.layout_more, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopupWindow(view, commentLayout, growthTreeEntity);
                    }
                });

                holder.setOnClickListener(R.id.layout_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MyConfirmDialog(getActivity(), "删除确认", "我们将删除该条记录",
                                new MyConfirmDialog.OnCancelDialogListener() {
                                    @Override
                                    public void onCancel() {
                                    }
                                },
                                new MyConfirmDialog.OnConfirmDialogListener() {
                                    @Override
                                    public void onConfirm() {
                                        deleteItem(growthTreeEntity.getContent_id());
                                    }
                                }
                        ).show();
                    }
                });

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppIntentManager.startGrowthTreeDetailActivity(mContext, growthTreeEntity, "2");
                    }
                });
            }
        };
        xListView.setAdapter(gtAdapter);
    }

    private void playVideo(String path, final FrameLayout videoPlayer, final LinearLayout movie_play) {
        videoPlayer.setVisibility(View.GONE);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.movie_play_view, null);
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

    private void stopVideo() {
        if (null != growthTreeListener) {
            growthTreeListener.stop();
        }

        if (null != growthTreeAudioListener) {
            growthTreeAudioListener.stop();
        }
    }

    private void deleteItem(String contentId) {
        showProgress();
        ApiClient.post(getActivity(), ApiConfig.SQUARE_CIRCLE_DELETE, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(getActivity(), errorInfo.getMessage());
                onRefresh();
            }
        }, "contentId", contentId);
    }

    private void dianZan(String relationId) {
        showProgress();
        ApiClient.post(getActivity(), ApiConfig.GROWTHTREE_ZAN, new ApiResponseHandler<ZanEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(getActivity(), errorInfo.getMessage());
            }
        }, "userId", getCurrentUser().getId(), "relationId", relationId, "type", "2");
    }

    private void submitComment(final LinearLayout layout, final EditText edit, final String contentId) {
        String s = edit.getText().toString();
        if (StringUtils.isEmpty(s)) {
            UIHelper.ToastMessage(getActivity(), "请输入评论内容！");
            return;
        }
        showProgress();
        ApiClient.post(getActivity(), ApiConfig.SS_SEND_COMMENT, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                edit.setText("");
                addComment(layout, contentId);
            }
        }, "contentId", contentId, "content", s, "userId", getCurrentUser().getId());
    }

    private void addComment(final LinearLayout layout, final String content_id) {
        if (layout != null) {
            layout.removeAllViews();
        }
        showProgress();
        ApiClient.post(getActivity(), ApiConfig.SS_COMMENT, new ApiResponseHandler<CommentEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                List<Object> list = entity.data;
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        View view = LinearLayout.inflate(getActivity(),
                                R.layout.layout_commentitem, null);
                        TextView user = (TextView) view.findViewById(R.id.comment_user);
                        TextView content = (TextView) view.findViewById(R.id.comment_content);
                        TextView date = (TextView) view.findViewById(R.id.comment_date);

                        CommentEntity gtEntity = (CommentEntity) list.get(i);
                        String comment_content = gtEntity.getComment_content();
                        StringUtils.setTextEmoji(getActivity(), content, comment_content);

                        String comment_createtime = gtEntity.getComment_time();
                        date.setText(comment_createtime);

                        user.setText(gtEntity.getUsername() + "：");

                        layout.addView(view);
                    }
                }
                View sendview = LinearLayout.inflate(getActivity(),
                        R.layout.layout_input, null);
                final EditText edit = (EditText) sendview.findViewById(R.id.editText);
                Button send = (Button) sendview.findViewById(R.id.sendBtn);
                ImageView emojiBtn = (ImageView) sendview.findViewById(R.id.emoji_btn);
                final EmojiView emojiView = (EmojiView) sendview.findViewById(R.id.emoji_view);
                emojiView.setVisibility(View.GONE);
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitComment(layout, edit, content_id);
                    }
                });
                emojiBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int visibility = emojiView.getVisibility();
                        if (visibility == View.VISIBLE) {
                            emojiView.setVisibility(View.GONE);
                        } else if (visibility == View.GONE) {
                            emojiView.setVisibility(View.VISIBLE);
                        }
                    }
                });
                emojiView.setOnItemEmojiClickListener(new EmojiView.OnItemEmojiClickListener() {
                    @Override
                    public void onItemEmojiClick(SpannableString spannableString) {
                        edit.append(spannableString);
                    }
                });
                layout.addView(sendview);

            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(getActivity(), errorInfo.getMessage());
            }
        }, "page", "1" + "", "contentId", content_id);

    }

    @Override
    public void onRefresh() {
        mlist.clear();
        stopVideo();
        nextCursor = 1;
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopVideo();
    }

    @Override
    public void onLoadMore() {
        if (nextCursor <= totalPage) {
            initData();
        } else {
            stopLoading(xListView);
        }
    }

    private void showPopupWindow(View view, final LinearLayout layout, final GrowthTreeEntity growthTreeEntity) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.gtitem_pop_window, null);
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

        // 设置按钮的点击事件
        ImageView btnZan = (ImageView) contentView.findViewById(R.id.zan_btn);
        ImageView btnShare = (ImageView) contentView.findViewById(R.id.share_btn);
        ImageView btnPunlun = (ImageView) contentView.findViewById(R.id.punlun_btn);
        btnZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dianZan(growthTreeEntity.getContent_id());
                popupWindow.dismiss();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneKeyShare oks = new OneKeyShare(getActivity());
                String images = growthTreeEntity.getImages();
                String imgPath = null;
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
                oks.setShareContent("分享", growthTreeEntity.getContent(), "", imgPath, growthTreeEntity.getVoice_url(), growthTreeEntity.getVideo_url());
                oks.addSharePlatforms();
                popupWindow.dismiss();
            }
        });
        btnPunlun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.VISIBLE);
                addComment(layout, growthTreeEntity.getContent_id());
                popupWindow.dismiss();
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(getActivity().getResources().getDrawable(
                R.color.transparent));
        popupWindow.showAsDropDown(view);
    }

    private GrowthTreeActivity.GrowthTreeAudioListener growthTreeAudioListener;
    private GrowthTreeActivity.GrowthTreeListener growthTreeListener;

    public void setGrowthTreeListener(GrowthTreeActivity.GrowthTreeListener growthTreeListener) {
        this.growthTreeListener = growthTreeListener;
    }

    public void setGrowthTreeAudioListener(GrowthTreeActivity.GrowthTreeAudioListener listener) {
        growthTreeAudioListener = listener;
    }

}
