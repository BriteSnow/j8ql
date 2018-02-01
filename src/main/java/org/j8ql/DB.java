/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

import com.google.common.primitives.Primitives;
import org.j8ql.def.TableDef;

import org.j8ql.generator.PGDeleteGenerator;
import org.j8ql.generator.PGInsertGenerator;
import org.j8ql.generator.PGSelectGenerator;
import org.j8ql.generator.PGUpdateGenerator;
import org.j8ql.query.*;
import org.j8ql.util.CloseableStream;

import org.jomni.util.Maps;
import org.jomni.JomniBuilder;
import org.jomni.JomniMapper;
import org.jomni.Omni;

import static java.util.stream.Collectors.toList;
import static org.j8ql.DBException.DBError;
import static org.j8ql.query.Query.and;

public class DB {
	public static final Calendar calUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

	private final DataSource dataSource;

	public final JomniMapper mapper = new JomniBuilder().build();


	private Map<Class, ToDbConverter> javaToDbConverterByJavaType = null;

	private Map<Class, ToJavaConverter> dbToJavaConverterByJavaType = null;

	private volatile Map<String, TableDef> tableDefByLowerCaseName = new HashMap<>();

	private PGSelectGenerator selectGenerator;
	private PGInsertGenerator insertGenerator;
	private PGUpdateGenerator updateGenerator;
	private PGDeleteGenerator deleteGenerator;

	DB(DataSource dataSource) {
		this.dataSource = dataSource;

		selectGenerator = new PGSelectGenerator(this);
		insertGenerator = new PGInsertGenerator(this);
		updateGenerator = new PGUpdateGenerator(this);
		deleteGenerator = new PGDeleteGenerator(this);
	}

	// --------- Query setters --------- //
	void setToDbConverter(Map<Class, ToDbConverter> javaToDbConverterByJavaType) {
		this.javaToDbConverterByJavaType = javaToDbConverterByJavaType;
	}

	void setToJavaConverter(Map<Class, ToJavaConverter> dbToJavaConverterByJavaType) {
		this.dbToJavaConverterByJavaType = dbToJavaConverterByJavaType;
	}
	// --------- /Query setters --------- //

	// --------- init --------- //
	void init() {
		dbScan();
	}

	public void reScan(){
		dbScan();
	}

	private void dbScan() {
		try {
			Connection con = dataSource.getConnection();
			DatabaseMetaData dmd = con.getMetaData();

			Map<String, TableDef> tmpTableNameByName = new HashMap<>();

			ResultSet rs = dmd.getTables(null, null, null, new String[]{"TABLE"});
			List<Record> results = buildResults(rs);
			for (Map tableMap : results) {
				String tableName = (String) tableMap.get("table_name");
				String lcTableName = tableName.toLowerCase();
				TableDef tableDef = tmpTableNameByName.get(lcTableName);
				if (tableDef == null) {
					List<Record> ids = buildResults(dmd.getPrimaryKeys(null,null,tableName));
					List<Record> cols = buildResults(dmd.getColumns(null, null, tableName, null));
					tableMap.put("columns", cols);
					tableMap.put("ids", ids);
					tableDef = new TableDef(tableMap);
					tmpTableNameByName.put(lcTableName, tableDef);
				}
			}
			con.close();
			tableDefByLowerCaseName = tmpTableNameByName;
		} catch (SQLException e) {
			throw new RSQLException(e);
		}
	}

	// --------- /init --------- //

	// --------- Def Methods --------- //
	public TableDef getTableDef(String tableName) {
		if (tableName != null) {
			return tableDefByLowerCaseName.get(tableName.toLowerCase());
		}
		return null;
	}

	public TableDef getTableDef(Class beanClass) {
		// for now, just use the Class name as the table name
		String tableName = beanClass.getSimpleName();
		if (tableName != null) {
			return tableDefByLowerCaseName.get(tableName.toLowerCase());
		}
		return null;
	}
	// --------- /Def Methods --------- //

