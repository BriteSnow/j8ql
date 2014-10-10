package org.j8ql.query;


import static org.j8ql.query.Query.and;

public interface Where<T> {

	/**
	 * Short hand for where(and(Object...)). As with and(Object...) the nameValues object array must be
	 * (name,value,name,value) where name is the column name (in fact, a FieldOp string that can just but the column name,
	 * or can combine an operator. For example where("age,>",21) will translate to a SQL like "age > ?"
	 * @param nameValues
	 * @return
	 */
	default public T where(Object... nameValues){
		return where(and(nameValues));
	}

	// --------- Query Setters --------- //
	public T where(Condition condition);

	public T whereId(Object whereId);
	// --------- /Query Setters --------- //

	// --------- Getters --------- //
	public Condition getWhere();

	public Object getWhereId();
	// --------- /Getters --------- //
}
