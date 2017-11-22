package cardlop.my.com.ytdateselect.view;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import cardlop.my.com.ytdateselect.bean.DayBean;
import cardlop.my.com.ytdateselect.bean.MonthBean;

/**
 * Author：mengyuan
 * Date  : 2017/11/22下午2:07
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class MonthViewManager {

    //所有月份的数据
    private List<MonthBean> monthBeanList;

    private RecyclerView.Adapter adapter;

    //选择的开始日期
    private SelectHolder startHolder;

    //选择的结束日期
    private SelectHolder endHolder;


    private IMonthViewItemSelect selectListener;

    public MonthViewManager(RecyclerView.Adapter adapter, List<MonthBean> monthBeanList, IMonthViewItemSelect selectListener) {
        this.adapter = adapter;
        this.monthBeanList = monthBeanList;
        this.selectListener = selectListener;
    }

    public void bind(MonthBean monthBean, MonthView monthView) {
        //初始化monthView
        monthView.setData(monthBean);
        monthView.setDayItemClickListener(new MonthView.DayItemClickListener() {
            @Override
            public void itemClick(MonthView monthView, MonthView.CurrentDayItemHolder holder, DayBean data) {
                if (selectListener == null) {
                    return;
                }
                selectItem(monthView, holder, data);
            }


        });

    }


    /**
     * 连续模式下的回调处理
     *
     * @param monthView
     * @param dayBean   本次选择的日期
     */
    private void selectItem(MonthView monthView, MonthView.CurrentDayItemHolder holder, DayBean dayBean) {
        //1、是否拦截本次选择,一个参数的拦截方法
        if (selectListener.onInterceptSelect(dayBean)) {
            return;
        }

        //2、如果 开始日期 为空，将本次选择的数据赋值给开始日期
        if (startHolder == null) {
            startHolder = new SelectHolder(monthView, holder, dayBean);
            startHolder.holder.changeSelect(true);
            startHolder.holder.setDesc("开始");
            endHolder = null;
            return;
        }
        //到这里，代表着 开始日期不为空，结束日期为空，需要对本次点击的日期进行详细的判断
        //3、是否拦截本次选择,两个参数的拦截方法
        if (selectListener.onInterceptSelect(startHolder.dayBean, dayBean)) {
            return;
        }

        //4、如果两次点击的日期完全一致,认为用户只想选择这一天的日期
        if (startHolder.dayBean.year == dayBean.year && startHolder.dayBean.month == dayBean.month && startHolder.dayBean.day == dayBean.day) {
            selectListener.onSelectSuccess(startHolder.dayBean, dayBean);
            startHolder.holder.setDesc("结束");
            startHolder = null;
            endHolder = null;
            return;
        }
        //5、如果两次点击的日期只有天数不一致
        if (startHolder.dayBean.year == dayBean.year && startHolder.dayBean.month == dayBean.month && startHolder.dayBean.day != dayBean.day) {
            //5-1、如果开始日期 > 本次选择的日期，代表着用户向前选择了，则重新赋值开始日期
            if (startHolder.dayBean.day > dayBean.day) {
                startHolder.holder.changeSelect(false);
                startHolder = new SelectHolder(monthView, holder, dayBean);
                startHolder.holder.changeSelect(true);
                startHolder.holder.setDesc("开始");
            }
            //5-2、如果开始日期 < 本次选择的日期，则将本次选择赋值给结束日期，并选中两个日期中的所有条目
            else {
                endHolder = new SelectHolder(monthView, holder, dayBean);

                for (MonthBean monthBean : monthBeanList) {
                    if (monthBean.month == startHolder.dayBean.month) {
                        for (int i = startHolder.dayBean.day - 1; i < endHolder.dayBean.day; i++) {
                            if (i == startHolder.dayBean.day - 1) {
                                monthBean.dayList.get(i).desc = "开始";
                            } else if (i == endHolder.dayBean.day - 1) {
                                monthBean.dayList.get(i).desc = "结束";
                            }
                            monthBean.dayList.get(i).isSelect = true;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                //选择成功的回调
                selectListener.onSelectSuccess(startHolder.dayBean, endHolder.dayBean);
            }
            return;
        }
        //5、如果两次点击的日期月份不一致
        if (startHolder.dayBean.year == dayBean.year && startHolder.dayBean.month != dayBean.month) {
            //5-1、如果 开始日期的月份 > 本次点击的月份，代表着用户向前选择，则重新赋值开始日期
            if (startHolder.dayBean.month > dayBean.month) {
                startHolder.holder.changeSelect(false);
                startHolder = new SelectHolder(monthView, holder, dayBean);
                startHolder.holder.changeSelect(true);
                startHolder.holder.setDesc("开始");
            }
            //5-2、如果 开始日期的月份  < 本次点击的月份，代表着用户跨月选择，需要选中两个日期中的所有条目
            else {

            }
        }
    }

    class SelectHolder {
        MonthView.CurrentDayItemHolder holder;
        DayBean dayBean;
        MonthView monthView;

        public SelectHolder(MonthView monthView, MonthView.CurrentDayItemHolder holder, DayBean dayBean) {
            this.monthView = monthView;
            this.holder = holder;
            this.dayBean = dayBean;

        }


    }
}
