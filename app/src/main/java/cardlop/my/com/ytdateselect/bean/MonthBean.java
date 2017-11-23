package cardlop.my.com.ytdateselect.bean;

import android.os.Parcel;
import android.os.Parcelable;

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

public class MonthBean implements Parcelable {
    //这个月的年份
    public int year;
    //这个月的月份
    public int month;
    //这个月的每一天
    public List<DayBean> dayList;
    //这个月的第一天如果不是从周日开始，则需要从上个月获取数据用来填充满一周
    public List<DayBean> beforeList;
    //这个月的最后一天如果不是从周六，同理
    public List<DayBean> lastList;

    /**
     * 给定一个年份、一个月份，自动生成这个月的Bean对象
     *
     * @param year
     * @param month
     */
    public MonthBean(int year, int month) {
        this(year, month, -1);
    }

    public MonthBean(int year, int month, int day) {
        this.year = year;
        this.month = month;
        //获取这个月有多少天
        int dayCount = YTDateUtils.getDayCountOfMonth(year, month);
        //获取这个月的第一天是周几
        int beforeDayWeek = YTDateUtils.getDayOfWeekInMonth(year, month, 1);

        //1代表从周日开始，如果>1代表不是从周日开始，需要从上个月取几天填充至周日
        if (beforeDayWeek > Constant.Week.SUN) {
            //如果当前月份为1月，则上月为去年的12月
            beforeList = new ArrayList<>();
            int beforeMonth = month - 1;
            int beforeYear = year;
            if (beforeMonth == 0) {
                beforeMonth = 12;
                beforeYear--;
            }
            //获取上一个月一共有多少天
            int lastDayCount = YTDateUtils.getDayCountOfMonth(beforeYear, beforeMonth);
            //从上个月取几天填充满一周
            for (int i = 1; i <= beforeDayWeek - 1; i++) {
                DayBean dayBean = YTDateUtils.getDayBean(beforeYear, beforeMonth, lastDayCount - (beforeDayWeek - 1 - i));

                beforeList.add(dayBean);
            }
        }

        //填充本月的Bean对象
        dayList = new ArrayList<>();
        for (int i = 1; i <= dayCount; i++) {
            DayBean dayBean = YTDateUtils.getDayBean(year, month, i);
            //如果天数小于参数day，则设置为不可点击
            if (i < day - 1) {

                dayBean.state = Constant.DayState.UNCLICK;
            }
            if (i == day - 1) {
                dayBean.desc = "今天";
            }
            dayList.add(dayBean);
        }
        //获取本月最后一天是周几
        int lastDayWeek = YTDateUtils.getDayOfWeekInMonth(year, month, dayCount);
        //7为周六，如果为7则无需从下个月取数据填充，否则需要从下个月取数据填充
        if (lastDayWeek < Constant.Week.SAT) {
            lastList = new ArrayList<>();
            int lastMonth = month + 1;
            int lastYear = year;
            if (lastMonth == 13) {
                lastMonth = 1;
                lastYear++;
            }
            for (int i = 1; i <= Constant.Week.SAT - lastDayWeek; i++) {
                DayBean dayBean = YTDateUtils.getDayBean(lastYear, lastMonth, i);
                lastList.add(dayBean);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeTypedList(dayList);
        dest.writeTypedList(beforeList);
        dest.writeTypedList(lastList);
    }

    protected MonthBean(Parcel in) {
        this.year = in.readInt();
        this.month = in.readInt();
        this.dayList = in.createTypedArrayList(DayBean.CREATOR);
        this.beforeList = in.createTypedArrayList(DayBean.CREATOR);
        this.lastList = in.createTypedArrayList(DayBean.CREATOR);
    }

    public static final Parcelable.Creator<MonthBean> CREATOR = new Parcelable.Creator<MonthBean>() {
        @Override
        public MonthBean createFromParcel(Parcel source) {
            return new MonthBean(source);
        }

        @Override
        public MonthBean[] newArray(int size) {
            return new MonthBean[size];
        }
    };

    /**
     * 清除选择记录
     */
    public void clearSelect() {
        if (dayList == null) {
            return;
        }
        for (DayBean dayBean : dayList) {
            switch (dayBean.state) {
                case Constant.DayState.SELECT:
                case Constant.DayState.START:
                case Constant.DayState.END:
                    dayBean.state = Constant.DayState.NORMAL;
                    break;
            }
        }
    }
}
