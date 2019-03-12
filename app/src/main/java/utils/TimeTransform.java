package utils;

import java.text.ParsePosition;
import java.util.Locale;
import java.util.Random;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

    /**
     * description: 时间格式
     * author:  kevin
     * date: 2017/6/13 19:46
     */
    public class TimeTransform {
        /**
         * 从时间(毫秒)中提取出时间(时:分)
         * 时间格式:  时:分
         *
         * @param millisecond
         * @return
         */
        public static String getTimeFromMillisecond(Long millisecond) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date(millisecond);
            String timeStr = simpleDateFormat.format(date);
            return timeStr;
        }

        /**
         * 将毫秒转化成固定格式的时间
         * 时间格式: yyyy-MM-dd HH:mm:ss
         *
         * @param millisecond
         * @return
         */
        public static String getDateTimeFromMillisecond(Long millisecond) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(millisecond);
            String dateStr = simpleDateFormat.format(date);
            return dateStr;
        }

        /**
         * 将时间转化成毫秒
         * 时间格式: yyyy-MM-dd HH:mm:ss
         *
         * @param time
         * @return
         */
        public static Long timeStrToSecond(String time) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Long second = format.parse(time).getTime();
                return second;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1l;
        }

        /**
         * 获取时间间隔
         *
         * @param millisecond
         * @return
         */
        public static String getSpaceTime(Long millisecond) {
            Calendar calendar = Calendar.getInstance();
            Long currentMillisecond = calendar.getTimeInMillis(); //间隔秒
             Long spaceSecond = (currentMillisecond - millisecond) / 1000;
             //一分钟之内
            if (spaceSecond >= 0 && spaceSecond < 60) {
                return "片刻之前";
            } //一小时之内
            else if (spaceSecond / 60 > 0 && spaceSecond / 60 < 60) {
                return spaceSecond / 60 + "分钟之前";
            } //一天之内
            else if (spaceSecond / (60 * 60) > 0 && spaceSecond / (60 * 60) < 24) {
                return spaceSecond / (60 * 60) + "小时之前";
            } //3天之内
            else if (spaceSecond/(60*60*24)>0&&spaceSecond/(60*60*24)<3){
                return spaceSecond/(60*60*24)+"天之前";
            }else {
                return getDateTimeFromMillisecond(millisecond);
            }
        }
}

