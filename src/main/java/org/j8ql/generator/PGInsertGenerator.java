/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.generator;

import org.j8ql.DB;
import org.j8ql.def.TableDef;
import org.j8ql.query.InsertQuery;
import org.j8ql.query.ValueObject;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * <p></p>
 */
public class PGInsertGenerator extends PGGenerator {


	public PGInsertGenerator(DB db) {
		super(db);
	}

	public String sql(InsertQuery insertBuilder) {

		TableDef tableDef = db.getTableDef(insertBuilder);
		StringBuilder sql = new StringBuilder("insert into ");
		sql.append('"').append(tableDef.getName()).append('"');

		// TODO: needs to handle the objectValue case.

		// COLUMNS VALUES
		// build the insert clause content "(column1,column2,column3,...) VALUES (?,?,?,...)"
		StringBuilder sbNames = new StringBuilder("(");
		StringBuilder sbValues = new StringBuilder("(");
		int i = 0;
		for (String name : getColumns(insertBuilder)){
			if (i > 0){
				sbNames.append(", ");
				sbValues.append(", ");
			}
			sbNames.append('"').append(name).append('"');
			sbValues.append("?");
			i++;
		}
		sbNames.append(")");
		sbValues.append(")");
		sql.append(' ').append(sbNames).append(" VALUES ").append(sbValues);

		if(insertBuilder.isOnConflictDoNothing()){
			sql.append(" ON CONFLICT DO NOTHING ");
		}

		// RETURNING
		processReturningSql(insertBuilder, tableDef, sql);

		return sql.toString();
	}

	public Object[] values(InsertQuery insertBuilder){
		return getValues(insertBuilder).toArray();
	}

	public List<List> batchValues(InsertQuery insertBuilder) {
		List<List> valuesList = insertBuilder.getBatchValues();
		// if there is no raw values List, then, built it from the batchValue.
		if (valuesList == null){
			Stream<ValueObject> s = insertBuilder.getBatchObjects().stream();
			valuesList = s.map(entity -> valuesFromValueObject(entity, insertBuilder)).collect(toList());
		}
		return valuesList;
	}
}
