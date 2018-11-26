/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.primitives.Primitives;
import org.j8ql.query.*;
import org.j8ql.util.CloseableStream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.j8ql.RunnerException.RunnerError;

public class RunnerImpl implements Runner {

	private final Connection con;

	// Since the db is final, it cannot be changed, so, safe to give public access.
	public final DB db;

	RunnerImpl(DB db, Connection con) {
		this.con = con;
		this.db = db;
	}

	// --------- Count --------- //
	@Override
	public long count(SelectQuery selectBuilder){
		String sql = db.sql(selectBuilder,"*");
		Object[] values = db.values(selectBuilder);
		return executeCount(sql, values);
	}
	// --------- /Count --------- //

	// --------- First --------- //
	@Override
	public <T> Optional<T> first(SelectQuery<T> selectBuilder) {
		// make sure it is a limit one query
		selectBuilder = Objects.equals(selectBuilder.getLimit(), 1)?selectBuilder:selectBuilder.limit(1);
		try (Stream<T> stream = stream(selectBuilder)){
			return stream.findFirst();
		}
	}
	// --------- /First --------- //

	// --------- SelectQuery --------- //
	@Override
	public <T> Stream<T> stream(SelectQuery<T> selectBuilder){
		String sql = db.sql(selectBuilder,null);
		Object[] values = db.values(selectBuilder);
		return stream(selectBuilder.getAsClass(), sql, values);
	}
	
	@Override
	public <T> List<T> list(SelectQuery<T> selectBuilder){
		try (Stream<T> s = stream(selectBuilder)) {
			return s.collect(toList());
		}
	}
	// --------- /SelectQuery --------- //

	// --------- InsertQuery --------- //
	@Override
	public <T> T exec(InsertQuery<T> insertBuilder) {
		String sql = db.sql(insertBuilder);
		Object[] values = db.values(insertBuilder);
		return exec(insertBuilder, sql, values);
	}

	@Override
	public <T> List<T> list(InsertQuery<T> insertBuilder) {
		try (Stream<T> stream = stream(insertBuilder)) {
			return stream.collect(toList());
		}
	}

	@Override
	public <T> Stream<T> stream(InsertQuery<T> insertBuilder) {
		if (!insertBuilder.hasReturning()) {
			throw new RunnerException(RunnerError.DML_BUILDER_HAS_NO_RETURNING_CANNOT_BE_STREAMED);
		}
		Object[] values = db.values(insertBuilder);
		String sql = db.sql(insertBuilder);
		return stream(insertBuilder.getAsClass(), sql, values);
	}
	// --------- /InsertQuery --------- //

	// --------- UpdateQuery --------- //
	@Override
	public <T> T exec(UpdateQuery<T> updateBuilder){
		String sql = db.sql(updateBuilder);
		Object[] values = db.values(updateBuilder);
		return exec(updateBuilder, sql, values);
	}

	@Override
	public <T> List<T> list(UpdateQuery<T> updateBuilder) {
		try (Stream<T> stream = stream(updateBuilder)) {
			return stream.collect(toList());
		}
	}

	@Override
	public <T> Stream<T> stream(UpdateQuery<T> updateBuilder){
		if (!updateBuilder.hasReturning()) {
			throw new RunnerException(RunnerError.DML_BUILDER_HAS_NO_RETURNING_CANNOT_BE_STREAMED);
		}

		String sql = db.sql(updateBuilder);
		Object[] values = db.values(updateBuilder);
		return stream(updateBuilder.getAsClass(), sql, values);
	}
	// --------- /UpdateQuery --------- //

	// --------- DeleteQuery --------- //
	@Override
	public <T> T exec(DeleteQuery<T> deleteBuilder) {
		String sql = db.sql(deleteBuilder);
		Object[] values = db.values(deleteBuilder);
		return exec(deleteBuilder, sql, values);
	}

	@Override
	public <T> List<T> list(DeleteQuery<T> deleteBuilder) {
		try(Stream<T> stream = stream(deleteBuilder)) {
			return stream.collect(toList());
		}
	}

	@Override
	public <T> Stream<T> stream(DeleteQuery<T> deleteBuilder){
		if (!deleteBuilder.hasReturning()) {
			throw new RunnerException(RunnerError.DML_BUILDER_HAS_NO_RETURNING_CANNOT_BE_STREAMED);
		}

		String sql = db.sql(deleteBuilder);
		Object[] values = db.values(deleteBuilder);
		return stream(deleteBuilder.getAsClass(), sql, values);
	}
	// --------- /DeleteQuery --------- //

	// --------- batch --------- //
	@Override
	public int[] executeBatch(InsertQuery builder) {
		if (builder.getBatchValues() == null && builder.getBatchObjects() == null){
			throw new RunnerException(RunnerError.CANNOT_BATCH_DOES_NOT_HAVE_BATCH_VALUE);
		}
		String sql = db.sql(builder);
		List<List> batchValues = db.batchValues(builder);
		try (PStmt query = newPQuery(sql)) {
			return query.executeBatch(batchValues);
		}
	}
	// --------- /batch --------- //

