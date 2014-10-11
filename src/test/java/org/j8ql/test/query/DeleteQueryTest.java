/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test.query;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Runner;
import org.j8ql.query.DeleteQuery;
import org.j8ql.query.SelectQuery;
import org.j8ql.test.TestSupport;
import org.j8ql.test.app.Contact;
import org.junit.Test;

import static org.j8ql.query.Query.*;
import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;

public class DeleteQueryTest extends TestSupport {

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
	public void simpleDeleteTest(){
		createDataSet();

		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			// make sure we start with 3 items
			assertEquals(3L, runner.executeCount("select count(*) from contact"));

			assertEquals((Integer)1, runner.exec(delete("contact").where(and("id", 2L))));
			assertEquals(2L, runner.executeCount("select count(*) from contact"));

			runner.exec(delete("contact").where("title","developer"));
			assertEquals(0L, runner.executeCount("select count(*) from contact"));
		}
	}

	@Test
	public void whereIdTest(){
		createDataSet();
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			SelectQuery sb = select("contact");

			long count = runner.count(sb);
			assertEquals(3L,count);

			DeleteQuery baseDelete = delete("contact");
			runner.exec(baseDelete.whereId(1L));
			assertEquals(2L,runner.count(sb));

			runner.exec(baseDelete.whereId(mapOf("id", 2)));
			assertEquals(1L,runner.count(sb));

			runner.exec(baseDelete.whereId(new Contact().setId(3L)));
			assertEquals(0L,runner.count(sb));
		}
	}
}
