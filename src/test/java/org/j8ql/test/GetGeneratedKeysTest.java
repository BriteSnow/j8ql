package org.j8ql.test;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Record;
import org.j8ql.Runner;
import org.j8ql.query.InsertQuery;
import org.j8ql.query.Query;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.j8ql.query.Query.select;
import static org.junit.Assert.assertEquals;

public class GetGeneratedKeysTest extends TestSupport {
    @Test
    public void testBatchInsertToFetchGeneratedKeys() {
        DB db = new DBBuilder().build(dataSource);

        try (Runner runner = db.openRunner()) {
            Object[][] data = {{"ticket 01 testBatchInsertToFetchGeneratedKeys"}, {"ticket 02 testBatchInsertToFetchGeneratedKeys"}};
            List<List> batchValues = Stream.of(data).map(Arrays::asList).collect(Collectors.toList());
            InsertQuery<Integer> insert = Query.insert("ticket").columns("subject");
            runner.executeBatch(insert.batchValues(batchValues));
            List<Record> generatedKeys = runner.getGeneratedKeys();
            assertEquals(2, runner.count(select("ticket")));
            assertEquals(2, generatedKeys.size());
        }
    }
}
