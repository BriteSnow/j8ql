package org.j8ql.test;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Runner;
import org.j8ql.test.app.Ticket;
import org.junit.Test;

import static org.j8ql.query.Query.insert;

public class EnumTypeTest extends TestSupport {

	@Test
	public void testSimpleEnum() {
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			//InsertQuery<Long> insertQuery = insert("ticket").columns("id","subject","type").values("1","test_testSimpleEnum", Ticket.Type.bug).returningIdAs(Long.class);

			runner.execute("insert into ticket (id,subject,type) values (?,?,?)",12L,"test_testSimpleEnum",Ticket.Type.bug);

		}
	}
}
