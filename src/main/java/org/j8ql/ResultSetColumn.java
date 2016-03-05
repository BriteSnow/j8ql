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
//	final String  baseColumnName;
//	final String  baseTableName;
	final String  tableName;
	final String  columnLabel;

	final String  name;

	ResultSetColumn(ResultSetMetaData rsmd, int cidx) {
		try {
			this.cidx = cidx;
			name  = columnName = rsmd.getColumnName(cidx);
			tableName = rsmd.getTableName(cidx);
			columnLabel = rsmd.getColumnLabel(cidx);

			//			baseColumnName = rsmd.getBaseColumnName(cidx);
			//			baseTableName = rsmd.getBaseTableName(cidx);
		} catch (SQLException e) {
			throw new RSQLException(e);
		}
	}

	public String toString() {
		return "cidx: " + cidx + " tableName: " + tableName + " columnLabel: " + columnLabel + " columnName: " +
				columnName;

		// This would require PgResultSetMetaData
		//+ "\t\t baseTableName: " + baseTableName +  "\t\t baseColumndName: " + baseColumnName;
	}
}
