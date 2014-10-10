/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test;

import org.j8ql.*;
import org.j8ql.test.app.Contact;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

/**
 * <p></p>
 */
public class StreamTest  extends TestSupport {

	private Object[][] data = new Object[][]{{1,"mike"},{2,"jen"},{3,"paul"}};

	private void createDataSet(){

		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			runner.executeUpdate("insert into contact (id,name) values (?,?)", data[0]);
			runner.executeUpdate("insert into contact (id,name) values (?,?)", data[1]);
			runner.executeUpdate("insert into contact (id,name) values (?,?)", data[2]);
		}
	}

	@Test
	public void testStreamMap(){
		createDataSet();

		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()){
			// insert 2 contacts


			// we do the query way
			try (PStmt pquery = runner.newPQuery("select id, name from contact")){
				try (Stream<Record> s = pquery.stream()){
					assertEquals(3, s.collect(toList()).size());
				}
			}

			try (Stream<Record> s = runner.stream("select * from contact")){
				Map m = s.findFirst().orElse(null);
				assertEquals(1L, m.get("id"));
			}

			// with filter
			try (Stream<Record> s = runner.stream("select * from contact")){
				List<Map> r = s.filter((m) -> m.get("id").equals(2L)).collect(toList());
				assertEquals(1,r.size());
				assertEquals(2L, r.get(0).get("id"));
			}

			// with limit
			try (Stream<Record> s = runner.stream("select * from contact")){
				assertEquals(2,s.limit(2).collect(toList()).size());
			}
		}
	}

	@Test
	public void testSteamType(){
		createDataSet();

		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()){
			try (PStmt pquery = runner.newPQuery("select id, name from contact")){
				try (Stream<Contact> s = pquery.stream(Contact.class)){
					List<Contact> l = s.filter((c) -> c.getId().equals(3L)).collect(toList());
					assertEquals(1, l.size());
					assertEquals("paul", l.get(0).getName());
				}
			}
		}
	}
}
