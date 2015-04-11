package org.j8ql.test;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Record;
import org.j8ql.Runner;
import org.j8ql.query.InsertQuery;
import org.j8ql.query.Query;
import org.j8ql.query.SelectQuery;
import org.j8ql.query.UpdateQuery;
import org.j8ql.test.app.Ticket;
import org.j8ql.test.app.User;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;



public class ReadmeTest extends TestSupport {

	@Test
	public void rawSQLs(){
		// dataSource can be built via standard JDBC, or Pool like C3P0 or HikariCP for example
		DB db = new DBBuilder().build(dataSource);

		// 1 runner == 1 database connection
		try (Runner runner = db.openRunner()) {

			// Execute raw SQL insert with parameters
			runner.execute("insert into \"user\" (id,username,since) values (?,?,?)",
					       12L,"john",1997);

			// Execute a SQL select and return a list of Record (Record implements Map)
			List<Record> records = runner.list("select * from \"user\" where id = ?",12L);
			// users.size() == 1
			assertEquals(1, records.size());
			// users.get(0).get("username") == "john"
			assertEquals("john", records.get(0).get("username"));

			// Execute same select but return as User.class
			List<User> users = runner.list(User.class, "select * from \"user\" where id = ?", 12L);
			// users.get(0).getUsername() == "john"
			assertEquals("john", users.get(0).getUsername());

			// Execute a sql select as stream
			try (Stream<User> stream = runner.stream(User.class,"select * from \"user\" where id = ?",12L)){
				User johnUser = stream.findFirst().get();
				assertEquals(Long.valueOf(12L), johnUser.getId());
				// johnUser.getUsername() == "john"
				assertEquals("john", johnUser.getUsername());
				assertEquals(Integer.valueOf(1997), johnUser.getSince());
			} // stream will be closed, which will close the enclosing PreparedStatement

		} // J8QL runner will be closed as well as the enclosing DB connection
	}


