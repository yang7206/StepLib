package cn.ezon.www.steplib.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import cn.ezon.www.steplib.utils.ResUtils;

/**
 * Created by Administrator on 2016/4/28.
 */
public class BuyEzonWatchDialog extends BaseDialog {

    private View buyView;
    private View closeView;

    public BuyEzonWatchDialog(Context context) {
        super(context);
    }

    @Override
    protected View onInflateView(LayoutInflater inflater) {
        View v = inflater.inflate(ResUtils.getLayoutRes(context, "ezon_dialog_buy"), null);
        closeView = v.findViewById(ResUtils.getIdRes(context, "ezon_iv_close"));
        buyView = v.findViewById(ResUtils.getIdRes(context, "ezon_bg_buy_watch_down"));
        closeView.setOnClickListener(this);
        buyView.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v == buyView) {
            listener.onBuyClick();
        }
        dismiss();
    }

    private OnBuyButtonClickListener listener;

    public void setOnBuyButtonClickListener(OnBuyButtonClickListener l) {
        this.listener = l;
    }

    public interface OnBuyButtonClickListener {

        void onBuyClick();
    }
}
