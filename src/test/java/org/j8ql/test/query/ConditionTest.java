/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test.query;

import org.j8ql.query.Condition;
import org.junit.Test;

import static org.j8ql.query.Query.*;
import static org.junit.Assert.assertEquals;

/**
 * <p></p>
 */
public class ConditionTest {


	@Test
	public void simpleConditions() {
		final String toString = "((name1 > val1 AND name2 = val2) OR name3 = val3)";
		final String toSql = "(\"name1\" > ? AND \"name2\" = ?) OR \"name3\" = ?";

		Condition one = and(one("name1;>", "val1"), one("name2", "val2")).or(one("name3", "val3"));
		assertEquals(toString,one.toString());
		assertEquals(toSql,one.toSql());

		Condition two = or(and(one("name1;>", "val1"), one("name2", "val2")), one("name3", "val3"));
		assertEquals(toString,two.toString());
		assertEquals(toSql,two.toSql());

		Condition three = and("name1;>", "val1", "name2", "val2").or("name3", "val3");
		assertEquals(toString,three.toString());
		assertEquals(toSql,three.toSql());

		Condition four = and("name1;>", "val1", "name2", "val2").or(one("name3", "val3"));
		assertEquals(toString,four.toString());
		assertEquals(toSql,four.toSql());
	}

	@Test
	public void simpleConditions2() {
		final String toString = "name1 > val1 OR name2 = val2 AND name3 = val3";
		final String toSql = "\"name1\" > ? OR (\"name2\" = ? AND \"name3\" = ? AND \"name4\" = ?)";

		//Condition one = or("name1,>", "val1", and("name2","val2","name3","val3"));
		Condition two = or(one("name1;>", "val1"),and("name2","val2","name3","val3").and("name4","val4"));
		assertEquals(toSql, two.toSql());

		// test error
		//Condition three = or(one("name1,>", "val1"),"name4","val4");
		//System.out.println("" + three);
	}
}
