/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

/**
 * <p></p>
 */
public class Join {

	public enum Type{
		inner("INNER JOIN"), left("LEFT JOIN"), right("RIGHT JOIN"), full("FULL JOIN");
		private String sql;

		Type(String sql) {
			this.sql = sql;
		}

		public String toString(){
			return sql;
		}
	}

	public final Type   type;
	public final String joinTable;
	public final String joinTableColumn;
	public final String onTable;
	public final String onTableColumn;

	public Join(Type type, String joinTable, String joinTableColumn, String onTable, String onTableColumn) {
		this.type = type;
		this.joinTable = joinTable;
		this.joinTableColumn = joinTableColumn;
		this.onTable = onTable;
		this.onTableColumn = onTableColumn;
	}


}
