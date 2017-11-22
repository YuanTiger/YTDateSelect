package view;

import java.util.List;

import cardlop.my.com.ytdateselect.bean.DayBean;

/**
 * Author：mengyuan
 * Date  : 2017/11/22下午2:05
 * E-Mail:mengyuanzz@126.com
 * Desc  :MonthView的接口回调
 */

public abstract class IMonthViewItemSelect {

    /**
     * 日期选择的点击拦截
     * 最早触发，可以选择拦截掉此事件
     *
     * @param dayBean 选择的具体日期
     * @return 返回true代表拦截，反之放行
     */
    boolean onInterceptSelect(DayBean dayBean) {
        return false;
    }


    /**
     * 第二次选择日期的事件拦截，可以用来做自定义
     *
     * @param startDay 上一次选择的日期
     * @param seletDay 这一次选择的日期
     * @return
     */
    boolean onInterceptSelect(DayBean startDay, DayBean seletDay) {
        return false;
    }

    /**
     * 选择成功回调
     *
     * @param startDay
     * @param endDay
     */
    abstract void onSelectSuccess(DayBean startDay, DayBean endDay);


}
