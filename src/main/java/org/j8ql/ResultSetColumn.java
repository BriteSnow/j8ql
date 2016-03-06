/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

import org.postgresql.jdbc.PgResultSetMetaData;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * This is a packaged scoped Column Definition for a statement
 */
class ResultSetColumn {
	final int     cidx;
	final String  columnName;
	final String  tableName;
	final String  columnLabel;
	final int	  sqlType;
	final String  sqlTypeName;
	final String  columnClassName;

	final String  name;

	ResultSetColumn(ResultSetMetaData rsmd, int cidx) {
		try {
			this.cidx = cidx;
			name  = columnName = rsmd.getColumnName(cidx);
			tableName = rsmd.getTableName(cidx);
			columnLabel = rsmd.getColumnLabel(cidx);
			sqlType = rsmd.getColumnType(cidx);
			sqlTypeName = rsmd.getColumnTypeName(cidx);
			columnClassName = rsmd.getColumnClassName(cidx);
		} catch (SQLException e) {
			throw new RSQLException(e);
		}
	}

	public String toString() {
		return "cidx: " + cidx + " tableName: " + tableName + " columnLabel: " + columnLabel + " columnName: " +
				columnName + " typeName: " + sqlTypeName + " type: " + sqlType + " columnClassName: " + columnClassName;

		// This would require PgResultSetMetaData
		//+ "\t\t baseTableName: " + baseTableName +  "\t\t baseColumndName: " + baseColumnName;
	}
}
