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

public class RawSelectTest  extends TestSupport {

	private Object[][] data = new Object[][]{{1,"mike"},{2,"jen"},{3,"paul"}};

	private void createDataSet(){
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			for (Object[] row : data){
				runner.executeUpdate("insert into contact (id,name) values (?,?)",row);
			}
		}
	}

	@Test
	public void testDataSet(){
		createDataSet();

		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()){

			try (Stream<Contact> stream = runner.stream(Contact.class,"select * from contact")){
				List<Contact> contacts = stream.filter(contact -> contact.getName().length() > 3).limit(1).collect(toList());
				assertEquals("mike",contacts.get(0).getName());
			}

			try (PStmt pquery = runner.newPQuery("select * from contact where name = ?")){

				try (Stream<Contact> stream = pquery.stream(Contact.class,"paul") ) {
					Contact contact = stream.findFirst().get();
					assertEquals("paul",contact.getName());
				}

				try (Stream<Record> stream = pquery.stream("jen") ){
					Map map = stream.findFirst().get();
					assertEquals("jen",map.get("name"));
				}
			}



		}
	}
}
