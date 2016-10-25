package cn.ezon.www.steplib.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.ezon.www.steplib.db.DBHelper;
import cn.ezon.www.steplib.db.entity.StepEntity;

public class StepEntityDao {
    private SQLiteDatabase database;

    private final String QUERY_ALL_SQL = "SELECT * FROM StepEntity";
    private final String QUERY_DAY_SQL = "SELECT * FROM StepEntity WHERE day = ?";

    protected StepEntityDao(Context ctx) {
        database = new DBHelper(ctx).getWritableDatabase();
    }

    private String getCsValue(Cursor cs, String column) {
        return cs.getString(cs.getColumnIndex(column));
    }

    private List<StepEntity> resolveCursor(Cursor cs) {
        List<StepEntity> data = new ArrayList<>();
        try {
            for (cs.moveToFirst(); !cs.isAfterLast(); cs.moveToNext()) {
                StepEntity stepEntity = new StepEntity();
                stepEntity.setId(cs.getInt(cs.getColumnIndex("id")));
                stepEntity.setDay(getCsValue(cs, "day"));
                stepEntity.setStep(cs.getInt(cs.getColumnIndex("step")));
                stepEntity.setActiveMin(cs.getInt(cs.getColumnIndex("activeMin")));
                stepEntity.setHourStep(getCsValue(cs, "hourStep"));
                data.add(stepEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cs != null) {
                cs.close();
            }
        }
        return data;
    }

    public List<StepEntity> queryAllData() {
        Cursor cs = database.rawQuery(QUERY_ALL_SQL, null);
        return resolveCursor(cs);
    }

    public StepEntity queryDayStepNotReturnNull(String day) {
        Cursor cs = database.rawQuery(QUERY_DAY_SQL, new String[]{day});
        List<StepEntity> data = resolveCursor(cs);
        return data.size() == 0 ? emptyStepEntity(day) : data.get(0);
    }

    public StepEntity queryDayStep(String day) {
        Cursor cs = database.rawQuery(QUERY_DAY_SQL, new String[]{day});
        List<StepEntity> data = resolveCursor(cs);
        return data.size() == 0 ? null: data.get(0);
    }

    private StepEntity emptyStepEntity(String day){
        StepEntity entity = new StepEntity();
        entity.setDay(day);
        entity.setActiveMin(0);
        entity.setStep(0);
        entity.setId(0);
        return entity;
    }

    private boolean hasData(String day) {
        Cursor cs = database.rawQuery(QUERY_DAY_SQL, new String[]{day});
        return cs.getCount() > 0;
    }

    public void addOrUpdate(StepEntity stepEntity) {
        if (hasData(stepEntity.getDay())) {
            update(stepEntity);
        } else {
            insert(stepEntity);
        }
    }

    private void insert(StepEntity stepEntity) {
        ContentValues values = new ContentValues();
        values.put("day", stepEntity.getDay());
        values.put("step", stepEntity.getStep());
        values.put("activeMin", stepEntity.getActiveMin());
        values.put("hourStep", stepEntity.getHourStep());
        database.insert("StepEntity", null, values);
    }

    private void update(StepEntity stepEntity) {
        ContentValues values = new ContentValues();
        values.put("step", stepEntity.getStep());
        values.put("activeMin", stepEntity.getActiveMin());
        values.put("hourStep", stepEntity.getHourStep());
        database.update("StepEntity", values, "day = ? ", new String[]{stepEntity.getDay()});
    }

}
