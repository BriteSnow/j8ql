/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

import org.j8ql.def.ColumnDef;
import org.j8ql.def.TableDef;
import org.j8ql.util.Immutables;

import org.jomni.ClassInfo;
import org.jomni.JomniMapper;
import org.jomni.Omni;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.j8ql.def.ColumnDef.Meta.hasDefault;
import static org.j8ql.def.ColumnDef.Meta.notNullable;
import static org.j8ql.def.ColumnDef.Meta.primaryKey;

/**
 * <p></p>
 */
public class ValueObject {

	private final Object obj;
	private final Map map;

	public ValueObject(Object value) {
		if (value instanceof Map) {
			obj = null;
			map = Immutables.of((Map) value);
		}else{
			obj = value;
			map = null;
		}
	}

	public static ValueObject of(Object value){
		return new ValueObject(value);
	}
	/**
	 * <p>If explicitColumns is not null, just returns it, otherwise if null, then, return the list of all valid columns present in this object.</p>
	 *
	 * <p>Note that if the column has  has a default value and the value is null, we will ignore it.</p>
	 *
	 * @param mapper
	 * @param tableDef
	 * @param explicitColumns
	 * @return
	 */
	public List<String> getColumns(JomniMapper mapper, TableDef tableDef, List<String> explicitColumns, Set<String> excludeColumns) {
		if (explicitColumns != null) {
			// if we do not have an exclude column, we can return as is.
			if (excludeColumns == null){
				return explicitColumns;
			}
			// if we have an excludeColumns then, we need to filter them out.
			else{
				return  explicitColumns.stream().filter(n -> !excludeColumns.contains(n)).collect(Collectors.toList());
			}

		}

		List<String> columns = new ArrayList<>();

		Omni omni = (obj != null)?mapper.omni(obj):mapper.omni(map);

		for (ColumnDef columnDef : tableDef.getColumnDefs()) {
			// if the column is in the objectProperty and if onlyColumnSet not null check it is there too
			if (omni.containsKey(columnDef.name)) {
				// We ignore null values for notNullable primaryKey columns that have a default.
				if (columnDef.hasMeta(notNullable,hasDefault, primaryKey) && omni.get(columnDef.name) == null){
					// Ignore null values when the column is not nullable and has a default.
				}else{
					// If no excludeColumns or if it is not contained in it, then, add this as a column name
					if (excludeColumns == null || !excludeColumns.contains(columnDef.name)){
						columns.add(columnDef.name);
					}
				} // (wrote this if/else this way for readability)

				// Note that this still voluntarily fail if it is not nullable and has no default as this is out of the scope
				// of this behavior.
			}
		}
		return Immutables.of(columns);
	}

	/**
	 * Return the values of this valueObject for this tableDef for either all the tableDef columns matching this object
	 * or if explicitColumns defined, then, just these columns.
	 *
	 * @param mapper
	 * @param tableDef
	 * @param explicitColumns explicit list of columns to get the values from
	 * @return
	 */
	public List getValues(JomniMapper mapper, TableDef tableDef, List<String> explicitColumns, Set<String> excludeColumns){
		// get the onlyColumns or the onlyColumns
		List<String> columns = getColumns(mapper, tableDef,explicitColumns, excludeColumns);

		List values = new ArrayList<>();

		ClassInfo ci = (obj != null)?mapper.getClassInfo(obj.getClass()):null;
		for (String column : columns) {
			Object val = (ci != null)?ci.getValue(obj,column):map.get(column);
			values.add(val);
		}

		return Immutables.of(values);
	}



}
