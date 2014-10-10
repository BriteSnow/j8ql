/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

/**
 * <p></p>
 */
public class DeleteQuery<T> extends IUDQuery<T> implements Where<DeleteQuery<T>> {

	private Condition where = null;
	private Object whereId = null;

	// --------- Base Constructor --------- //
	private DeleteQuery(Class<T> asClass) {
		super(asClass);
	}
	// --------- /Base Constructor --------- //



	// --------- Clone Constructors --------- //
	private DeleteQuery(DeleteQuery<T> oldBuilder) {
		this(oldBuilder, oldBuilder.getAsClass());
	}

	private DeleteQuery(DeleteQuery oldBuilder, Class<T> asClass) {
		super(oldBuilder, asClass);
		this.where = oldBuilder.where;
	}
	// --------- /Clone Constructors --------- //

	// --------- Package Scoped Static Factories (used by Builders) --------- //
	static DeleteQuery<Integer> delete() {
		return new DeleteQuery(Integer.class);
	}
	// --------- /Package Scoped Static Factories (used by Builders) --------- //

	// --------- From --------- //
	public DeleteQuery<T> from(String intoTableName){
		return table(new DeleteQuery<T>(this),intoTableName);
	}
	public DeleteQuery<T> from(Class intoTableClass){
		return table(new DeleteQuery<T>(this),intoTableClass);
	}
	// --------- /From --------- //

	// --------- Where --------- //
	@Override
	public DeleteQuery<T> where(Condition where){
		DeleteQuery<T> newBuilder = new DeleteQuery<>(this);
		newBuilder.where = where;
		newBuilder.whereId = null;
		return newBuilder;
	}
	public DeleteQuery<T> whereId(Object whereId){
		DeleteQuery<T> newBuilder = new DeleteQuery<>(this);
		newBuilder.whereId = whereId;
		newBuilder.where = null;
		return newBuilder;
	}
	// --------- /Where --------- //

	// --------- Returning --------- //
	public <A> DeleteQuery<A> returningIdAs(Class<A> returningAs){
		return returningIdAs(new DeleteQuery<A>(this,returningAs),returningAs);
	}

	public <A> DeleteQuery<A> returning(Class<A> returningAs, String... returningColumns){
		return returning(new DeleteQuery<A>(this, returningAs), returningAs, returningColumns);
	}
	// --------- /Returning --------- //


	// --------- Getters --------- //
	@Override
	public Condition getWhere() {
		return where;
	}

	@Override
	public Object getWhereId(){
		return whereId;
	}
	// --------- /Getters --------- //
}
