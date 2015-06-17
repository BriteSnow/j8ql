package org.j8ql.query;

import java.util.Set;

public interface Columns<T> {

	// --------- Query Setters --------- //
	public T columns(Object... returningColumns);
	// --------- /Query Setters --------- //

	public default T columns(Set<Object> columnSet){
		if (columnSet != null){
			return columns(columnSet.toArray(new Object[columnSet.size()]));
		}else {
			return columns((Object[]) null);
		}
	}

}
