package org.j8ql.query;

public interface Columns<T> {

	// --------- Query Setters --------- //
	public T columns(String... returningColumns);
	// --------- /Query Setters --------- //

}
