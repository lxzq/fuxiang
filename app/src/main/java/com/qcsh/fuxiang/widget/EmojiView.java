package com.qcsh.fuxiang.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WWW on 2015/10/15.
 */
public class EmojiView extends GridView {

    private List<Object> emojis;

    public EmojiView(Context context) {
        super(context);
        initView();
    }

    public EmojiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public EmojiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setNumColumns(8);
        initEmojiList();
        initEmoji();
    }


    private void initEmojiList(){
        Resources resources = getResources();
        emojis  = new ArrayList<Object>();

        EmojiObject emoji_baoquan = new EmojiObject();
        emoji_baoquan.id = "emoji_01";
        emoji_baoquan.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_baoquan);
        emojis.add(emoji_baoquan);

        EmojiObject emoji_biezui = new EmojiObject();
        emoji_biezui.id = "emoji_02";
        emoji_biezui.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_biezui);
        emojis.add(emoji_biezui);

        EmojiObject emoji_bishi = new EmojiObject();
        emoji_bishi.id = "emoji_03";
        emoji_bishi.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_bishi);
        emojis.add(emoji_bishi);

        EmojiObject emoji_chijing = new EmojiObject();
        emoji_chijing.id = "emoji_04";
        emoji_chijing.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_chijing);
        emojis.add(emoji_chijing);

        EmojiObject emoji_ciya = new EmojiObject();
        emoji_ciya.id = "emoji_05";
        emoji_ciya.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_ciya);
        emojis.add(emoji_ciya);

        EmojiObject emoji_dajing = new EmojiObject();
        emoji_dajing.id = "emoji_06";
        emoji_dajing.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_dajing);
        emojis.add(emoji_dajing);

        EmojiObject emoji_daku = new EmojiObject();
        emoji_daku.id = "emoji_07";
        emoji_daku.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_daku);
        emojis.add(emoji_daku);

        EmojiObject emoji_dengyan = new EmojiObject();
        emoji_dengyan.id = "emoji_08";
        emoji_dengyan.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_dengyan);
        emojis.add(emoji_dengyan);

        EmojiObject emoji_fahuo = new EmojiObject();
        emoji_fahuo.id = "emoji_09";
        emoji_fahuo.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_fahuo);
        emojis.add(emoji_fahuo);

        EmojiObject emoji_fanu = new EmojiObject();
        emoji_fanu.id = "emoji_10";
        emoji_fanu.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_fanu);
        emojis.add(emoji_fanu);

        EmojiObject emoji_guzhang = new EmojiObject();
        emoji_guzhang.id = "emoji_11";
        emoji_guzhang.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_guzhang);
        emojis.add(emoji_guzhang);

        EmojiObject emoji_haha = new EmojiObject();
        emoji_haha.id = "emoji_12";
        emoji_haha.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_haha);
        emojis.add(emoji_haha);

        EmojiObject emoji_haixiu = new EmojiObject();
        emoji_haixiu.id = "emoji_13";
        emoji_haixiu.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_haixiu);
        emojis.add(emoji_haixiu);

        EmojiObject emoji_han = new EmojiObject();
        emoji_han.id = "emoji_14";
        emoji_han.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_han);
        emojis.add(emoji_han);

        EmojiObject emoji_haqian = new EmojiObject();
        emoji_haqian.id = "emoji_15";
        emoji_haqian.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_haqian);
        emojis.add(emoji_haqian);

        EmojiObject emoji_kelian = new EmojiObject();
        emoji_kelian.id = "emoji_16";
        emoji_kelian.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_kelian);
        emojis.add(emoji_kelian);

        EmojiObject emoji_koubi = new EmojiObject();
        emoji_koubi.id = "emoji_17";
        emoji_koubi.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_koubi);
        emojis.add(emoji_koubi);

        EmojiObject emoji_nanguo = new EmojiObject();
        emoji_nanguo.id = "emoji_18";
        emoji_nanguo.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_nanguo);
        emojis.add(emoji_nanguo);

        EmojiObject emoji_tiaomei = new EmojiObject();
        emoji_tiaomei.id = "emoji_19";
        emoji_tiaomei.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_tiaomei);
        emojis.add(emoji_tiaomei);

        EmojiObject emoji_tiaopi = new EmojiObject();
        emoji_tiaopi.id = "emoji_20";
        emoji_tiaopi.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_tiaopi);
        emojis.add(emoji_tiaopi);

        EmojiObject emoji_touxiao = new EmojiObject();
        emoji_touxiao.id = "emoji_21";
        emoji_touxiao.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_touxiao);
        emojis.add(emoji_touxiao);

        EmojiObject emoji_wenhao = new EmojiObject();
        emoji_wenhao.id = "emoji_22";
        emoji_wenhao.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_wenhao);
        emojis.add(emoji_wenhao);

        EmojiObject emoji_woyun = new EmojiObject();
        emoji_woyun.id = "emoji_23";
        emoji_woyun.bitmap = BitmapFactory.decodeResource(resources,R.mipmap.emoji_woyun);
        emojis.add(emoji_woyun);
    }

    public class EmojiObject {
        public String id;
        public Bitmap bitmap;
    }

    /**
     * 初始化表情包
     */
    private void initEmoji(){

        CommonAdapter commonAdapter = new CommonAdapter<EmojiObject>(getContext(),emojis,R.layout.message_item_time) {
            @Override
            public void convert(ViewHolder holder, final EmojiObject o) {
//                ImageView imageView =  holder.getView(R.id.msg_bq);
//                imageView.setImageBitmap(o.bitmap);
                holder.setImageBitmap(R.id.msg_bq,o.bitmap);
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap bitmap = o.bitmap;
                        ImageSpan imageSpan = new ImageSpan(getContext(), bitmap);
                        SpannableString spannableString = new SpannableString("|" + o.id + "|");   //“emoji”是图片名称的前缀
                        spannableString.setSpan(imageSpan, 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        listener.onItemEmojiClick(spannableString);
                    }
                });
            }
        };
        setAdapter(commonAdapter);
    }

    public void setOnItemEmojiClickListener(OnItemEmojiClickListener listener) {
        this.listener = listener;
    }

    OnItemEmojiClickListener listener ;

    public interface OnItemEmojiClickListener{
        void onItemEmojiClick(SpannableString spannableString);
    }
}
