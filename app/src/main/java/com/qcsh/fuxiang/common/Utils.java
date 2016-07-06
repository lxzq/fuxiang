package com.qcsh.fuxiang.common;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liu on 13-11-4.
 * 工具类
 */
public class Utils {


    //3分钟之内显示”刚刚“，一个小时内显示“多少分钟前”，今天和昨天显示”今/昨天  HH:mm"，其他显示“yyyy-MM-dd HH:mm”
    public static String getStandardDate(long timeStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStr*1000);
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        calendar.setTimeInMillis(System.currentTimeMillis());
        int cy = calendar.get(Calendar.YEAR);
        int cm = calendar.get(Calendar.MONTH);
        int cd = calendar.get(Calendar.DAY_OF_MONTH);
        if (y == cy && m == cm && d == cd) {
            long diff = System.currentTimeMillis() - (timeStr*1000);
            if (diff < 60 * 1000 * 3) {
                return "刚刚";
            } else if (diff < 60 * 1000 * 60) {
                return diff / (60 * 1000) + "分钟前";
            } else {
                return "今天 " + (h < 10 ? "0" + h : h) + ":" + (min < 10 ? "0" + min : min);
            }
        } else {
            calendar.add(Calendar.DATE, -1);
            int yy = calendar.get(Calendar.YEAR);
            int ym = calendar.get(Calendar.MONTH);
            int yd = calendar.get(Calendar.DAY_OF_MONTH);
            if (y == yy && m == ym && d == yd) {
                return "昨天 " + (h < 10 ? "0" + h : h) + ":" + (min < 10 ? "0" + min : min);
            } else {
                return simpleDateFormat.format(new Date(timeStr*1000));
            }
        }

    }

    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     *
     * @param timeStr 时间戳
     */

    public static String getDistanceDate(long timeStr) {
        StringBuilder sb = new StringBuilder();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        long time = System.currentTimeMillis() - timeStr*1000;
        long mill = (long) Math.ceil(time / 1000);// 秒前
        long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前
        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时
        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前
        if (day - 1 > 0) {
            //  sb.append(day).append("天");

            sb.append(simpleDateFormat.format(new Date(timeStr*1000)));
        } else if (hour - 1 > 0) {
            if (hour >= 24) {
                sb.append("昨天");
            } else {
                sb.append(hour).append("小时前");
            }
        } else if (minute - 1 > 0) {
            if (minute == 60) {
                sb.append("1小时前");
            } else {
                sb.append(minute).append("分钟前");
            }
        } else if (mill - 1 > 0) {
            if (mill == 60) {
                sb.append("1分钟前");
            } else {
                sb.append(mill).append("秒前");
            }
        } else {
            sb.append("刚刚");
        }
//        if (!sb.toString().equals("刚刚")) {
        //         sb.append("前");
        //      }
        return sb.toString();
    }

    //获取图片所在文件夹名称
    public static String getDir(String path) {
        String subString = path.substring(0, path.lastIndexOf('/'));
        return subString.substring(subString.lastIndexOf('/') + 1, subString.length());
    }

    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfoList = activityManager.getRunningServices(30);
        if (serviceInfoList == null || serviceInfoList.size() == 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo aServiceInfoList : serviceInfoList) {
            String aClassName = aServiceInfoList.service.getClassName();
            if (aClassName != null && aClassName.equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 判断网络状态，是Wifi还是3G
     */
    public static float checkNetworkState2ScaleNumber(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS
                        || info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA
                        || info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
                    return 0.7f;
                } else {
                    return 1f;
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return 1f;
            }
        }
        return 0f;
    }

    // 获取AppKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return apiKey;
    }

//    public static String ToSBC(String input) {
//        // 半角转全角：
//        char[] c = input.toCharArray();
//        for (int i = 0; i < c.length; i++) {
//            if (c[i] == 32) {
//                c[i] = (char) 12288;
//                continue;
//            }
//            if (c[i] < 127 && c[i] > 32)
//                c[i] = (char) (c[i] + 65248);
//        }
//        return new String(c);
//    }

