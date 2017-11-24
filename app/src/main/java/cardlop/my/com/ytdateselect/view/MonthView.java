package cardlop.my.com.ytdateselect.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cardlop.my.com.ytdateselect.R;
import cardlop.my.com.ytdateselect.base.BaseRecyclerViewHolder;
import cardlop.my.com.ytdateselect.bean.DayBean;
import cardlop.my.com.ytdateselect.bean.MonthListBean;
import cardlop.my.com.ytdateselect.constant.Constant;

/**
 * Author：mengyuan
 * Date  : 2017/11/22上午9:55
 * E-Mail:mengyuanzz@126.com
 * Desc  :每个月数据展示的RecyclerView
 * 一个 MonthView 只能展示一个月的数据
 * MonthView 的LayoutManager是一行7个的GridLayoutManager
 */

public class MonthView extends RecyclerView {

    //月份下标，也就是外层RecyclerView的下标
    private int monthDataPosition;
    //是否显示本月中包含的其他月份日期
    private boolean isShowOtherMotn = false;
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
     * 日期数据为单例的，只需告诉我下标即可
     */
    public void setData(int position) {

        monthDataPosition = position;

        beforeSize = MonthListBean.getInstance().dataList.get(position).beforeList == null ? 0 : MonthListBean.getInstance().dataList.get(position).beforeList.size();
        daySize = MonthListBean.getInstance().dataList.get(position).dayList == null ? 0 : MonthListBean.getInstance().dataList.get(position).dayList.size();
        lastSize = MonthListBean.getInstance().dataList.get(position).lastList == null ? 0 : MonthListBean.getInstance().dataList.get(position).lastList.size();
        //设置RecyclerView的高度
        //计算行数
        int line = (beforeSize + daySize + lastSize) % 7 == 0 ? (beforeSize + daySize + lastSize) / 7 : (beforeSize + daySize + lastSize) / 7 + 1;
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.height = line * dp2Px(80);
        this.setLayoutParams(layoutParams);

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
     * 根据手机的分辨率从 dp 的单位 转成为 px
     */
    private int dp2Px(int dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.getContext().getResources().getDisplayMetrics()) + 0.5f);
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
                holder.refreshData(MonthListBean.getInstance().dataList.get(monthDataPosition).beforeList.get(position), position);
            } else if (position < beforeSize + daySize) {
                holder.refreshData(MonthListBean.getInstance().dataList.get(monthDataPosition).dayList.get(position - beforeSize), position - beforeSize);
            } else if (position < beforeSize + daySize + lastSize) {
                holder.refreshData(MonthListBean.getInstance().dataList.get(monthDataPosition).lastList.get(position - beforeSize - daySize), position - beforeSize - daySize);
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
            ll_content.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.color_f1f1f1));
            tv_day.setTextColor(itemView.getContext().getResources().getColor(R.color.color_cccccc));
            //设置日期
            tv_day.setText(String.valueOf(data.day));
            //描述文字
            tv_desc.setText(data.desc);

        }
    }

    //本月的数据条目
    public class CurrentDayItemHolder extends BaseRecyclerViewHolder<DayBean> {

        private LinearLayout ll_content;
        private TextView tv_day;
        private TextView tv_desc;

        public CurrentDayItemHolder(int viewId, ViewGroup parent) {
            super(viewId, parent);

            ll_content = itemView.findViewById(R.id.ll_content);

            tv_day = itemView.findViewById(R.id.tv_day);

            tv_desc = itemView.findViewById(R.id.tv_desc);
        }

        @Override
        public void refreshData(final DayBean data, final int position) {

            //设置日期
            tv_day.setText(String.valueOf(data.day));
            //描述文字
            tv_desc.setText(data.desc);
            //根据状态初始化ui
            changeState(data);


            ll_content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dayItemClickListener != null) {
                        dayItemClickListener.itemClick(monthDataPosition, position);
                    }
                }
            });

        }


        public void changeState(DayBean data) {
            switch (data.state) {
                case Constant.DayState.UNCLICK://不可点击状态
                    ll_content.setEnabled(false);
                    ll_content.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.color_f1f1f1));
                    tv_day.setTextColor(itemView.getContext().getResources().getColor(R.color.color_cccccc));
                    tv_desc.setText("");
                    break;
                case Constant.DayState.START://开始
                    ll_content.setEnabled(true);
                    ll_content.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.color_0070ff));
                    tv_day.setTextColor(itemView.getContext().getResources().getColor(R.color.color_ffffff));
                    tv_desc.setTextColor(itemView.getContext().getResources().getColor(R.color.color_ffffff));
                    tv_desc.setText("开始");
                    break;
                case Constant.DayState.END://结束
                    ll_content.setEnabled(true);
                    ll_content.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.color_0070ff));
                    tv_day.setTextColor(itemView.getContext().getResources().getColor(R.color.color_ffffff));
                    tv_desc.setTextColor(itemView.getContext().getResources().getColor(R.color.color_ffffff));
                    tv_desc.setText("结束");
                    break;
                case Constant.DayState.SELECT://选中
                    ll_content.setEnabled(true);
                    ll_content.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.color_9900ddff));
                    if (data.week == Constant.Week.SUN || data.week == Constant.Week.SAT) {
                        tv_day.setTextColor(itemView.getContext().getResources().getColor(R.color.color_f43531));
                    } else {
                        tv_day.setTextColor(itemView.getContext().getResources().getColor(R.color.color_333333));
                    }
                    tv_desc.setTextColor(itemView.getContext().getResources().getColor(R.color.color_f43531));
                    break;
                case Constant.DayState.NORMAL://默认状态
                    ll_content.setEnabled(true);
                    ll_content.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.color_ffffff));
                    if (data.week == Constant.Week.SUN || data.week == Constant.Week.SAT) {
                        tv_day.setTextColor(itemView.getContext().getResources().getColor(R.color.color_f43531));
                    } else {
                        tv_day.setTextColor(itemView.getContext().getResources().getColor(R.color.color_333333));
                    }
                    tv_desc.setTextColor(itemView.getContext().getResources().getColor(R.color.color_f43531));
                    tv_desc.setText(data.desc);
                    break;
            }

        }
    }

    //--------------------------------Listener--------------------------------
    //--------------------------------Listener--------------------------------
    //--------------------------------Listener--------------------------------
    public interface DayItemClickListener {
        /**
         * 每个日期的点击回调
         *
         * @param monthViewPosition 月份对应的Position，也就是外层RecyclerView的Position
         * @param dayPosition       点击天数的Position，也就是内层RecyclerView的Position
         */
        void itemClick(int monthViewPosition, int dayPosition);
    }

    public void setDayItemClickListener(DayItemClickListener listener) {
        dayItemClickListener = listener;
    }

}
