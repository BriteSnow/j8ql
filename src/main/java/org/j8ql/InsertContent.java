/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

/**
 * SQL Clause Query Helper for the content of an insert.
 */
public class InsertContent extends ClauseContent {

	public InsertContent(String[] names, Object[] values) {
		super(names, values);
	}

	@Override
	protected void init() {
		// build the insert clause content
		//"(column1,column2,column3,...) VALUES (?,?,?,...)"
		StringBuilder sbNames = new StringBuilder("(");
		StringBuilder sbValues = new StringBuilder("(");
		for (int i = 0, l = names.length; i <l; i++){
			if (i > 0){
				sbNames.append(", ");
				sbValues.append(", ");
			}
			sbNames.append('"').append(names[i]).append('"');
			sbValues.append("?");
		}
		sbNames.append(")");
		sbValues.append(")");

		sql = sbNames.append(" VALUES ").append(sbValues.toString()).toString();
	}
}
