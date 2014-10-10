/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.generator;

import org.j8ql.DB;
import org.j8ql.query.DeleteQuery;
import org.j8ql.def.TableDef;

import java.util.List;


/**
 * <p></p>
 */
public class PGDeleteGenerator extends PGGenerator {


	public PGDeleteGenerator(DB db) {
		super(db);
	}

	public String sql(DeleteQuery deleteBuilder) {
		TableDef tableDef = db.getTableDef(deleteBuilder);
		StringBuilder sql = new StringBuilder("delete from");
		sql.append(" \"").append(tableDef.getName()).append('"');

		// WHERE
		processWhereSql(deleteBuilder, tableDef, sql);

		// RETURNING
		processReturningSql(deleteBuilder, tableDef, sql);

		return sql.toString();
	}

	public Object[] values(DeleteQuery builder) {
		TableDef tableDef = db.getTableDef(builder);
		List values = getWhereValues(builder, tableDef);
		return (values != null) ? values.toArray() : null;
	}
}
