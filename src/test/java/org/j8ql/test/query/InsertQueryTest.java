/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test.query;

import com.google.common.collect.Sets;
import org.j8ql.*;
import org.j8ql.query.InsertQuery;
import org.j8ql.query.Query;
import org.j8ql.test.TestSupport;
import static org.j8ql.query.Query.*;
import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.j8ql.test.app.Label;
import org.j8ql.test.app.User;
import org.junit.Test;

import java.util.*;
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
			int numberOfCreated = runner.exec(simpleInsert);
			assertEquals(1,numberOfCreated);

			// insert returning the column id as Long
			InsertQuery<Long> insertWithId = baseInsert.returningIdAs(Long.class).values(2L,"jen","manager");
			Long userId = runner.exec(insertWithId);
			assertEquals((Long)2L,userId);


			// or we can can return the contact object itself with some specific columns
			InsertQuery<User> insertWithUser = baseInsert.returning(User.class, "id", "username", "title").values(3L,"paul","manager");
			User user = runner.exec(insertWithUser);
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
				int numUpdated = runner.exec(ib.values(row));
				assertEquals(1, numUpdated);
			}

			List<User> users = runner.list(User.class, "select * from \"user\"");
			assertEquals(3,users.size());
			assertEquals("mikeIBT", users.get(0).getUsername());
		}
	}

	@Test
	public void simpleInsertColumns() {
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()){

			//// Test with explicit column array
			// the userMap value will have the title
			Map userMap = mapOf("id",123L,"username","test_simpleInsertColumns-user-1","title","test_title");

			// we run the insertQuery
			runner.exec(insert("user").columns("id","username").value(userMap));

			// get it back from the db
			Map userMapFromDb = runner.first(select("user").whereId(123L)).get();
			// check that the username match
			assertEquals("test_simpleInsertColumns-user-1", userMapFromDb.get("username"));
			// and check title is null
			assertNull(userMapFromDb.get("title"));

			//// try it with a Set for columns
			userMap = mapOf("id",124L,"username","test_simpleInsertColumns-user-2","title","test_title");
			runner.exec(insert("user").columns(Sets.newHashSet("id", "username")).value(userMap));
			userMapFromDb = runner.first(select("user").whereId(124L)).get();
			assertEquals("test_simpleInsertColumns-user-2", userMapFromDb.get("username"));
			assertNull(userMapFromDb.get("title"));
		}
	}

	@Test
	public void simpleExcludeColumns(){
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {

			//// check with a value map and excludeColumns
			// here we not not include title
			InsertQuery<Integer> ib = insert().into("user");

			// Insert the userMap and exclude the title
			Map userMap = mapOf("id",123L,"username","test_simpleExcludeColumns-user-1","title","test_title");
			runner.exec(insert("user").value(userMap).excludeColumns("title"));

			// check username set correctly, and title null (because it was excluded)
			Map userMapFromDb = runner.first(select("user").whereId(123L)).get();
			assertEquals("test_simpleExcludeColumns-user-1", userMapFromDb.get("username"));
			assertNull(userMapFromDb.get("title"));

			///// check with explicit columns and excluded columns
			userMap = mapOf("id",124L,"username","test_simpleExcludeColumns-user-2","title","test_title");
			runner.exec(insert("user").value(userMap).columns("id","username","title").excludeColumns("title"));
			userMapFromDb = runner.first(select("user").whereId(124L)).get();
			assertEquals("test_simpleExcludeColumns-user-2", userMapFromDb.get("username"));
			assertNull(userMapFromDb.get("title"));

			//// Check with Entity object
			User user = new User();
			user.setUsername("test_simpleExcludeColumns-user-2").setId(125L).setTitle("title");
			runner.exec(insert(User.class).value(user).excludeColumns("title"));
			assertNull(runner.first(select(User.class).whereId(125L)).get().getTitle());
		}
	}

	@Test
	public void insertWithReturningIdAsLong(){
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			InsertQuery<Long> ib = insert().columns("id", "username", "title").into("user").returningIdAs(Long.class);
			for (Object[] row : users) {
				Long id = runner.exec(ib.values(row));
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
				User user = runner.exec(ib.values(row));
				assertEquals(row[1], user.getUsername());
			}
		}
	}

	@Test
	public void insertUserMapWithExtraColumns(){
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			InsertQuery<Long> ib = insert("user").returningIdAs(Long.class);

			Map userMap = mapOf("id", 123L, "username", "test_username123");
			userMap.put("foo","bar");
			Set<String> columns = userMap.keySet();

			Long userId = runner.exec(ib.value(userMap).columns(db.getValidColumns(ib, columns)));

			User user = runner.first(Query.select(User.class).whereId(userId)).get();
			assertEquals(123L, user.getId().longValue());
			assertEquals("test_username123", user.getUsername());


		}
	}


	@Test
	public void insertWithReturningUserClass(){
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			InsertQuery<User> ib = insert().columns("id", "username", "title").into("user").returning(User.class);

			for (Object[] row : users) {
				User user = runner.exec(ib.values(row));
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
			int numUpdated = runner.exec(ib.value(userMap));
			assertEquals(1, numUpdated);

			// insert with a Type object
			User user = new User().setId(2L).setUsername("mike").setTitle("manager");
			numUpdated = runner.exec(ib.value(user));
			assertEquals(1, numUpdated);

			// with a specific set of columns
			user = new User().setId(3L).setUsername("jen").setTitle("manager");
			runner.exec(ib.columns("id", "username").value(user));

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
			runner.exec(insert("label").value(label));
		}
	}

	@Test
	public void invalidColumnExceptionTest(){
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			// test exception when invalid column
			User user = new User().setId(4L).setUsername("brian").setTitle("manager");
			try {
				runner.exec(insert().into("user").columns("id", "username", "foo").value(user));
			} catch (RSQLException re) {
				// exception should be something like: ERROR: column "foo" of relation "contact" does not exist
				// So, simply test that the message contain "foo"
				assertTrue(re.getMessage().indexOf("foo") > -1);
			}
		}
	}

	// just to test compilation issue.
	public int compilationWithColumnsSets(){
		DB db = new DBBuilder().build(dataSource);

		Set<String> eCols = new HashSet<>();
		eCols.add("pwd");

		Map map = new HashMap();

		try (Runner runner = db.openRunner()) {
			// Note: needs to have the (Set<Object>) cast to return the appropriate type.
			return runner.exec(update("user").columns((Set<Object>)map.keySet()));
		}

	}
}
