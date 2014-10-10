/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

import com.google.common.collect.ObjectArrays;
import org.j8ql.Record;
import org.j8ql.util.Immutables;

import java.util.Arrays;
import java.util.List;

import static org.j8ql.query.Query.and;

/**
 * A Query to build select queries. Can be used for runner.count(selectBuilder) as well.
 */
public class SelectQuery<T> extends BaseQuery<T> implements Where<SelectQuery<T>> {

	private List<String> columns;

	private Condition where = null;
	private Object whereId = null;

	private Long limit = null;
	private Long offset = null;

	private String[] orderBys = null;

	private Join[] joins = null;

	// --------- Base Constructors --------- //
	SelectQuery(Class<T> asClass) {
		super(asClass);
	}

	SelectQuery(Class<T> asClass, Class tableClass) {
		super(asClass, tableClass);
	}
	// --------- /Base Constructors --------- //

	// --------- Clone Constructors --------- //
	private SelectQuery(SelectQuery<T> oldBuilder) {
		this(oldBuilder, oldBuilder.getAsClass());
	}

	private SelectQuery(SelectQuery oldBuilder, Class<T> asClass){
		super(oldBuilder, asClass);
		columns = oldBuilder.columns;
		where = oldBuilder.where;
		whereId = oldBuilder.whereId;
		limit = oldBuilder.limit;
		offset = oldBuilder.offset;
		orderBys = oldBuilder.orderBys;
		joins = oldBuilder.joins;
	}
	// --------- /Clone Constructors --------- //

	// --------- Package Scoped Static Factories (used by Builders) --------- //
	static SelectQuery<Record> select(){
		return new SelectQuery<Record>(Record.class);
	}

	static <T> SelectQuery<T> select(Class<T> asClass, Class tableClass){
		return new SelectQuery<>(asClass, tableClass);
	}

	// --------- /Package Scoped Static Factories (used by Builders) --------- //

	public <K> SelectQuery<K> as(Class<K> asClass){
		return new SelectQuery<K>(this,asClass);
	}

	// --------- From --------- //
	public SelectQuery<T> from(String tableName) {
		return table(new SelectQuery<T>(this),tableName);
	}

	public SelectQuery<T> from(Class tableClass) {
		return table(new SelectQuery<T>(this), tableClass);
	}
	// --------- /From --------- //

	// --------- Columns --------- //
	public SelectQuery<T> columns(String... columnNames){
		if (columnNames == null){
			this.columns = null;
		}else{
			// TODO: probably can use the Guava Immutables as we should not have null in this list.
			this.columns = Immutables.of(columnNames);
		}
		return this;
	}
	// --------- /Columns --------- //

	// --------- Where --------- //
	@Override
	public SelectQuery<T> where(Condition where){
		SelectQuery<T> newSelectBuilder = new SelectQuery<>(this);
		newSelectBuilder.where = where;
		newSelectBuilder.whereId = null;
		return newSelectBuilder;
	}

	@Override
	public SelectQuery<T> whereId(Object whereId){
		SelectQuery<T> newBuilder = new SelectQuery<>(this);
		newBuilder.whereId = whereId;
		newBuilder.where = null;
		return newBuilder;
	}
	// --------- /Where --------- //

	// --------- Clause Methods --------- //
	public SelectQuery<T> limit(Integer limit){
		return limit((limit != null)? limit.longValue() : null);
	}

	public SelectQuery<T> limit(Long limit){
		SelectQuery<T> newSelectBuilder = new SelectQuery<>(this);
		newSelectBuilder.limit = limit;
		return newSelectBuilder;
	}

	public SelectQuery<T> offset(Integer offset){
		return offset((offset != null)? offset.longValue() : null);
	}

	public SelectQuery<T> offset(Long offset){
		SelectQuery<T> newSelectBuilder = new SelectQuery<>(this);
		newSelectBuilder.offset = offset;
		return newSelectBuilder;
	}

	/**
	 * <p>Add orderBys to the new SelectQuery.</p>
	 * <p>If orderBys is not null, it will be added to the current one (or set if not preexisting array).</p>
	 * <p>If orderBys is NULL, then, it will set the orderbys of the new SelectQuery to null (use that to remove previously set orderBys)</p>
	 * @param orderBys
	 * @return
	 */
	public SelectQuery<T> orderBy(String... orderBys){
		SelectQuery<T> newSelectBuilder = new SelectQuery<>(this);
		String[] newOrderBys = null;
		if (orderBys != null){
			if (this.orderBys != null){
				String[] both = ObjectArrays.concat(this.orderBys, orderBys, String.class);
				newOrderBys = both;
			}else{
				newOrderBys = Arrays.copyOf(orderBys,orderBys.length);
			}
		}
		// Note that if the param orderBys is null, then, null will be set (useful to reset orderbys for a next query)
		newSelectBuilder.orderBys = newOrderBys;
		return newSelectBuilder;
	}
	// --------- /Clause Methods --------- //

	// --------- Join --------- //
	public SelectQuery<T> innerJoin(String joinTable, String joinTableColumn, String onTable, String onTableColumn) {
		return join(Join.Type.inner,joinTable,joinTableColumn,onTable, onTableColumn);
	}

	public SelectQuery<T> leftJoin(String joinTable, String joinTableColumn, String onTable, String onTableColumn) {
		return join(Join.Type.left,joinTable,joinTableColumn,onTable, onTableColumn);
	}

	public SelectQuery<T> rightJoin(String joinTable, String joinTableColumn, String onTable, String onTableColumn) {
		return join(Join.Type.right,joinTable,joinTableColumn,onTable, onTableColumn);
	}

	private SelectQuery<T> join(Join.Type type, String joinTable, String joinTableColumn, String onTable, String onTableColumn) {
		SelectQuery<T> newSelectBuilder = new SelectQuery<>(this);
		Join[] newJoins = null;
		Join join = new Join(type, joinTable, joinTableColumn, onTable, onTableColumn);
		if (this.joins != null){
			newJoins = ObjectArrays.concat(this.joins,join);
		}else{
			newJoins = new Join[1];
			newJoins[0]= join;
		}
		newSelectBuilder.joins = newJoins;

		return newSelectBuilder;
	}
	// --------- /Join --------- //

	// --------- Accessors --------- //
	public List<String> getColumns() {
		return columns;
	}

	@Override
	public Condition getWhere() {
		return where;
	}

	@Override
	public Object getWhereId() {
		return whereId;
	}

	public Long getLimit() {
		return limit;
	}

	public Long getOffset() {
		return offset;
	}

	public String[] getOrderBys() {
		return orderBys;
	}

	public Join[] getJoins() {
		return joins;
	}
	// --------- /Accessors --------- //
}
