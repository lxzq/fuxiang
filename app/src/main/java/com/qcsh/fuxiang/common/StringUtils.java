package com.qcsh.fuxiang.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.ui.look.GrowthtreeDetailActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    // private final static SimpleDateFormat dateFormater = new
    // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // private final static SimpleDateFormat dateFormater2 = new
    // SimpleDateFormat("yyyy-MM-dd");

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 将字符串转为日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {
        Date time = null;
        if (TimeZoneUtil.isInEasternEightZones()) {
            time = toDate(sdate);
        } else {
            time = TimeZoneUtil.transformTime(toDate(sdate),
                    TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault());
        }
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 返回年月日
     *
     * @return
     */
    public static String getYearMonthDay() {

        Calendar cal = Calendar.getInstance();
        return dateFormater2.get().format(cal.getTime());
    }

    /**
     * 返回long类型的今天的日期
     *
     * @return
     */
    public static long getToday() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater2.get().format(cal.getTime());
        curDate = curDate.replace("-", "");
        return Long.parseLong(curDate);
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
    public static final String getAgeWithYearDay(String birthday) {
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
     * 根据日期计算年龄
     *
     * @param birthday
     * @return
     */
    public static final String getAgeWithYearDay(String birthday, String date) {
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

    public static String getWeek(String date) {
        int year = Integer.valueOf(date.substring(0, 4));
        int month = Integer.valueOf(date.substring(5, 7));
        int day = Integer.valueOf(date.substring(8, 10));
        Calendar calendar = Calendar.getInstance();//获得一个日历
        calendar.set(year, month - 1, day);//设置当前时间,月份是从0月开始计算
        int number = calendar.get(Calendar.DAY_OF_WEEK);//星期表示1-7，是从星期日开始，
        String[] str = {"", "周日", "周一", "周二", "周三", "周四", "周五", "周六",};
        return str[number];
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) || "null".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        return !(email == null || email.trim().length() == 0) && emailer.matcher(email).matches();
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     * @return
     */
    public static String toConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line);
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                isr.close();
                isr.close();
                read.close();
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * 设置文字和表情
     *
     * @param context
     * @param textView
     * @param content
     */
    public static void setTextEmoji(Context context, TextView textView, String content) {
        initEmojiList(context);
        textView.setText("");
        if (content.indexOf("|") != -1) {
            String[] strings = content.split("\\|");
            for (String id : strings) {
                if (TextUtils.isEmpty(id)) continue;
                boolean isBreak = true;
                for (int i = 0; i < emojis.size(); i++) {
                    EmojiObject emojiObject = (EmojiObject) emojis.get(i);
                    if (emojiObject.id.equals(id)) {
                        ImageSpan imageSpan = new ImageSpan(context, emojiObject.bitmap);
                        SpannableString spannableString = new SpannableString("emoji");   //“emoji”是图片名称的前缀
                        spannableString.setSpan(imageSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textView.append(spannableString);
                        isBreak = false;
                        break;
                    }
                }
                if (isBreak) textView.append(id);
            }
        } else {
            textView.setText(content);
        }
    }

    static ArrayList<Object> emojis;

    private static void initEmojiList(Context context) {
        Resources resources = context.getResources();
        emojis = new ArrayList<Object>();

        EmojiObject emoji_baoquan = new EmojiObject();
        emoji_baoquan.id = "emoji_01";
        emoji_baoquan.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_baoquan);
        emojis.add(emoji_baoquan);

        EmojiObject emoji_biezui = new EmojiObject();
        emoji_biezui.id = "emoji_02";
        emoji_biezui.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_biezui);
        emojis.add(emoji_biezui);

        EmojiObject emoji_bishi = new EmojiObject();
        emoji_bishi.id = "emoji_03";
        emoji_bishi.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_bishi);
        emojis.add(emoji_bishi);

        EmojiObject emoji_chijing = new EmojiObject();
        emoji_chijing.id = "emoji_04";
        emoji_chijing.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_chijing);
        emojis.add(emoji_chijing);

        EmojiObject emoji_ciya = new EmojiObject();
        emoji_ciya.id = "emoji_05";
        emoji_ciya.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_ciya);
        emojis.add(emoji_ciya);

        EmojiObject emoji_dajing = new EmojiObject();
        emoji_dajing.id = "emoji_06";
        emoji_dajing.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_dajing);
        emojis.add(emoji_dajing);

        EmojiObject emoji_daku = new EmojiObject();
        emoji_daku.id = "emoji_07";
        emoji_daku.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_daku);
        emojis.add(emoji_daku);

        EmojiObject emoji_dengyan = new EmojiObject();
        emoji_dengyan.id = "emoji_08";
        emoji_dengyan.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_dengyan);
        emojis.add(emoji_dengyan);

        EmojiObject emoji_fahuo = new EmojiObject();
        emoji_fahuo.id = "emoji_09";
        emoji_fahuo.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_fahuo);
        emojis.add(emoji_fahuo);

        EmojiObject emoji_fanu = new EmojiObject();
        emoji_fanu.id = "emoji_10";
        emoji_fanu.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_fanu);
        emojis.add(emoji_fanu);

        EmojiObject emoji_guzhang = new EmojiObject();
        emoji_guzhang.id = "emoji_11";
        emoji_guzhang.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_guzhang);
        emojis.add(emoji_guzhang);

        EmojiObject emoji_haha = new EmojiObject();
        emoji_haha.id = "emoji_12";
        emoji_haha.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_haha);
        emojis.add(emoji_haha);

        EmojiObject emoji_haixiu = new EmojiObject();
        emoji_haixiu.id = "emoji_13";
        emoji_haixiu.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_haixiu);
        emojis.add(emoji_haixiu);

        EmojiObject emoji_han = new EmojiObject();
        emoji_han.id = "emoji_14";
        emoji_han.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_han);
        emojis.add(emoji_han);

        EmojiObject emoji_haqian = new EmojiObject();
        emoji_haqian.id = "emoji_15";
        emoji_haqian.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_haqian);
        emojis.add(emoji_haqian);

        EmojiObject emoji_kelian = new EmojiObject();
        emoji_kelian.id = "emoji_16";
        emoji_kelian.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_kelian);
        emojis.add(emoji_kelian);

        EmojiObject emoji_koubi = new EmojiObject();
        emoji_koubi.id = "emoji_17";
        emoji_koubi.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_koubi);
        emojis.add(emoji_koubi);

        EmojiObject emoji_nanguo = new EmojiObject();
        emoji_nanguo.id = "emoji_18";
        emoji_nanguo.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_nanguo);
        emojis.add(emoji_nanguo);

        EmojiObject emoji_tiaomei = new EmojiObject();
        emoji_tiaomei.id = "emoji_19";
        emoji_tiaomei.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_tiaomei);
        emojis.add(emoji_tiaomei);

        EmojiObject emoji_tiaopi = new EmojiObject();
        emoji_tiaopi.id = "emoji_20";
        emoji_tiaopi.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_tiaopi);
        emojis.add(emoji_tiaopi);

        EmojiObject emoji_touxiao = new EmojiObject();
        emoji_touxiao.id = "emoji_21";
        emoji_touxiao.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_touxiao);
        emojis.add(emoji_touxiao);

        EmojiObject emoji_wenhao = new EmojiObject();
        emoji_wenhao.id = "emoji_22";
        emoji_wenhao.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_wenhao);
        emojis.add(emoji_wenhao);

        EmojiObject emoji_woyun = new EmojiObject();
        emoji_woyun.id = "emoji_23";
        emoji_woyun.bitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_woyun);
        emojis.add(emoji_woyun);
    }

    public static class EmojiObject {
        public String id;
        public Bitmap bitmap;
    }
}
