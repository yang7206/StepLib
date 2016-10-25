package cn.ezon.www.steplib.core;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.ezon.www.steplib.db.dao.DBDaoFactory;
import cn.ezon.www.steplib.db.entity.StepEntity;
import cn.ezon.www.steplib.service.StepService;
import cn.ezon.www.steplib.utils.TextUtils;

/**
 * Created by Administrator on 2016/4/28.
 */
public class StepOperater {

    private static StepOperater mInstance;
    private StepService stepService;

    private ExecutorService thread;

    private StepOperater() {
        thread = Executors.newSingleThreadExecutor();
    }

    public synchronized static StepOperater getInstance() {
        if (mInstance == null) {
            mInstance = new StepOperater();
        }
        return mInstance;
    }


    public StepOperater init(StepService context) {
        this.stepService = context;
        restoreData();
        return this;
    }

    public void destory() {
        if (thread != null) {
            thread.shutdown();
            thread = null;
        }
    }

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    private String getDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(time));
    }

    private String getDateMin(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(time));
    }

    private String getDateHour(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        return sdf.format(new Date(time));
    }

    public void restoreData() {
        StepEntity stepEntity = DBDaoFactory.getStepEntityDao(stepService).queryDayStepNotReturnNull(getTodayDate());
        stepService.sendStep(stepEntity);
    }

    private StepEntity getStepEntity(String day) {
        return DBDaoFactory.getStepEntityDao(stepService).queryDayStep(day);
    }

    public void onNewStep(long time, int step) {
        thread.submit(new OperateRunnable(time, step));
    }

    public void onStepChange(long time) {
        thread.submit(new OperateRunnable(time));
    }

    private String lastTimeMin = "";

    private class OperateRunnable implements Runnable {
        private long time;
        private int step;

        private OperateRunnable(long time, int step) {
            this.time = time;
            this.step = step;
        }

        private OperateRunnable(long time) {
            this.time = time;
            this.step = -1;
        }

        @Override
        public void run() {
            Log.d("OperateRunnable", "time :" + time + ",step :" + step);
            String timeDay = getDate(time);
            String timeMin = getDateMin(time);
            String timeHour = getDateHour(time);
            StepEntity entity = getStepEntity(timeDay);
            if (entity == null) {
                entity = new StepEntity();
                entity.setDay(getTodayDate());
            }
            if (step != -1) {
                entity.setStep(step);

                String hourStep[] = entity.getHourStep().split(",");
                int hour = TextUtils.getInt(timeHour);
                int allStep = 0;
                int s = TextUtils.getInt(hourStep[hour]);
                for (int i = 0; i < hour + 1; i++) {
                    allStep += TextUtils.getInt(hourStep[i]);
                }
                hourStep[hour] = (s + step - allStep) + "";
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < hourStep.length; i++) {
                    sb.append(hourStep[i] + ",");
                }
                entity.setHourStep(sb.substring(0, sb.length() - 1));
            } else {
                entity.stepIncrease();
                String hourStep[] = entity.getHourStep().split(",");
                int hour = TextUtils.getInt(timeHour);
                int s = TextUtils.getInt(hourStep[hour]);
                s++;
                hourStep[hour] = s + "";
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < hourStep.length; i++) {
                    sb.append(hourStep[i] + ",");
                }
                entity.setHourStep(sb.substring(0, sb.length() - 1));
            }
            if (!timeMin.equals(lastTimeMin)) {
                entity.activeMinIncrease();
            }
            lastTimeMin = timeMin;
            stepService.sendStep(entity);
            writeDatabase(entity);
        }
    }

    private void writeDatabase(StepEntity stepEntity) {
        DBDaoFactory.getStepEntityDao(stepService).addOrUpdate(stepEntity);
    }

}
