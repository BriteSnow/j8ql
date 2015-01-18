/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

/**
 * <p></p>
 */
public class DBException extends BaseException{
	public enum DBError implements Error {
		INVALID_WHERE_ID_OBJECT_PRIMITIVE_TYPE_BUT_NO_SINGLE_ID_COLUMN_FOR_TABLE("Invalid WhereId object for this Query. The WhereId Object is of a primitive type [%s] but this table [%s]" +
				"has more than one column ids. The whereId needs to pass a Map or an Object with the appropriate getters."),
		INVALID_WHERE_ID_DOES_NOT_CONTAIN_ID_COLUMN_PROPERTY("Invalid whereId object. This whereId object [%s] does not contain the column id property [%s] for the table [%s]"),
		INCOMPATIBLE_JAVA_TYPE_WITH_COLUMN_TYPE("Java type [%s] not compatible with db type [%s]");

		String msg;
		DBError(String msg) {
			this.msg = msg;
		}
		public String msg() {
			return msg;
		}
	}

	public DBException(DBError DBError, Object... values){
		super(DBError,values);
	}

}
