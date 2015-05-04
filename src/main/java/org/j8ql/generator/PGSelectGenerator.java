/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.generator;

import org.j8ql.DB;
import org.j8ql.def.TableDef;
import org.j8ql.query.Case;
import org.j8ql.query.Join;
import org.j8ql.query.SelectQuery;
import org.j8ql.util.SqlUtils;
import org.jomni.util.Pair;

import java.util.List;

import static org.j8ql.util.SqlUtils.escapeColumnName;
import static org.j8ql.util.SqlUtils.inlineValue;

/**
 * <p></p>
 */
public class PGSelectGenerator extends PGGenerator {


	public PGSelectGenerator(DB db) {
		super(db);
	}


	public String sql(SelectQuery selectBuilder, String countColumn) {
		TableDef tableDef = db.getTableDef(selectBuilder);
		StringBuilder sql = new StringBuilder("select");

		// if countColumn, then, it is a select count
		if (countColumn != null){
			sql.append(" count(").append(countColumn).append(')');
		}else {
			List<Object> columnNames = selectBuilder.getColumns();
			// if we have some columns specified, then, take those ones.
			if (columnNames != null) {

				StringBuilder sb = columnNames.stream().collect(StringBuilder::new,
						(sb1, name) -> {
							if (sb1.length() > 0){
								sb1.append(", ");
							}else{
								sb1.append(" ");
							}
							if (name instanceof Case) {
								sb1.append(buildCase((Case) name));
							}else {
								sb1.append(escapeColumnName(name.toString()));
							}

						},
						StringBuilder::append
				);
				sql.append(sb.toString());
				
			}else{
				// when no columns, default to tableName.*
				sql.append(" \"").append(tableDef.getName()).append('"').append(".*");
			}
		}

		sql.append(" from ").append('"').append(tableDef.getName()).append('"');

		// JOIN
		String joins = buildJoins(selectBuilder.getJoins());
		if (joins != null) {
			sql.append(" ").append(joins);
		}

		// WHERE
		processWhereSql(selectBuilder, tableDef, sql);

		// ORDERBY
		String orderBySql = buildOrderBy(selectBuilder.getOrderBys());
		if (orderBySql != null){
			sql.append(" ").append(orderBySql);
		}

		// OFFSET
		Long offset = selectBuilder.getOffset();
		if (offset != null) {
			sql.append(" offset ").append(offset);
		}

		// LIMIT
		Long limit = selectBuilder.getLimit();
		if (limit != null) {
			sql.append(" limit ").append(limit);
		}

		return sql.toString();
	}

	public Object[] values(SelectQuery selectBuilder){
		TableDef tableDef = db.getTableDef(selectBuilder);
		List values = getWhereValues(selectBuilder, tableDef);
		if (values != null){
			return values.toArray();
		}else{
			return null;
		}
	}


	// --------- Private Helpers --------- //
	private String buildCase(Case c) {
		StringBuilder sb = new StringBuilder();
		sb.append("CASE");

		String on = c.getOn();
		if (on != null) {
			sb.append(" ").append(escapeColumnName(on));
		}

		for (Pair<Object,Object> whenThen : c.getWhenThenList()){
			// append the when expression
			sb.append(" WHEN ").append(whenThen.getA());
			// append the value (for now, those will be inline, not parametrize)
			sb.append(" THEN ").append(inlineValue(whenThen.getB()));
		}

		Object orElse = c.getOrElse();
		if (orElse != null) {
			sb.append(" ELSE ").append(inlineValue(orElse));
		}
		sb.append(" END");

		String alias = c.getAlias();
		if (alias != null) {
			sb.append(' ').append(escapeColumnName(alias));
		}
		return sb.toString();
	}

	private String buildOrderBy(String[] orderBys){
		if (orderBys != null && orderBys.length > 0) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String col : orderBys) {
				if (col != null) {
					if (first) {
						sb.append("order by ");
						first = false;
					} else {
						sb.append(", ");
					}
					// get the direction.
					String dir = "asc";
					if (col.startsWith("!")) {
						dir = "desc";
						col = col.substring(1);
					}
					// add the column name and direction.
					sb.append('\"').append(col).append('\"').append(" ").append(dir);
				}
			}
			return sb.toString();
		}else{
			return null;
		}
	}

	private String buildJoins(Join[] joins){
		if (joins != null && joins.length > 0){
			StringBuilder sb = new StringBuilder();
			for (Join join : joins) {
				sb.append(" ").append(join.type).append(" ");
				sb.append(" \"").append(join.joinTable).append("\" on ");
				sb.append('"').append(join.joinTable).append("\".");
				sb.append('"').append(join.joinTableColumn).append('"');
				sb.append(" = ");
				sb.append('"').append(join.onTable).append("\".");
				sb.append('"').append(join.onTableColumn).append("\"");
			}
			return sb.toString();
		}
		return null;
	}
	// --------- /Private Helpers --------- //

}
