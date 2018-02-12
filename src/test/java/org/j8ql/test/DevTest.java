package org.j8ql.test;

import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Record;
import org.j8ql.query.Query;
import org.j8ql.query.SelectQuery;
import org.j8ql.query.UpdateQuery;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DevTest extends TestSupport {

	@Test
	public void empty(){

	}

	@Test
	@Ignore
	public void streamTyping(){
		// does not work if List does not have <Object>
		List<Object> values = Arrays.asList("1","1");
		List<Integer> valueObjects = values.stream().map(o -> Integer.valueOf(o.toString())).collect(toList());
	}

	//@Test
	public void testLocalDateType() {
		System.out.println("...");
		DB db = new DBBuilder().build(dataSource);
		try(Connection con = dataSource.getConnection()){

			LocalDate date = LocalDate.now();
			Date sqlDate = Date.valueOf(date);
			PreparedStatement ps = con.prepareStatement("insert into ticket (id,subject,\"dueDate\") values (?,?,?)");
			ps.setObject(1,1);
			ps.setObject(2,"test subject");
			ps.setObject(3, sqlDate);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("...");
	}

	//@Test
	public void dbMetaExperiments() {
		System.out.println("...");
		DB db = new DBBuilder().build(dataSource);
		try(Connection con = dataSource.getConnection()){
			DatabaseMetaData dmd = con.getMetaData();
			//Primary keys
			List<Record> ids = db.buildResults(dmd.getPrimaryKeys(null,null,"label"));
			ids.stream().forEach(System.out::println);

			List<Record> cols = db.buildResults(dmd.getColumns(null,null,"label",null));
			cols.stream().forEach(System.out::println);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("...");
	}
}
