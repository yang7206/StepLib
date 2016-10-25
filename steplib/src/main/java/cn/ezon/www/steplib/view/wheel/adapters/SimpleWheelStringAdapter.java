package cn.ezon.www.steplib.view.wheel.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SimpleWheelStringAdapter extends AbstractWheelTextAdapter {
    private List<String> list;
    private String units;

    public SimpleWheelStringAdapter(Context context, List<String> list, String units) {
        super(context, android.R.layout.simple_list_item_1, NO_RESOURCE);
        this.list = list;
        this.units = units;
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        View view = super.getItem(index, cachedView, parent);
        TextView tView = (TextView) view.findViewById(android.R.id.text1);
        configureTextView(tView);
        tView.setText(getItemText(index));
        return view;
    }


    @Override
    public int getItemsCount() {
        return list.size();
    }

    @Override
    protected CharSequence getItemText(int index) {
        return list.get(index) + units;
    }
}