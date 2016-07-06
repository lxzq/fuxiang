package com.qcsh.fuxiang.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.DateTimePicker;
import com.qcsh.fuxiang.widget.MyConfirmDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by wo on 15/9/17.
 */
public class HomeBabyInfoActivity extends BaseActivity {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private ImageView babyLayout;
    private ImageView babyImage;

    private LinearLayout nameLayout;
    private TextView nameText;
    private TextView nameEditText;
    private TextView birthdayText;
    private TextView birthdayEditText;
    private TextView sexText;
    private Spinner sexEditSpinner;

    private static final String[] sexs = {"男","女"};
    private ArrayAdapter<String> adapter;
    private String years;
    private int months;
    private String days;
    private View  dateView;
    private TextView titleText;

    private LinearLayout codeLayout;
    private LinearLayout jiyuLayout;
    private TextView jiyuTex;
    private LinearLayout mainLayout;
    private ScrollView scrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_baby);
        initToolBar();
        initView();
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
        rightBtn.setVisibility(View.INVISIBLE);
        title.setText("张星星的详细信息");
    }

    private void initView() {

        babyLayout = (ImageView)findViewById(R.id.layout_baby);
        babyImage = (ImageView)findViewById(R.id.iv_baby);

        nameLayout = (LinearLayout)findViewById(R.id.layout_name);
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeBabyInfoActivity.this,HomeEditNameActivity.class);
                startActivityForResult(intent, 2000);
            }
        });
        nameEditText = (TextView)findViewById(R.id.iv_name2);

        birthdayEditText = (TextView)findViewById(R.id.iv_birthday2);
        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = LayoutInflater.from(HomeBabyInfoActivity.this);
                 dateView = inflater.inflate(R.layout.datepicker_home, null);
                 titleText = (TextView)dateView.findViewById(R.id.ic_time);
                 titleText.setText(birthdayEditText.getText().toString());
                 DateTimePicker dateTimePicker = (DateTimePicker)dateView.findViewById(R.id.datetime);

                //默认生成系统当前时间
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String str=sdf.format(new Date());
                years = str.substring(0, 4);
                months=Integer.parseInt(str.substring(5,7))-1;
                days=str.substring(8, 10);

                dateTimePicker.initDateTime(DateTimePicker.ONLYDATE,years,String.valueOf(months),days,null,null);
                dateTimePicker.setOnScrollListener(new DateTimePicker.OnScrollListener() {
                    @Override
                    public void onYearScroll(int year) {

                        if (year != 0)
                        years = String.valueOf(year);
                    }

                    @Override
                    public void onMonthScroll(int month) {

                        if (month != 0)
                        months = month;
                    }

                    @Override
                    public void onDayScroll(int day) {

                        if (day != 0)
                        days = String.valueOf(day);
                    }

                    @Override
                    public void onHourScroll(int hour) {

                    }

                    @Override
                    public void onMinuteScroll(int minute) {

                    }
                });

                new AlertDialog.Builder(HomeBabyInfoActivity.this)
                        .setTitle("选择日期")
                        .setView(dateView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int value = months/10;
                                switch (value){

                                    case 0:
                                        months = months%10;
                                        break;
                                }

                                birthdayEditText.setText(years+"年"+String.valueOf(months)+"月"+days);
                                titleText.setText(years+"年"+String.valueOf(months)+"月"+days);

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();


            }
        });

        sexEditSpinner = (Spinner)findViewById(R.id.iv_sex2);
        sexEditSpinner.setPrompt("选择性别");
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sexs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexEditSpinner.setAdapter(adapter);
        sexEditSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                //修改spinner字体
                TextView tv = (TextView) view;
                tv.setTextSize(14.0f);
                tv.setTextColor(getResources().getColor(R.color.text_medium));
                tv.setGravity(Gravity.CENTER);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        codeLayout = (LinearLayout)findViewById(R.id.layout_code);
        codeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppIntentManager.startHomeCodeActivity(HomeBabyInfoActivity.this);
            }
        });

        jiyuLayout = (LinearLayout)findViewById(R.id.layout_jiyu);
        jiyuTex = (TextView)findViewById(R.id.iv_jiyu2);
        mainLayout = (LinearLayout)findViewById(R.id.layout_main);

        scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView.scrollTo(mainLayout.getTop(), mainLayout.getBottom());


    }

    //回调传值修改昵称
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if (resultCode == 1001 && requestCode == 2000){

            String name = data.getStringExtra("Name");
           if (name.length()>0)
           nameEditText.setText(name);

        }
    }


}
