/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test.query;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Record;
import org.j8ql.Runner;
import org.j8ql.query.SelectQuery;
import org.j8ql.test.TestSupport;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.j8ql.query.Query.select;
import static org.junit.Assert.assertEquals;

/**
 * <p></p>
 */
public class JoinSelectQueryTest extends TestSupport {

	private Object[][] projects = new Object[][]{{0,"db project"},{1,"web project"}};
	private Object[][] tickets = new Object[][]{{0,"fix db",0},{1,"fix java",1},{2,"fix ui",1},{3,"Lost ticket (no project)",null}};
	private Object[][] labels = new Object[][]{{0,"URGENT"},{1,"LATER"},{2,"MINOR"}};
	private Object[][] ticketIdLabelIds = new Object[][]{{0,0},{1,0},{2,1},{2,2}};

	private void createDataSet(){
		DB db = new DBBuilder().build(dataSource);
		try (Runner runner = db.openRunner()) {
			for (Object[] project : projects){
				runner.executeUpdate("insert into project (id,name) values (?,?)", project);
			}
			for (Object[] ticket : tickets){
				runner.executeUpdate("insert into ticket (id,subject,\"projectId\") values (?,?,?)", ticket);
			}
			for (Object[] label : labels){
				runner.executeUpdate("insert into label (id,name) values (?,?)",label);
			}
			for (Object[] ticketIdLabelId : ticketIdLabelIds){
				runner.executeUpdate("insert into ticketlabel (\"ticketId\",\"labelId\") values (?,?)",ticketIdLabelId);
			}
		}
	}

	@Test
	public void simpleTicketLabelJoinOneOneLabel(){
		createDataSet();

		DB db = new DBBuilder().build(dataSource);

		try(Runner runner = db.openRunner()){
			SelectQuery<Record> selectBuilder = select().from("ticket").innerJoin("ticketlabel","ticketId","ticket","id")
					.innerJoin("label","id","ticketlabel","labelId");

			// select all tickets with label.id == 0
			selectBuilder = selectBuilder.where("label.id",0);
			// there should be two
			List<Record> list = runner.list(selectBuilder);
			assertEquals(2, list.size());
		}
	}

	@Test
	public void simpleTicketWithProjectName(){
		createDataSet();

		DB db = new DBBuilder().build(dataSource);

		try(Runner runner = db.openRunner()){
			SelectQuery<Record> selectBuilder = select().from("ticket").leftJoin("project", "id", "ticket", "projectId");
			selectBuilder = selectBuilder.columns("ticket.*","project.name projectName");
			selectBuilder = selectBuilder.whereId(0);
			Optional<Record> rec = runner.first(selectBuilder);
			Record record = rec.get();
			// project name for ticket 0 is project 0.
			assertEquals(projects[0][1],rec.get().get("projectName"));
		}
	}


	//@Test
	public void joinAndCollectorsExperiments(){
		createDataSet();

		DB db = new DBBuilder().build(dataSource);

		try(Runner runner = db.openRunner()){
			SelectQuery<Record> selectBuilder = select().columns("ticket.*","label.name").from("ticket").innerJoin("ticketlabel","ticketId","ticket","id")
					.innerJoin("label","id","ticketlabel","labelId");

			// print SQL
			System.out.println(db.sql(selectBuilder, null));

			// print all record
			Stream<Record> stream = runner.stream(selectBuilder);
			stream.forEach(r -> {
				System.out.println(r);
			});

			// manual groupby with collectors
			stream = runner.stream(selectBuilder);
			HashMap<Long,Record> recById = stream.collect(HashMap::new,
					(map,rec) -> {
						Long id = (Long) rec.get("id");
						Map recInMap = map.get(id);
						if (recInMap == null){
							map.put(id,rec);
							recInMap = rec;
						}
						String label = (String) rec.get("name");
						if (label != null) {
							List<String> labels = (List<String>) recInMap.get("labels");
							if (labels == null) {
								labels = new ArrayList<String>();
								recInMap.put("labels",labels);
							}
							labels.add(label);
							recInMap.remove("name");
						}

					},
					// This need to be overriden to add the labels as well
					HashMap::putAll);
			System.out.println(recById);

			// simple groupingBy
			stream = runner.stream(selectBuilder);
			Map<Long,List<Record>> recordsByRecordId = stream.collect(groupingBy(r -> {return (Long)r.get("id");}));
			System.out.println(recordsByRecordId);

			// simple mapOf
			stream = runner.stream(selectBuilder);
			Map<Long, Record> recordById  = stream.collect(toMap(r -> (Long)r.get("id"),
					Function.identity(),
					(rec1, rec2) -> {
						System.out.println("conflict " + rec1 + " " + rec2);
						return rec1;
					},
					() -> {
						System.out.println("calling supplierMap");
						return new LinkedHashMap<Long, Record>();
					}));
			System.out.println(recordById.getClass().getSimpleName());
			System.out.println(recordById);

		}
	}

}
