/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

import org.j8ql.DB;

import java.util.List;

/**
 * <p></p>
 */
public interface Elem {

	/**
	 * Populate the stringBuilder parameter with the sql of this element. Note that this return exactly the same StringBuilder for chainability sake.
	 * @param sb
	 * @return Must return the same StringBuilder sb for chainability.
	 */
	public StringBuilder buildSql(DB db, StringBuilder sb);

	public List buildValues(DB db, List values);

	public List<Elem> getElems();
}
