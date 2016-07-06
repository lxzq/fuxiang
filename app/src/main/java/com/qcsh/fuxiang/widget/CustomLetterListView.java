package com.qcsh.fuxiang.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.qcsh.fuxiang.R;

import java.util.ArrayList;

/**
 * Created by liu on 13-10-23.
 * 自定义字母View
 */
public class CustomLetterListView extends View {
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    // 26个字母，#表示不在26个字母内的显示它
//    private String[] b = {"*", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
//            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
//            "Y", "Z", "#"};
    private int choose = -1;
    public ArrayList<String> letters = new ArrayList<String>();
    private Paint paint = new Paint();
    public CustomLetterListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public CustomLetterListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomLetterListView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取当前View的宽和高
        int height = getHeight();
        int width = getWidth();
        float singleHeight = (float) height / letters.size();
        for (int i = 0; i < letters.size(); i++) {

            paint.setColor(Color.rgb(170, 170, 170));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);// 设置抗锯齿

            Resources resources = getResources();
            assert resources != null;
            int offset_size = resources.getDimensionPixelOffset(R.dimen.letter_text_size);
            paint.setTextSize(offset_size);
            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"));// 选中时画笔设置为蓝色
            }
            // 设置绘制的位置
            float xPos = width / 2 - paint.measureText(letters.get(i)) / 2;// X轴方向居中
            float yPos = singleHeight * i + singleHeight / 2;
            canvas.drawText(letters.get(i), xPos, yPos, paint);
            paint.reset();// 恢复画笔属性到默认值
        }

    }


    @SuppressWarnings("NullableProblems")
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击位置的Y坐标
        final int oldChoose = choose;// oldChoose用于去掉同一个字母被多次点击多次回调的情况
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * letters.size());// 根据点击位置确定点击了第几个字母

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < letters.size()) {
                        listener.onTouchingLetterChanged(letters.get(c));
                        choose = c;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < letters.size()) {
                        listener.onTouchingLetterChanged(letters.get(c));
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                choose = -1;
                invalidate();
                break;
        }
        return true;
    }


    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }
}
