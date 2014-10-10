/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.def;

import java.util.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class TableDef {
    
    private final String name;
    
    private final ImmutableMap<String,ColumnDef> colDefByName;
    
    // will be set with 0, 1 or more ID column name for this table
    private Set<String> idColumnNames = null;

    // will be set only if there is one and only one ID Column
    private  String singleIdColumn = null;


    public TableDef(Map tableMap){
        this.name = (String) tableMap.get("table_name");

		// get the ids
		List<Map> ids = (List<Map>) tableMap.get("ids");
		int idColumnsSize = ids.size();
		String[] idNames = new String[idColumnsSize];
		String columnName = null;
		for (int i = 0, c = idColumnsSize; i < c; i++) {
			Map columnMap = ids.get(i);
			columnName = (String) columnMap.get("column_name");
			idNames[i] = columnName;
		}
		idColumnNames = ImmutableSet.copyOf(idNames);

		if (idColumnNames.size() == 1){
			singleIdColumn = columnName;
		}

		// get the table columns
		Map<String,ColumnDef> colDefByName = new HashMap<>();
        List<Map> columnMapList = (List<Map>) tableMap.get("columns");
		for (Map col : columnMapList){
			columnName = (String) col.get("COLUMN_NAME");
            ColumnDef colDef = new ColumnDef(columnName, col, idColumnNames.contains(columnName));
            colDefByName.put(columnName, colDef);
        }
		this.colDefByName = ImmutableMap.copyOf(colDefByName);

    }
    
    public String getName(){
        return name;
    }


	/**
	 * Get a columnDef per name.
	 */
	public ColumnDef getColumnDef(String name) {
		return colDefByName.get(name);
	}

    /**
     * Return the set if ID columns name for this table.
     * @return
     */
    public Set<String> getIdColumnNames(){
        return idColumnNames;
    }

    /**
     * Return the column name of the id column name only and only if this table as one and only one id column
     *
     * @return the name of the idColumn
     */
    public String getSingleIdColumnName() {
        return singleIdColumn;
    }

    public boolean hasColumnName(String columnName){
        return colDefByName.containsKey(columnName);
    }

	public Collection<ColumnDef> getColumnDefs(){
		return colDefByName.values();
	}

    public Set<String> getColumnNames(){
        return colDefByName.keySet();
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder(name);
        
        sb.append(" [");
        boolean f = true;
        for (String name : colDefByName.keySet()){
            ColumnDef colDef = colDefByName.get(name);
            if (!f){
                sb.append(",");
            }else{
                f = false;
            }
            sb.append(colDef.name);
        }
        sb.append("]");
        
        return sb.toString();
    }

}
