package org.j8ql;

import java.sql.ParameterMetaData;
import java.sql.SQLException;

public class ConvertContext {
	private final String columnName;
	private final int 	 columnSqlType;
	private final String columnSqlTypeName;
	private final String columnClassName;

	public ConvertContext(ResultSetColumn resultSetColumn) {
		columnName = resultSetColumn.columnName;
		columnSqlType = resultSetColumn.sqlType;
		columnSqlTypeName = resultSetColumn.sqlTypeName;
		columnClassName = resultSetColumn.columnClassName;
	}

	public ConvertContext(ParameterMetaData pmd, int idx){
		try{
			columnName = null;
			columnSqlType = pmd.getParameterType(idx);
			columnSqlTypeName = pmd.getParameterTypeName(idx);
			columnClassName = pmd.getParameterClassName(idx);
		}catch (SQLException e) {
			throw new RSQLException(e);
		}
	}

	// --------- Getters --------- //
	public String getColumnClassName() {
		return columnClassName;
	}

	public String getColumnName() {
		return columnName;
	}

	public int getColumnSqlType() {
		return columnSqlType;
	}

	public String getColumnSqlTypeName() {
		return columnSqlTypeName;
	}
	// --------- /Getters --------- //
}
