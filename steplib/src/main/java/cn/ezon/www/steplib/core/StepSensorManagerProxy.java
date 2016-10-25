package cn.ezon.www.steplib.core;

import android.content.Context;
import android.os.PowerManager;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/4/28.
 */
public class StepSensorManagerProxy {

    private static StepSensorManagerProxy mInstance;
    private PowerManager.WakeLock mWakeLock;
    private Context context;

    private StepSensorManagerProxy() {
    }

    public synchronized static StepSensorManagerProxy getInstance() {
        if (mInstance == null) {
            mInstance = new StepSensorManagerProxy();
        }
        return mInstance;
    }

    public StepSensorManagerProxy init(Context context) {
        this.context = context;
        StepSensorManager.getInstance().init(context);
        return this;
    }


    public StepSensorManagerProxy setOnSensorChangeListener(
            OnSensorChangeListener onSensorChangeListener) {
        StepSensorManager.getInstance().setOnSensorChangeListener(onSensorChangeListener);
        return this;
    }

    public void start() {
        getLock(context);
        StepSensorManager.getInstance().start();
    }

    private synchronized PowerManager.WakeLock getLock(Context context) {
        if (mWakeLock != null) {
            if (mWakeLock.isHeld())
                mWakeLock.release();
            mWakeLock = null;
        }

        if (mWakeLock == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    StepSensorManagerProxy.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour >= 23 || hour <= 6) {
                mWakeLock.acquire(5000);
            } else {
                mWakeLock.acquire(300000);
            }
        }

        return (mWakeLock);
    }

    public void destory() {
        StepSensorManager.getInstance().recycle();
    }

}
