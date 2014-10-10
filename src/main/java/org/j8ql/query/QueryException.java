/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

import org.j8ql.BaseException;

/**
 * <p></p>
 */
public class QueryException extends BaseException {

	public enum BuilderError implements BaseException.Error {
		NO_TABLE_DEF_FOUND("No table found for %s"),
		CANT_MISSMATCH_NAME_VALUE_WITH_CONDITION("Wrong and/or parameter array. Cannot mix 'name,value' with condition or fov in the same array." +
				"\n\t\tFor example, it cannot 'and(\"name1\",\"value1\",or(...))' but " +
				"\n\t\t'and(one(\"name1\",\"value1\"),or(...))' (e.g. use one(name,value) when single condition, or the appropriate and(...) or(...)");

		String msg;
		BuilderError(String msg) {
			this.msg = msg;
		}
		public String msg() {
			return msg;
		}
	}

	public QueryException(BuilderError builderError, Object... values){
		super(builderError,values);
	}

}
