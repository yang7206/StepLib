package cn.ezon.www.steplib.db.utils;

public class ColumnEntity {

	private boolean isPrimaryKey = false;
	private String columnName;
	private Class<?> type;

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDatabaseFieldType(boolean isPrimaryKey) {
		if (type == Integer.class) {
			return isPrimaryKey ? "INTEGER PRIMARY KEY AUTOINCREMENT" : "INTEGER";
		} else if (type == Long.class) {
			return "NUMBER default 0";
		}
		return "VARCHAR2";
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

}
