/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test.query;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Record;
import org.j8ql.Runner;
import org.j8ql.query.SelectQuery;
import org.j8ql.test.TestSupport;
import org.j8ql.test.app.Contact;
import org.j8ql.test.app.User;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.j8ql.query.Query.and;
import static org.j8ql.query.Query.insert;
import static org.j8ql.query.Query.select;
import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * <p></p>
 */
public class SelectQueryTest extends TestSupport {

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
	public void devTest() {
		createDataSet();

		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			try(Stream<Record> s = runner.stream(select("contact"))) {
				s.forEach(System.out::println);
			}
		}
	}

	@Test
	public void simpleMapSelectBuilders(){
		createDataSet();

		DB db = new DBBuilder().build(dataSource);

		try(Runner runner = db.openRunner()){
			SelectQuery<Record> selectBuilder = select().from("contact").where(and("title","manager"));
			try (Stream<Record> stream = runner.stream(selectBuilder)){
				Record rec = stream.findFirst().get();
			}
			selectBuilder = select().from("contact").limit(2);
			List<Record> list = runner.list(selectBuilder);
			assertEquals(2,list.size());
		}
	}

	@Test
	public void simpleTypedSelectBuilders(){
		createDataSet();

		DB db = new DBBuilder().build(dataSource);

		try(Runner runner = db.openRunner()){

			SelectQuery<Contact> selectBuilder = select(Contact.class).where("title", "manager");

			try (Stream<Contact> stream = runner.stream(selectBuilder)){
				Contact contact = stream.findFirst().get();
				assertEquals("jen",contact.getName());
			}

			selectBuilder = select(Contact.class).limit(2);
			List<Contact> list = runner.list(selectBuilder);
			assertEquals(2,list.size());
		}
	}

	@Test
	public void noMatchFirst() {
		createDataSet();

		DB db = new DBBuilder().build(dataSource);

		try(Runner runner = db.openRunner()) {
			Contact contact = runner.first(select(Contact.class).where("name","foo")).orElse(null);
			assertNull(contact);
		}
	}


		@Test
	public void createWithCaseColumns(){
		DB db = new DBBuilder().build(dataSource);

		try(Runner runner = db.openRunner()){
			runner.exec(insert(User.class).columns("username","firstName").values("mhork","Mike"));
			List<User> users = runner.list(select(User.class).where("firstName","Mike"));
		}
	}

	@Test
	public void whereIdTest(){
		createDataSet();

		DB db = new DBBuilder().build(dataSource);

		try(Runner runner = db.openRunner()){
			Contact contact = runner.first(select(Contact.class).whereId(1)).get();
			assertEquals("mike", contact.getName());

			contact = runner.first(select(Contact.class).whereId(mapOf("id", 2))).get();
			assertEquals("jen", contact.getName());

			contact = runner.first(select(Contact.class).whereId(new Contact().setId(3L))).get();
			assertEquals("paul", contact.getName());
		}


	}


}