	public Runner openRunner() {
		return new RunnerImpl(this, getConnection());
	}

	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RSQLException(e);
		}
	}

	// --------- val --------- //
	public Object getJavaVal(Object val, ConvertContext ctx) {
		if (dbToJavaConverterByJavaType != null) {
			ToJavaConverter conv = dbToJavaConverterByJavaType.get(val.getClass());
			if (conv != null) {
				val = conv.toJava(val, ctx);
			}
		}
		return val;
	}

	public Object getDbVal(Object val, ConvertContext ctx) {
		if (javaToDbConverterByJavaType != null) {
			ToDbConverter conv = javaToDbConverterByJavaType.get(val.getClass());
			if (conv != null) {
				val = conv.toDb(val, ctx);
			}
		}
		return val;
	}
	// --------- /val --------- //

	// --------- SelectQuery --------- //
	public String sql(SelectQuery selectBuilder) {
		return sql(selectBuilder, null);
	}
	public String sql(SelectQuery selectBuilder, String countColumn){
		return selectGenerator.sql(selectBuilder, countColumn);
	}
	public Object[] values(SelectQuery selectBuilder) {
		return selectGenerator.values(selectBuilder);
	}
	// --------- /SelectQuery --------- //

	// --------- InsertQuery --------- //
	public String sql(InsertQuery insertBuilder){
		return insertGenerator.sql(insertBuilder);
	}
	public Object[] values(InsertQuery insertBuilder) {
		return insertGenerator.values(insertBuilder);
	}
	// --------- /InsertQuery --------- //

	// --------- UpdateQuery --------- //
	public String sql(UpdateQuery updateBuilder){
		return updateGenerator.sql(updateBuilder);
	}
	public Object[] values(UpdateQuery updateBuilder) {
		return updateGenerator.values(updateBuilder);
	}
	// --------- /UpdateQuery --------- //

	// --------- DeleteQuery --------- //
	public String sql(DeleteQuery deleteBuilder) {
		return deleteGenerator.sql(deleteBuilder);
	}

	public Object[] values(DeleteQuery deleteBuilder) {
		return deleteGenerator.values(deleteBuilder);
	}
	// --------- /DeleteQuery --------- //

	// --------- BatchValues --------- //
	public List<List> batchValues(InsertQuery insertBuilder) {
		return insertGenerator.batchValues(insertBuilder);
	}
	// --------- /BatchValues --------- //

	// --------- Table Helpers --------- //
	public TableDef getTableDef(BaseQuery baseQuery) {
		return getTableDefByNameOrClass(baseQuery.getTableName(), baseQuery.getTableClass());
	}

	public TableDef getTableDefByNameOrClass(String tableName,Class tableClass){
		TableDef tableDef = null;
		// first we look at the tableName
		if (tableName != null){
			tableDef = getTableDef(tableName);
			if (tableDef == null){
				throw new QueryException(QueryException.BuilderError.NO_TABLE_DEF_FOUND,tableName);
			}
		}
		// otherwise, we try to get it from the class
		else{
			if (tableClass != null) {
				tableDef = getTableDef(tableClass);
				if (tableDef == null){
					throw new QueryException(QueryException.BuilderError.NO_TABLE_DEF_FOUND,tableClass.getName());
				}
			}
		}
		return tableDef;
	}
	// --------- /Table Helpers --------- //


	// --------- Map To Table Column Utilities --------- //

	/**
	 * Return the list of valid column names for this target query table.
	 *
	 * If columns is null, will return null.
	 *
	 * @param query
	 * @param columns collection of column names (null proof, meaning, if null, the method will return null)
	 * @return
	 */
	public List<String> getValidColumns(BaseQuery query, Collection<String> columns) {
		if (columns == null) {
			return null;
		}
		TableDef tableDef = getTableDef(query);
		List<String> validColumns = new ArrayList<>();
		for (String name : columns) {
			if (tableDef.hasColumnName(name)) {
				validColumns.add(name);
			}
		}
		return validColumns;
	}

	public Condition getWhereIdCondition(TableDef tableDef, Object obj){
		String tableDot = tableDef.getName() + ".";

		if (Primitives.isWrapperType(obj.getClass())){
			String singleIdColumnName = tableDef.getSingleIdColumnName();
			if (singleIdColumnName == null){
				throw new DBException(DBError.INVALID_WHERE_ID_OBJECT_PRIMITIVE_TYPE_BUT_NO_SINGLE_ID_COLUMN_FOR_TABLE,obj.getClass().getSimpleName(),tableDef.getName());
			}
			return and(tableDot + singleIdColumnName, obj);
		}else {
			Omni objWrapped = mapper.omni(obj);
			List namesValues = new ArrayList<>();
			for (String idColumn : tableDef.getIdColumnNames()) {
				namesValues.add(tableDot + idColumn);
				if (!objWrapped.containsKey(idColumn)) {
					throw new DBException(DBError.INVALID_WHERE_ID_DOES_NOT_CONTAIN_ID_COLUMN_PROPERTY, obj, idColumn, tableDef.getName());
				}
				namesValues.add(objWrapped.get(idColumn));
			}
			return and(namesValues.toArray());
		}
	}

	// --------- /Map To Table Column Utilities --------- //

	// --------- Package Stmt Related Methods --------- //
	PreparedStatement setValues(PreparedStatement pStmt, Object[] vals) {
		try {
			pStmt.clearParameters();

			// --------- Database Type Logging for Debugging --------- //
			//ParameterMetaData pmd = pStmt.getParameterMetaData();
			//if (vals.length > 0){
			//	int l = pmd.getParameterCount();
			//	for (int i = 1 ; i <= l; i++){
			//		Object val = vals[i-1];
			//		System.out.printf("col: %s | dbTypeName: %s | dbJavaType: %s | valJavaType: %s\n",i, pmd.getParameterTypeName(i),pmd.getParameterClassName(i),val.getClass());
			//	}
			//}
			// --------- /Database Type Logging for Debugging --------- //

			// return now if no vals
			if (vals == null) {
				return pStmt;
			}

			// Grab metadata here outside of the vals loop to prevent major performance issues
			ParameterMetaData pmd = pStmt.getParameterMetaData();

			for (int i = 0; i < vals.length; i++) {
				int cidx = i + 1;
				Object val = vals[i];

				// null seems to work for update/insert, but not for select in the where close (has to use is null)
				if (val == null) {
					pStmt.setObject(cidx, null);
				} else {
					ConvertContext ctx = new ConvertContext(pmd, i + 1);

					val = getDbVal(val, ctx);


					// --------- More Value Post Processing When Needed --------- //
					// Handle the case that this is a special type (enum, full object)
					// NOTE: Perhaps the getDbVal should take a targetType. However, not sure for performance.
					//       Here we just do extra work if we really need too, and try to avoid getting the MetaData access.
					Class valClass = val.getClass();

					// Java Enum to Db String
					if (valClass.isEnum()) {
						if ("java.lang.String".equals(pmd.getParameterClassName(i + 1))) {
							val = ((Enum) val).name();
						} else {
							// TODO: for now, nothing, it will probably fail. Later need to handle ordinal if number
						}
					} else if( !isStandardDbCompatibleType(valClass)) {
						if ("hstore".equals(ctx.getColumnSqlTypeName())){
							val = mapper.asMap(val);
						}else{
							throw new DBException(DBError.INCOMPATIBLE_JAVA_TYPE_WITH_COLUMN_TYPE,valClass,ctx.getColumnSqlTypeName());
						}
						// TODO: will need to handle the case where the target type is a varchar/text (we should serialize to json in this case)
					}
					// --------- /More Value Post Processing When Needed --------- //
					try {

						if ("timestamptz".equals(ctx.getColumnSqlTypeName()) && val instanceof Timestamp){
							// for now, not sure if this makes much of a difference. Timestamp with timezone seems to
							// be always stored with system timezone.
							pStmt.setTimestamp(cidx, (Timestamp) val, calUTC);
						}else{
							pStmt.setObject(cidx, val);
						}

					}catch(Throwable t){
						throw new DBException(t, DBError.INCOMPATIBLE_JAVA_TYPE_WITH_COLUMN_TYPE,valClass,ctx.getColumnSqlTypeName());
					}
				}
			}
			return pStmt;
		} catch (SQLException e) {
			throw new RSQLException(e);
		}
	}

	// Broad assumptions
	static private final Set<String> dbCompatiblePackages = Maps.setOf("java.lang", "java.sql", "java.math");
	static private final Set<Class> dbCompatibleClasses = Maps.setOf(ZonedDateTime.class);

	private boolean isStandardDbCompatibleType(Class cls) {
		return (cls.isPrimitive() || Map.class.isAssignableFrom(cls)
				|| dbCompatiblePackages.contains(cls.getPackage().getName())
				|| dbCompatibleClasses.contains(cls));
	}


	// --------- /Package Stmt Related Methods --------- //

	// --------- Public Stmt Related Methods --------- //
	/**
	 * Convenient methods to build a List<Map> from a result set.
 	 */
	public List<Record> buildResults(ResultSet rs) {
		Stream<Record> resultSetStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(new ResultSetIterator(rs, this), 0), false);
		try(Stream<Record> autoCloseableResultSetStream = new CloseableStream<Record>(resultSetStream,rs::close)){
			return autoCloseableResultSetStream.collect(toList());
		}
	}
	// --------- /Public Related Methods --------- //

}