/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

import org.j8ql.util.CloseableStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class PStmt extends Stmt {

    private final PreparedStatement pstmt;

    PStmt(DB db, String sql, PreparedStatement pstmt) {
        super(db, sql, pstmt);
        this.pstmt = pstmt;
    }

    public PStmt setValues(Object... values){
        db.setValues(pstmt, values);
        return this;
    }

    // --------- High Level Execute --------- //
    // -- The higher level executes are execute methods that provide
    // -- higher level execute which makes it more convenient to execute
    // -- common combo execute.

	/**
	 * Convenient method to execute a select count....
	 * Calling this method assume the select was a "select count..."
	 * @param values
	 * @return
	 */
    public long executeCount(Object... values){
        long r = 0;
        ResultSet rs;
        setValues(values);

        try {
            rs = pstmt.executeQuery();
            if (rs.next()){
                r = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RSQLException(e,sql);
        }

        return r;
    }

    public Optional<Record> executeWithSingleReturn(Object... values){
        Optional<Record> r;
        setValues(values);
        try {
            boolean b = pstmt.execute();
            // we assume we have a resultset back which should be the result of a sql... "returning ..."
            ResultSet rs = pstmt.getResultSet();
            List<Record> list = db.buildResults(rs);
            if (list.size() > 0){
                r = Optional.of(list.get(0));
            }else{
				r = Optional.empty();
			}
            return r;
        } catch (SQLException e){
            throw new RSQLException(e,sql);
        }
    }
    // --------- /High Level Execute --------- //

    // --------- Low Level Execute --------- //
    // -- This section is about just wrapping the JDBC execute methods
    // -- by respecting the corresponding return semantic.

    /**
     * Raw jdbc execute that just execute the attached prepared statements with the 'values'.
     *
     * @param values
     * @return the boolean value returned by the JDBC .execute() method.
     */
    public boolean execute(Object... values) {
        boolean r;
        setValues(values);
        try{
            r = pstmt.execute();
        }catch(SQLException e){
            throw new RSQLException(e,sql);
        }
        return r;
    }

	/**
	 * Pass through to the underlying statement getUpdateCount
	 * @return
	 */
	public int getUpdateCount(){
		try{
			return pstmt.getUpdateCount();
		}catch(SQLException e){
			throw new RSQLException(e,sql);
		}
	}

	public int executeUpdate(Object... values) throws RSQLException{
		int r;
		setValues(values);
		try {
			r = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RSQLException(e,sql);
		}
		return r;
	}

	public int[] executeBatch(List<List> valuesArray) throws RSQLException{
		int[] r;
		try {
			for (List values : valuesArray) {
				setValues(values.toArray());
				pstmt.addBatch();
			}
			r = pstmt.executeBatch();
			return r;
		}catch (SQLException e) {
			throw new RSQLException(e,sql);
		}
	}
	// --------- /Low Level Execute --------- //

	// --------- List --------- //
	/**
	 * Convenient method which calls steam and collect it to a list and close the stream.
	 */
	public <T> List<T> list(Class<T> cls, Object... values) {
		try (Stream<T> s = stream(cls, values)) {
			return s.collect(toList());
		}
	}

	/**
	 * Convenient method which calls steam and collect it to a list and close the stream.
	 * @param values
	 * @return
	 */
	public List<Record> list(Object... values) {
		try (Stream<Record> s = stream(values)) {
			return s.collect(toList());
		}
	}
	// --------- /List --------- //

	// --------- Stream --------- //
	public <T> Stream<T> stream(Class<T> cls, Object... values) {
		return stream(values).map((m) -> db.mapper.as(cls,m));
	}

	public Stream<Record> stream(Object... values){
		ResultSet rs;
		setValues(values);
		try {
			rs = pstmt.executeQuery();
			Stream<Record> resultSetStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(new ResultSetIterator(rs, db), 0), false);
			resultSetStream = new CloseableStream<>(resultSetStream,rs::close);
			return resultSetStream;
		} catch (SQLException e) {
			throw new RSQLException(e, sql);
		}
	}
	// --------- /Stream --------- //





}
