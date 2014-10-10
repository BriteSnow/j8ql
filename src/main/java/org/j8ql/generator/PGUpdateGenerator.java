/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.generator;

import com.google.common.collect.ObjectArrays;
import org.j8ql.DB;
import org.j8ql.query.UpdateQuery;
import org.j8ql.def.TableDef;

import java.util.List;

/**
 * <p></p>
 */
public class PGUpdateGenerator extends PGGenerator {


	public PGUpdateGenerator(DB db) {
		super(db);
	}

	public String sql(UpdateQuery updateBuilder) {

		TableDef tableDef = db.getTableDef(updateBuilder);

		StringBuilder sql = new StringBuilder("update");
		sql.append(" \"").append(tableDef.getName()).append("\" set");

		// TODO: needs to handle the objectValue case.

		// COLUMNS VALUES
		boolean first = true;
		for (String name : getColumns(updateBuilder)){
			if (!first){
			 	sql.append(", ");
			}else{
				first = false;
			}
			sql.append(" \"").append(name).append("\" = ").append("?");
		}

		// WHERE
		processWhereSql(updateBuilder, tableDef, sql);

		// RETURNING
		processReturningSql(updateBuilder, tableDef, sql);

		return sql.toString();
	}

	public Object[] values(UpdateQuery updateBuilder){
		TableDef tableDef = db.getTableDef(updateBuilder);
		// get the values from the "set" section of the update.
		Object[] vals = getValues(updateBuilder).toArray();
		// if we have a where, contact both array.
		List whereVals = getWhereValues(updateBuilder,tableDef);
		if (whereVals != null) {
			vals = ObjectArrays.concat(vals,whereVals.toArray(),Object.class);
		}
		return vals;
	}
}