	@Test
	public void simpleQueryBuilder(){
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()){

			// Simple Insert Query via columns and values
			InsertQuery<Integer> insertJohn = Query.insert("user").columns("id", "username", "since")
					                            .values(12L, "john", 1997);
			// execute the insert (by default, return the numOfRowChanged -same as PreprateStatement.executeUpdate)
			int numOfRowChanged = runner.exec(insertJohn);
			assertEquals(numOfRowChanged, 1);

			// mapOf is an static utility to create name/value hashmap
			Map jenMap = mapOf("id",13L,"username","jen","since",2004);

			// Insert a Map or Pojo object, and returning the table PK as type Long
			InsertQuery<Long> insertJen = Query.insert("user").value(jenMap).returningIdAs(Long.class);
			// execute the insert
			Long jenId = runner.exec(insertJen);
			assertEquals(13L, jenId.longValue());

			// Using Select Query to list all User.class
			SelectQuery<User> selectUsers = Query.select(User.class).orderBy("since");

			// List all users
			List<User> users = runner.list(selectUsers);
			assertEquals(12L, users.get(0).getId().longValue());
			assertEquals(13L, users.get(1).getId().longValue());

			// Select the first and add a condition to an existing Query
			// Note 1: Query objects immutable and any call return new ones (so, completely thread safe)
			// Note 2: Runner::first will actually set if needed offset:0 and limit:1 to request only one.
			User jenUser = runner.first(selectUsers.where("username","jen")).get(); // .first return Optional
			assertEquals(13L, jenUser.getId().longValue());

			// Can use stream (make sure to close them)
			try(Stream<User> userStream = runner.stream(selectUsers)){
				List<User> tweentyFirstCenturyUsers = userStream.filter(u -> u.getSince() >= 2000).collect(toList());
				assertEquals(1,tweentyFirstCenturyUsers.size());
				assertEquals("jen", tweentyFirstCenturyUsers.get(0).getUsername());
			}

			// Updating is as trivial, and can even return the whole Object
			// Note: where clause name can have a convenient ";_OPERATOR_"
			UpdateQuery<Integer> updateTo21stCentury = Query.update(User.class).columns("since").values(2000);
			int numOfUpdatedUsers = runner.exec(updateTo21stCentury.where("since;<",2000));
			assertEquals(1,numOfUpdatedUsers);

			// Using a SelectQuery to count
			// Note: t\This will do the appropriate select count... with the query info
			long numOf21stCenturyUsers = runner.count(Query.select("user").where("since;>=",2000));
			assertEquals(2,numOf21stCenturyUsers);

		}
	}

	@Test
	public void columnAndValueExpression(){
		// dataSource can be built via standard JDBC, or Pool like C3P0 or HikariCP for example
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			// insert a tickets (just using raw SQL)
			runner.execute("insert into ticket (id,subject) values (?,?)", 1L, "UPPER ticket");
			runner.execute("insert into ticket (id,subject) values (?,?)", 2L, "lower ticket");

			SelectQuery<Ticket> selectQuery;

			// without the column expression (count of this select should be 0)
			selectQuery = Query.select(Ticket.class).where("subject", "upper ticket");
			// System.out.println(db.sql(selectQuery));
			// select "ticket".* from "ticket" where "subject" = ?
			assertEquals(0, runner.count(selectQuery));


			// With a custom operator (here ilike for case insensitive)
			selectQuery = Query.select(Ticket.class).where("subject;ilike", "upper ticket");
			// System.out.println(db.sql(selectQuery));
			// select "ticket".* from "ticket" where "subject" ilike ?
			assertEquals(1, runner.count(selectQuery));

			// With a column expression
			selectQuery = Query.select(Ticket.class).where("lower(subject)", "upper ticket");
			// System.out.println(db.sql(selectQuery));
			// select "ticket".* from "ticket" where lower(subject) = ?
			assertEquals(1, runner.count(selectQuery));


			// Not that when no operator "=" is used, so the above Query is similar than:
			selectQuery = Query.select(Ticket.class).where("lower(subject);=", "upper ticket");
			// System.out.println(db.sql(selectQuery));
			// select "ticket".* from "ticket" where lower(subject) = ?
			assertEquals(1, runner.count(selectQuery));

			// With a column expression and value expression
			selectQuery = Query.select(Ticket.class).where("lower(subject);=;lower(?)", "UPPER TICKET");
			// System.out.println(db.sql(selectQuery));
			// select "ticket".* from "ticket" where lower(subject) = lower(?)
			assertEquals(1, runner.count(selectQuery));
		}
	}


	@Test
	public void fullTextSearchExample() {
		// dataSource can be built via standard JDBC, or Pool like C3P0 or HikariCP for example
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			// insert the data
			runner.execute("insert into ticket (id,subject) values (?,?)", 1L, "test_ticket first ticket for the manager");
			runner.execute("insert into ticket (id,subject) values (?,?)", 2L, "test_ticket second ticket for a manager");
			runner.execute("insert into ticket (id,subject) values (?,?)", 3L, "test_ticket third ticket for a staff");

			// Build the tsv query
			// NOTE 1: You can add third part (with the second ";") and in this case, it will be columnNameOrFunction;operator;valueFunction
			// NOTE 2: The first "columnName" can be a function, and in this case, it won't be escaped;
			// NOTE 3: the last ";to_tsquery(?)" allow to optionally add a function value to the query (which is what we need for tsv search).
			SelectQuery<Ticket> tsvSelect = Query.select(Ticket.class).where("to_tsvector(ticket.subject);@@;to_tsquery(?)", "management");
			System.out.println("sql: " + db.sql(tsvSelect));
			// sql: select "ticket".* from "ticket" where to_tsvector(ticket.subject) @@ to_tsquery(?)

			List<Ticket> tickets = runner.list(tsvSelect);
			assertEquals(2, tickets.size());
		}
	}
}
