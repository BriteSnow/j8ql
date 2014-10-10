/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test.query;

import org.j8ql.*;
import org.j8ql.query.InsertQuery;
import org.j8ql.test.TestSupport;
import static org.j8ql.query.Query.*;
import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.j8ql.test.app.Label;
import org.j8ql.test.app.User;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * <p></p>
 */
public class InsertQueryTest extends TestSupport {

	private Object[][] users = new Object[][]{{1L,"mikeIBT","developer"},{2L,"jenIBT","manager"},{3L,"paulIBT","developer"}};


	@Test
	public void readmeInserts(){
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()){
			// You can create a baseInsert InsertQuery to be reused later on, because
			// each InsertQuery call create a new immutable InsertQuery, making composition thread safe.
			InsertQuery<Integer> baseInsert = insert().into("user").columns("id","username","title");

			// Note: the <generic> (by default <Integer>) is the type of the value returned when executing this InsertQuery
			//       by default, executing an InsertQuery will return the number of row changed

			// create a new InsertQuery from baseInsert with the values.
			InsertQuery<Integer> simpleInsert = baseInsert.values(1L,"mike","developer");
			int numberOfCreated = runner.execute(simpleInsert);
			assertEquals(1,numberOfCreated);

			// insert returning the column id as Long
			InsertQuery<Long> insertWithId = baseInsert.returningIdAs(Long.class).values(2L,"jen","manager");
			Long userId = runner.execute(insertWithId);
			assertEquals((Long)2L,userId);


			// or we can can return the contact object itself with some specific columns
			InsertQuery<User> insertWithUser = baseInsert.returning(User.class, "id", "username", "title").values(3L,"paul","manager");
			User user = runner.execute(insertWithUser);
			assertEquals((Long)3L,user.getId());
			assertEquals("paul",user.getUsername());
		}
	}


	@Test
	public void simpleInsert() {
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()){
			InsertQuery<Integer> ib = insert().columns("id","username","title").into("user");
			for (Object[] row : users) {
				int numUpdated = runner.execute(ib.values(row));
				assertEquals(1, numUpdated);
			}

			List<User> users = runner.list(User.class, "select * from \"user\"");
			assertEquals(3,users.size());
			assertEquals("mikeIBT", users.get(0).getUsername());
		}
	}

	@Test
	public void insertWithReturningIdAsLong(){
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			InsertQuery<Long> ib = insert().columns("id", "username", "title").into("user").returningIdAs(Long.class);
			for (Object[] row : users) {
				Long id = runner.execute(ib.values(row));
				assertEquals(row[0], id);
			}
		}
	}

	@Test
	public void insertWithReturningUserColumns(){
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			InsertQuery<User> ib = insert().columns("id", "username", "title").into("user").returning(User.class, "id", "username", "title");

			for (Object[] row : users) {
				User user = runner.execute(ib.values(row));
				assertEquals(row[1], user.getUsername());
			}
		}
	}

	@Test
	public void insertWithReturningUserClass(){
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			InsertQuery<User> ib = insert().columns("id", "username", "title").into("user").returning(User.class);

			for (Object[] row : users) {
				User user = runner.execute(ib.values(row));
				assertEquals(row[1], user.getUsername());
			}
		}
	}

	@Test
	public void insertWithStream(){
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			InsertQuery<User> ib = insert().columns("id", "username", "title").into("user").returning(User.class, "id", "username", "title");

			try (Stream<User> stream = runner.stream(ib.values(users[0]))){
				User user = stream.findFirst().get();
				assertEquals("mikeIBT", user.getUsername());
			}
		}
	}

	@Test
	public void insertValueObject() {
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			InsertQuery<Integer> ib = insert().into("user");

			// insert with a Map
			Map userMap = mapOf("id", 1L, "username", "jon", "title", "director");
			int numUpdated = runner.execute(ib.value(userMap));
			assertEquals(1, numUpdated);

			// insert with a Type object
			User user = new User().setId(2L).setUsername("mike").setTitle("manager");
			numUpdated = runner.execute(ib.value(user));
			assertEquals(1, numUpdated);

			// with a specific set of columns
			user = new User().setId(3L).setUsername("jen").setTitle("manager");
			runner.execute(ib.columns("id", "username").value(user));

			List<User> users = runner.list(User.class, "select * from \"user\"");
			assertEquals(3, users.size());
		}
	}


	@Test
	public void insertLabelWithNullId(){
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			Label label = new Label();
			label.setName("Test Label");
			runner.execute(insert("label").value(label));
		}
	}

	@Test
	public void invalidColumnExceptionTest(){
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			// test exception when invalid column
			User user = new User().setId(4L).setUsername("brian").setTitle("manager");
			try {
				runner.execute(insert().into("user").columns("id", "username", "foo").value(user));
			} catch (RSQLException re) {
				// exception should be something like: ERROR: column "foo" of relation "contact" does not exist
				// So, simply test that the message contain "foo"
				assertTrue(re.getMessage().indexOf("foo") > -1);
			}
		}
	}


}
