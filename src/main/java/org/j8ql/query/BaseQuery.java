/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

/**
 * The base query object use to build a SQL query.
 */
public class BaseQuery<T> {

	private Class<T> asClass;

	private String tableName;
	private Class tableClass;

	protected BaseQuery(Class<T> asClass) {
		this.asClass = asClass;
	}

	// --------- Base Constructors --------- //
	public BaseQuery(Class<T> asClass, Class tableClass) {
		this.asClass = asClass;
		this.tableClass = tableClass;
	}

	protected BaseQuery(BaseQuery<T> oldBaseQuery){
		this.asClass = oldBaseQuery.asClass;
		this.tableName = oldBaseQuery.tableName;
		this.tableClass = oldBaseQuery.tableClass;
	}
	// --------- /Base Constructors --------- //


	// --------- Clone Constructors --------- //
	protected BaseQuery(BaseQuery oldBaseQuery, Class<T> asClass) {
		this(oldBaseQuery);
		this.asClass = asClass;
	}
	// --------- /Clone Constructors --------- //


	// --------- Table --------- //
	protected <K extends BaseQuery> K table(K newBuilder, String tableName){
		((BaseQuery)newBuilder).tableName = tableName;
		((BaseQuery)newBuilder).tableClass = null;
		return newBuilder;
	}

	protected <K extends BaseQuery> K table(K newBuilder, Class tableClass){
		((BaseQuery)newBuilder).tableClass = tableClass;
		((BaseQuery)newBuilder).tableName = null;
		return newBuilder;
	}
	// --------- /Table --------- //


	// --------- Getters --------- //
	public Class<T> getAsClass() {
		return asClass;
	}

	public String getTableName() {
		return tableName;
	}

	public Class getTableClass() {
		return tableClass;
	}
	// --------- /Getters --------- //
}