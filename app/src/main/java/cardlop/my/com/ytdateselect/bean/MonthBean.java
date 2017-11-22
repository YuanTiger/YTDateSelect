package cardlop.my.com.ytdateselect.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cardlop.my.com.ytdateselect.constant.Constant;
import cardlop.my.com.ytdateselect.utils.YTDateUtils;

/**
 * Author：mengyuan
 * Date  : 2017/11/22上午9:48
 * E-Mail:mengyuanzz@126.com
 * Desc  :一个月的Bean对象
 */

public class MonthBean implements Serializable {
    //这个月的年份
    public int year;
    //这个月的月份
    public int month;
    //这个月的每一天
    public List<DayBean> dayList;


    /**
     * 给定一个年份、一个月份，自动生成这个月的Bean对象
     *
     * @param year
     * @param month
     */
    public MonthBean(int year, int month) {
        this.year = year;
        this.month = month;
        dayList = new ArrayList<>();
        //获取这个月有多少天
        int dayCount = YTDateUtils.getDayCountOfMonth(year, month);
        //获取这个月的第一天是周几
        int beforeDayWeek = YTDateUtils.getDayOfWeekInMonth(year, month, 1);

        //1代表从周日开始，如果>1代表不是从周日开始，需要从上个月取几天填充至周日
        if (beforeDayWeek > Constant.Week.SUN) {
            //如果当前月份为1月，则上月为去年的12月
            int beforeMonth = month - 1;
            int beforeYear = year;
            if (beforeMonth == 0) {
                beforeMonth = 12;
                beforeYear--;
            }
            //获取上一个月一共有多少天
            int lastDayCount = YTDateUtils.getDayCountOfMonth(beforeYear, beforeMonth);
            //从上个月取几天填充满一周
            for (int i = 0; i < beforeDayWeek - 1; i++) {
                DayBean dayBean = YTDateUtils.getDayBean(beforeYear, beforeMonth, lastDayCount - (beforeDayWeek - 1 - i));

                dayList.add(dayBean);
            }
        }

        //填充本月的Bean对象
        for (int i = 1; i <= dayCount; i++) {
            DayBean dayBean = YTDateUtils.getDayBean(year, month, i);
            dayBean.isBelongMonth = true;
            dayList.add(dayBean);
        }
        //获取本月最后一天是周几
        int lastDayWeek = YTDateUtils.getDayOfWeekInMonth(year, month, dayCount);
        //7为周六，如果为7则无需从下个月取数据填充，否则需要从下个月取数据填充
        if (lastDayWeek < Constant.Week.SAT) {
            int lastMonth = month + 1;
            int lastYear = year;
            if (lastMonth == 13) {
                lastMonth = 1;
                lastYear++;
            }
            for (int i = 1; i <= Constant.Week.SAT - lastDayWeek; i++) {
                DayBean dayBean = YTDateUtils.getDayBean(lastYear, lastMonth, i);
                dayList.add(dayBean);
            }
        }


    }

}
