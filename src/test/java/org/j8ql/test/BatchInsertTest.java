package org.j8ql.test;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.PStmt;
import org.j8ql.Runner;
import org.j8ql.query.Query;
import org.j8ql.query.InsertQuery;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.j8ql.query.Query.select;
import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;

public class BatchInsertTest extends TestSupport {


	@Test
	public void testBatchInsertPQuery() {
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			Object[][] data = {{1, "ticket 01"}, {2, "ticket 02"}};

			try (PStmt pQuery = runner.newPQuery("insert into ticket (id, subject) values (?,?)")) {
				List<List> batchValues = Stream.of(data).map(Arrays::asList).collect(Collectors.toList());
				pQuery.executeBatch(batchValues);
			}
			assertEquals(2, runner.count(select("ticket")));
			assertEquals(data[0][1],runner.first(select("ticket").whereId(1)).get().get("subject"));
		}
	}

	@Test
	public void testBatchInsertBuilderWithColumnValues() {
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			Object[][] data = {{1, "ticket 01 testBatchInsertValues"}, {2, "ticket 02 testBatchInsertValues"}};
			List<List> batchValues = Stream.of(data).map(Arrays::asList).collect(Collectors.toList());
			InsertQuery<Integer> insert = Query.insert("ticket").columns("id", "subject");

			runner.executeBatch(insert.batchValues(batchValues));

			assertEquals(2, runner.count(select("ticket")));
			assertEquals(data[0][1],runner.first(select("ticket").whereId(1)).get().get("subject"));
		}
	}

	@Test
	public void testBatchInsertBuilderWithColumnsObjectValues() {
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			Map[] data = {mapOf("id", 1, "subject", "ticket 01 testBatchInsertBuilderWithObjectValues"), mapOf("id", 2, "subject", "ticket 02 testBatchInsertBuilderWithObjectValues") };
			List batchObjects = Arrays.asList(data);
			InsertQuery<Integer> insert = Query.insert("ticket").columns("id", "subject");
			runner.executeBatch(insert.batchObjects(batchObjects));

			// check
			assertEquals(2, runner.count(select("ticket")));
			assertEquals(data[0].get("subject"),runner.first(select("ticket").whereId(1)).get().get("subject"));
		}
	}

	@Test
	public void testBatchInsertBuilderWithObjectValues() {
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			Map[] data = {mapOf("id", 1, "subject", "ticket 01 testBatchInsertBuilderWithObjectValues"), mapOf("id", 2, "subject", "ticket 02 testBatchInsertBuilderWithObjectValues") };
			List batchObjects = Arrays.asList(data);
			InsertQuery<Integer> insert = Query.insert("ticket");
			runner.executeBatch(insert.batchObjects(batchObjects));

			// check
			assertEquals(2, runner.count(select("ticket")));
			assertEquals(data[0].get("subject"),runner.first(select("ticket").whereId(1)).get().get("subject"));

		}
	}
}
