package com.qcsh.fuxiang.ui.look;

import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aliyun.mbaas.oss.model.OSSException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppContext;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.bean.look.GrowthTreeEntity;
import com.qcsh.fuxiang.common.ImageUtils;
import com.qcsh.fuxiang.common.OssHandler;
import com.qcsh.fuxiang.common.OssUtils;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.ui.PhotoViewActivity;
import com.qcsh.fuxiang.ui.bang.PublishQuestionActivity;
import com.qcsh.fuxiang.widget.AudioView;
import com.qcsh.fuxiang.widget.DateTimePicker;
import com.qcsh.fuxiang.widget.NoScrollGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class SubmitActivity extends BaseActivity {

    private int submitType;
    private String videoPath;
    private String audioPath;
    private ArrayList<String> picPath = new ArrayList<String>();
    private EditText mEditText;
    private TextView recordtime;
    private TextView address;
    private String recordTime;
    private RadioButton audioBtn;
    private RadioButton moodBtn;
    private RadioButton fistBtn;
    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private ArrayList<Object> moodTagsList;
    private ArrayList<Object> firstTagsList;
    private String selectedFirstTag = "";
    private String selectedMoodTag = "";
    private NoScrollGridView picGrid;
    private PicturesGridViewAdapter picGridAdapter;
    private Bitmap videobitmap;
    private TextView max9Text;
    private LinearLayout addressLayout;
    private CommonAdapter<String> commonAdapter;
    private String location;
    private String content;
    private String tags;
    private String time;
    private String curAddress;
    private String picUrl;
    private String audioUrl;
    private String videoUrl;
    private GrowthTreeEntity gtEntity;
    private JSONArray imgArray;
    private boolean isEdit = false;
    private boolean isEditVideo = false;
    private boolean isEditAudio = false;
    private pictureGridViewEditAdapter picGridEditAdapter;
    private String submitTo = "0";//发布到 1：成长树  2：晒晒
    //private String visibleAnge = "1";//1:社区  2：广场

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        moodTagsList = new ArrayList<Object>();
        firstTagsList = new ArrayList<Object>();
        submitType = getIntent().getIntExtra("submitType", 0);
        videoPath = getIntent().getStringExtra("videoPath");
        picPath = getIntent().getStringArrayListExtra("picPath");
        gtEntity = (GrowthTreeEntity) getIntent().getSerializableExtra("gtEntity");
        submitTo = getIntent().getStringExtra("submitTo");
        /*String[] sts = st.split(",");
        if (sts.length > 1) {
            submitTo = sts[0];
            visibleAnge = sts[1];
        } else {
            submitTo = sts[0];
        }*/
        location = ((AppContext) getApplication()).getAddress();
        if (gtEntity != null) {
            isEdit = true;
            try {
                imgArray = new JSONArray(gtEntity.getImages());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!StringUtils.isEmpty(videoPath)) {
                isEditVideo = true;
            }
        }
        initToolBar();
        initView();
        showSubmitTypeWindow();
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
        rightBtn.setText("提交");
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
        title.setText(R.string.submit);
    }

    private void showSubmitTypeWindow() {
        switch (submitType) {
            case AppConfig.AUDIOTYPE:
                audioBtn.setChecked(true);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        audioBtn.callOnClick();
                    }
                }, 200);
                break;
            case AppConfig.MOODTYPE:
                moodBtn.setChecked(true);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moodBtn.callOnClick();
                    }
                }, 200);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        mEditText = (EditText) findViewById(R.id.submit_edittext);
        recordtime = (TextView) findViewById(R.id.recordtime);
        address = (TextView) findViewById(R.id.address);
        addressLayout = (LinearLayout) findViewById(R.id.address_layout);
        fistBtn = (RadioButton) findViewById(R.id.first_btn);
        audioBtn = (RadioButton) findViewById(R.id.audio_btn);
        moodBtn = (RadioButton) findViewById(R.id.mood_btn);
        picGrid = (NoScrollGridView) findViewById(R.id.pictures_gridview);
        max9Text = (TextView) findViewById(R.id.text_max9);

        addressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startEditActivity(SubmitActivity.this, null, address.getText().toString(), 0, 0);
            }
        });
        fistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(2);
            }
        });
        audioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(0);
            }
        });
        moodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(1);
            }
        });
        recordtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTime();
            }
        });

        picGridAdapter = new PicturesGridViewAdapter();

        if (isEdit) {
            if (!isEditVideo) {
                picGridEditAdapter = new pictureGridViewEditAdapter();
                picGrid.setAdapter(picGridEditAdapter);
            } else {
                audioBtn.setVisibility(View.GONE);
                max9Text.setVisibility(View.GONE);
                videobitmap = ImageUtils.getVideoThumbnail(videoPath, AppConfig.IMAGE_WIDTH, AppConfig.IMAGE_HEIGHT, MediaStore.Video.Thumbnails.MICRO_KIND);
                picGrid.setAdapter(picGridAdapter);
            }

            mEditText.setText(gtEntity.getContent());

            if (!StringUtils.isEmpty(gtEntity.getVoice_url())) {
                audioUrl = gtEntity.getVoice_url();
                isEditAudio = true;
            }

            if (!StringUtils.isEmpty(gtEntity.getVideo_url())) {
                if (!isEditVideo) {
                    videoUrl = gtEntity.getVideo_url();
                }
            }

            if (!StringUtils.isEmpty(gtEntity.getImages())) {
                try {
                    picUrl = "";
                    for (int i = 0; i < imgArray.length(); i++) {
                        JSONObject obj = imgArray.getJSONObject(i);
                        String imgurl = obj.getString("imageName");
                        picUrl = picUrl + imgurl + "|" + i + ",";
                        if (i == imgArray.length() - 1) {
                            picUrl = picUrl.substring(0, picUrl.length() - 1);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            String tagStr = gtEntity.getTags();
            String[] tagsArr = tagStr.split(",");
            if (!StringUtils.isEmpty(tagsArr[0])) {
                selectedMoodTag = tagsArr[0];
            }
            if (tagsArr.length > 1 && !StringUtils.isEmpty(tagsArr[1])) {
                selectedFirstTag = tagsArr[1];
            }

            if (!StringUtils.isEmpty(gtEntity.getRelease_time())) {
                recordtime.setText(gtEntity.getRelease_time().substring(0, 16));
            }
            if (!StringUtils.isEmpty(gtEntity.getAddress())) {
                address.setText(gtEntity.getAddress());
            }

            submitType = Integer.valueOf(gtEntity.getType());
            if (submitType == AppConfig.VIDEOTYPE) {
                audioBtn.setVisibility(View.GONE);
                max9Text.setVisibility(View.GONE);
            }

        } else {
            picGrid.setAdapter(picGridAdapter);
            File mfile;
            long time = 0;
            switch (submitType) {
                case AppConfig.MOODTYPE:
                    time = System.currentTimeMillis();
                    break;
                case AppConfig.VIDEOTYPE:
                    audioBtn.setVisibility(View.GONE);
                    max9Text.setVisibility(View.GONE);
                    videobitmap = ImageUtils.getVideoThumbnail(videoPath, AppConfig.IMAGE_WIDTH, AppConfig.IMAGE_HEIGHT, MediaStore.Video.Thumbnails.MICRO_KIND);
                    mfile = new File(videoPath);
                    time = mfile.lastModified();
                    break;
                case AppConfig.AUDIOTYPE:
                    time = System.currentTimeMillis();
                    break;
                case AppConfig.PICTURETYPE:
                    if (null != picPath && picPath.size() > 0) {
                        mfile = new File(picPath.get(0));
                        time = mfile.lastModified();
                    } else {
                        time = System.currentTimeMillis();
                    }
                    break;
            }
            recordTime = Utils.jsonTimeToString(time);
            recordtime.setText(recordTime.substring(0, 16));
            if (!TextUtils.isEmpty(location)) {
                address.setText(location);
            } else {
                address.setText("");
            }
        }

    }

    private void submitData() {
        content = mEditText.getText().toString();
        tags = selectedMoodTag + "," + selectedFirstTag;
        time = recordtime.getText().toString();
        curAddress = address.getText().toString();

        if (StringUtils.isEmpty(content)) {
            UIHelper.ToastMessage(SubmitActivity.this, "请输入内容！");
            return;
        }
        if (StringUtils.isEmpty(selectedMoodTag)) {
            UIHelper.ToastMessage(SubmitActivity.this, "请添加心情！");
            return;
        }
        switch (submitType) {
            case AppConfig.MOODTYPE:
                upLoadData();
                break;
            case AppConfig.PICTURETYPE:
                if (picPath != null && picPath.size() > 0 || isEdit) {
                    upLoadData();
                } else {
                    UIHelper.ToastMessage(SubmitActivity.this, "请选择图片！");
                }
                break;
            case AppConfig.AUDIOTYPE:
                if (!StringUtils.isEmpty(audioPath) || isEditAudio) {
                    upLoadData();
                } else {
                    UIHelper.ToastMessage(SubmitActivity.this, "请录制音频！");
                }
                break;
            case AppConfig.VIDEOTYPE:
                if (!StringUtils.isEmpty(videoPath) || isEdit) {
                    upLoadData();
                } else {
                    UIHelper.ToastMessage(SubmitActivity.this, "请录制视频！");
                }
                break;
        }
    }

    private void upLoadData() {
        showProgress();
        switch (submitType) {
            case AppConfig.MOODTYPE:
            case AppConfig.PICTURETYPE:
            case AppConfig.AUDIOTYPE:
                if (picPath != null && picPath.size() > 0) {
                    upLoadPictures();
                } else {
                    upLoadAudio();
                }
                break;
            case AppConfig.VIDEOTYPE:
                if (!StringUtils.isEmpty(videoPath)) {
                    uploadVideoPic();
                } else {
                    handler.sendEmptyMessage(0);
                }
                break;
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String contentId = null;
                    String childid = null;
                    if (gtEntity != null) {
                        contentId = gtEntity.getContent_id();
                        childid = gtEntity.getChildren_id();
                    } else {
                        childid = getCurrentUser().getChildId();
                    }
                    postData(getCurrentUser().getId(), submitType + "", content, tags, time, curAddress, picUrl, audioUrl, videoUrl, childid, contentId);
                    break;
                case 1:
                    upLoadAudio();
                    break;
                case 2:
                    upLoadVideo();
                    break;
                case 3:
                    break;
            }
        }
    };

    private void postData(String uid, String type, String content, String tags, String time, String address, String imageUrls, String voiceUrl, String videoUrl, String childId, String contentId) {
        String url = "";
        if ("1".equals(submitTo)) {
            url = ApiConfig.SEND_DATA;
        } else if ("2".equals(submitTo)) {
            url = ApiConfig.SQUARE_CIRCLE_SUBMIT;
        }
        ApiClient.post(SubmitActivity.this, url, new ApiResponseHandler<BaseEntity>() {
                    @Override
                    public void onSuccess(DataEntity entity) {
                        closeProgress();
                    }

                    @Override
                    public void onFailure(ErrorEntity errorInfo) {
                        closeProgress();
                        if ("10000".equals(errorInfo.getCode())) {
                            finish();
                        }
                        UIHelper.ToastMessage(SubmitActivity.this, errorInfo.getMessage());
                    }
                }, "userId", uid, "type", type, "content", content, "tags", tags, "time", time, "address", address,
                "imageUrls", imageUrls, "voiceUrl", voiceUrl, "videoUrl", videoUrl, "childId", childId, "contentId", contentId);
    }

    private void upLoadPictures() {
        final StringBuffer imageBuffer = new StringBuffer();
        for (int i = 0; i < picPath.size(); i++) {
            final int index = i;
            String path = picPath.get(i);
            OssUtils.upload(path, AppConfig.OSS_UPLOAD.images.toString(), new OssHandler() {
                @Override
                public void onSuccess(String strPath) {
                    imageBuffer.append(strPath + "|" + index + ",");
                    picUrl = imageBuffer.substring(0, imageBuffer.length() - 1);
                    if (picUrl.split(",").length == picPath.size()) {
                        //isPicOK = true;
                        handler.sendEmptyMessage(1);
                    }
                }

                @Override
                public void onFailure(String strPath, OSSException ossException) {
                    super.onFailure(strPath, ossException);
                    UIHelper.ToastMessage(getApplication(), ossException.getMessage());
                }
            });
        }
    }

    private void uploadVideoPic() {
        String fileName = System.currentTimeMillis() + ".png";
        Bitmap bitmap = ImageUtils.getVideoThumbnail(videoPath);
        try {
            ImageUtils.saveImage(SubmitActivity.this, fileName, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String videoThumbnailPath = "/data/data/com.qcsh.fuxiang/files/" + fileName;
        OssUtils.upload(videoThumbnailPath, AppConfig.OSS_UPLOAD.images.toString(), new OssHandler() {
            @Override
            public void onSuccess(String strPath) {
                picUrl = strPath + "|0";
                handler.sendEmptyMessage(2);
            }

            @Override
            public void onFailure(String strPath, OSSException ossException) {
                super.onFailure(strPath, ossException);
                UIHelper.ToastMessage(getApplication(), ossException.getMessage());
            }
        });
    }

    //boolean isAudioOK = false;

    private void upLoadAudio() {
        if (!StringUtils.isEmpty(audioPath)) {
            OssUtils.upload(audioPath, AppConfig.OSS_UPLOAD.audios.toString(), new OssHandler() {
                @Override
                public void onSuccess(String strPath) {
                    audioUrl = strPath;
                    //isAudioOK = true;
                    handler.sendEmptyMessage(0);
                }

                @Override
                public void onFailure(String strPath, OSSException ossException) {
                    super.onFailure(strPath, ossException);
                    UIHelper.ToastMessage(getApplication(), ossException.getMessage());
                }
            });
        } else {
            handler.sendEmptyMessage(0);
        }
    }

    //boolean isVideoOK = false;

    private void upLoadVideo() {
        if (!StringUtils.isEmpty(videoPath)) {
            OssUtils.upload(videoPath, AppConfig.OSS_UPLOAD.videos.toString(), new OssHandler() {
                @Override
                public void onSuccess(String strPath) {
                    videoUrl = strPath;
                    //isVideoOK = true;
                    handler.sendEmptyMessage(0);
                }

                @Override
                public void onFailure(String strPath, OSSException ossException) {
                    super.onFailure(strPath, ossException);
                    UIHelper.ToastMessage(getApplication(), ossException.getMessage());
                }
            });
        }
    }

    public class pictureGridViewEditAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int i = 1;
            if (imgArray != null && imgArray.length() > 0) {
                i = imgArray.length() + 1;
            }
            return i;
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_picgridview_item, null);
            ImageView img = (ImageView) convertView.findViewById(R.id.img);
            ImageView imgDel = (ImageView) convertView.findViewById(R.id.img_delete);

            imgDel.setVisibility(View.GONE);
            if (!StringUtils.isEmpty(gtEntity.getImages()) && imgArray.length() > 0) {
                if (position < imgArray.length()) {
                    try {
                        JSONObject obj = imgArray.getJSONObject(position);
                        String imgurl = obj.getString("imageName");
                        ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_xx(imgurl), img);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    img.setImageResource(R.mipmap.btn_kanzhe_jhpic);
                }
            } else {
                img.setImageResource(R.mipmap.btn_kanzhe_jhpic);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (submitType) {
                        case AppConfig.VIDEOTYPE:
                            AppIntentManager.startRecordVideoActivity(SubmitActivity.this, gtEntity, submitTo);
                            finish();
                            break;
                        default:
                            AppIntentManager.startImageSelectorForResult(SubmitActivity.this, picPath, 9, MultiImageSelectorActivity.IMAGE_FILE);
                            break;
                    }
                }
            });

            return convertView;
        }
    }


    public class PicturesGridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (picPath != null) {
                return picPath.size() + 1;
            } else {
                return 1;
            }

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_picgridview_item, null);
            ImageView img = (ImageView) convertView.findViewById(R.id.img);
            ImageView imgDel = (ImageView) convertView.findViewById(R.id.img_delete);

            if (picPath == null || position == picPath.size()) {
                imgDel.setVisibility(View.GONE);
                switch (submitType) {
                    case AppConfig.VIDEOTYPE:
                        img.setImageBitmap(videobitmap);
                        break;
                    default:
                        img.setImageResource(R.mipmap.btn_kanzhe_jhpic);
                        break;
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (submitType) {
                            case AppConfig.VIDEOTYPE:
                                AppIntentManager.startSysVideoPlay(SubmitActivity.this, videoPath, AppConfig.VIDEO.LOCAL_VIDEO);
                                break;
                            default:
                                AppIntentManager.startImageSelectorForResult(SubmitActivity.this, picPath, 9, MultiImageSelectorActivity.IMAGE_FILE);
                                break;
                        }
                    }
                });
            } else {
                imgDel.setVisibility(View.VISIBLE);
                final String path = picPath.get(position);
                Bitmap picBitmap = ImageUtils.getSmallBitmap(path);
                img.setImageBitmap(picBitmap);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        picPath.remove(path);
                        picGridAdapter.notifyDataSetChanged();
                    }
                });
                    /*img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppIntentManager.startPhotoViewActivity(SubmitActivity.this, picPath.get(position), picPath, PhotoViewActivity.LOCALIMG);
                        }
                    });*/
            }
            if (picPath != null && picPath.size() == 9 && position == 9) {
                convertView.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //选择图片后处理结果
            if (requestCode == AppConfig.REQUEST_IMAGE) {
                isEdit = false;
                picPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                picGrid.setAdapter(picGridAdapter);
            } else if (requestCode == 0) {
                String s = data.getStringExtra("content");
                address.setText(s);
            }
        }
    }

    private void showPopWindow(int type) {
        View view = LayoutInflater.from(SubmitActivity.this).inflate(R.layout.layout_submit_popwindow, null);
        FrameLayout audioLayout = (FrameLayout) view.findViewById(R.id.layout_audio);
        LinearLayout moodfirstLayout = (LinearLayout) view.findViewById(R.id.layout_moodandfirst);
        final AudioView mAudioView = (AudioView) view.findViewById(R.id.audio_view);
        final Button cancelBtn = (Button) view.findViewById(R.id.audio_cacel);
        TextView title = (TextView) view.findViewById(R.id.text_title);
        GridView mGridView = (GridView) view.findViewById(R.id.tab_grid);

        PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(SubmitActivity.this, 300), true);

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

        switch (type) {
            case 0:
                audioLayout.setVisibility(View.VISIBLE);
                moodfirstLayout.setVisibility(View.GONE);
                mAudioView.start(new AudioView.OnAudioListener() {
                    @Override
                    public void onStartRecord() {
                        if (!TextUtils.isEmpty(audioPath)) {
                            mAudioView.setIsFinishRecord(true, audioPath);
                            cancelBtn.setVisibility(View.VISIBLE);
                            mAudioView.setIconBackround(R.mipmap.btn_kanzhe_yuyin_bofang);
                        } else {
                            cancelBtn.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onRecording(int time) {

                    }

                    @Override
                    public void onRecordFinish() {
                        cancelBtn.setVisibility(View.VISIBLE);
                        audioPath = mAudioView.getAudioPath();
                    }

                    @Override
                    public void onPlaying() {

                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        audioPath = null;
                        mAudioView.cancelRecord();
                        cancelBtn.setVisibility(View.INVISIBLE);
                        mAudioView.setIconBackround(R.mipmap.btn_kanzhe_yuyin_selected);
                    }
                });
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                    @Override
                    public void onDismiss() {
                        mAudioView.stopRecord();
                        mAudioView.stopPlay();
                    }
                });
                break;
            case 1:
                audioLayout.setVisibility(View.GONE);
                moodfirstLayout.setVisibility(View.VISIBLE);
                title.setText("请选择心情标签");
                initTags();
                commonAdapter = new CommonAdapter<String>(SubmitActivity.this, moodTagsList, R.layout.look_tags) {
                    @Override
                    public void convert(final ViewHolder holder, final String tags) {
                        CheckBox checkBox = holder.getView(R.id.tags);
                        checkBox.setText(tags);
                        if (selectedMoodTag.equals(tags)) {
                            checkBox.setChecked(true);
                            holder.setBackgroundRes(R.id.tag_layout, R.drawable.tags_selected);
                        } else {
                            checkBox.setChecked(false);
                            holder.setBackgroundRes(R.id.tag_layout, R.drawable.tags_noselected);
                        }
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    selectedMoodTag = tags;
                                }
                                commonAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                };
                mGridView.setAdapter(commonAdapter);
                break;
            case 2:
                audioLayout.setVisibility(View.GONE);
                moodfirstLayout.setVisibility(View.VISIBLE);
                title.setText("请选择宝宝大事记标签");
                initTags();
                commonAdapter = new CommonAdapter<String>(SubmitActivity.this, firstTagsList, R.layout.look_tags) {
                    @Override
                    public void convert(final ViewHolder holder, final String tags) {
                        CheckBox checkBox = holder.getView(R.id.tags);
                        checkBox.setText(tags);
                        if (/*selectTags.contains(tags*/selectedFirstTag.equals(tags)) {
                            checkBox.setChecked(true);
                            holder.setBackgroundRes(R.id.tag_layout, R.drawable.tags_selected);
                        } else {
                            checkBox.setChecked(false);
                            holder.setBackgroundRes(R.id.tag_layout, R.drawable.tags_noselected);
                        }
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                //selectTags.clear();
                                if (isChecked) {
                                    // selectTags.add(tags);
                                    selectedFirstTag = tags;
                                }
                                commonAdapter.notifyDataSetChanged();
                            }
                        });


                    }
                };
                mGridView.setAdapter(commonAdapter);
                break;
        }
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white
                /*R.mipmap.ic_launcher*/));
        popupWindow.setAnimationStyle(R.style.PopupAnimationToast);
        popupWindow.showAtLocation(SubmitActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0); // 添加popwindow显示的位置
    }

