package org.j8ql;

import org.j8ql.query.DeleteQuery;
import org.j8ql.query.InsertQuery;
import org.j8ql.query.SelectQuery;
import org.j8ql.query.UpdateQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class RunnerProxy implements Runner {

	protected final Runner runner;

	public RunnerProxy(Runner runner) {
		this.runner = runner;
	}

	@Override
	public long count(SelectQuery selectBuilder) {
		return runner.count(selectBuilder);
	}

	@Override
	public <T> Optional<T> first(SelectQuery<T> selectBuilder) {
		return runner.first(selectBuilder);
	}

	@Override
	public <T> Stream<T> stream(SelectQuery<T> selectBuilder) {
		return runner.stream(selectBuilder);
	}

	@Override
	public <T> List<T> list(SelectQuery<T> selectBuilder) {
		return runner.list(selectBuilder);
	}

	@Override
	public <T> T exec(InsertQuery<T> insertBuilder) {
		return runner.exec(insertBuilder);
	}

	@Override
	public <T> List<T> list(InsertQuery<T> insertBuilder) {
		return runner.list(insertBuilder);
	}

	@Override
	public <T> Stream<T> stream(InsertQuery<T> insertBuilder) {
		return runner.stream(insertBuilder);
	}

	@Override
	public <T> T exec(UpdateQuery<T> updateBuilder) {
		return runner.exec(updateBuilder);
	}

	@Override
	public <T> List<T> list(UpdateQuery<T> updateBuilder) {
		return runner.list(updateBuilder);
	}

	@Override
	public <T> Stream<T> stream(UpdateQuery<T> updateBuilder) {
		return runner.stream(updateBuilder);
	}

	@Override
	public <T> T exec(DeleteQuery<T> deleteBuilder) {
		return runner.exec(deleteBuilder);
	}

	@Override
	public <T> List<T> list(DeleteQuery<T> deleteBuilder) {
		return runner.list(deleteBuilder);
	}

	@Override
	public <T> Stream<T> stream(DeleteQuery<T> deleteBuilder) {
		return runner.stream(deleteBuilder);
	}

	@Override
	public int[] executeBatch(InsertQuery builder) {
		return runner.executeBatch(builder);
	}

	@Override
	public <T> List<T> list(Class<T> cls, String sql, Object... values) {
		return runner.list(cls, sql, values);
	}

	@Override
	public List<Record> list(String sql, Object... values) {
		return runner.list(sql, values);
	}

	@Override
	public <T> Stream<T> stream(Class<T> cls, String sql, Object... values) {
		return runner.stream(cls, sql, values);
	}

	@Override
	public Stream<Record> stream(String sql, Object... values) {
		return runner.stream(sql, values);
	}

	@Override
	public long executeCount(String sql, Object... values) {
		return runner.executeCount(sql, values);
	}

	@Override
	public Optional<Record> executeWithSingleReturn(String sql, Object... values) {
		return runner.executeWithSingleReturn(sql, values);
	}

	@Override
	public int executeUpdate(String sql, Object... values) {
		return runner.executeUpdate(sql, values);
	}

	@Override
	public boolean execute(String sql, Object... values) {
		return runner.execute(sql, values);
	}

	@Override
	public PStmt newPQuery(String sql) {
		return runner.newPQuery(sql);
	}

	@Override
	public Runner startTransaction() {
		return runner.startTransaction();
	}

	@Override
	public Runner endTransaction() {
		return runner.endTransaction();
	}

	@Override
	public Runner roolback() {
		return runner.roolback();
	}

	@Override
	public Runner commit() {
		return runner.commit();
	}

	@Override
	public boolean isClosed() {
		return runner.isClosed();
	}

	@Override
	public void close() {
		runner.close();
	}
}
