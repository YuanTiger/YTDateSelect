package cardlop.my.com.ytdateselect.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * AUTHOR:       Yuan.Meng
 * E-MAIL:       mengyuanzz@126.com
 * CREATE-TIME:  16/8/31/下午5:20
 * DESC:
 */
public class BaseRecyclerViewHolder<T> extends RecyclerView.ViewHolder {


    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public BaseRecyclerViewHolder(int viewId, ViewGroup parent) {
        super(((LayoutInflater) parent.getContext().getSystemService(parent.getContext().LAYOUT_INFLATER_SERVICE)).inflate(viewId, parent, false));
    }

    public void refreshData(T data, int position) {

    }


}
