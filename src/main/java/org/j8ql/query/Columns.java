package org.j8ql.query;

import java.util.List;
import java.util.Set;

public interface Columns<T> {

	// --------- Query Setters --------- //
	// TODO: need to have String... types here (and have the Select columns supporting other type)
	// TODO: probably the (columns(List)....) should be the interface, and the default being the String.... one
	public T columns(Object... returningColumns);

	public T excludeColumns(String... columnsToExclude);
	// --------- /Query Setters --------- //

	public default T columns(List<String> columnNames){
		if (columnNames != null){
			return columns(columnNames.toArray(new Object[columnNames.size()]));
		}else {
			return columns((Object[]) null);
		}
	}

	// TODO: need to have Set<String> here
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
