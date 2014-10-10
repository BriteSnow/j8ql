/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test;

import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.j8ql.DBBuilder;
import org.j8ql.DB;
import org.j8ql.test.app.Contact;
import org.junit.Test;

public class DBMapperTest extends TestSupport {

    
    //@Test
    //public void testToMapIgnoreNulls(){
    //    DB db = new DBBuilder().build(dataSource);
    //
    //    Contact contact = new Contact();
    //    contact.setName("luckyluc");
    //    contact.setFirstName(null);
    //    contact.setLastName(null);
    //
    //    Map map = db.mapper.asMap(contact,true);
    //
    //    assertEquals("luckyluc",map.get("name"));
    //    assertEquals(null,map.get("firstName"));
    //}

	@Test
	public void testMapToObject(){
		DB db = new DBBuilder().build(dataSource);
		Contact contact = db.mapper.as(Contact.class,mapOf("id", 1L, "name", "luckyluc"));
		assertEquals((Long)1L, contact.getId());
		assertEquals("luckyluc", contact.getName());
	}

}
