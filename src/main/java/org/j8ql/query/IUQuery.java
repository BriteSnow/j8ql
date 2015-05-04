/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

import org.j8ql.util.Immutables;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * <p>Base query for Builders that insert or update data (InsertQuery and UpdateQuery).</p>
 * <p>Basically, this Query just add "values/valueObject" to the IUDQuery.</p>
 */
public class IUQuery<T> extends IUDQuery<T> {

	private List<Object> columns;

	private List<Object> values;
	private ValueObject valueObject;

	private List<List> batchValues;
	private List<ValueObject> batchObjects;

	protected IUQuery(Class<T> asClass) {
		super(asClass);
	}

	// --------- Clone Constructors --------- //
	public IUQuery(IUQuery oldBuilder, Class<T> asClass) {
		super(oldBuilder, asClass);
		columns = oldBuilder.columns;
		values = oldBuilder.values;
		valueObject = oldBuilder.valueObject;
		batchValues = oldBuilder.batchValues;
		batchObjects = oldBuilder.batchObjects;
	}
	// --------- /Clone Constructors --------- //

	// --------- Columns --------- //
	protected <K extends IUQuery> K columns(K newBuilder, Object... columnNames){
		if (columnNames == null){
			((IUQuery)newBuilder).columns = null;
		}else{
			((IUQuery)newBuilder).columns = Immutables.of(columnNames);
		}
		return newBuilder;
	}
	// --------- /Columns --------- //

	// --------- values --------- //
	protected <K extends IUQuery> K values(K newBuilder, Object... values){
		((IUQuery)newBuilder).values = Immutables.of(values);
		((IUQuery)newBuilder).valueObject = null;
		return newBuilder;
	}

	public <K extends IUQuery> K value(K newBuilder, Object value) {
		((IUQuery)newBuilder).valueObject = new ValueObject(value);
		((IUQuery)newBuilder).values = null;
		return newBuilder;
	}
	// --------- /values --------- //

	// --------- batch values --------- //
	protected <K extends IUQuery> K batchValues(K newBuilder, List<List> values){
		((IUQuery)newBuilder).batchValues = Immutables.of(values);
		((IUQuery)newBuilder).batchObjects = null;
		return newBuilder;
	}

	public <K extends IUQuery> K batchObjects(K newBuilder, List<Object> values) {
		// create a list of ValueObject from a list of objects
		((IUQuery)newBuilder).batchObjects = Immutables.of(values.stream().map(ValueObject::new).collect(toList()));
		((IUQuery)newBuilder).batchValues = null;

		return newBuilder;
	}
	// --------- /batch values --------- //

	// --------- Getters --------- //
	public List<Object> getColumns() {
		return columns;
	}

	public List<Object> getValues() {
		return values;
	}

	public ValueObject getValueObject() {
		return valueObject;
	}

	public List<List> getBatchValues() {
		return batchValues;
	}

	public List<ValueObject> getBatchObjects() {
		return batchObjects;
	}

	// --------- /Getters --------- //
}
