package org.j8ql.test;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Runner;
import org.j8ql.test.app.Ticket;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Date;

import static org.j8ql.query.Query.select;
import static org.junit.Assert.assertEquals;

public class TypeInsertTest extends TestSupport {

	@Test
	public void testLocalDateType() {
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			Date oldDueDate = new Date();
			runner.executeUpdate("insert into ticket (id,subject,\"dueDate\") values (?,?,?)", 1, "test subject", oldDueDate);

			LocalDate dueDate = LocalDate.now();
			runner.executeUpdate("insert into ticket (id,subject,\"dueDate\") values (?,?,?)", 2, "test subject", dueDate);

			Ticket ticket = runner.first(select(Ticket.class).whereId(2)).get();
			assertEquals(dueDate.atStartOfDay(), ticket.getDueDate());
		}
	}
}
