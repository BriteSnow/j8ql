/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

/**
 * A ClauseContent is the representation of a insert, where, or update clause with the prepared
 * sql statement (e.g., "name1 = ? and name2 = ?" for the WhereContent) and the list of values.
 *
 */
public abstract class ClauseContent {

	protected String[] names;
	private   Object[] values;

	protected String sql;

	ClauseContent(String[] names, Object[] values) {
		this.names = names;
		this.values = values;
		init();
	}

	protected abstract void init();


	public String sql(){
		return sql;
	}

	/**
	 *
	 * @return the values of this ClauseContent
	 */
    public Object[] values() {
		return values;
    }

	/**
	 * Return a concatinated Object array of values from this ClauseContent and the ones from the clauses... parameter (in order)
	 *
	 * @param clauses
	 * @return
	 */
	public Object[] values(ClauseContent... clauses){
		// get the total number of values
		int total = values.length;
		for (ClauseContent clause : clauses){
			total += clause.values().length;
		}

		// create the contact array
		Object[] allValues = new Object[total];

		// copy this values array to the allValues
		int idx = 0;
		Object[] vals = values();
		System.arraycopy(vals,0,allValues,0,vals.length);
		idx += vals.length;

		// copy the clauses content ones
		for (ClauseContent clause : clauses){
			vals = clause.values();
			System.arraycopy(vals,0,allValues,idx,vals.length);
			idx += vals.length;
		}

		return allValues;
	}

}
