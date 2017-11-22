package cardlop.my.com.ytdateselect.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cardlop.my.com.ytdateselect.bean.DayBean;
import cardlop.my.com.ytdateselect.bean.MonthBean;

/**
 * Author：mengyuan
 * Date  : 2017/11/22上午9:47
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class YTDateUtils {

    /**
     * 计算当月有多少天
     */
    public static int getDayCountOfMonth(int year, int month) {
        int[] arr = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int days = 0;
        //如果是闰年，二月= 29天
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            arr[1] = 29;
        }
        try {
            days = arr[month - 1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;
    }


    /**
     * 获取指定日期的当天对象
     *
     * @return 当天对象
     */
    public static DayBean getDayBean(int year, int month, int day) {
        DayBean dayBean = new DayBean();
        dayBean.year = year;
        dayBean.month = month;
        dayBean.day = day;
        dayBean.week = getDayOfWeekInMonth(year, month, day);

        return dayBean;

    }

    /**
     * 计算指定日期是周几
     * 1为周日，2为周一，3为周二，4为周三，5为周四，6为周五，7为周六
     */
    public static int getDayOfWeekInMonth(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, day);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 计算两个日期的相差天数
     *
     * @return day count 相差天数
     */
    public static int countDays(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        Calendar startC = Calendar.getInstance();
        startC.set(Calendar.YEAR, startYear);
        startC.set(Calendar.MONTH, startMonth - 1);
        startC.set(Calendar.DAY_OF_MONTH, startDay);
        Calendar endC = Calendar.getInstance();
        endC.set(Calendar.YEAR, endYear);
        endC.set(Calendar.MONTH, endMonth - 1);
        endC.set(Calendar.DAY_OF_MONTH, endDay);
        return (int) ((endC.getTimeInMillis() - startC.getTimeInMillis()) / 86400000 + 1);
    }

    public static List<MonthBean> generateMonths(int startYear, int endYear) {
        return generateMonths(startYear, 1, endYear, 12);
    }


    /**
     * 生成月份Bean对象
     *
     * @return
     */
    public static List<MonthBean> generateMonths(int startYear, int startMonth, int endYear, int endMonth) {

        if (startYear <= 0 || endYear <= 0 || startMonth <= 0 || endMonth <= 0 || startMonth > 12 || endMonth > 12) {
            throw new IllegalArgumentException("日期不规范");
        }

        if (startYear > endYear) {
            throw new IllegalArgumentException("开始年份必须小于等于结束年份");
        }

        if (startYear == endYear && startMonth > endMonth)
            throw new IllegalArgumentException("在年份相同时，开始月份必须小于结束年份");

        List<MonthBean> data = new ArrayList<>();
        if (startYear == endYear) {
            for (int currentMonth = startMonth; currentMonth <= endMonth; currentMonth++) {
                data.add(new MonthBean(startYear, currentMonth));
            }
        } else {
            for (int currentMonth = startMonth; currentMonth <= 12; currentMonth++) {
                data.add(new MonthBean(startYear, currentMonth));
            }
            while (endYear - startYear > 1) {
                startYear++;
                for (int currentMonth = 1; currentMonth <= 12; currentMonth++) {
                    data.add(new MonthBean(startYear, currentMonth));
                }
            }
            for (int currentMonth = 1; currentMonth <= endMonth; currentMonth++) {
                data.add(new MonthBean(endYear, currentMonth));
            }
        }
        return data;
    }

}
