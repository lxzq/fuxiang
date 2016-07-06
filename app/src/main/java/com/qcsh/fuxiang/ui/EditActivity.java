package com.qcsh.fuxiang.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcsh.fuxiang.R;

/**
 * Created by WWW on 2015/9/6.
 */
public class EditActivity extends BaseActivity {
    private String titleStr;
    private int limitCount;
    private String content;
    private EditText editContent;
    private TextView tvCount;
    private TextView titleText;
    private Button rightBtn;
    private ImageButton leftBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        content = getIntent().getStringExtra("content");
        titleStr = getIntent().getStringExtra("title");
        limitCount = getIntent().getIntExtra("limitCount", 0);
        initToolBar();
        editContent = (EditText) findViewById(R.id.edit_content);
        tvCount = (TextView) findViewById(R.id.edit_tv_count);
        editContent.setText(content);
        editContent.setSelection(editContent.length()); // 将光标移动最后一个字符后面
        if (limitCount!=0) {
            editContent.setHint("最多可以输入"+limitCount+"个字");
            editContent.addTextChangedListener(mTextWatcher);
            tvCount.setVisibility(View.VISIBLE);
            setLeftCount();
        }
    }


    private void initToolBar() {
        leftBtn = (ImageButton) findViewById(R.id.action_bar_back);
        titleText = (TextView) findViewById(R.id.action_bar_title);
        rightBtn = (Button) findViewById(R.id.action_bar_action);
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (TextUtils.isEmpty(titleStr)) {
            titleText.setText("编辑");
        }else{
            titleText.setText(titleStr);
        }
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("完成");
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                des = editContent.getText().toString();
                Intent intent = new Intent(EditActivity.this, Activity.class);
                intent.putExtra("content", des);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        private int editStart;

        private int editEnd;

        public void afterTextChanged(Editable s) {
            editStart = editContent.getSelectionStart();
            editEnd = editContent.getSelectionEnd();

            // 先去掉监听器，否则会出现栈溢出
            editContent.removeTextChangedListener(mTextWatcher);

            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
            while (calculateLength(s) > limitCount) { // 当输入字符个数超过限制的大小时，进行截断操作
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
            // mEditText.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒
            editContent.setSelection(editStart);

            // 恢复监听器
            editContent.addTextChangedListener(mTextWatcher);

            setLeftCount();
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

    };
    private String des;

    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    private void setLeftCount() {
        tvCount.setText(String.valueOf((getLeftCount())));
    }

    private long getLeftCount() {
        return limitCount - calculateLength(editContent.getText());

    }
}