//    public static String ToDBC(String input) {
//        char[] c = input.toCharArray();
//        for (int i = 0; i < c.length; i++) {
//            if (isChinese(c[i])) {
//                if (c[i] == 12288) {
//                    c[i] = (char) 32;
//                    continue;
//                }
//                if (c[i] > 65280 && c[i] < 65375)
//                    c[i] = (char) (c[i] - 65248);
//            }
//        }
//        return new String(c);
//    }

//    private static boolean isChinese(char c) {
//        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
//        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
//                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
//                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
//                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
//                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
//                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
//            return true;
//        }
//        return false;
//    }

//    /**
//     * ASCII表中可见字符从!开始，偏移位值为33(Decimal)
//     */
//    static final char DBC_CHAR_START = 33; // 半角!
//
//    /**
//     * ASCII表中可见字符到~结束，偏移位值为126(Decimal)
//     */
//    static final char DBC_CHAR_END = 126; // 半角~
//
//    /**
//     * 全角对应于ASCII表的可见字符从！开始，偏移值为65281
//     */
//    static final char SBC_CHAR_START = 65281; // 全角！
//
//    /**
//     * 全角对应于ASCII表的可见字符到～结束，偏移值为65374
//     */
//    static final char SBC_CHAR_END = 65374; // 全角～
//
//    /**
//     * ASCII表中除空格外的可见字符与对应的全角字符的相对偏移
//     */
//    static final int CONVERT_STEP = 65248; // 全角半角转换间隔
//
//    /**
//     * 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理
//     */
//    static final char SBC_SPACE = 12288; // 全角空格 12288
//
//    /**
//     * 半角空格的值，在ASCII中为32(Decimal)
//     */
//    static final char DBC_SPACE = ' '; // 半角空格

    /**
     * <PRE>
     * 半角字符->全角字符转换
     * 只处理空格，!到˜之间的字符，忽略其他
     * </PRE>
     */
//    public static String bj2qj(String src) {
//        if (src == null) {
//            return src;
//        }
//        StringBuilder buf = new StringBuilder(src.length());
//        char[] ca = src.toCharArray();
//        for (int i = 0; i < ca.length; i++) {
//            if (ca[i] == DBC_SPACE) { // 如果是半角空格，直接用全角空格替代
//                buf.append(SBC_SPACE);
//            } else if ((ca[i] >= DBC_CHAR_START) && (ca[i] <= DBC_CHAR_END)) { // 字符是!到~之间的可见字符
//                buf.append((char) (ca[i] + CONVERT_STEP));
//            } else { // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
//                buf.append(ca[i]);
//            }
//        }
//        return buf.toString();
//    }

    /**
     * <PRE>
     * 全角字符->半角字符转换
     * 只处理全角的空格，全角！到全角～之间的字符，忽略其他
     * </PRE>
     */
