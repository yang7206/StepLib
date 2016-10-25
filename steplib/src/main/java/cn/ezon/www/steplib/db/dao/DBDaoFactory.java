package cn.ezon.www.steplib.db.dao;


import android.content.Context;

public class DBDaoFactory {
    private static StepEntityDao stepEntityDao;

    public synchronized static StepEntityDao getStepEntityDao(Context context) {
        if (stepEntityDao == null) {
            stepEntityDao = new StepEntityDao(context);
        }
        return stepEntityDao;
    }

}
