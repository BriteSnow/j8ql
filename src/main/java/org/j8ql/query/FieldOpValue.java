/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

import org.j8ql.DB;
import org.j8ql.util.Immutables;
import org.j8ql.util.SqlUtils;

import java.util.Arrays;
import java.util.List;

/**
 * <p></p>
 */
public class FieldOpValue implements Elem {

	private final FieldOp fieldOp;
	private final Object value;

	private final List<Elem> elems;

	public FieldOpValue(String fieldKey, Object value) {
		this(new FieldOp(fieldKey), value);
	}

	public FieldOpValue(FieldOp fieldOp, Object value) {
		this.fieldOp = fieldOp;
		this.value = value;
		elems = Immutables.of(this);
	}


	public Object getValue(){
		return value;
	}

	// --------- Elem Implementation --------- //
	@Override
	public StringBuilder buildSql(DB db, StringBuilder sb){
		// append the escaped column name
		sb.append(SqlUtils.escapeColumnName(fieldOp.name));

		// do the "IS NULL" or "IS NOT NULL" if value is null
		if (value == null){
			if ("=".equals(fieldOp.operator)){
				return sb.append(" IS NULL");
			}else if ("!=".equals(fieldOp.operator)){
				return sb.append(" IS NOT NULL");
			}
		}

		// append the operator
		sb.append(" ").append(fieldOp.operator);

		// append the value expression ("?" the 'value expression' or the subSelect)
		sb.append(" ");

		if (value instanceof SelectQuery) {
			sb.append("(").append(db.sql((SelectQuery) value)).append(")");
		}else if (fieldOp.valueFunction != null){
			sb.append(fieldOp.valueFunction);
		}else{
			sb.append("?"); // default if no valueFunction
		}
		return sb;
	}

	@Override
	public List buildValues(DB db, List values) {
		// if it is a "IS ... NULL", then, we do not need to add the value
		if (value == null && ("=".equals(fieldOp.operator) || "!=".equals(fieldOp.operator))){
			// do not not add this null value to the list, as the statement will have already the "NULL" value
		}
		else{
			if (value instanceof SelectQuery){
				SelectQuery subSelect = (SelectQuery)value;
				Object[] subValues = db.values(subSelect);
				if (subValues != null) {
					values.addAll(Arrays.asList(subValues));
				}
			}else{
				values.add(value);
			}
		}
		return values;
	}

	@Override
	public List<Elem> getElems() {
		return elems;
	}
	// --------- /Elem Implementation --------- //


	@Override
	public String toString(){
		return fieldOp.toString() + " " + value;
	}
}
