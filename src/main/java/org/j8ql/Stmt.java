/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

import java.sql.SQLException;
import java.sql.Statement;

public class Stmt implements AutoCloseable{

    final Statement stmt;
	final String sql;
    final protected DB db;

    Stmt(DB db, String sql, Statement stmt){
        this.db = db;
		this.sql = sql;
        this.stmt = stmt;
    }
    
    public void close(){
        try {
            stmt.close();
        } catch (SQLException e) {
            throw new RSQLException(e);
        }
    }
    
}
