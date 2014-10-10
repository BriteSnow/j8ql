/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

import org.j8ql.util.Immutables;

import java.util.List;

/**
 * Query for Update queries.
 */
public class UpdateQuery<T> extends IUQuery<T> implements Where<UpdateQuery<T>>, Columns<UpdateQuery<T>> {

	private Condition where = null;
	private Object whereId = null;

	private List<Condition> batchWhere = null;
	private List batchWhereId = null;


	// --------- Base Constructors --------- //
	UpdateQuery(Class<T> asClass) {
		super(asClass);
	}
	// --------- /Base Constructors --------- //

	// --------- Clone Constructors --------- //
	private UpdateQuery(UpdateQuery<T> oldBuilder) {
		this(oldBuilder,oldBuilder.getAsClass());
	}

	private UpdateQuery(UpdateQuery oldBuilder, Class<T> asClass) {
		super(oldBuilder, asClass);
		where = oldBuilder.where;
		whereId = oldBuilder.whereId;
	}
	// --------- /Clone Constructors --------- //

	// --------- Package Scoped Static Factories (used by Builders) --------- //
	static UpdateQuery<Integer> update(){
		return new UpdateQuery(Integer.class);
	}
	// --------- /Package Scoped Static Factories (used by Builders) --------- //

	// --------- Into --------- //
	public UpdateQuery<T> into(String intoTableName) {
		return table(new UpdateQuery<T>(this), intoTableName);
	}
	public UpdateQuery<T> into(Class intoTableClass){
		return table(new UpdateQuery<T>(this),intoTableClass);
	}
	// --------- /Into --------- //

	// --------- Where --------- //
	@Override
	public UpdateQuery<T> where(Condition where){
		UpdateQuery<T> newBuilder = new UpdateQuery<>(this);
		newBuilder.where = where;
		newBuilder.whereId = null;
		return newBuilder;
	}
	@Override
	public UpdateQuery<T> whereId(Object whereId){
		UpdateQuery<T> newBuilder = new UpdateQuery<>(this);
		newBuilder.whereId = whereId;
		newBuilder.where = null;
		return newBuilder;
	}
	// --------- /Where --------- //

	// --------- BatchWhere --------- //
	public UpdateQuery<T> batchWhere(List<Condition> where){
		UpdateQuery<T> newBuilder = new UpdateQuery<>(this);
		newBuilder.batchWhere = Immutables.of(where);
		newBuilder.whereId = null;
		return newBuilder;
	}
	public UpdateQuery<T> batchWhereId(List whereIds){
		UpdateQuery<T> newBuilder = new UpdateQuery<>(this);
		newBuilder.batchWhereId = Immutables.of(whereIds);
		newBuilder.batchWhere = null;
		return newBuilder;
	}
	// --------- /BatchWhere --------- //

	// --------- Columns --------- //
	@Override
	public UpdateQuery<T> columns(String... columnNames){
		return columns(new UpdateQuery<T>(this), columnNames);
	}
	// --------- /Columns --------- //

	// --------- values --------- //
	public UpdateQuery<T> values(Object... values){
		return values(new UpdateQuery<T>(this),values);
	}

	public UpdateQuery<T> value(Object value){
		return value(new UpdateQuery<T>(this),value);
	}
	// --------- /values --------- //

	// --------- Returning --------- //
	public <A> UpdateQuery<A> returningIdAs(Class<A> returningAs){
		return returningIdAs(new UpdateQuery<A>(this,returningAs),returningAs);
	}

	public <A> UpdateQuery<A> returning(Class<A> returningAs, String... returningColumns){
		return returning(new UpdateQuery<A>(this, returningAs), returningAs, returningColumns);
	}
	// --------- /Returning --------- //

	// --------- Getters --------- //
	@Override
	public Condition getWhere() {
		return where;
	}

	@Override
	public Object getWhereId() {
		return whereId;
	}
	// --------- /Getters --------- //
}
