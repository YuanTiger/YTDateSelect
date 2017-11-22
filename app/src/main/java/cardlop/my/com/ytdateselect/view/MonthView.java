package cardlop.my.com.ytdateselect.view;

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
    private MonthBean dataMonth;
    //是否显示本月中包含的其他月份日期
    private boolean isShowOtherMotn = true;
    //是否显示星期的标题
    private boolean isShowWeekTitle = false;
    //每天的点击事件回调
    private DayItemClickListener dayItemClickListener;

    private MonthAdapter adapter;

    private int beforeSize;//本月数据之前的条目长度
    private int daySize;//本月数据的条目长度
    private int lastSize;//本月数据之后的条目长度

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
        dataMonth = data;


        beforeSize = dataMonth.beforeList == null ? 0 : dataMonth.beforeList.size();
        daySize = dataMonth.dayList == null ? 0 : dataMonth.dayList.size();
        lastSize = dataMonth.lastList == null ? 0 : dataMonth.lastList.size();

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

    //--------------------------------Adapter--------------------------------
    //--------------------------------Adapter--------------------------------
    //--------------------------------Adapter--------------------------------
    public class MonthAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

        private final int ITEM_CURRENT = 101;//本月的条目
        private final int ITEM_OTHER = 102;//非本月的条目


        @Override
        public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ITEM_CURRENT:
                    return new CurrentDayItemHolder(R.layout.item_month_view, parent);
                case ITEM_OTHER:
                    return new OhterDayItemHolder(R.layout.item_month_view, parent);

            }
            return null;
        }

        @Override
        public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
            if (position < beforeSize) {
                holder.refreshData(dataMonth.beforeList.get(position), position);
            } else if (position < beforeSize + daySize) {
                holder.refreshData(dataMonth.dayList.get(position - beforeSize), position - beforeSize);
            } else if (position < beforeSize + daySize + lastSize) {
                holder.refreshData(dataMonth.lastList.get(position - beforeSize - daySize), position - beforeSize - daySize);
            }
        }

        @Override
        public int getItemCount() {

            return beforeSize + daySize + lastSize;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < beforeSize) {
                return ITEM_OTHER;
            }
            if (position < beforeSize + daySize) {
                return ITEM_CURRENT;
            }
            return ITEM_OTHER;
        }
    }

    //--------------------------------Holder--------------------------------
    //--------------------------------Holder--------------------------------
    //--------------------------------Holder--------------------------------

    //非本月的数据条目
    public class OhterDayItemHolder extends BaseRecyclerViewHolder<DayBean> {
        private LinearLayout ll_content;
        private TextView tv_day;
        private TextView tv_desc;


        public OhterDayItemHolder(int viewId, ViewGroup parent) {
            super(viewId, parent);

            ll_content = itemView.findViewById(R.id.ll_content);

            tv_day = itemView.findViewById(R.id.tv_day);

            tv_desc = itemView.findViewById(R.id.tv_desc);
        }

        @Override
        public void refreshData(DayBean data, int position) {
            //如果不需要显示其他月份的数据
            if (!isShowOtherMotn) {
                ll_content.setVisibility(View.INVISIBLE);
                return;
            }
            ll_content.setVisibility(View.VISIBLE);
            ll_content.setEnabled(false);
            tv_day.setEnabled(false);
            tv_day.setText(String.valueOf(data.day));

        }
    }

    //本月的数据条目
    public class CurrentDayItemHolder extends BaseRecyclerViewHolder<DayBean> {

        private LinearLayout ll_content;
        private TextView tv_day;
        private TextView tv_desc;

        private DayBean data;


        public CurrentDayItemHolder(int viewId, ViewGroup parent) {
            super(viewId, parent);

            ll_content = itemView.findViewById(R.id.ll_content);

            tv_day = itemView.findViewById(R.id.tv_day);

            tv_desc = itemView.findViewById(R.id.tv_desc);
        }

        @Override
        public void refreshData(final DayBean data, int position) {

            this.data = data;

            //改变选中状态
            changeSelect(data.isSelect);
            //设置日期
            tv_day.setText(String.valueOf(data.day));
            //描述文字
            tv_desc.setText(data.desc);

            ll_content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dayItemClickListener != null) {
                        dayItemClickListener.itemClick(MonthView.this, CurrentDayItemHolder.this, data);
                    }
                }
            });

        }


        public void changeSelect(boolean selectFlag) {
            if (ll_content.isSelected() == selectFlag) {
                return;
            }
            data.isSelect = selectFlag;
            ll_content.setSelected(selectFlag);
            tv_day.setSelected(selectFlag);
            tv_desc.setText("");
        }

        public void setDesc(String desc) {
            tv_desc.setText(desc);
        }
    }

    //--------------------------------Listener--------------------------------
    //--------------------------------Listener--------------------------------
    //--------------------------------Listener--------------------------------
    public interface DayItemClickListener {
        void itemClick(MonthView monthView, CurrentDayItemHolder holder, DayBean data);
    }

    public void setDayItemClickListener(DayItemClickListener listener) {
        dayItemClickListener = listener;
    }

}
