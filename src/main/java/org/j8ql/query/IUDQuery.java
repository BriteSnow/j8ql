/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;


import org.j8ql.util.Immutables;

import java.util.List;

/**
 * <p>Base query for every builders that write to the DB (InsertQuery, UpdateQuery, DeleteQuery)</p>
 */
public class IUDQuery<T> extends BaseQuery<T> {



	private List<String> returningColumns;
	private boolean returningId = false;

	protected IUDQuery(Class<T> asClass) {
		super(asClass);
	}

	// --------- Clone Constructors --------- //
	protected IUDQuery(IUDQuery oldBuilder, Class<T> asClass) {
		super(oldBuilder, asClass);
		returningColumns = oldBuilder.getReturningColumns();
		returningId = oldBuilder.returningId;
	}
	// --------- /Clone Constructors --------- //

	// --------- Returning --------- //
	public <K extends IUDQuery> K returningIdAs(K newBuilder, Class returningAs){
		((IUDQuery)newBuilder).returningId = true;
		((IUDQuery)newBuilder).returningColumns = null;
		return newBuilder;
	}

	public  <K extends IUDQuery> K returning(K newBuilder, Class returningAs, String... returningColumns){
		((IUDQuery)newBuilder).returningColumns = Immutables.of(returningColumns);
		((IUDQuery)newBuilder).returningId = false;
		return newBuilder;
	}
	// --------- /Returning --------- //



	// --------- Getters --------- //

	public boolean hasReturning(){
		return (isReturningId() || getReturningColumns() != null);
	}

	public List<String> getReturningColumns() {
		return returningColumns;
	}

	public boolean isReturningId() {
		return returningId;
	}
	// --------- /Getters --------- //

}
