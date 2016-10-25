package cn.ezon.www.steplib.core;

/**
 * Created by Administrator on 2016/4/29.
 */
public interface OnSensorChangeListener {

    /**
     *  步数递增一步
     *
     * @param time
     */
    void onStepChange(long time);

    /**
     *
     *  新的步数
     *
     * @param time
     * @param step
     */
    void onNewStep(long time,int step);
}
