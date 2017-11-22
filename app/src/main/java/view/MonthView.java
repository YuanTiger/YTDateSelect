package view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cardlop.my.com.ytdateselect.R;
import cardlop.my.com.ytdateselect.base.BaseRecyclerViewHolder;
import cardlop.my.com.ytdateselect.bean.DayBean;
import cardlop.my.com.ytdateselect.bean.MonthBean;

/**
 * Author：mengyuan
 * Date  : 2017/11/22上午9:55
 * E-Mail:mengyuanzz@126.com
 * Desc  :每个月数据展示的RecyclerView
 * 一个 MonthView 只能展示一个月的数据
 * MonthView 的LayoutManager是一行7个的GridLayoutManager
 */

public class MonthView extends RecyclerView {

    //本月的数据
    private MonthBean monthBean;
    //是否显示本月中包含的其他月份日期
    private boolean isShowOtherMotn = false;
    //是否显示星期的标题
    private boolean isShowWeekTitle = false;
    //每天的点击事件回调
    private DayItemClickListener dayItemClickListener;

    private MonthAdapter adapter;


    public MonthView(Context context) {
        super(context);
        initView(context);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public MonthView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        this.setLayoutManager(new GridLayoutManager(context, 7, LinearLayoutManager.VERTICAL, false));
    }

    /**
     * 设置数据，并初始化
     *
     * @param data
     */
    public void setData(MonthBean data) {
        monthBean = data;

        initAdapter();
    }

    private void initAdapter() {
        if (adapter == null) {
            adapter = new MonthAdapter();
            this.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 选中两个日期中的所有日期
     */
    public void addSelectedDay(DayBean startDay,DayBean endDay) {

    }

    /**
     * 清除所有选中的日期,直接进行RecyclerVie的刷新即可
     * 因为Holder默认未选中
     */
    public void clearSelectedDays() {
        for (DayBean dayBean : monthBean.dayList) {
            dayBean.isSelect = false;
        }
        initAdapter();
    }

    //--------------------------------Adapter--------------------------------
    //--------------------------------Adapter--------------------------------
    //--------------------------------Adapter--------------------------------
    public class MonthAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

        @Override
        public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DayItemHolder(R.layout.item_month_view, parent);
        }

        @Override
        public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
            holder.refreshData(monthBean.dayList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return monthBean.dayList == null ? 0 : monthBean.dayList.size();
        }
    }

    //--------------------------------Holder--------------------------------
    //--------------------------------Holder--------------------------------
    //--------------------------------Holder--------------------------------
    public class DayItemHolder extends BaseRecyclerViewHolder<DayBean> {

        private LinearLayout ll_content;
        private TextView tv_day;
        private TextView tv_desc;


        public DayItemHolder(int viewId, ViewGroup parent) {
            super(viewId, parent);

            ll_content = itemView.findViewById(R.id.ll_content);

            tv_day = itemView.findViewById(R.id.tv_day);

            tv_desc = itemView.findViewById(R.id.tv_desc);
        }

        @Override
        public void refreshData(final DayBean data, int position) {
            //如果不需要显示其他月份的数据
            if (!isShowOtherMotn) {
                //如果该条目不是这个月的天数数据
                if (!data.isBelongMonth) {
                    ll_content.setVisibility(View.INVISIBLE);
                    return;
                }
            }
            ll_content.setVisibility(View.VISIBLE);
            //改变选中状态
            changeSelect(data, data.isSelect);
            //设置日期
            tv_day.setText(String.valueOf(data.day));


            ll_content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dayItemClickListener != null) {
                        dayItemClickListener.itemClick(DayItemHolder.this, data);
                    }
                }
            });

        }


        public void changeSelect(DayBean data, boolean selectFlag) {
            if (data.isSelect == selectFlag && ll_content.isSelected() == selectFlag) {
                return;
            }
            data.isSelect = selectFlag;
            ll_content.setSelected(selectFlag);
            tv_day.setSelected(selectFlag);
        }

        public void setDesc(String desc) {
            tv_desc.setText(desc);
        }
    }

    //--------------------------------Listener--------------------------------
    //--------------------------------Listener--------------------------------
    //--------------------------------Listener--------------------------------
    public interface DayItemClickListener {
        void itemClick(DayItemHolder holder, DayBean data);
    }

    public void setDayItemClickListener(DayItemClickListener listener) {
        dayItemClickListener = listener;
    }

}
