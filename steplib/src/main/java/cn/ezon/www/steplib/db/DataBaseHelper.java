package cn.ezon.www.steplib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.ezon.www.steplib.db.entity.StepEntity;
import cn.ezon.www.steplib.db.utils.DBUtils;

/**
 * @author yangch 2014-4-22 描述：数据库创建和更新基类
 */
public abstract class DataBaseHelper extends SQLiteOpenHelper {

    /**
     * 构造函数
     *
     * @param context         上下文
     * @param databaseName    数据库名字
     * @param databaseVersion 数据库版本号
     */
    public DataBaseHelper(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
    }

    /**
     * 获取数据库表（需要创建的表）
     *
     * @return
     */
    public String[] getDBTables() {
        String[] tables = {
                StepEntity.class.getName()
        };
        return tables;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        GreateTables(db);
    }

    /**
     * 更新表
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int arg2, int arg3) {
        DelAllTables(db);
        GreateTables(db);
    }

    /**
     * 创建表方法
     */
    public void GreateTables(SQLiteDatabase db) {
        String tables[] = getDBTables();
        for (int i = 0; i < tables.length; i++) {
            try {
                Class<?> cls = Class.forName(tables[i]);
                DBUtils.createTableFromClass(cls, db);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除所有表
     */
    private void DelAllTables(SQLiteDatabase db) {
        try {
            String[] tables = getDBTables();
            if (tables != null) {
                for (int i = 0; i < tables.length; i++) {
                    Class<?> cls = Class.forName(tables[i]);
                    DBUtils.dropTable(cls, db);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
