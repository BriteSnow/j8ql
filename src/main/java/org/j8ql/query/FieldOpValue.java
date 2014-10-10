/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

import org.j8ql.util.Immutables;
import org.j8ql.util.SqlUtils;

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
	public StringBuilder buildSql(StringBuilder sb){
		return sb.append(SqlUtils.escapeColumnName(fieldOp.name)).append(" ").append(fieldOp.operator).append(" ?");
	}

	@Override
	public List buildValues(List values) {
		values.add(value);
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
