package cardlop.my.com.ytdateselect.view;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cardlop.my.com.ytdateselect.bean.DayBean;
import cardlop.my.com.ytdateselect.bean.MonthBean;
import cardlop.my.com.ytdateselect.constant.Constant;
import cardlop.my.com.ytdateselect.utils.YTDateUtils;

/**
 * Author：mengyuan
 * Date  : 2017/11/22下午2:07
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class MonthViewManager {

    //所有月份的数据
    private ArrayList<MonthBean> monthBeanList;

    private RecyclerView.Adapter adapter;

    //选择的开始日期
    private SelectHolder startHolder;

    //选择的结束日期
    private SelectHolder endHolder;


    private IMonthViewItemSelect selectListener;

    public MonthViewManager(RecyclerView.Adapter adapter, ArrayList<MonthBean> monthBeanList, IMonthViewItemSelect selectListener) {
        this.adapter = adapter;
        this.monthBeanList = monthBeanList;
        this.selectListener = selectListener;
    }

    public void bind(MonthBean monthBean, MonthView monthView) {
        //初始化monthView
        monthView.setData(monthBean);
        monthView.setDayItemClickListener(new MonthView.DayItemClickListener() {
            @Override
            public void itemClick(MonthView.CurrentDayItemHolder holder, DayBean data) {
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
    private void selectItem(MonthView.CurrentDayItemHolder holder, DayBean dayBean) {
        //1、是否拦截本次选择,一个参数的拦截方法
        if (selectListener.onInterceptSelect(dayBean)) {
            return;
        }
        //2、如果 开始日期 为空，将本次选择的数据赋值给开始日期，并清除之前回填的数据
        if (startHolder == null) {
            for (MonthBean monthBean : monthBeanList) {
                monthBean.clearSelect();
            }
            dayBean.state = Constant.DayState.START;
            startHolder = new SelectHolder(holder, dayBean);
            endHolder = null;

            adapter.notifyDataSetChanged();
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
            startHolder.dayBean.state = Constant.DayState.END;
            startHolder.holder.changeSelect(startHolder.dayBean);
            startHolder = null;
            endHolder = null;
            return;
        }
        //5、如果两次点击的年份不一致
        if (startHolder.dayBean.year != dayBean.year) {
            //5-1、如果开始年份  > 本次选择的年份，则代表用户向前选择了，重新赋值开始日期
            if (startHolder.dayBean.year > dayBean.year) {
                reSetStartSelect(holder, dayBean);
            }
            //5-2、如果开始年份 < 本次选择的年份，则需要选中两年之间的所有日期，这种情况一般发生在用户在12月份选择时，可能发生跨到明年一月的情况
            //这种情况无需关心月份和日期，只需要从 开始日期 直接遍历到 开始日期 的年底，接着从 结束日期 的年初，遍历到 结束日期 即可。
            else {
                endHolder = new SelectHolder(holder, dayBean);
                //遍历所有数据，取出的数据是一个月一个月的
                for (MonthBean monthBean : monthBeanList) {
                    //5-2-1、如果开始年份匹配，需要选中开始年份后面的所有日期
                    if (monthBean.year == startHolder.dayBean.year) {
                        //如果月份数据=开始月，找出开始的这一天，并选中这一天之后的天数
                        if (monthBean.month == startHolder.dayBean.month) {
                            //获取开始月的总天数
                            int startMonthCount = YTDateUtils.getDayCountOfMonth(monthBean.year, monthBean.month);
                            for (int startDay = startHolder.dayBean.day - 1; startDay < startMonthCount; startDay++) {
                                if (startDay == startHolder.dayBean.day - 1) {
                                    monthBean.dayList.get(startDay).state = Constant.DayState.START;
                                } else {
                                    monthBean.dayList.get(startDay).state = Constant.DayState.SELECT;
                                }
                            }
                        }
                        //如果数据月份的月 > 开始日期的月份，并且年份相同，则需要选中整个月
                        else if (monthBean.month > startHolder.dayBean.month) {
                            int dayCount = YTDateUtils.getDayCountOfMonth(monthBean.year, monthBean.month);
                            for (int i = 0; i < dayCount; i++) {
                                monthBean.dayList.get(i).state = Constant.DayState.SELECT;
                            }

                        }
                    }
                    //5-2-3、如果结束年份匹配，需要选中结束年份的1月1日-具体的结束日期
                    else if (monthBean.year == endHolder.dayBean.year) {
                        //找到结束月份，并找到具体结束日期
                        if (monthBean.month == endHolder.dayBean.month) {
                            for (int endDay = 0; endDay < endHolder.dayBean.day; endDay++) {
                                if (endDay == endHolder.dayBean.day - 1) {
                                    monthBean.dayList.get(endDay).state = Constant.DayState.END;
                                } else {
                                    monthBean.dayList.get(endDay).state = Constant.DayState.SELECT;
                                }
                            }
                        }
                        //如果数据月份的月 < 结束日期的 月份，并且年份相同，则需要选中整个月
                        else if (monthBean.month < endHolder.dayBean.month) {
                            int dayCount = YTDateUtils.getDayCountOfMonth(monthBean.year, monthBean.month);
                            for (int i = 0; i < dayCount; i++) {
                                monthBean.dayList.get(i).state = Constant.DayState.SELECT;
                            }
                        }
                    }
                    //5-2-3、如果年份没有匹配上，代表着用户选择了跨N年的数据
                    // 类似 2017年12月5日 - 2019年1月5日，5-2-1选中了2017年12月5日-12月31日的数据，5-2-2选中了2019年1月1日-1月5日的数据
                    //还剩下2018年整年的数据没有选择，那么整个年份的数据选择任务就会到这里
                    //所以我们无需进行任何处理，直接选中即可
                    else {
                        for (int i = 0; i < monthBean.dayList.size(); i++) {
                            monthBean.dayList.get(i).state = Constant.DayState.SELECT;
                        }

                    }

                }

                adapter.notifyDataSetChanged();
                //选择成功的回调
                selectListener.onSelectSuccess(startHolder.dayBean, endHolder.dayBean);
            }
            return;
        }
        //6、如果两次点击的日期月份不一致
        if (startHolder.dayBean.month != dayBean.month) {
            //6-1、如果 开始日期的月份 > 本次点击的月份，代表着用户向前选择，则重新赋值开始日期
            if (startHolder.dayBean.month > dayBean.month) {
                reSetStartSelect(holder, dayBean);
            }
            //6-2、如果 开始日期的月份  < 本次点击的月份，代表着用户跨月选择，需要选中两个日期中的所有条目
            //比如我选中的是2017年10月15日-2017年12月5日，我们需要选中的是：2017年10月15日-2017年10月31日、2017年11月、2017年12月1日-2017年12月5日
            else {
                endHolder = new SelectHolder(holder, dayBean);
                //遍历所有数据
                for (MonthBean monthBean : monthBeanList) {
                    //选中 开始月份的
                    if (monthBean.month == startHolder.dayBean.month) {
                        int startMonthCount = YTDateUtils.getDayCountOfMonth(startHolder.dayBean.year, startHolder.dayBean.month);
                        for (int startMonthDay = startHolder.dayBean.day - 1; startMonthDay < startMonthCount; startMonthDay++) {
                            if (startMonthDay == startHolder.dayBean.day - 1) {
                                monthBean.dayList.get(startMonthDay).state = Constant.DayState.START;
                            } else {
                                monthBean.dayList.get(startMonthDay).state = Constant.DayState.SELECT;
                            }
                        }
                    }
                    //选中 结束月的最后天数
                    else if (monthBean.month == endHolder.dayBean.month) {
                        for (int endMonthDay = 0; endMonthDay < dayBean.day; endMonthDay++) {
                            if (endMonthDay == dayBean.day - 1) {
                                monthBean.dayList.get(endMonthDay).state = Constant.DayState.END;
                            } else {
                                monthBean.dayList.get(endMonthDay).state = Constant.DayState.SELECT;

                            }
                        }
                    }
                    //选中 中间月份的
                    else if (monthBean.month > startHolder.dayBean.month && monthBean.month < endHolder.dayBean.month) {
                        int startMonthCount = YTDateUtils.getDayCountOfMonth(monthBean.year, monthBean.month);
                        for (int i1 = 0; i1 < startMonthCount; i1++) {
                            monthBean.dayList.get(i1).state = Constant.DayState.SELECT;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                //选择成功的回调
                selectListener.onSelectSuccess(startHolder.dayBean, endHolder.dayBean);
            }
            return;
        }
        //7、如果两次点击的日期只有天数不一致
        if (startHolder.dayBean.day != dayBean.day) {
            //7-1、如果开始日期 > 本次选择的日期，代表着用户向前选择了，则重新赋值开始日期
            if (startHolder.dayBean.day > dayBean.day) {
                reSetStartSelect(holder, dayBean);
            }
            //7-2、如果开始日期 < 本次选择的日期，则将本次选择赋值给结束日期，并选中两个日期中的所有条目
            else {
                endHolder = new SelectHolder(holder, dayBean);

                for (MonthBean monthBean : monthBeanList) {
                    if (monthBean.month == startHolder.dayBean.month && monthBean.year == startHolder.dayBean.year) {
                        for (int i = startHolder.dayBean.day - 1; i < endHolder.dayBean.day; i++) {
                            if (i == startHolder.dayBean.day - 1) {
                                monthBean.dayList.get(i).state = Constant.DayState.START;
                            } else if (i == endHolder.dayBean.day - 1) {
                                monthBean.dayList.get(i).state = Constant.DayState.END;
                            } else {
                                monthBean.dayList.get(i).state = Constant.DayState.SELECT;
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                //选择成功的回调
                selectListener.onSelectSuccess(startHolder.dayBean, endHolder.dayBean);
            }
            return;
        }


    }

    /**
     * 重新赋值开始坐标
     */
    private void reSetStartSelect(MonthView.CurrentDayItemHolder holder, DayBean dayBean) {
        //将上个开始日期重置为默认状态
        startHolder.dayBean.state = Constant.DayState.NORMAL;
        //将最新的开始日期改为开始状态
        dayBean.state = Constant.DayState.START;
        startHolder = new SelectHolder(holder, dayBean);
        //刷新ui
        adapter.notifyDataSetChanged();

    }

    public void onDestory() {
        startHolder = null;
        endHolder = null;
        monthBeanList = null;
        adapter = null;
    }

    class SelectHolder {
        MonthView.CurrentDayItemHolder holder;
        DayBean dayBean;

        public SelectHolder(MonthView.CurrentDayItemHolder holder, DayBean dayBean) {
            this.holder = holder;
            this.dayBean = dayBean;

        }


    }
}
