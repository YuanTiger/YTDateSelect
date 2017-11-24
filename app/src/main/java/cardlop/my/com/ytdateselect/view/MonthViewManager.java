package cardlop.my.com.ytdateselect.view;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import cardlop.my.com.ytdateselect.bean.DayBean;
import cardlop.my.com.ytdateselect.bean.MonthBean;
import cardlop.my.com.ytdateselect.bean.MonthListBean;
import cardlop.my.com.ytdateselect.constant.Constant;
import cardlop.my.com.ytdateselect.utils.YTDateUtils;

/**
 * Author：mengyuan
 * Date  : 2017/11/22下午2:07
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class MonthViewManager {


    private RecyclerView.Adapter adapter;

    //选择的开始日期
    private SelectHolder startHolder;

    //选择的结束日期
    private SelectHolder endHolder;


    private IMonthViewItemSelect selectListener;


    private MonthListBean monthListBean;

    public MonthViewManager(RecyclerView.Adapter adapter, IMonthViewItemSelect selectListener) {
        this.adapter = adapter;
        this.selectListener = selectListener;

        monthListBean = MonthListBean.getInstance();

    }

    public void bind(int position, MonthView monthView) {
        //初始化monthView
        monthView.setData(position);
        monthView.setDayItemClickListener(new MonthView.DayItemClickListener() {
            @Override
            public void itemClick(int monthViewPosition, int dayPosition) {
                if (selectListener == null) {
                    return;
                }

                selectItem(monthViewPosition, dayPosition);
            }
        });

    }

    /**
     * 连续模式下的回调处理
     */
    private void selectItem(int monthPosition, int dayPosition) {
        //1、是否拦截本次选择,一个参数的拦截方法
        if (selectListener.onInterceptSelect(monthListBean.getDayBean(monthPosition, dayPosition))) {
            return;
        }
        //2、如果 开始日期 为空，将本次选择的数据赋值给开始日期，并清除之前回填的数据
        if (startHolder == null) {
            //2-1、如果需要清除数据(回填数据)，先清除数据
            if (monthListBean.isNeedClear) {

                monthListBean.isNeedClear = false;

                for (int i = monthListBean.startPosition; i < monthListBean.startPosition + monthListBean.refreshCount; i++) {
                    monthListBean.dataList.get(i).clearSelect();
                }

                adapter.notifyItemRangeChanged(monthListBean.startPosition, monthListBean.refreshCount);

                monthListBean.startPosition = 0;
                monthListBean.refreshCount = 0;
            }
            //2-2、如果不需要清除数据，就直接将本次点击日期赋值到开始日期上
            startHolder = new SelectHolder(monthPosition, dayPosition);
            startHolder.changeState(Constant.DayState.START);
            adapter.notifyItemChanged(monthPosition);
            return;
        }
        //到这里，代表着 开始日期不为空，结束日期为空，需要对本次点击的日期进行详细的判断
        //3、是否拦截本次选择,两个参数的拦截方法
        if (selectListener.onInterceptSelect(startHolder.getDayBean(), monthListBean.getDayBean(monthPosition, dayPosition))) {
            return;
        }

        //4、如果两次点击的日期完全一致,认为用户只想选择这一天的日期
        if (startHolder.getDayBean().year == monthListBean.getDayBean(monthPosition, dayPosition).year
                && startHolder.getDayBean().month == monthListBean.getDayBean(monthPosition, dayPosition).month
                && startHolder.getDayBean().day == monthListBean.getDayBean(monthPosition, dayPosition).day) {
            endHolder = new SelectHolder(monthPosition, dayPosition);
            endHolder.changeState(Constant.DayState.END);
            adapter.notifyItemChanged(monthPosition);
            for (int position = 0; position < monthListBean.dataList.size(); position++) {
                if (monthListBean.dataList.get(position).year == startHolder.getDayBean().year && monthListBean.dataList.get(position).month == startHolder.getDayBean().month) {
                    monthListBean.startPosition = position;
                    monthListBean.refreshCount = 1;
                }
            }
            selectSuccess();
            return;
        }
        //5、如果两次点击的日期只有天数不一致,因为步骤4的判断，到这里仅需这两个条件，就可以判断出只有天数不一致
        if (startHolder.getDayBean().year == monthListBean.getDayBean(monthPosition, dayPosition).year
                && startHolder.getDayBean().month == monthListBean.getDayBean(monthPosition, dayPosition).month) {
            changeUiByDay(monthPosition, dayPosition);

            return;
        }
        //6、如果两次点击的月份不一致
        if (startHolder.getDayBean().year == monthListBean.getDayBean(monthPosition, dayPosition).year) {
            changeUiByMonth(monthPosition, dayPosition);

            return;
        }
        //7、如果两次点击的年份不一致
        changeUiByYear(monthPosition, dayPosition);

    }

    /**
     * 当两次选择年份不一致时的数据处理具体逻辑
     *
     * @param monthPosition 第二次点击的月下标
     * @param dayPosition   第二次点击的天下标
     */
    private void changeUiByYear(int monthPosition, int dayPosition) {
        //7-1、如果开始年份  > 本次选择的年份，则代表用户向前选择了，重新赋值开始日期
        if (startHolder.getDayBean().year > monthListBean.getDayBean(monthPosition, dayPosition).year) {
            reSetStartSelect(monthPosition, dayPosition);
        }
        //7-2、如果开始年份 < 本次选择的年份，则需要选中两年之间的所有日期，这种情况一般发生在用户在12月份选择时，可能发生跨到明年一月的情况
        //这种情况无需关心月份和日期，只需要从 开始日期 直接遍历到 开始日期 的年底，接着从 结束日期 的年初，遍历到 结束日期 即可。
        else {
            endHolder = new SelectHolder(monthPosition, dayPosition);
            //遍历所有数据，取出的数据是一个月一个月的
            for (int position = 0; position < monthListBean.dataList.size(); position++) {
                //7-2-1、如果开始年份匹配，需要选中开始年份后面的所有日期
                if (monthListBean.dataList.get(position).year == startHolder.getDayBean().year) {
                    //如果月份数据=开始月，找出开始的这一天，并选中这一天之后的天数
                    if (monthListBean.dataList.get(position).month == startHolder.getDayBean().month) {
                        //获取开始月的总天数
                        int startMonthCount = YTDateUtils.getDayCountOfMonth(monthListBean.dataList.get(position).year, monthListBean.dataList.get(position).month);
                        for (int startDay = startHolder.getDayBean().day - 1; startDay < startMonthCount; startDay++) {
                            if (startDay == startHolder.getDayBean().day - 1) {
                                monthListBean.dataList.get(position).dayList.get(startDay).state = Constant.DayState.START;
                                monthListBean.startPosition = position;
                            } else {
                                monthListBean.dataList.get(position).dayList.get(startDay).state = Constant.DayState.SELECT;
                            }
                        }
                    }
                    //如果数据月份的月 > 开始日期的月份，并且年份相同，则需要选中整个月
                    else if (monthListBean.dataList.get(position).month > startHolder.getDayBean().month) {
                        int dayCount = YTDateUtils.getDayCountOfMonth(monthListBean.dataList.get(position).year, monthListBean.dataList.get(position).month);
                        for (int i = 0; i < dayCount; i++) {
                            monthListBean.dataList.get(position).dayList.get(i).state = Constant.DayState.SELECT;
                        }

                    }
                }
                //7-2-2、如果结束年份匹配，需要选中结束年份的1月1日-具体的结束日期
                else if (monthListBean.dataList.get(position).year == endHolder.getDayBean().year) {
                    //找到结束月份，并找到具体结束日期
                    if (monthListBean.dataList.get(position).month == endHolder.getDayBean().month) {
                        for (int endDay = 0; endDay < endHolder.getDayBean().day; endDay++) {
                            if (endDay == endHolder.getDayBean().day - 1) {
                                monthListBean.dataList.get(position).dayList.get(endDay).state = Constant.DayState.END;
                                monthListBean.refreshCount = position - monthListBean.startPosition + 1;
                            } else {
                                monthListBean.dataList.get(position).dayList.get(endDay).state = Constant.DayState.SELECT;
                            }
                        }
                    }
                    //如果数据月份的月 < 结束日期的 月份，并且年份相同，则需要选中整个月
                    else if (monthListBean.dataList.get(position).month < endHolder.getDayBean().month) {
                        int dayCount = YTDateUtils.getDayCountOfMonth(monthListBean.dataList.get(position).year, monthListBean.dataList.get(position).month);
                        for (int i = 0; i < dayCount; i++) {
                            monthListBean.dataList.get(position).dayList.get(i).state = Constant.DayState.SELECT;
                        }
                    }
                }

                //7-2-3、如果年份没有匹配上，代表着用户选择了跨N年的数据
                // 类似 2017年12月5日 - 2019年1月5日，7-2-1选中了2017年12月5日-12月31日的数据，7-2-2选中了2019年1月1日-1月5日的数据
                //还剩下2018年整年的数据没有选择，那么整个年份的数据选择任务就会到这里
                //所以我们无需进行任何处理，直接选中即可
                else {
                    for (int i = 0; i < monthListBean.dataList.get(position).dayList.size(); i++) {
                        monthListBean.dataList.get(position).dayList.get(i).state = Constant.DayState.SELECT;
                    }
                }

            }
            adapter.notifyItemRangeChanged(monthListBean.startPosition, monthListBean.refreshCount);
            //选择成功的回调
            selectSuccess();
        }
    }

    /**
     * 当两次选择年份一致，月份不一致时的数据处理具体逻辑
     *
     * @param monthPosition 第二次点击的月下标
     * @param dayPosition   第二次点击的天下标
     */
    private void changeUiByMonth(int monthPosition, int dayPosition) {
        //6-1、如果 开始日期的月份 > 本次点击的月份，代表着用户向前选择，则重新赋值开始日期
        if (startHolder.getDayBean().month > monthListBean.getDayBean(monthPosition, dayPosition).month) {
            reSetStartSelect(monthPosition, dayPosition);

        }
        //6-2、如果 开始日期的月份  < 本次点击的月份，代表着用户跨月选择，需要选中两个日期中的所有条目
        //比如我选中的是2017年10月15日-2017年12月5日，我们需要选中的是：2017年10月15日-2017年10月31日、2017年11月、2017年12月1日-2017年12月5日
        else {
            endHolder = new SelectHolder(monthPosition, dayPosition);
            //遍历所有数据，取出的数据是一个月一个月的
            for (int position = 0; position < monthListBean.dataList.size(); position++) {
                if (monthListBean.dataList.get(position).year != startHolder.getDayBean().year) {
                    continue;
                }
                //选中 开始月份的
                if (monthListBean.dataList.get(position).month == startHolder.getDayBean().month) {
                    int startMonthCount = YTDateUtils.getDayCountOfMonth(startHolder.getDayBean().year, startHolder.getDayBean().month);
                    for (int startMonthDay = startHolder.getDayBean().day - 1; startMonthDay < startMonthCount; startMonthDay++) {
                        if (startMonthDay == startHolder.getDayBean().day - 1) {
                            monthListBean.dataList.get(position).dayList.get(startMonthDay).state = Constant.DayState.START;
                            monthListBean.startPosition = position;
                        } else {
                            monthListBean.dataList.get(position).dayList.get(startMonthDay).state = Constant.DayState.SELECT;
                        }
                    }
                }
                //选中 结束月的最后天数
                else if (monthListBean.dataList.get(position).month == endHolder.getDayBean().month) {
                    for (int endMonthDay = 0; endMonthDay < monthListBean.getDayBean(monthPosition, dayPosition).day; endMonthDay++) {
                        if (endMonthDay == monthListBean.getDayBean(monthPosition, dayPosition).day - 1) {
                            monthListBean.dataList.get(position).dayList.get(endMonthDay).state = Constant.DayState.END;
                            monthListBean.refreshCount = position - monthListBean.startPosition + 1;
                        } else {
                            monthListBean.dataList.get(position).dayList.get(endMonthDay).state = Constant.DayState.SELECT;

                        }
                    }
                }
                //选中 中间月份的
                else if (monthListBean.dataList.get(position).month > startHolder.getDayBean().month && monthListBean.dataList.get(position).month < endHolder.getDayBean().month) {
                    int startMonthCount = YTDateUtils.getDayCountOfMonth(monthListBean.dataList.get(position).year, monthListBean.dataList.get(position).month);
                    for (int i1 = 0; i1 < startMonthCount; i1++) {
                        monthListBean.dataList.get(position).dayList.get(i1).state = Constant.DayState.SELECT;
                    }
                }
            }
            adapter.notifyItemRangeChanged(monthListBean.startPosition, monthListBean.refreshCount);
            //选择成功的回调
            selectSuccess();
        }
    }

    /**
     * 当两次选择只有天数不一致时的具体处理逻辑
     *
     * @param monthPosition 第二次点击的月下标
     * @param dayPosition   第二次点击的天下标
     */
    private void changeUiByDay(int monthPosition, int dayPosition) {
        //5-1、如果开始日期 > 本次选择的日期，代表着用户向前选择了，则重新赋值开始日期
        if (startHolder.getDayBean().day > monthListBean.getDayBean(monthPosition, dayPosition).day) {
            reSetStartSelect(monthPosition, dayPosition);
        }
        //5-2、如果开始日期 < 本次选择的日期，则将本次选择赋值给结束日期，并选中两个日期中的所有条目
        else {
            endHolder = new SelectHolder(monthPosition, dayPosition);

            //遍历所有数据，取出的数据是一个月一个月的
            for (int position = 0; position < monthListBean.dataList.size(); position++) {
                if (monthListBean.dataList.get(position).month == startHolder.getDayBean().month && monthListBean.dataList.get(position).year == startHolder.getDayBean().year) {
                    for (int i = startHolder.getDayBean().day - 1; i < endHolder.getDayBean().day; i++) {
                        if (i == startHolder.getDayBean().day - 1) {
                            monthListBean.dataList.get(position).dayList.get(i).state = Constant.DayState.START;
                            monthListBean.startPosition = position;
                            monthListBean.refreshCount = 1;
                        } else if (i == endHolder.getDayBean().day - 1) {
                            monthListBean.dataList.get(position).dayList.get(i).state = Constant.DayState.END;
                        } else {
                            monthListBean.dataList.get(position).dayList.get(i).state = Constant.DayState.SELECT;
                        }
                    }
                }
            }
            adapter.notifyItemChanged(monthListBean.startPosition);
            selectSuccess();

        }
    }

    private void selectSuccess() {
        monthListBean.isNeedClear = true;

        selectListener.onSelectSuccess(startHolder.getDayBean(), endHolder.getDayBean());
    }


    /**
     * 重新赋值开始位置
     */
    private void reSetStartSelect(int monthPosition, int dayPosition) {
        //判断是否是同一个月
        boolean isSameMonth = startHolder.monthPosition == monthPosition;
        //将上个开始日期重置为默认状态
        startHolder.changeState(Constant.DayState.NORMAL);
        //如果不是同一个月，刷新该条目
        if (!isSameMonth) {
            adapter.notifyItemChanged(startHolder.monthPosition);
        }
        //将最新的开始日期改为开始状态
        startHolder = new SelectHolder(monthPosition, dayPosition);
        startHolder.changeState(Constant.DayState.START);
        adapter.notifyItemChanged(startHolder.monthPosition);

    }

    public void onDestory() {
        startHolder = null;
        endHolder = null;
        adapter = null;
    }

    class SelectHolder {
        int monthPosition;
        int dayPosition;

        public SelectHolder(int monthPosition, int dayPosition) {
            this.monthPosition = monthPosition;
            this.dayPosition = dayPosition;
        }


        public void changeState(int state) {
            monthListBean.getDayBean(monthPosition, dayPosition).state = state;
        }

        public DayBean getDayBean() {
            return monthListBean.getDayBean(monthPosition, dayPosition);
        }
    }
}
