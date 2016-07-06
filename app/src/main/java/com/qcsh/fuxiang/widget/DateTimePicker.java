package com.qcsh.fuxiang.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.widget.wheelview.OnWheelScrollListener;
import com.qcsh.fuxiang.widget.wheelview.WheelView;
import com.qcsh.fuxiang.widget.wheelview.adapter.NumericWheelAdapter;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by WWW on 2015/9/17.
 */
public class DateTimePicker extends LinearLayout {
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private WheelView hour;
    private WheelView minute;
    private int mYear = 1996;
    private int mMonth = 0;
    private int mDay = 1;
    private int mHour = 12;
    private int mMinute = 0;
    //    private int type = 0;
    public static final int DATATIME = 0;
    public static final int ONLYDATE = 1;
    public static final int ONLYTIME = 2;

    private OnScrollListener onScrollListener;

    public DateTimePicker(Context context) {
        super(context);
        initDateTimePicker(context);
    }

    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDateTimePicker(context);
    }

    public DateTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDateTimePicker(context);
    }

    private void initDateTimePicker(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_datetime_picker, this);
        year = (WheelView) rootView.findViewById(R.id.year);
        month = (WheelView) rootView.findViewById(R.id.month);
        day = (WheelView) rootView.findViewById(R.id.day);
        hour = (WheelView) rootView.findViewById(R.id.hour);
        minute = (WheelView) rootView.findViewById(R.id.minute);
    }

    public void initDateTime(int type, String cy, String cm, String cd, String ch, String cmin) {
        switch (type) {
            case DATATIME:
                break;
            case ONLYDATE:
                hour.setVisibility(GONE);
                minute.setVisibility(GONE);
                break;
            case ONLYTIME:
                year.setVisibility(GONE);
                month.setVisibility(GONE);
                day.setVisibility(GONE);
                break;
        }
        Calendar c = Calendar.getInstance();
        int norYear = c.get(Calendar.YEAR);
        int calMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
        int calDay = c.get(Calendar.DAY_OF_MONTH);
        int calHour = c.get(Calendar.HOUR_OF_DAY);
        int calMinute = c.get(Calendar.MINUTE);

        if (!TextUtils.isEmpty(cy)) {
            mYear = Integer.parseInt(cy);
        } else {
            mYear = norYear;
        }
        if (!TextUtils.isEmpty(cm)) {
            mMonth = Integer.parseInt(cm);
        } else {
            mMonth = calMonth;
        }
        if (!TextUtils.isEmpty(cd)) {
            mDay = Integer.parseInt(cd);
        } else {
            mDay = calDay;
        }
        if (!TextUtils.isEmpty(ch)) {
            mHour = Integer.parseInt(ch);
        } else {
            mHour = calHour;
        }
        if (!TextUtils.isEmpty(cmin)) {
            mMinute = Integer.parseInt(cmin);
        } else {
            mMinute = calMinute;
        }

        int curYear = mYear;
        int curMonth = mMonth;
        int curDay = mDay;
        int curHour = mHour;
        int curMinute = mMinute;

        NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(getContext(), 1950, norYear);
        numericWheelAdapter1.setLabel("年");
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(false);//是否可循环滑动
        year.addScrollingListener(scrollListener);

        NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(getContext(), 1, 12, "%02d");
        numericWheelAdapter2.setLabel("月");
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(false);
        month.addScrollingListener(scrollListener);

        initDay(curYear, curMonth);
        day.setCyclic(false);
        day.addScrollingListener(scrollListener);

        NumericWheelAdapter numericWheelAdapter4 = new NumericWheelAdapter(getContext(), 0, 23, "%02d");
        numericWheelAdapter4.setLabel("时");
        hour.setViewAdapter(numericWheelAdapter4);
        hour.setCyclic(false);
        hour.addScrollingListener(scrollListener);

        NumericWheelAdapter numericWheelAdapter5 = new NumericWheelAdapter(getContext(), 0, 59, "%02d");
        numericWheelAdapter5.setLabel("分");
        minute.setViewAdapter(numericWheelAdapter5);
        minute.setCyclic(false);
        minute.addScrollingListener(scrollListener);

        year.setVisibleItems(7);//设置显示行数
        month.setVisibleItems(7);
        day.setVisibleItems(7);
        hour.setVisibleItems(7);
        minute.setVisibleItems(7);

        year.setCurrentItem(curYear - 1950);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDay - 1);
        hour.setCurrentItem(curHour);
        minute.setCurrentItem(curMinute);
    }

    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(getContext(), 1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel("日");
        day.setViewAdapter(numericWheelAdapter);
    }

    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

    public String getDateTime(int type) {
        int y = year.getCurrentItem() + 1950;
        Serializable m = (month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1);
        Serializable d = (day.getCurrentItem() + 1) < 10 ? "0" + (day.getCurrentItem() + 1) : (day.getCurrentItem() + 1);
        Serializable h = hour.getCurrentItem() < 10 ? "0" + hour.getCurrentItem() : hour.getCurrentItem();
        Serializable min = minute.getCurrentItem() < 10 ? "0" + minute.getCurrentItem() : minute.getCurrentItem();
        String dt = "";
        switch (type) {
            case DATATIME:
                dt = new StringBuilder().append(y).append("-").append(m).append("-").append(d).append(" ").append(h).append(":").append(min).toString();
                break;
            case ONLYDATE:
                dt = new StringBuilder().append(y).append("-").append(m).append("-").append(d).toString();
                break;
            case ONLYTIME:
                dt = new StringBuilder().append(h).append(":").append(min).toString();
                break;
        }
        return dt;
    }

    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = year.getCurrentItem() + 1950;//年
            int n_month = month.getCurrentItem() + 1;//月
            initDay(n_year, n_month);
            int n_day = day.getCurrentItem() + 1;
            int n_hour = hour.getCurrentItem();
            int n_minute = minute.getCurrentItem();
            if (onScrollListener != null) {
                switch (wheel.getId()) {
                    case R.id.year:
                        onScrollListener.onYearScroll(n_year);
                        break;
                    case R.id.month:
                        onScrollListener.onMonthScroll(n_month);
                        break;
                    case R.id.day:
                        onScrollListener.onDayScroll(n_day);
                        break;
                    case R.id.hour:
                        onScrollListener.onHourScroll(n_hour);
                        break;
                    case R.id.minute:
                        onScrollListener.onMinuteScroll(n_minute);
                        break;
                }
            }
        }
    };

    public void setOnScrollListener(final OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /**
     * 根据月日计算星座
     *
     * @param month
     * @param day
     * @return
     */
    public String getAstro(int month, int day) {
        String[] astro = new String[]{"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座",
                "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};
        int[] arr = new int[]{20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};// 两个星座分割日
        int index = month;
        // 所查询日期在分割日之前，索引-1，否则不变
        if (day < arr[month - 1]) {
            index = index - 1;
        }
        // 返回索引指向的星座string
        return astro[index];
    }

    /**
     * 计算某人的生日到当前一共有几个闰年，此方法是为了更精确考虑闰年，从而计算出生日。   
     */
    public static int getLeapYearCount(String birthday, String curYear) {
        int leapYear = 0;
        if (!StringUtils.isEmpty(birthday)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Date date = new Date();
            int birthYear = Integer.parseInt(birthday.substring(0, 4));//获取出生日期，解析为Date类型
            int currYear = 0;
            if (StringUtils.isEmpty(curYear)) {
                currYear = Integer.parseInt(sdf.format(date)); //获取当前日期
            } else {
                currYear = Integer.parseInt(curYear.substring(0, 4));
            }
            for (int year = birthYear; year <= currYear; year++) {
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    leapYear++;//从出生年到当前年，只有是闰年就+1
                }
            }
        }

        return leapYear;
    }

    /**
     * 根据日期计算年龄
     *
     * @param birthday
     * @return
     */
    public static final String calculateDatePoor(String birthday) {
        try {
            if (TextUtils.isEmpty(birthday))
                return "0";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthdayDate = sdf.parse(birthday);
            String currTimeStr = sdf.format(new Date());
            Date currDate = sdf.parse(currTimeStr);
            if (birthdayDate.getTime() > currDate.getTime()) {
                return "0";
            }
            int leapYear = getLeapYearCount(birthday, null);
            long age = (currDate.getTime() - birthdayDate.getTime())
                    / (24 * 60 * 60 * 1000) + 1;
            String year = new DecimalFormat("0.00").format((age - leapYear) / 365f);
            if (TextUtils.isEmpty(year))
                return "0";
            return String.valueOf(new Double(year).intValue());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0";
    }

    /**
     * 根据日期计算年龄
     *
     * @param birthday
     * @return
     */
    public static final String calculateDatePoor(String birthday, String date) {
        try {
            if (TextUtils.isEmpty(birthday))
                return "0";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthdayDate = sdf.parse(birthday);
            Date currDate = sdf.parse(date);
            if (birthdayDate.getTime() > currDate.getTime()) {
                return "0";
            }
            int leapYear = getLeapYearCount(birthday, date);
            long age = (currDate.getTime() - birthdayDate.getTime())
                    / (24 * 60 * 60 * 1000) + 1;
            String year = new DecimalFormat("0.00").format((age - leapYear) / 365f);
            String day = new DecimalFormat("0.00").format((age - leapYear) % 365f + leapYear);
            if (TextUtils.isEmpty(year))
                return "0";
            return String.valueOf(new Double(year).intValue()) + "岁" + String.valueOf(new Double(day).intValue()) + "天";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0";
    }


    /**
     * 滚动回调接口
     */
    public interface OnScrollListener {
        void onYearScroll(int year);

        void onMonthScroll(int month);

        void onDayScroll(int day);

        void onHourScroll(int hour);

        void onMinuteScroll(int minute);


    }
}
