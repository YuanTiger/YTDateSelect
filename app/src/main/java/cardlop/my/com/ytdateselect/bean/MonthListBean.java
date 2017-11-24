package cardlop.my.com.ytdateselect.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import cardlop.my.com.ytdateselect.utils.YTDateUtils;

/**
 * Author：mengyuan
 * Date  : 2017/11/23下午5:51
 * E-Mail:mengyuanzz@126.com
 * Desc  : 月数据List的封装，开发者主要使用此Bean
 */

public class MonthListBean implements Parcelable {

    public ArrayList<MonthBean> dataList;
    //用户回填数据时自动定位
    public int startPosition = 0;
    //刷新的条目数
    public int refreshCount = 0;
    //是否需要重置数据，用于回填数据后，用户再次切换日期时
    public boolean isNeedClear = false;


    private volatile static MonthListBean monthListBean;

    private MonthListBean() {
    }

    public static MonthListBean getInstance() {
        //在调用该方法时进行判空，在对象为null时创建对象
        if (monthListBean == null) {
            synchronized (new Object()) {
                if (monthListBean == null) {
                    monthListBean = new MonthListBean();
                    monthListBean.dataList = YTDateUtils.getOneYearMonthData();
                }
            }
        }
        return monthListBean;
    }


    /**
     * 获取 某一天的 Bean 对象
     * @param monthPosition
     * @param dayPosition
     * @return
     */
    public DayBean getDayBean(int monthPosition,int dayPosition){
        return monthListBean.dataList.get(monthPosition).dayList.get(dayPosition);
    }

    /**
     * 获取 某一月的 Bean 对象
     * @param monthPosition
     * @return
     */
    public MonthBean getMonthBean(int monthPosition){
        return monthListBean.dataList.get(monthPosition);
    }

    public static void onDestory() {
        monthListBean = null;
    }

    private MonthListBean(Parcel in) {
        dataList = in.createTypedArrayList(MonthBean.CREATOR);
        startPosition = in.readInt();
        refreshCount = in.readInt();
        isNeedClear = in.readByte() != 0;
    }

    public static final Creator<MonthListBean> CREATOR = new Creator<MonthListBean>() {
        @Override
        public MonthListBean createFromParcel(Parcel in) {
            return new MonthListBean(in);
        }

        @Override
        public MonthListBean[] newArray(int size) {
            return new MonthListBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(dataList);
        dest.writeInt(startPosition);
        dest.writeInt(refreshCount);
        dest.writeByte((byte) (isNeedClear ? 1 : 0));
    }
}
