package org.j8ql.test;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Record;
import org.j8ql.Runner;
import org.j8ql.query.Query;
import org.j8ql.query.SelectQuery;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubSelectTest extends TestSupport {
	private Object[][] data = new Object[][]{{1,40, "Medium low ticket 1"},
			{2,40, "Medium low ticket 2"},
			{3,49, "Medium ticket 3"},
			{4,51, "Medium ticket 4"},
			{5,60, "Medium high ticket 5"}};


	@Test
	public void simpleSubSelect() {
		// dataSource can be built via standard JDBC, or Pool like C3P0 or HikariCP for example
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {

			// insert the data
			for (Object[] vals : data) {
				runner.execute("insert into ticket (id,priority,subject) values (?,?,?)", vals);
			}

			SelectQuery<Record> subSelect, fullSelect;


			// --------- SubSelect --------- //
			// select avg(priority) from "ticket"
			subSelect = Query.select("ticket").columns("avg(priority)");
			// System.out.println(db.sql(subSelect));

			// select "ticket".* from "ticket" where "priority" > (select avg(priority) from "ticket")
			fullSelect = Query.select("ticket").where("priority;>", subSelect);
			// System.out.println(db.sql(fullSelect));

			// Should match three rows (because avg is 48)
			assertEquals(3, runner.count(fullSelect));
			// --------- /SubSelect --------- //

			// --------- SubSelect with parameters --------- //
			// select "id" from "ticket" where "priority" >= ?
			subSelect = Query.select("ticket").columns("id").where("priority;>=", 50);
			// System.out.println(db.sql(subSelect));

			// select "ticket".* from "ticket" where "id" in (select "id" from "ticket" where "priority" >= ?)
			fullSelect = Query.select("ticket").where("id;in", subSelect);
			// System.out.println(db.sql(fullSelect));

			// Should match two rows
			assertEquals(2, runner.count(fullSelect));
			// --------- /SubSelect with parameters --------- //

		}
	}
}
