package org.j8ql.test;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Record;
import org.j8ql.Runner;
import org.j8ql.test.app.User;
import org.jomni.util.Maps;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class HstoreTest extends TestSupport {


	@Test
	public void testSimpleSQLHstore(){

		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()){

			// with full string
			String insertSQL = "INSERT INTO \"user\" (username, pref) VALUES (" +
					" 'test_testSQLHstore'," +
					" 'lang    => \"US\"," +
					"  rank     => 368," +
					"  type  => test'" +
					" );";
			runner.execute(insertSQL);

			Record r  = runner.list("select * from \"user\" where username = ?","test_testSQLHstore").get(0);
			Map prefs = (Map) r.get("pref");
			assertEquals("US",prefs.get("lang"));
			assertEquals("368", prefs.get("rank"));
		}
	}

	@Test
	public void testSQLHstoreWithMap(){

		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()){

			// --------- Test with String Maps --------- //
			// with full string
			String insertSQL = "INSERT INTO \"user\" (username, pref) VALUES (?,?)";
			Map prefs = Maps.mapOf("lang", "US", "rank", "368", "type", "test");
			runner.execute(insertSQL,"test_testSQLHstoreWithMap_1",prefs);

			Record r  = runner.list("select * from \"user\" where username = ?","test_testSQLHstoreWithMap_1").get(0);
			prefs = (Map) r.get("pref");
			assertEquals("US",prefs.get("lang"));
			assertEquals("368",prefs.get("rank"));
			// --------- /Test with String Maps --------- //

			// --------- Test with Non-String values --------- //
			// with full string
			insertSQL = "INSERT INTO \"user\" (username, pref) VALUES (?,?)";
			prefs = Maps.mapOf("lang", "US", "rank", 368, "type", "test");
			runner.execute(insertSQL,"test_testSQLHstoreWithMap_2",prefs);

			r  = runner.list("select * from \"user\" where username = ?","test_testSQLHstoreWithMap_2").get(0);
			prefs = (Map) r.get("pref");
			assertEquals("US",prefs.get("lang"));
			assertEquals("368",prefs.get("rank"));
			// --------- /Test with Non-String values --------- //
		}
	}

	@Test
	public void testSQLHstoreWithObject(){
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()){
			String insertSQL = "INSERT INTO \"user\" (username,create_date, pref) VALUES (?,?,?)";
			User.Pref pref = new User.Pref();
			pref.setLang("US");
			pref.setRank(368L);
			pref.setType("test");
			runner.execute(insertSQL,"test_testSQLHstoreWithMap", LocalDateTime.now(), pref);

			Record r  = runner.list("select * from \"user\" where username = ?","test_testSQLHstoreWithMap").get(0);
			User user = db.mapper.as(User.class, r);
			System.out.println("r.pref: " + r.get("pref"));
			System.out.println("user: " + user.getPref());
		}
	}


}
