package cn.ezon.www.steplib.db.utils;


import android.database.sqlite.SQLiteDatabase;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

	public static List<ColumnEntity> getTableColumn(Class<?> cls) {
		Field[] fields = cls.getDeclaredFields();
		List<ColumnEntity> columnList = new ArrayList<ColumnEntity>();
		for (Field field : fields) {
			Annotation[] file = field.getDeclaredAnnotations();
			if (file != null && file.length > 0) {
				DatabaseField anno = null;
				for (Annotation a : file) {
					if (a.annotationType() == DatabaseField.class) {
						anno = (DatabaseField) a;
						break;
					}
				}
				if (anno != null) {
					ColumnEntity column = new ColumnEntity();
					column.setPrimaryKey(anno.id());
					column.setColumnName(field.getName());
					column.setType(field.getType());
					columnList.add(column);
				}
			}
		}
		return columnList;
	}

	public static void createTableFromClass(Class<?> cls, SQLiteDatabase db) {
		List<ColumnEntity> columnList = getTableColumn(cls);

		StringBuffer sb = new StringBuffer(256);
		sb.append("CREATE TABLE IF NOT EXISTS ");
		sb.append(cls.getSimpleName());
		sb.append("(");
		boolean isFirst = true;
		for (ColumnEntity entity : columnList) {
			if (!isFirst) {
				sb.append(",");
			} else {
				isFirst = false;
			}

			sb.append(entity.getColumnName());
			sb.append(" ");
			sb.append(entity.getDatabaseFieldType(entity.isPrimaryKey()));
		}
		sb.append(")");
		db.execSQL(sb.toString());
	}

	public static void dropTable(Class<?> cls, SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + cls.getSimpleName());
	}

}
