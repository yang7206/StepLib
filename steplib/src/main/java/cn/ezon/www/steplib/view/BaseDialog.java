package cn.ezon.www.steplib.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import cn.ezon.www.steplib.utils.DeviceUtils;
import cn.ezon.www.steplib.utils.ResUtils;


public abstract class BaseDialog extends Dialog implements View.OnClickListener {
    protected Context context;

    public BaseDialog(Context context) {
        super(context, ResUtils.getStyleRes(context, "ezon_dialog_style"));
        this.context = context;
    }

    protected abstract View onInflateView(LayoutInflater inflater);

    protected void onInitView(View view) {
        // TODO: 子类实现
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = onInflateView(inflater);
        onInitView(view);

        setContentView(view);
    }

    @Override
    public void show() {
        super.show();
        setHPading30Content();
    }

    protected void setHPading30Content() {
        LayoutParams lp = this.getWindow().getAttributes();
        lp.width = DeviceUtils.getScreenWidth(context) - DeviceUtils.dip2px(context, 60);
        lp.height = LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);
    }

    protected void setWarpContent() {
        LayoutParams lp = this.getWindow().getAttributes();
        lp.width = LayoutParams.WRAP_CONTENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);
    }

    protected void setPaddingFullContent() {
        LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int) (DeviceUtils.getScreenWidth(context) - DeviceUtils.dip2px(context, 30));
        lp.height = (int) (DeviceUtils.getScreenHeigth(context) - DeviceUtils.dip2px(context, 50));
        this.getWindow().setAttributes(lp);
    }

    protected void setFullContent() {

        Rect frame = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int) (DeviceUtils.getScreenWidth(context));
        lp.height = (int) (DeviceUtils.getScreenHeigth(context) - statusBarHeight);
        this.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        // TODO: 子类实现
    }

    protected void setClickable(View parent, int... ids) {
        for (int i = 0; i < ids.length; i++) {
            View v = parent.findViewById(ids[i]);
            if (v != null) {
                v.setOnClickListener(this);
            }
        }
    }
}
