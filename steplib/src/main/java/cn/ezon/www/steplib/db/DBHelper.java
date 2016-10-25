package cn.ezon.www.steplib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import cn.ezon.www.steplib.db.entity.StepEntity;


public class DBHelper extends DataBaseHelper {
    private static final String DB_NAME = "ezon_step";
    private static final int VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, VERSION);
    }
}
