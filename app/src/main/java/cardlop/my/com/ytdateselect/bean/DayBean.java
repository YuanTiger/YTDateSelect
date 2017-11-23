package cardlop.my.com.ytdateselect.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;

/**
 * Author：mengyuan
 * Date  : 2017/11/22上午9:49
 * E-Mail:mengyuanzz@126.com
 * Desc  :一天的Bean对象
 */

public class DayBean implements Parcelable {
    //这一天的年份
    public int year;
    //这一天的月份
    public int month;
    //这一天的具体日期
    public int day;
    //星期几
    public int week;
    //描述信息
    public String desc;
    //状态值
    public int state;
    //如果这一天有节日，则是具体的日期名称，否则为空
    //未实现，全部为空
    public String holiday;


    public DayBean(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
        dest.writeInt(this.week);
        dest.writeInt(this.state);
        dest.writeString(this.desc);
        dest.writeString(this.holiday);
    }

    protected DayBean(Parcel in) {
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
        this.week = in.readInt();
        this.state = in.readInt();
        this.desc = in.readString();
        this.holiday = in.readString();
    }

    public static final Parcelable.Creator<DayBean> CREATOR = new Parcelable.Creator<DayBean>() {
        @Override
        public DayBean createFromParcel(Parcel source) {
            return new DayBean(source);
        }

        @Override
        public DayBean[] newArray(int size) {
            return new DayBean[size];
        }
    };
}
