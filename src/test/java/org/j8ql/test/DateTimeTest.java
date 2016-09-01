package org.j8ql.test;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Record;
import org.j8ql.Runner;
import org.junit.Test;
import org.junit.runner.Result;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DateTimeTest extends TestSupport{


	@Test
	public void testZonedDateTime(){
		DB db = new DBBuilder().build(dataSource);

		try (Runner runner = db.openRunner()) {
			ZoneId est = ZoneId.of("US/Eastern");
			ZoneId utc = ZoneId.of("UTC");

			LocalDateTime jan1 = LocalDateTime.of(2016, 01, 01, 10, 0, 0);
			ZonedDateTime jan1Est = ZonedDateTime.of(jan1, est);

			runner.execute("insert into project (name, \"startTime\", \"createTime\") values (?,?,?)",
					"test_zoneddatetime",
					jan1Est,
					jan1Est);

			List<Record> results = runner.list("select * from project");

			Record r = results.get(0);

			assertEquals(jan1Est.withZoneSameInstant(utc), r.get("createTime"));
		}
	}
}
