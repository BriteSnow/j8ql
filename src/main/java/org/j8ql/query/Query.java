/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

import org.j8ql.Record;

import java.util.Map;
import java.util.Set;

/**
 * Query factory. All queries start with a Query.
 */
public class Query {


	// --------- SELECT --------- //
	public static SelectQuery<Record> select(){
		return SelectQuery.select();
	}

	public static SelectQuery<Record> select(String tableName) {
		return SelectQuery.select().from(tableName);
	}

	public static <T> SelectQuery<T> select(Class<T> asClassAndTableClass){
		return SelectQuery.select(asClassAndTableClass, asClassAndTableClass);
	}
	// --------- /SELECT --------- //

	// --------- UPDATE --------- //
	public static InsertQuery<Integer> insert(){
		return InsertQuery.insert();
	}

	public static InsertQuery<Integer> insert(String tableName){
		return insert().into(tableName);
	}

	public static InsertQuery<Integer> insert(Class intoClass){
		return insert().into(intoClass);
	}
	// --------- /UPDATE --------- //

	// --------- UPDATE --------- //
	public static UpdateQuery<Integer> update(){
		return UpdateQuery.update();
	}

	public static UpdateQuery<Integer> update(String intoTable) {
		return update().into(intoTable);
	}

	public static UpdateQuery<Integer> update(Class intoClass) {
		return update().into(intoClass);
	}
	// --------- /UPDATE --------- //

	// --------- DELETE --------- //
	public static DeleteQuery<Integer> delete(){
		return DeleteQuery.delete();
	}

	public static DeleteQuery<Integer> delete(String tableName) {
		return DeleteQuery.delete().from(tableName);
	}
	public static DeleteQuery<Integer> delete(Class tableClass) {
		return DeleteQuery.delete().from(tableClass);
	}
	// --------- /DELETE --------- //


	// --------- FieldOpValue Static Factories --------- //
	public static FieldOpValue[] fovs(Object... keyValues){
		int fovsL = keyValues.length / 2 + keyValues.length % 2;
		FieldOpValue[] fieldOpValues = new FieldOpValue[fovsL];
		for (int i = 0; i < fovsL; i++) {
			int keyIdx = i * 2;
			int valIdx = keyIdx + 1;
			Object key = keyValues[keyIdx];
			Object value = (valIdx < keyValues.length)?keyValues[valIdx]:null;
			if (key instanceof Elem || value instanceof Elem){
				throw new QueryException(QueryException.BuilderError.CANT_MISSMATCH_NAME_VALUE_WITH_CONDITION);
			}
			String keyName = key.toString();
			fieldOpValues[i] = new FieldOpValue(keyName, value);
		}
		return fieldOpValues;
	}

	public static FieldOpValue[] fovs(Map keyValueMap){
		Set<Map.Entry> entrySet = keyValueMap.entrySet();
		FieldOpValue[] fovs = new FieldOpValue[entrySet.size()];
		int i = 0;
		for (Map.Entry entry : entrySet) {
			FieldOpValue fov = new FieldOpValue(entry.getKey().toString(), entry.getValue());
			fovs[i] = fov;
			i++;
		}
		return fovs;
	}

	public static FieldOpValue fov(String key, Object value){
		return new FieldOpValue(key, value);
	}
	// --------- /FieldOpValue Static Factories --------- //

	// --------- CONDITIONS --------- //
	public static Condition and(Object... elems) {
		return and(fovs(elems));
	}

	public static Condition and(Elem... elems){
		return new Condition(Condition.Type.AND, elems);
	}

	public static Condition or(Object... elems) {
		return or(fovs(elems));
	}

	public static Condition or(Elem... elems) {
		return new Condition(Condition.Type.OR, elems);
	}

	/**
	 * Return a single condition
	 * @param key
	 * @param value
	 * @return
	 */
	public static Condition one(String key, Object value) {
		return and(key, value);
	}
	// --------- /CONDITIONS --------- //

}
