package cn.ezon.www.steplib.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ezon.www.steplib.utils.DeviceUtils;
import cn.ezon.www.steplib.utils.ResUtils;
import cn.ezon.www.steplib.utils.StoreUtils;
import cn.ezon.www.steplib.utils.TextUtils;
import cn.ezon.www.steplib.view.wheel.WheelView;
import cn.ezon.www.steplib.view.wheel.adapters.SimpleWheelStringAdapter;

/**
 * Created by Administrator on 2016/4/28.
 */
public class EzonSetDialog extends BaseDialog {
    private List<String> weightDataSource;
    private WheelView weightWheelView;
    private SimpleWheelStringAdapter weightWheelViewAdapter;

    private List<String> heightDataSource;
    private WheelView heightWheelView;
    private SimpleWheelStringAdapter heightWheelViewAdapter;

    public EzonSetDialog(Context context) {
        super(context);
    }

    @Override
    protected View onInflateView(LayoutInflater inflater) {
        View v = inflater.inflate(ResUtils.getLayoutRes(context, "ezon_dialog_set"), null);
        buildHeightWheelView(v);
        buildWeightWheelView(v);
        buildTextView(v);
        setupData();
        return v;
    }

    private void buildWeightWheelView(View v) {
        weightWheelView = (WheelView) v.findViewById(ResUtils.getIdRes(context, "ezon_wheelview_set_weight"));
        weightDataSource = new ArrayList<String>();
        for (int i = 30; i < 121; i++) {
            weightDataSource.add(i + "");
        }
        weightWheelViewAdapter = new SimpleWheelStringAdapter(context, weightDataSource, "");
        weightWheelView.setMinimumHeight(DeviceUtils.dip2px(context, 100));
        weightWheelView.setVisibleItems(3);
        weightWheelView.setViewAdapter(weightWheelViewAdapter);

    }

    private void buildHeightWheelView(View v) {
        heightWheelView = (WheelView) v.findViewById(ResUtils.getIdRes(context, "ezon_wheelview_set_height"));
        heightDataSource = new ArrayList<String>();
        for (int i = 80; i < 221; i++) {
            heightDataSource.add(i + "");
        }
        heightWheelViewAdapter = new SimpleWheelStringAdapter(context, heightDataSource, "");
        heightWheelView.setMinimumHeight(DeviceUtils.dip2px(context, 100));
        heightWheelView.setVisibleItems(3);
        heightWheelView.setViewAdapter(heightWheelViewAdapter);
    }

    private void setupData() {
        if (weightWheelView != null) {
            int weight = StoreUtils.getUserWeight(context);
            weightWheelView.setCurrentItem(weight - 30);
        }
        if (weightWheelView != null) {
            int height = StoreUtils.getUserHeight(context);
            heightWheelView.setCurrentItem(height - 80);
        }
    }

    @Override
    public void show() {
        super.show();
        setupData();
    }

    private TextView tv_ezon_cancel;
    private TextView tv_ezon_comfirm;

    private void buildTextView(View v) {
        tv_ezon_cancel = (TextView) v.findViewById(ResUtils.getIdRes(context, "ezon_tv_cancel"));
        tv_ezon_comfirm = (TextView) v.findViewById(ResUtils.getIdRes(context, "ezon_tv_comfirm"));
        tv_ezon_cancel.setOnClickListener(this);
        tv_ezon_comfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == tv_ezon_comfirm) {
            int weight = 60;
            int height = 170;
            try {
                weight = TextUtils.getInt(weightDataSource.get(weightWheelView.getCurrentItem()));
                height = TextUtils.getInt(heightDataSource.get(heightWheelView.getCurrentItem()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            StoreUtils.setUserHeight(context, height);
            StoreUtils.setUserWeight(context, weight);
        }
        dismiss();
    }
}
