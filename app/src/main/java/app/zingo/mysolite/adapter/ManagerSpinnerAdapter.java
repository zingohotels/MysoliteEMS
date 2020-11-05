package app.zingo.mysolite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.R;

public class ManagerSpinnerAdapter extends BaseAdapter {
    private Context context;
    private ArrayList< Employee > mList = new ArrayList<>();

    public ManagerSpinnerAdapter(Context context, ArrayList< Employee > mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int pos) {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.adapter_department_spinner,viewGroup,false);
        }

        TextView mCategoryName = view.findViewById(R.id.category_spinner_item);
        mCategoryName.setText(mList.get(pos).getEmployeeName());

        return view;
    }
}