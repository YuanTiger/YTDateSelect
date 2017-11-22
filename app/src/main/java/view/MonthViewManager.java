package view;

import cardlop.my.com.ytdateselect.bean.DayBean;
import cardlop.my.com.ytdateselect.bean.MonthBean;
import cardlop.my.com.ytdateselect.utils.YTDateUtils;

/**
 * Author：mengyuan
 * Date  : 2017/11/22下午2:07
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class MonthViewManager {

    private MonthBean monthBean;
    private MonthView monthView;
    private int position;

    //选择的开始日期
    private SelectHolder startHolder;

    //选择的结束日期
    private SelectHolder endHolder;


    private IMonthViewItemSelect selectListener;

    public MonthViewManager(IMonthViewItemSelect selectListener) {
        this.selectListener = selectListener;
    }

    public void bind(MonthBean monthBean, MonthView monthView, int position) {
        this.monthBean = monthBean;
        this.monthView = monthView;
        this.position = position;
        //初始化monthView
        monthView.setData(monthBean);
        monthView.setDayItemClickListener(new MonthView.DayItemClickListener() {
            @Override
            public void itemClick(MonthView.DayItemHolder holder, DayBean data) {
                if (selectListener == null) {
                    return;
                }
                selectItem(holder, data);
            }


        });

    }


    /**
     * 连续模式下的回调处理
     *
     * @param dayBean 本次选择的日期
     */
    private void selectItem(MonthView.DayItemHolder holder, DayBean dayBean) {
        //是否拦截本次选择,一个参数的拦截方法
        if (selectListener.onInterceptSelect(dayBean)) {
            return;
        }

        //1、如果开始日期 和 结束日期都为空，将本次选择的数据赋值给开始日期
        if (startHolder == null && endHolder == null) {
            startHolder = new SelectHolder(holder, dayBean);
            startHolder.holder.changeSelect(startHolder.dayBean, true);
            startHolder.holder.setDesc("开始");
        }
        //2、如果仅仅是结束日期为空
        else if (endHolder == null) {
            //2-1：如果开始日期 不等于 本次选择的日期
            if (startHolder.dayBean.day != dayBean.day) {
                //2-1-1:如果开始日期 < 本次选择的日期
                if (startHolder.dayBean.day < dayBean.day) {
                    //2-1-1-1：是否拦截本次选择
                    if (selectListener.onInterceptSelect(startHolder.dayBean, dayBean)) {
                        return;
                    }
                    //2-1-1-2：不拦截的话，遍历两次日期，将两个日期中的所有天数都选中
                    //并且将本次选择的日期记为结束日期
                    endHolder = new SelectHolder(holder, dayBean);
                    monthView.addSelectedDay(startHolder.dayBean, endHolder.dayBean);
                }
                //2-1-2：如果开始日期 > 本次选择的日期
                else if (startHolder.dayBean.day > dayBean.day) {
                    //2-1-2-1:是否拦截本次选择
                    if (selectListener.onInterceptSelect(dayBean, startHolder.dayBean)) {
                        return;
                    }
                    //2-1-2-2：仍然遍历，两个日期差，选择其中所有的间隔日期
                    for (int day = dayBean.day; day <= startHolder.dayBean.day; day++) {
                        monthView.addSelectedDay(dayBean, startHolder.dayBean);
                    }
                    //2-1-2-3：因为这里是开始日期 > 本次选择日期，所有需要将本次选择的日期置为开始，将之前的开始日期置为结束日期
                    endHolder = startHolder;
                    startHolder = new SelectHolder(holder, dayBean);

                    startHolder.holder.setDesc("开始");
                    endHolder.holder.setDesc("结束");
                }
                //选择成功的回调
                selectListener.onSelectSuccess(startHolder.dayBean, endHolder.dayBean);
            }
            //2-2：如果开始日期 == 本次选择的日期
            else {
                //2-2-1:调用   选择成功回调
                selectListener.onSelectSuccess(startHolder.dayBean, dayBean);
                startHolder = null;
                endHolder = null;
            }

        }
        //3、如果开始日期和结束日期都不为空，则清空之前选择的数据，并将此次选择的日期记为开始日期
        else {
            monthView.clearSelectedDays();
            startHolder = new SelectHolder(holder, dayBean);
            startHolder.holder.changeSelect(startHolder.dayBean, true);
            startHolder.holder.setDesc("开始");
            endHolder = null;
        }
    }

    class SelectHolder {
        MonthView.DayItemHolder holder;
        DayBean dayBean;

        public SelectHolder(MonthView.DayItemHolder holder, DayBean dayBean) {
            this.holder = holder;
            this.dayBean = dayBean;

        }


    }
}