	// --------- Private Helpers --------- //
	private <T> T exec(IUDQuery<T> builder, String sql, Object[] values) {
		Class<T> asClass = builder.getAsClass();
		boolean hasReturning = builder.hasReturning();
		String tableName = db.getTableDef(builder).getName();
		try (PStmt query = newPQuery(sql)) {
			if (asClass == Integer.class && !hasReturning){
				// Here
				Integer num = query.executeUpdate(values);
				return (T) num;
			}else {
				Record rec = query.executeWithSingleReturn(values).orElse(null);

				if (rec != null){
					if (Primitives.isWrapperType(asClass)){
						// if it is a primitive type, then, we assume a single value
						if (rec.size() > 1){
							// TODO: need to create a RunnerException and DBError
							throw new DataAccessException(format("updateBuilder or insertBuilder with a primitive asClass returning more than one values. Can't convert %s to %s",rec,asClass));
						}else{
							Object singleVal = rec.values().iterator().next();
							return db.mapper.as(asClass, singleVal);
						}
					}else {
						return db.mapper.as(asClass,rec);
					}
				}else{
					// TODO: Probably need to throw an error here.
					return null;
				}
			}
		}
	}
	// --------- /Private Helpers --------- //

	// --------- Stream and List APIs --------- //
	@Override
	public <T> List<T> list(Class<T> cls, String sql, Object... values) {
		try (Stream<T> s = stream(cls,sql,values)) {
			return s.collect(toList());
		}
	}

	@Override
	public List<Record> list(String sql, Object... values) {
		try (Stream<Record> s = stream(sql,values)) {
			return s.collect(toList());
		}
	}

	@Override
	public <T> Stream<T> stream(Class<T> cls, String sql, Object... values) {
		PStmt query = newPQuery(sql);
		Stream<T> queryStream = query.stream(cls,values);
		Stream<T> runnerScopedStream = new CloseableStream<>(queryStream,query::close);
		return runnerScopedStream;
	}

	@Override
	public Stream<Record> stream(String sql, Object... values) {
		PStmt query = newPQuery(sql);
		Stream<Record> queryStream = query.stream(values);
		Stream<Record> runnerScopedStream = new CloseableStream<>(queryStream,query::close);
		return runnerScopedStream;
	}
	// --------- /Stream and List APIs --------- //

	// --------- j8ql High Level Execute --------- //
	@Override
	public long executeCount(String sql, Object... values) {
		PStmt query = null;
		long r;
		try {
			query = newPQuery(sql);
			r = query.executeCount(values);
		} finally {
			if (query != null) {
				query.close();
			}
		}
		return r;
	}

	@Override
	public Optional<Record> executeWithSingleReturn(String sql, Object... values) {
		try (PStmt query = newPQuery(sql)){
			return query.executeWithSingleReturn(values);
		}
	}
	// --------- /j8ql High Level Execute --------- //

	// --------- j8ql Low Level Execute --------- //
	@Override
	public int executeUpdate(String sql, Object... values) {
		try (PStmt query = newPQuery(sql)) {
			return query.executeUpdate(values);
		}
	}

	@Override
	public boolean execute(String sql, Object... values) {
		try (PStmt query = newPQuery(sql)){
			return query.execute(values);
		}
	}
	// --------- /j8ql Low Level Execute --------- //

	// --------- Stmt Factory --------- //
	@Override
	public PStmt newPQuery(String sql) {
		try {
			PreparedStatement pstmt;
			pstmt = con.prepareStatement(sql);

			return new PStmt(db, sql, pstmt);
		} catch (SQLException e) {
			throw new RSQLException(e);
		}
	}
	// --------- /Stmt Factory --------- //

	// --------- Transaction --------- //
	@Override
	public Runner startTransaction() {
		try {
			con.setAutoCommit(false);
			return this;
		} catch (SQLException e) {
			throw new RSQLException(e);
		}
	}

	@Override
	public Runner endTransaction() {
		try {
			con.setAutoCommit(true);
			return this;
		} catch (SQLException e) {
			throw new RSQLException(e);
		}
	}

	@Override
	public Runner roolback() {
		try {
			con.rollback();
			return this;
		} catch (SQLException e) {
			throw new RSQLException(e);
		}
	}

	@Override
	public Runner commit() {
		try {
			con.commit();
			return this;
		} catch (SQLException e) {
			throw new RSQLException(e);
		}
	}
	// --------- /Transaction --------- //

	@Override
	public boolean isClosed(){
		try {
			return con.isClosed();
		} catch (SQLException e) {
			throw new RSQLException(e);
		}
	}

	// --------- AutoCloseable --------- //
	/**
	 * Close the runner (i.e. the connection)
	 */
	@Override
	public void close() {
		try {
			con.close();
		} catch (SQLException e) {
			throw new RSQLException(e);
		}
	}
	// --------- /AutoCloseable --------- //



}
