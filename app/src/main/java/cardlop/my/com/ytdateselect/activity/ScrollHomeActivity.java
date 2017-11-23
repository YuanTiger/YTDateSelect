package cardlop.my.com.ytdateselect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cardlop.my.com.ytdateselect.R;
import cardlop.my.com.ytdateselect.bean.MonthBean;
import cardlop.my.com.ytdateselect.utils.YTDateUtils;

/**
 * Author：mengyuan
 * Date  : 2017/11/23下午2:40
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class ScrollHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_desc;

    private LinearLayout ll_date;

    //数据初始化、保存都在这里，因为要回显
    private ArrayList<MonthBean> dataList;

    private static final int REQUEST_CODE_CHANGE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scroll_home);


        tv_desc = findViewById(R.id.tv_desc);

        ll_date = findViewById(R.id.ll_date);

        ll_date.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_date:
                if (dataList == null) {
                    dataList = YTDateUtils.getOneYearMonthData();
                }
                ScrollDateActivity.goResult(this, dataList, REQUEST_CODE_CHANGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQUEST_CODE_CHANGE://数据发生变化
                        //将最新数据赋值
                        dataList = data.getParcelableArrayListExtra("data");
                        String desc = data.getStringExtra("desc");
                        if(TextUtils.isEmpty(desc)){
                            tv_desc.setText("请选择使用的时间段(北京时间)");
                            tv_desc.setTextColor(getResources().getColor(R.color.color_bbbbbb));
                        }else{
                            tv_desc.setText(desc);
                            tv_desc.setTextColor(getResources().getColor(R.color.color_333333));
                        }

                        break;
                }
                break;
        }

    }
}