//    private class MFTag {
//        public String tagName;
//        public String tagId;
//        public String tagImage;
//    }

    private void initTags() {
        moodTagsList.clear();
        firstTagsList.clear();

        moodTagsList.add("开心");
        moodTagsList.add("难过");
        moodTagsList.add("沮丧");
        moodTagsList.add("兴奋");
        moodTagsList.add("给力");
        moodTagsList.add("伤心");

        firstTagsList.add("哭");
        firstTagsList.add("喊妈妈");
        firstTagsList.add("爬");
        firstTagsList.add("走路");
        firstTagsList.add("跑");
        firstTagsList.add("坐车");
    }

    private void showDateTime() {
        View view = LayoutInflater.from(SubmitActivity.this).inflate(R.layout.layout_datetime, null);
        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        final DateTimePicker mPicker = (DateTimePicker) view.findViewById(R.id.mdatetime_picker);
        Button sureBtn = (Button) view.findViewById(R.id.surebtn);
        Button cacelBtn = (Button) view.findViewById(R.id.cacelbtn);
        String date = recordtime.getText().toString();
        String[] dt = date.split(" ");
        String[] d = dt[0].split("-");
        String[] t = dt[1].split(":");
        mPicker.initDateTime(DateTimePicker.DATATIME, d[0], d[1], d[2], t[0], t[1]);

        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String datetime = mPicker.getDateTime(DateTimePicker.DATATIME);
                recordtime.setText(datetime);
                popupWindow.dismiss();
            }
        });
        cacelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

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
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        popupWindow.setAnimationStyle(R.style.PopupAnimationToast);
        popupWindow.showAtLocation(SubmitActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0); // 添加popwindow显示的位置
    }
}
