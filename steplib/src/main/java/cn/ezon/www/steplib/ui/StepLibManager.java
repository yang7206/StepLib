package cn.ezon.www.steplib.ui;

import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.ezon.www.steplib.service.StepService;
import cn.ezon.www.steplib.utils.StoreUtils;

/**
 * Created by Administrator on 2016/4/28.
 */
public class StepLibManager {

    private List<WeakReference<OnButtonClickListener>> list = new ArrayList<WeakReference<OnButtonClickListener>>();

    private static StepLibManager mInstance;

    private StepLibManager() {
    }

    public synchronized static StepLibManager getInstance() {
        if (mInstance == null) {
            mInstance = new StepLibManager();
        }
        return mInstance;
    }

    public StepLibManager init(Context context) {
        Intent startIntent = new Intent(context, StepService.class);
        context.startService(startIntent);
        return this;
    }

    public synchronized void registerListener(OnButtonClickListener listener) {
        boolean isFind = false;
        for (WeakReference<OnButtonClickListener> weak : list) {
            if (weak != null && weak.get() != null && weak.get() == listener) {
                isFind = true;
                break;
            }
        }
        if (!isFind) {
            list.add(new WeakReference<OnButtonClickListener>(listener));
        }
    }

    public synchronized void unregisterListener(OnButtonClickListener listener) {
        WeakReference<OnButtonClickListener> removeWeak = null;
        for (WeakReference<OnButtonClickListener> weak : list) {
            if (weak != null && weak.get() != null && weak.get() == listener) {
                removeWeak = weak;
                break;
            }
        }
        if (removeWeak != null) {
            list.remove(removeWeak);
        }
    }

    public void setUserMale(Context context, boolean isMale) {
        StoreUtils.setUserMale(context, isMale);
    }

    protected void onCompetitionClick() {
        for (WeakReference<OnButtonClickListener> weak : list) {
            if (weak != null && weak.get() != null) {
                weak.get().onCompetitionClick();
            }
        }
    }

    protected void onStadiumClick() {
        for (WeakReference<OnButtonClickListener> weak : list) {
            if (weak != null && weak.get() != null) {
                weak.get().onStadiumClick();
            }
        }
    }

    protected void onBuyEzonWatchClick() {
        for (WeakReference<OnButtonClickListener> weak : list) {
            if (weak != null && weak.get() != null) {
                weak.get().onBuyEzonWatchClick();
            }
        }
    }

    public interface OnButtonClickListener {

        void onCompetitionClick();

        void onStadiumClick();

        void onBuyEzonWatchClick();

    }
}
