package com.qcsh.fuxiang.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.mbaas.oss.model.OSSException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppConfig;

import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.bean.look.MessageEntity;
import com.qcsh.fuxiang.common.ImageUtils;
import com.qcsh.fuxiang.common.OssHandler;
import com.qcsh.fuxiang.common.OssUtils;
import com.qcsh.fuxiang.ui.PhotoViewActivity;
import com.qcsh.fuxiang.ui.look.MessageActivity;
import com.qcsh.fuxiang.ui.media.AudioUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2015/9/25.
 */
public class MessageAdapter extends BaseAdapter implements AudioUtils.AudioStopListener {

    public enum MESSAGE_TYPE {text,pic,video,audio}
    public enum SEND_STATUS {SUCCESS,FAIL,LOAD_ING}

    private Activity appContext;
    private ArrayList<Object> messages;
    private LayoutInflater inflater;
    private HashMap<String,Bitmap> videoPics ;
    private List<Object> emojis ;

    private String userId;
    private ViewHolder viewHolder;

    public MessageAdapter (Activity appContext,ArrayList<Object> messages,String userId,List<Object> emojis){
        this.appContext = appContext;
        this.messages = messages;
        this.userId = userId;
        this.emojis = emojis;
        inflater = LayoutInflater.from(appContext);
        videoPics = new HashMap<String,Bitmap>();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       final MessageEntity messageEntity = (MessageEntity) messages.get(position);
        ViewHolder viewHolder;
        if(null == convertView ){
            viewHolder = new ViewHolder();
            convertView = getConvertView(viewHolder);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        String user_id = messageEntity.getUser_id();
        viewHolder.left.setVisibility(View.GONE);
        viewHolder.right.setVisibility(View.GONE);

        if(userId.equals(user_id)){
            viewHolder.right.setVisibility(View.VISIBLE);

            viewHolder.faceView = viewHolder.rightFaceView;
            viewHolder.textView = viewHolder.rightTextView;
            viewHolder.msgImage = viewHolder.rightMsgImage;
            viewHolder.audioView = viewHolder.rightAudioView;
            viewHolder.videoView = viewHolder.rightVideoView;
            viewHolder.msgStatus = viewHolder.rightMsgStatus;
            viewHolder.loadView = viewHolder.rightLoadView;
            viewHolder.audioLin = viewHolder.rightAudioLin;
            viewHolder.audioAnim = viewHolder.rightAudioAnim;

        }else{
            viewHolder.left.setVisibility(View.VISIBLE);

            viewHolder.faceView = viewHolder.leftFaceView;
            viewHolder.textView = viewHolder.leftTextView;
            viewHolder.msgImage = viewHolder.leftMsgImage;
            viewHolder.audioView = viewHolder.leftAudioView;
            viewHolder.videoView = viewHolder.leftVideoView;
            viewHolder.msgStatus = viewHolder.leftMsgStatus;
            viewHolder.loadView = viewHolder.leftLoadView;
            viewHolder.audioLin = viewHolder.leftAudioLin;
            viewHolder.audioAnim = viewHolder.leftAudioAnim;
        }

        initView(messageEntity,viewHolder);
        // FAIL 失败 SUCCESS 成功  LOAD_ING 待发送
        if(SEND_STATUS.LOAD_ING.toString().equals(messageEntity.getSendStatus())){

            viewHolder.msgStatus.setVisibility(View.GONE);
            viewHolder.loadView.setVisibility(View.VISIBLE);
            AnimationDrawable spinner = (AnimationDrawable) viewHolder.loadView.getBackground();
            assert spinner != null;
            spinner.start();

            if("0".equals(messageEntity.getSend())){
                setViewData(messageEntity, viewHolder);
                Message message = handler.obtainMessage();
                message.obj = messageEntity;

                if(MESSAGE_TYPE.text.toString().equals(messageEntity.getMsgtype())){
                    Bundle bundle = new Bundle();
                    message.what = 2 ;
                    bundle.putString("path",messageEntity.getContent());
                    message.setData(bundle);
                }
                else{
                    message.what = 3 ;
                }
                handler.sendMessage(message);
                messageEntity.setSend("1");//避免重复发送数据
            }


        }else if(SEND_STATUS.FAIL.toString().equals(messageEntity.getSendStatus())){
            viewHolder.msgStatus.setVisibility(View.VISIBLE);
            viewHolder.loadView.setVisibility(View.GONE);
            viewHolder.msgStatus.setImageBitmap(BitmapFactory.decodeResource(appContext.getResources(), R.mipmap.msg_fasongshibai));
            setViewData(messageEntity,viewHolder);

        }else {
            viewHolder.loadView.setVisibility(View.GONE);
            viewHolder.msgStatus.setVisibility(View.GONE);
            setViewDataSuccess(messageEntity,viewHolder);
        }

        if (!TextUtils.isEmpty(messageEntity.getFace()) && messageEntity.getFace().trim().length() > 5 ){
            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(messageEntity.getFace()), viewHolder.faceView);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView timeView;

        ImageView faceView;
        TextView textView;
        ImageView msgImage;
        TextView audioView;
        ImageButton videoView;
        ImageView msgStatus;
        ImageView loadView;
        LinearLayout audioLin;
        ImageView audioAnim;

        LinearLayout left;
        LinearLayout right;


        ImageView leftFaceView;
        TextView leftTextView;
        ImageView leftMsgImage;
        TextView leftAudioView;
        ImageButton leftVideoView;
        ImageView leftMsgStatus;
        ImageView leftLoadView;
        LinearLayout leftAudioLin;
        ImageView leftAudioAnim;


        ImageView rightFaceView;
        TextView rightTextView;
        ImageView rightMsgImage;
        TextView rightAudioView;
        ImageButton rightVideoView;
        ImageView rightMsgStatus;
        ImageView rightLoadView;
        LinearLayout rightAudioLin;
        ImageView rightAudioAnim;

    }

    private View getConvertView(ViewHolder viewHolder){
        View view = inflater.inflate(R.layout.message_item_text,null);
        viewHolder.left = (LinearLayout) view.findViewById(R.id.left);
        viewHolder.right = (LinearLayout) view.findViewById(R.id.right);
        viewHolder.timeView = (TextView)view.findViewById(R.id.time);


        viewHolder.leftFaceView = (ImageView)view.findViewById(R.id.face);
        viewHolder.leftTextView = (TextView)view.findViewById(R.id.msg_text);
        viewHolder.leftMsgImage = (ImageView)view.findViewById(R.id.msg_pic);
        viewHolder.leftVideoView = (ImageButton)view.findViewById(R.id.msg_video);
        viewHolder.leftAudioView = (TextView)view.findViewById(R.id.msg_audio);
        viewHolder.leftMsgStatus = (ImageView)view.findViewById(R.id.status);
        viewHolder.leftLoadView = (ImageView)view.findViewById(R.id.spinnerImageView);
        viewHolder.leftAudioLin = (LinearLayout) view.findViewById(R.id.left_msg_audio_anim_lin);
        viewHolder.leftAudioAnim = (ImageView)view.findViewById(R.id.left_msg_audio_anim);

        viewHolder.rightFaceView = (ImageView)view.findViewById(R.id.right_face);
        viewHolder.rightTextView = (TextView)view.findViewById(R.id.right_msg_text);
        viewHolder.rightMsgImage = (ImageView)view.findViewById(R.id.right_msg_pic);
        viewHolder.rightVideoView = (ImageButton)view.findViewById(R.id.right_msg_video);
        viewHolder.rightAudioView = (TextView)view.findViewById(R.id.right_msg_audio);
        viewHolder.rightMsgStatus = (ImageView)view.findViewById(R.id.right_status);
        viewHolder.rightLoadView = (ImageView)view.findViewById(R.id.right_spinnerImageView);
        viewHolder.rightAudioLin = (LinearLayout) view.findViewById(R.id.right_msg_audio_anim_lin);
        viewHolder.rightAudioAnim = (ImageView)view.findViewById(R.id.right_msg_audio_anim);
        return view;
    }


    private void initView(MessageEntity messageEntity,ViewHolder viewHolder){

        // 1文字 2图片 3视频 4语音
        if("1".equals(messageEntity.getMsgtype())){
            messageEntity.setMsgtype(MESSAGE_TYPE.text.toString());
        }else if("2".equals(messageEntity.getMsgtype())){
            messageEntity.setMsgtype(MESSAGE_TYPE.pic.toString());
        }else if("3".equals(messageEntity.getMsgtype())){
            messageEntity.setMsgtype(MESSAGE_TYPE.video.toString());
        }else if("4".equals(messageEntity.getMsgtype())){
            messageEntity.setMsgtype(MESSAGE_TYPE.audio.toString());
        }

        viewHolder.textView.setVisibility(View.GONE);
        viewHolder.msgImage.setVisibility(View.GONE);
        viewHolder.videoView.setVisibility(View.GONE);
        viewHolder.audioView.setVisibility(View.GONE);
        viewHolder.audioLin.setVisibility(View.GONE);


        viewHolder.timeView.setText(messageEntity.getCreatetime());

        if(MESSAGE_TYPE.text.toString().equals(messageEntity.getMsgtype())) {
            viewHolder.textView.setVisibility(View.VISIBLE);
        }else if(MESSAGE_TYPE.pic.toString().equals(messageEntity.getMsgtype())){
            viewHolder.msgImage.setVisibility(View.VISIBLE);
        }else if((MESSAGE_TYPE.audio.toString().equals(messageEntity.getMsgtype()))){
            viewHolder.audioView.setVisibility(View.VISIBLE);
        }else if((MESSAGE_TYPE.video.toString().equals(messageEntity.getMsgtype()))){
            viewHolder.videoView.setVisibility(View.VISIBLE);
            viewHolder.msgImage.setVisibility(View.VISIBLE);
        }

    }


    private void setViewData(MessageEntity messageEntity ,ViewHolder viewHolder){

        if(MESSAGE_TYPE.text.toString().equals(messageEntity.getMsgtype())) {
            setTextEmoji(viewHolder.textView,messageEntity.getContent());
        }
        else if(MESSAGE_TYPE.pic.toString().equals(messageEntity.getMsgtype())){

            Bitmap bitmap = ImageUtils.getSmallBitmap(messageEntity.getContent());
            viewHolder.msgImage.setImageBitmap(bitmap);

        }else if((MESSAGE_TYPE.audio.toString().equals(messageEntity.getMsgtype()))){
            viewHolder.audioView.setText(messageEntity.getmTimeMill()+"''");
        }else if((MESSAGE_TYPE.video.toString().equals(messageEntity.getMsgtype()))){

            String path = messageEntity.getContent();
            Bitmap bitmap = null;
            if(!videoPics.containsKey(path)){
                bitmap = getVideoThumbnail(path);
                videoPics.put(path,bitmap);
            }else{
                bitmap = videoPics.get(path);
            }
            viewHolder.msgImage.setImageBitmap(bitmap);
        }
    }

    private void setViewDataSuccess(final MessageEntity messageEntity ,final ViewHolder viewHolder){
        if(MESSAGE_TYPE.text.toString().equals(messageEntity.getMsgtype())) {
            setTextEmoji(viewHolder.textView,messageEntity.getContent());
        }
        else if(MESSAGE_TYPE.pic.toString().equals(messageEntity.getMsgtype())){

            ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(messageEntity.getContent()), viewHolder.msgImage);
            viewHolder.msgImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> imagesList = new ArrayList<String>();
                    imagesList.add(messageEntity.getContent());
                    AppIntentManager.startPhotoViewActivity(appContext, messageEntity.getContent(), imagesList, PhotoViewActivity.HTTPIMG);
                }
            });

        }else if((MESSAGE_TYPE.audio.toString().equals(messageEntity.getMsgtype()))){

            String[] paths = messageEntity.getContent().split("\\|");
            final String audioPath = paths[0];
            String mll = paths[1];//录音时长
            viewHolder.audioView.setText(mll+"''");

            viewHolder.audioView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    viewHolder.audioView.setVisibility(View.GONE);
                    viewHolder.audioLin.setVisibility(View.VISIBLE);
                    AnimationDrawable spinner = (AnimationDrawable) viewHolder.audioAnim.getBackground();
                    assert spinner != null;
                    spinner.start();

                    //播放音频
                    MessageAdapter.this.viewHolder = viewHolder;
                    AudioUtils audioUtils = AudioUtils.getInstance();
                    audioUtils.startPlay(AppConfig.FILE_SERVER + audioPath);

                    audioUtils.setAudioStopListener(MessageAdapter.this);
                }
            });

        }else if((MESSAGE_TYPE.video.toString().equals(messageEntity.getMsgtype()))){

            String path = messageEntity.getContent();
            String[] strings = path.split("\\|");
            for(int i = 0 ; i < strings.length ; i++){

                final String paths = strings[i];
                if(!TextUtils.isEmpty(paths) && paths.contains("images")){
                    ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(paths), viewHolder.msgImage);
                }else if(!TextUtils.isEmpty(paths) && paths.contains("videos")){
                    viewHolder.videoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //播放视频
                            AppIntentManager.startSysVideoPlay(appContext, paths, AppConfig.VIDEO.HTTP_VIDEO);
                        }
                    });
                }
            }
        }
    }


    private void uploadFile(final MessageEntity entity){
        String path = entity.getContent();
        String type = "";
        if(MESSAGE_TYPE.pic.toString().equals(entity.getMsgtype())){
            type = AppConfig.OSS_UPLOAD.images.toString();
        }else if(MESSAGE_TYPE.video.toString().equals(entity.getMsgtype())){
            ArrayList<String> paths = new ArrayList<String>();
            paths.add(path);
           String fileName = System.currentTimeMillis()+".png";
            Bitmap bitmap = getVideoThumbnail(path);
            try{
                ImageUtils.saveImage(appContext,fileName,bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
            paths.add("/data/data/com.qcsh.fuxiang/files/" + fileName);
            upLoadVideo(paths, entity);
            return;

        }else if(MESSAGE_TYPE.audio.toString().equals(entity.getMsgtype())){
            type = AppConfig.OSS_UPLOAD.audios.toString();
        }

        OssUtils.upload(path, type, new OssHandler() {
            @Override
            public void onSuccess(String strPath) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                if(MESSAGE_TYPE.audio.toString().equals(entity.getMsgtype())){
                    bundle.putString("path",strPath+"|"+entity.getmTimeMill());
                }else{
                    bundle.putString("path",strPath);
                }
                message.what = 2 ;
                message.obj = entity;
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(String strPath, OSSException ossException) {

                super.onFailure(strPath, ossException);
            }
        });
    }

    private int index;
    private void upLoadVideo(ArrayList<String> paths,final MessageEntity entity){
        final StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0 ; i < paths.size() ; i++){
            String path = paths.get(i);
            String type = AppConfig.OSS_UPLOAD.videos.toString();

            if(i == 1) type = AppConfig.OSS_UPLOAD.images.toString();

            OssUtils.upload(path, type, new OssHandler() {
                @Override
                public void onSuccess(String strPath) {
                    stringBuffer.append(strPath);
                    stringBuffer.append("|");
                    index++;
                    if(index == 2){
                        index = 0;
                        Message message = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("path",stringBuffer.toString());
                        message.what = 2 ;
                        message.obj = entity;
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(String strPath, OSSException ossException) {

                    super.onFailure(strPath, ossException);
                }
            });
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 2 ://发送数据到后台
                    MessageEntity entity = (MessageEntity)msg.obj;
                    Bundle data = msg.getData();
                    sendData(entity,data.getString("path"));
                    break;
                case 3://上传图片 视频 音频
                    MessageEntity entity2 = (MessageEntity)msg.obj;
                    uploadFile(entity2);
                    break;
                default://刷新
                    notifyDataSetChanged();

                    break;
            }
        }
    };

    private void sendData(final MessageEntity entity,final String path){
        //  1文字 2图片 3视频 4语音
        String type = "1";
        if(MESSAGE_TYPE.pic.toString().equals(entity.getMsgtype())){
            type = "2";
        }else if(MESSAGE_TYPE.video.toString().equals(entity.getMsgtype())){
            type = "3";
        }else if(MESSAGE_TYPE.audio.toString().equals(entity.getMsgtype())){
            type = "4";
        }

        ApiClient.post(appContext, ApiConfig.SEND_MESSAGE, new ApiResponseHandler<BaseEntity>() {

            @Override
            public void onSuccess(DataEntity dataEntity) {
                Message message = handler.obtainMessage();
                entity.setSendStatus(SEND_STATUS.SUCCESS.toString());
                entity.setContent(path);
                message.what = 1 ;
                message.obj = entity;
                handler.sendMessage(message);
            }
            @Override
            public void onFailure(ErrorEntity errorInfo) {
                Message message = handler.obtainMessage();
                entity.setSendStatus(SEND_STATUS.FAIL.toString());
                message.what = 0 ;
                message.obj = entity;
                handler.sendMessage(message);
            }
        },"userId",userId,"content",path,"msgType",type);
    }


    /**
     * 获取本地视频缩略图
     * @param filePath
     * @return
     */
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    /**
     * 获取网络视频缩略图
     * @param url
     * @param width
     * @param height
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    public void setTextEmoji(TextView textView,String content){
        textView.setText("");
        if(content.indexOf("|") != -1){
            String[] strings = content.split("\\|");
            for(String id : strings){
                if(TextUtils.isEmpty(id))continue;
                boolean isBreak = true;
                for(int i = 0 ; i < emojis.size() ; i++){
                    MessageActivity.EmojiObject emojiObject = (MessageActivity.EmojiObject) emojis.get(i);
                    if(emojiObject.id.equals(id)){
                        ImageSpan imageSpan=new ImageSpan(appContext, emojiObject.bitmap);
                        SpannableString spannableString=new SpannableString("emoji");   //“emoji”是图片名称的前缀
                        spannableString.setSpan(imageSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textView.append(spannableString);
                        isBreak = false;
                        break;
                    }
                }
                if(isBreak)textView.append(id);
            }
        }else{
            textView.setText(content);
        }
    }

    @Override
    public void stopPlay() {
        viewHolder.audioView.setVisibility(View.VISIBLE);
        viewHolder.audioLin.setVisibility(View.GONE);
    }
}