//    public static String qj2bj(String src) {
//        if (src == null) {
//            return src;
//        }
//        StringBuilder buf = new StringBuilder(src.length());
//        char[] ca = src.toCharArray();
//        for (int i = 0; i < src.length(); i++) {
//            if (ca[i] >= SBC_CHAR_START && ca[i] <= SBC_CHAR_END) { // 如果位于全角！到全角～区间内
//                buf.append((char) (ca[i] - CONVERT_STEP));
//            } else if (ca[i] == SBC_SPACE) { // 如果是全角空格
//                buf.append(DBC_SPACE);
//            } else { // 不处理全角空格，全角！到全角～区间外的字符
//                buf.append(ca[i]);
//            }
//        }
//        return buf.toString();
//    }


    /**
     * 将时间戳转为date类型
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date jsonTimeToDate(Long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Long t = time*1000;
        String d = format.format(t);
        try {
            return format.parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }


    }

    /**
     * 将时间戳转为yyyy-MM-dd HH:mm:ss类型
     *
     * @param time
     * @return
     */
    public static String jsonTimeToString(Long time) {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
    }
    /**
     * 将时间戳转为yyyy-MM-dd类型
     *
     * @param time
     * @return
     */
    public static String jsonTimeToStringYMD(Long time) {

        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(time));
    }


    public static String getAge(int year) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int age = currentYear - year;
        return age + "岁";
    }


    public static void main(String[] args) {
        String str = Utils.getDistanceDate(1420520696000L);
        System.out.println(str + "***");
    }

    //判断字符串是不是中文
    public static boolean checkZhifubao(String str) {
        boolean mark = false;
        Pattern pattern = Pattern.compile("^[a-zA-Z]|[\u4E00-\u9FA5]|^[0-9]*$");
        Matcher matc = pattern.matcher(str);
        StringBuffer stb = new StringBuffer();
        while (matc.find()) {
            mark = true;
            stb.append(matc.group());
        }

        if (mark) {
            System.out.println("匹配的字符串为：" + stb.toString());
        }
        return mark;
    }

    //校验银行卡卡号
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId
                .substring(0, cardId.length() - 1));
        return bit != 'N' && cardId.charAt(cardId.length() - 1) == bit;

    }

    //  从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N  
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * 功能：身份证的有效验证
     *
     * @param IDStr 身份证号
     * @return 有效：返回"" 无效：返回String信息
     * @throws ParseException
     */
    public static boolean IDCardValidate(String IDStr) {
        String errorInfo = "";// 记录错误信息  
        String[] ValCodeArr = {"1", "0", "X", "9", "8", "7", "6", "5", "4",
                "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2"};
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================  
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为15位或18位。";
            return false;
        }
        // =======================(end)========================  

        // ================ 数字 除最后以为都为数字 ================  
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (!isNumeric(Ai)) {
            errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            return false;
        }
        // =======================(end)========================  

        // ================ 出生年月是否有效 ================  
        String strYear = Ai.substring(6, 10);// 年份  
        String strMonth = Ai.substring(10, 12);// 月份  
        String strDay = Ai.substring(12, 14);// 月份  
        if (!isDataFormat(strYear + "-" + strMonth + "-" + strDay)) {
            errorInfo = "身份证生日无效。";
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(
                    strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                errorInfo = "身份证生日不在有效范围。";
                return false;
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            return false;
        }
        // =====================(end)=====================  

        // ================ 地区码时候有效 ================  
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误。";
            return false;
        }
        // ==============================================  

        // ================ 判断最后一位的值 ================  
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (!Ai.equals(IDStr)) {
                errorInfo = "身份证无效，不是合法的身份证号码";
                return false;
            }
        } else {
            return true;
        }
        // =====================(end)=====================  
        return true;
    }

    /**
     * 功能：判断字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }


    /**
     * 功能：判断字符串是否为邮编
     *
     * @param str
     * @return
     */
    public static boolean isZipCode(String str) {
        Pattern pattern = Pattern.compile("/^[1-9][0-9]{5}$/");
        Matcher isNum = pattern.matcher(str);
        return  isNum.matches();
    }


    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 验证日期字符串是否是YYYY-MM-DD格式
     *
     * @param str
     * @return
     */
    public static boolean isDataFormat(String str) {
        boolean flag = false;
        // String  
        // regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";  
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1 = Pattern.compile(regxStr);
        Matcher isNo = pattern1.matcher(str);
        if (isNo.matches()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 格式化价格,保留两位小数,没有小数补0,如:468转换为468.00
     *
     * @param price
     * @return
     */
    public static String formatFloat(String price) {
        String[] original_Price_format = price.split("\\.");
        if (original_Price_format.length > 1) {
            String right = original_Price_format[1];
            if (right.trim().length() < 2) {
                price = price + "0";
            } else {
                price = String.format("%.2f", Float.parseFloat(price));

            }
        } else {
            price = price + ".00";
        }
        return price;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2int(Context context,int dip) {
        final float scale = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        final int scale2 = Math.round(scale);
        return scale2;
    }

    // 获取屏幕的宽度
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    // 获取屏幕的高度
    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * 秒转换分钟
     * @param second  00 ：00
     * @return
     */
    public static String secondToMinute(int second){

        int mm = second / 60 ;
        int ss = second % 60 ;
        String minute = mm + "" ;
        String seconds = ss + "";
        if(mm < 10) minute = "0" + mm;
        if(ss < 10) seconds = "0" + ss;

        return minute + ":" + seconds;
    }
}
