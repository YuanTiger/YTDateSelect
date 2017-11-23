package cardlop.my.com.ytdateselect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;

import cardlop.my.com.ytdateselect.R;
import cardlop.my.com.ytdateselect.base.BaseRecyclerViewHolder;
import cardlop.my.com.ytdateselect.bean.DayBean;
import cardlop.my.com.ytdateselect.bean.MonthBean;
import cardlop.my.com.ytdateselect.view.IMonthViewItemSelect;
import cardlop.my.com.ytdateselect.view.MonthView;
import cardlop.my.com.ytdateselect.view.MonthViewManager;

/**
 * Author：mengyuan
 * Date  : 2017/11/22上午9:52
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class ScrollDateActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ArrayList<MonthBean> dataList;

    private RecyclerViewAdapter adapter;

    private MonthViewManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scroll_date);


        parseIntent();


        initRecyclerView();


    }


    /**
     * Intent解析
     */
    private void parseIntent() {
        //获取2017年11月-2018年11月的日期数据
        if (getIntent() == null) {
            Toast.makeText(this, "数据异常, 请使用ScrollDateActivity.go()", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        dataList = getIntent().getParcelableArrayListExtra("data");
        if (dataList == null || dataList.size() == 0) {
            Toast.makeText(this, "数据异常, 请使用ScrollDateActivity.go()", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }


    /**
     * 初始化页面
     */
    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (adapter == null) {
            adapter = new RecyclerViewAdapter();
            recyclerView.setAdapter(adapter);
            //设置固定顶部的头Adapter
            recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));

        } else {
            adapter.notifyDataSetChanged();
        }

        //选择事件的回调
        manager = new MonthViewManager(adapter, dataList, new IMonthViewItemSelect() {
            @Override
            public void onSelectSuccess(DayBean startDay, DayBean endDay) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("data", dataList);
                String desc = startDay.year + "-" + startDay.month + "-" + startDay.day + " 至 " + endDay.year + "-" + endDay.month + "-" + endDay.day;
                intent.putExtra("desc", desc);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public static void go(Context context, ArrayList<MonthBean> dataList) {
        Intent intent = new Intent(context, ScrollDateActivity.class);
        intent.putParcelableArrayListExtra("data", dataList);
        context.startActivity(intent);
    }

    public static void goResult(AppCompatActivity activity, ArrayList<MonthBean> dataList, int requestCode) {
        Intent intent = new Intent(activity, ScrollDateActivity.class);
        intent.putParcelableArrayListExtra("data", dataList);
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (manager != null) {
            manager.onDestory();
        }
    }

    //--------------------------------Adapter--------------------------------
    //--------------------------------Adapter--------------------------------
    //--------------------------------Adapter--------------------------------
    //StickyRecyclerHeadersAdapter：固定顶部的实现
    public class RecyclerViewAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> implements StickyRecyclerHeadersAdapter<BaseRecyclerViewHolder> {


        /**
         * StickyRecyclerHeadersAdapter中的方法
         * 下标为几的条目需要固定头，这边直接返回position，代表每个条目我都需要固定头
         *
         * @param position 当前下标，不算固定头，是正常的条目下标
         * @return
         */
        @Override
        public long getHeaderId(int position) {
            return position;
        }

        /**
         * StickyRecyclerHeadersAdapter中的方法
         * 相当于onCreateViewHolder
         *
         * @param parent
         * @return
         */
        @Override
        public BaseRecyclerViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            return new HeaderHolder(R.layout.item_scroll_data_header, parent);

        }

        /**
         * StickyRecyclerHeadersAdapter中的方法
         * 相当于onBindViewHolder
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindHeaderViewHolder(BaseRecyclerViewHolder holder, int position) {
            holder.refreshData(dataList.get(position), position);
        }

        @Override
        public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MonthItemHolder(R.layout.item_scroll_data_item, parent);

        }

        @Override
        public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
            holder.refreshData(dataList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }
    }

    //--------------------------------Holder--------------------------------
    //--------------------------------Holder--------------------------------
    //--------------------------------Holder--------------------------------
    //固定头的Holder
    public class HeaderHolder extends BaseRecyclerViewHolder<MonthBean> {

        private TextView tv_title;


        public HeaderHolder(int viewId, ViewGroup parent) {
            super(viewId, parent);

            tv_title = itemView.findViewById(R.id.tv_title);
        }

        @Override
        public void refreshData(MonthBean data, int position) {
            tv_title.setText(data.year + "-" + data.month);
        }
    }

    //日历Holder
    public class MonthItemHolder extends BaseRecyclerViewHolder<MonthBean> {
        private MonthView monthView;

        public MonthItemHolder(int viewId, ViewGroup parent) {
            super(viewId, parent);
            monthView = itemView.findViewById(R.id.month_view);
        }

        @Override
        public void refreshData(MonthBean data, int position) {


            //初始化条目
            manager.bind(data, monthView);
        }
    }
}
