/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.def;

import com.google.common.collect.Sets;

import java.util.*;

/**
 * Immutable Column Definition created by TableDef with the databaseMetaData.getColumns Record for this column
 */
public class ColumnDef {

	public enum Meta{
		notNullable, primaryKey,hasDefault
	}

	private final EnumSet<Meta> metas;
    public final String name;

    ColumnDef(String name, Map<String,String> columnMap, boolean isPrimary) {
		Set<Meta> metaSet = new HashSet<>();

		this.name = name;

		if (columnMap.containsKey("COLUMN_DEF")) {
			metaSet.add(Meta.hasDefault);
		}

		if ("NO".equals(columnMap.get("IS_NULLABLE"))){
			metaSet.add(Meta.notNullable);
		}

		if (isPrimary) {
			metaSet.add(Meta.primaryKey);
		}

		if (metaSet.size() > 0) {
			this.metas = EnumSet.copyOf(metaSet);
		}else {
			this.metas = EnumSet.noneOf(Meta.class);
		}
	}

	public boolean hasMeta(Meta... meta){
		return metas.containsAll(Arrays.asList(meta));
	}

    public String toString(){
        return name + " " + metas;
    }
    
}
