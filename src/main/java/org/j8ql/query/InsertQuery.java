/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

import java.util.List;

/**
 * <p></p>
 */
public class InsertQuery<T> extends IUQuery<T> implements Columns<InsertQuery<T>>      {

	// ---------Base Constructors --------- //
	private InsertQuery(Class<T> asClass){
		super(asClass);
	}
	// --------- /Base Constructors --------- //

	// --------- Clone Constructors --------- //
	private InsertQuery(InsertQuery<T> insertBuilder) {
		this(insertBuilder,insertBuilder.getAsClass());
	}

	private InsertQuery(InsertQuery insertBuilder, Class<T> asClass) {
		super(insertBuilder, asClass);
	}
	// --------- /Clone Constructors --------- //

	// --------- Package Scoped Static Factories (used by Builders) --------- //
	static InsertQuery<Integer> insert() {
		return new InsertQuery(Integer.class);
	}
	// --------- /Package Scoped Static Factories (used by Builders) --------- //

	//public <K> InsertQuery<K> as(Class<K> asClass) {
	//	return new InsertQuery<K>(this,asClass);
	//}

	// --------- Into --------- //
	public InsertQuery<T> into(String intoTableName) {
		return table(new InsertQuery<T>(this), intoTableName);
	}
	public InsertQuery<T> into(Class intoTableClass){
		return table(new InsertQuery<T>(this), intoTableClass);
	}
	// --------- /Into --------- //

	// --------- Columns --------- //
	@Override
	public InsertQuery<T> columns(Object... columnNames){
		return columns(new InsertQuery<T>(this), columnNames);
	}

	@Override
	public InsertQuery<T> excludeColumns(String... columnNames) {
		return excludeColumns(new InsertQuery<T>(this), columnNames);
	}
	// --------- /Columns --------- //

	// --------- values --------- //
	public InsertQuery<T> values(Object... values) {
		return values(new InsertQuery<T>(this), values);
	}

	public InsertQuery<T> value(Object value){
		return value(new InsertQuery<T>(this), value);
	}
	// --------- /values --------- //

	// --------- batchValues --------- //
	public InsertQuery<T> batchValues(List<List> values) {
		return batchValues(new InsertQuery<T>(this), values);
	}

	public InsertQuery<T> batchObjects(List objects){
		return batchObjects(new InsertQuery<T>(this), objects);
	}
	// --------- /batchValues --------- //

	// --------- Returning --------- //
	public <A> InsertQuery<A> returningIdAs(Class<A> returningAs){
		return returningIdAs(new InsertQuery<A>(this,returningAs),returningAs);
	}

	public <A> InsertQuery<A> returning(Class<A> returningAs, String... returningColumns){
		return returning(new InsertQuery<A>(this, returningAs), returningAs, returningColumns);
	}
	// --------- /Returning --------- //
}
