/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Record;
import org.j8ql.Runner;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * <p></p>
 */
public class ExecuteUpdateTest extends TestSupport {

	@Test
	public void executeUpdate(){
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()){
			runner.executeUpdate("insert into contact (id,name,create_date) values (?,?,?)", 1, "mike", new Timestamp(System.currentTimeMillis()));
			runner.executeUpdate("insert into contact (id,name,create_date) values (?,?,?)", new Object[]{2, "jen",
					new Date()});
			runner.executeUpdate("insert into contact (id,name,email) values (?,?,?)", 3, "angie", "wrong@email.com");
			runner.executeUpdate("insert into contact (id,name,email) values (?,?,?)", 4, "dan", "dan@gmail.com");

			runner.executeUpdate("update contact set email = ? where id = ?", null, 3);

			List<Record> list = runner.list("select * from contact where id = ?", 3);
			assertNull(list.get(0).get("email"));

			list = runner.list("select * from contact where id = ?", 4);
			assertEquals("dan@gmail.com", list.get(0).get("email"));
		}
	}


}
