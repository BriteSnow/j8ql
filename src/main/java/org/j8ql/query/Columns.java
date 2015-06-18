package org.j8ql.query;

import java.util.Set;

public interface Columns<T> {

	// --------- Query Setters --------- //
	public T columns(Object... returningColumns);

	public T excludeColumns(String... columnsToExclude);
	// --------- /Query Setters --------- //

	public default T columns(Set<Object> columnSet){
		if (columnSet != null){
			return columns(columnSet.toArray(new Object[columnSet.size()]));
		}else {
			return columns((Object[]) null);
		}
	}

	public default T excludeColumns(Set<String> columnsExcludeSet){
		if (columnsExcludeSet != null){
			return excludeColumns(columnsExcludeSet.toArray(new String[columnsExcludeSet.size()]));
		}else {
			return excludeColumns((String[]) null);
		}
	}

}
