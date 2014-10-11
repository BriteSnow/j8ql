package org.j8ql;

import org.j8ql.query.DeleteQuery;
import org.j8ql.query.InsertQuery;
import org.j8ql.query.SelectQuery;
import org.j8ql.query.UpdateQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface Runner extends AutoCloseable {
	long count(SelectQuery selectBuilder);

	<T> Optional<T> first(SelectQuery<T> selectBuilder);

	// --------- SelectQuery --------- //
	<T> Stream<T> stream(SelectQuery<T> selectBuilder);

	<T> List<T> list(SelectQuery<T> selectBuilder);
	// --------- /SelectQuery --------- //

	// --------- InsertQuery --------- //
	<T> T exec(InsertQuery<T> insertBuilder);

	<T> List<T> list(InsertQuery<T> insertBuilder);

	<T> Stream<T> stream(InsertQuery<T> insertBuilder);
	// --------- /InsertQuery --------- //

	// --------- UpdateQuery --------- //
	<T> T exec(UpdateQuery<T> updateBuilder);

	<T> List<T> list(UpdateQuery<T> updateBuilder);

	<T> Stream<T> stream(UpdateQuery<T> updateBuilder);
	// --------- /UpdateQuery --------- //

	// --------- DeleteQuery --------- //
	<T> T exec(DeleteQuery<T> deleteBuilder);

	<T> List<T> list(DeleteQuery<T> deleteBuilder);

	<T> Stream<T> stream(DeleteQuery<T> deleteBuilder);
	// --------- /DeleteQuery --------- //

	int[] executeBatch(InsertQuery builder);

	// --------- SQL Query APIs --------- //
	<T> List<T> list(Class<T> cls, String sql, Object... values);

	List<Record> list(String sql, Object... values);

	<T> Stream<T> stream(Class<T> cls, String sql, Object... values);

	Stream<Record> stream(String sql, Object... values);
	// --------- /SQL Query APIs --------- //

	// --------- j8ql High Level Execute --------- //
	long executeCount(String sql, Object... values);

	Optional<Record> executeWithSingleReturn(String sql, Object... values);
	// --------- /j8ql High Level Execute --------- //

	// --------- j8ql Raw SQL Execute --------- //

	/**
	 * Create a JDBC PreparedStatement, call executeUpdate() and return its result.
	 *
	 * @param sql
	 * @param values
	 * @return the int returned by JDBC PreparedStatement.executeUpdate
	 */
	int executeUpdate(String sql, Object... values);

	/**
	 * Create a JDBC PreparedStatement, call .execute() and return its boolean result.
	 * @param sql
	 * @param values
	 * @return the boolean returned by the JDBC prepared statement .execute
	 */
	boolean execute(String sql, Object... values);
	// --------- /j8ql Raw SQL Execute --------- //

	// --------- Stmt Factory --------- //
	PStmt newPQuery(String sql);
	// --------- /Stmt Factory --------- //

	// --------- Transaction --------- //
	Runner startTransaction();

	Runner endTransaction();

	Runner roolback();

	Runner commit();
	// --------- /Transaction --------- //

	boolean isClosed();

	void close();
}
