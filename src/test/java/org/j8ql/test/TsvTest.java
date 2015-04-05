package org.j8ql.test;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Record;
import org.j8ql.Runner;
import org.j8ql.query.Query;
import org.j8ql.query.SelectQuery;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TsvTest  extends TestSupport {

	private Object[][] data = new Object[][]{{1,"test_ticket first ticket manager"},{2,"test_ticket second manager ticket"},{3,"test_ticket third ticket"}};

	@Test
	public void simpleTsvSearch(){
		// dataSource can be built via standard JDBC, or Pool like C3P0 or HikariCP for example
		DB db = new DBBuilder().build(dataSource);

		//
		try (Runner runner = db.openRunner()) {

			// insert the data
			for (Object[] vals : data){
				runner.execute("insert into ticket (id,subject) values (?,?)", vals);
			}

			// raw SQL tsv search (postgres tsv search will match "management" with "manager", as both have the "manag" lexeme)
			List<Record> recordsFromSql = runner.list("select subject from ticket where to_tsvector(subject) @@ to_tsquery(?)","management");
			assertEquals(2, recordsFromSql.size());

			// same, will
			SelectQuery<Record> selectQuery = Query.select("ticket").columns("subject").where("to_tsvector(subject);@@;to_tsquery(?)","management");
			List<Record> recordsFromSelectQuery = runner.list(selectQuery);
			assertEquals(2, recordsFromSelectQuery.size());

		}



	}
}
