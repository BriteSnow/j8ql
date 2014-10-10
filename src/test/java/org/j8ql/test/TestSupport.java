/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test;

import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.j8ql.DBBuilder;
import org.j8ql.DB;
import org.j8ql.Runner;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.postgresql.ds.PGSimpleDataSource;

public class TestSupport {
    static private String URL = "jdbc:postgresql://localhost:5432/j8ql_db"; // ?searchpath=test (not implemented in driver 9.1 yet)
    static private String USER = "j8ql_user";
    static private String PWD = "welcome";
    
    static protected DataSource dataSource; 
    
    @BeforeClass
    static public void initDataSource(){
		Properties p = new Properties(System.getProperties());
		p.put("org.slf4j.simpleLogger.defaultLogLevel","error");
		System.setProperties(p);

		HikariConfig config = new HikariConfig();
		PGSimpleDataSource pg = new PGSimpleDataSource();
		pg.setPortNumber(5432);
		pg.setServerName("localhost");
		pg.setDatabaseName("j8ql_db");
		pg.setUser(USER);
		pg.setPassword(PWD);
		config.setDataSource(pg);
		config.setMaximumPoolSize(10);
		HikariDataSource ds = new HikariDataSource(config);

		dataSource = ds;
    }

	@AfterClass
	static public void afterClass(){
		((HikariDataSource)dataSource).close();
	}
    @Before
    public void before(){
        cleanTables();
    }
    
    static protected void cleanTables(){
        DB dbh = new DBBuilder().build(dataSource);
        
        // dbh.newRunner().executeUpdate("delete from contact").close().updateResult();
        
        Runner runner = dbh.openRunner();
		runner.executeUpdate("delete from \"user\"");
		runner.executeUpdate("delete from project");
		runner.executeUpdate("delete from ticket");
		runner.executeUpdate("delete from label");
		runner.executeUpdate("delete from ticketlabel");

		// to be deprecated tables:
        runner.executeUpdate("delete from contact");
        runner.executeUpdate("delete from contactlabel");

        runner.close();
    }
        
}
