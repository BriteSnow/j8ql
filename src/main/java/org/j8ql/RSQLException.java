/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

import java.sql.SQLException;

@SuppressWarnings("serial")
public class RSQLException extends RuntimeException {
    
    public enum Error{
        PARAM_VALUE_CANNOT_BE_NULL, 
        CANNOT_INSTANCIATE_LIST_RESOURCE_BUILDER;
    }
    
    private Error error;
	private String sql;

    public RSQLException(Error error, Throwable e){
        super(e);
        this.error = error;
    }
    
    public RSQLException(Error error){
        this.error = error;
    }
    
    public RSQLException(SQLException e){
        super(e);
    }

	public RSQLException(SQLException e,String sql){
		super(e);
		this.sql = sql;
	}
    
    public String getMessage(){
        StringBuilder sb = new StringBuilder();
        
        Throwable cause = this.getCause();
        if (error != null){
            sb.append(error.toString()).append(" ");
        }
        if (cause != null){
            sb.append(cause.getMessage());
        }
		if (sql != null) {
			sb.append("\n\t").append(sql);
		}

		String message = sb.toString();
        
        message = (message.length() == 0)?"no message":message;
        
        return message;
        
        
    }
}
