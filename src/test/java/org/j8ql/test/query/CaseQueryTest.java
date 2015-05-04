package org.j8ql.test.query;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Record;
import org.j8ql.Runner;
import org.j8ql.query.Case;
import org.j8ql.query.Case.CaseBuilder;
import org.j8ql.query.Query;
import org.j8ql.query.SelectQuery;
import org.j8ql.test.TestSupport;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CaseQueryTest extends TestSupport {

	private Object[][] tickets = new Object[][]{{0,"Important issue",1},{1,"Blocking issue",0},{2,"Shourd fix issue",2},{3,"minor issue",3},{4,"another minor issue",null}};

	private DB db;

	private void createDataSet() {
		db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			for (Object[] ticket : tickets) {
				runner.executeUpdate("insert into ticket (id,subject,\"priority\") values (?,?,?)", ticket);
			}
		}
	}


	@Test
	public void testCase(){
		createDataSet();
		try (Runner runner = db.openRunner()) {

			// raw sql
			String rawsql = "select id, subject, case priority when 0 then 'blocker' when 1 then 'important' when 2 then 'shouldfix' else 'other' end p from ticket order by priority, id;";
			List<Record> list = runner.list(rawsql);
			assertEquals("Blocking issue", list.get(0).get("subject"));
			assertEquals("blocker", list.get(0).get("p"));
			assertEquals("shouldfix", list.get(2).get("p"));
			assertEquals("other", list.get(3).get("p"));
			assertEquals("other", list.get(4).get("p"));

			// same test with the SelectBuilder and CaseBuilder
			Case pcase = new CaseBuilder().on("priority").whenThen(0, "blocker").whenThen(1, "important").whenThen(2, "shouldfix").orElse("other").alias("p").build();
			SelectQuery<Record> select = Query.select("ticket").columns("id","subject",pcase).orderBy("priority");
			list = runner.list(select);
			assertEquals("Blocking issue", list.get(0).get("subject"));
			assertEquals("blocker", list.get(0).get("p"));
			assertEquals("shouldfix", list.get(2).get("p"));
			assertEquals("other", list.get(3).get("p"));
			assertEquals("other", list.get(4).get("p"));

			//list.forEach(System.out::println);
		}
	}
}
