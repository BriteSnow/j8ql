/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test.query;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Runner;
import org.j8ql.query.UpdateQuery;
import org.j8ql.test.TestSupport;
import org.j8ql.test.app.Contact;
import org.j8ql.test.app.User;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.j8ql.query.Query.and;
import static org.j8ql.query.Query.select;
import static org.j8ql.query.Query.update;
import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * <p></p>
 */
public class UpdateQueryTest extends TestSupport {

	private Object[][] contacts = new Object[][]{{1,"mike","developer"},{2,"jen","manager"},{3,"paul","developer"}};

	private void createDataSet(){
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			for (Object[] row : contacts){
				runner.executeUpdate("insert into contact (id,name,title) values (?,?,?)", row);
			}
		}
	}

	@Test
	public void simpleUpdate(){
		createDataSet();
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			UpdateQuery<Integer> ub;
			int numUpdated;
			// --------- Single Update --------- //
			// update a single contact
			ub = update().into("contact").columns("name").values("Michael").where(and("id",1L));
			numUpdated = runner.exec(ub);
			assertEquals(1, numUpdated);
			// check contact
			Map contactMap = runner.first(select("contact").where(and("id",1L))).get();
			assertEquals("Michael", contactMap.get("name"));
			// --------- /Single Update --------- //

			// --------- Multiple Update --------- //
			ub = update("contact").columns("title").values("Dev").where(and("title","developer"));

			numUpdated = runner.exec(ub);
			assertEquals(2, numUpdated);
			List<Contact> developers = runner.list(Contact.class, "select * from contact where title = ? ", "Dev");
			assertEquals(2, developers.size());
			// --------- /Multiple Update --------- //
		}
	}

	@Test
	public void simpleExcludeColumns(){
		// most is already tested in insertQueryTest, so, just doing a sanity check
		createDataSet();

		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {

			Map userMike = mapOf("name","MIKE","title","NO TITLE TO BE SET");
			runner.exec(update("contact").value(userMike).excludeColumns("title"));
			Contact contact = runner.first(select(Contact.class).whereId(1L)).get();
			assertEquals("MIKE", contact.getName());
			assertEquals("developer", contact.getTitle()); // should not have changed (because in exclude list)
		}
	}

	@Test
	public void updateAndStream(){
		createDataSet();
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			// update of 2 developers.
			UpdateQuery<Contact> ub = update().into("contact").columns("title").values("Dev").where(and("title","developer")).returning(Contact.class, "id", "name", "title");
			try (Stream<Contact> stream = runner.stream(ub)){
				assertEquals("mike", stream.findFirst().get().getName());
			}

			// update with a non matching where close
			ub = update("contact").where(and("title","developer")).columns("title").values("Dev").returning(Contact.class, "id", "name", "title");
			List<Contact> developers = runner.list(ub);
			assertEquals(0, developers.size());
		}
	}

	@Test
	public void whereIdTest(){
		createDataSet();
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			UpdateQuery ub = update("contact").columns("title").values("Dev").whereId(1);
			runner.exec(ub);

			assertEquals(1L,runner.count(select("contact").where(and("title","Dev"))));
		}
	}


}
