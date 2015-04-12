/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.generator;

import org.j8ql.DB;
import org.j8ql.Record;
import org.j8ql.query.Condition;
import org.j8ql.query.IUDQuery;
import org.j8ql.query.Where;
import org.j8ql.def.TableDef;
import org.j8ql.util.SqlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 */
public class PGGenerator extends Generator {


	public PGGenerator(DB db) {
		super(db);
	}

	protected void processWhereSql(Where builder, TableDef tableDef, StringBuilder sql) {
		Condition where = builder.getWhere();
		if (where == null && builder.getWhereId() != null){
			where = db.getWhereIdCondition(tableDef, builder.getWhereId());
		}
		if (where != null){
			sql.append(" where ");
			sql.append(where.toSql(db));
		}
	}

	protected void processReturningSql(IUDQuery IUDBuilder, TableDef tableDef, StringBuilder sql){
		if (IUDBuilder.hasReturning()){
			sql.append(" returning ");
			List<String> returnColumnNames;
			if (IUDBuilder.isReturningId()){
				returnColumnNames = new ArrayList<>(tableDef.getIdColumnNames());
			}else{
				returnColumnNames = IUDBuilder.getReturningColumns();
			}


			if ((returnColumnNames == null || returnColumnNames.size() == 0) && IUDBuilder.getAsClass() != Record.class){
				sql.append("*");
				// TODO: need to handle the case when asClass is Record
				// TODO: also, when returning a non Record asClass, should just add the columns that have a matching writer property in the class.
			}else{
				boolean firstIdCol = true;
				for (String columnName : returnColumnNames) {
					if (firstIdCol) {
						firstIdCol = false;
					} else {
						sql.append(" , ");
					}
					sql.append(SqlUtils.escapeColumnName(columnName));
				}
			}

		}
	}




}
