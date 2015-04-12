/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.generator;

import org.j8ql.DB;
import org.j8ql.query.Condition;
import org.j8ql.query.IUQuery;
import org.j8ql.query.ValueObject;
import org.j8ql.query.Where;
import org.j8ql.def.TableDef;

import java.util.Arrays;
import java.util.List;

/**
 * <p></p>
 */
public class Generator {

	protected final DB db;

	public Generator(DB db) {
		this.db = db;
	}


	public List<String> getColumns(IUQuery builder) {
		TableDef tableDef = db.getTableDef(builder);

		List<String> columns = null;
		ValueObject valueObject = builder.getValueObject();
		List<ValueObject> batchObjects = builder.getBatchObjects();

		if (valueObject != null) {
			columns = valueObject.getColumns(db.mapper, tableDef, builder.getColumns());
		}else if (batchObjects != null){
			// if not columns, and we have batchObjects, then, try to get the columns from the first batchObject
			if (batchObjects.size() > 0){
				columns = batchObjects.get(0).getColumns(db.mapper, tableDef, builder.getColumns());
			}
			// TODO: might want to raise an exception here or later.
		}else{
			columns = builder.getColumns();
		}
		return columns;
	}

	public List getWhereValues(Where builder, TableDef tableDef){
		Condition where = builder.getWhere();
		if (where == null){
			Object whereIdObj = builder.getWhereId();
			if (whereIdObj != null){
				where = db.getWhereIdCondition(tableDef,whereIdObj);
			}
		}
		if (where != null) {
			return Arrays.asList(where.toValues(db));
		}else{
			return null;
		}
	}

	public List getValues(IUQuery iuBuilder) {
		List vals;
		ValueObject valueObject = iuBuilder.getValueObject();
		if (valueObject != null) {
			vals = valuesFromValueObject(valueObject, iuBuilder);
		}else{
			vals = iuBuilder.getValues();
		}
		return vals;
	}

	protected List<Object> valuesFromValueObject(ValueObject valueObject, IUQuery iuBuilder){
		TableDef tableDef = db.getTableDef(iuBuilder);
		return valueObject.getValues(db.mapper, tableDef, iuBuilder.getColumns());
	}


}
