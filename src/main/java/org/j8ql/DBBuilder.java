/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

/**
 * Mutable Query to build a immutable DB object. Make sure to not share a DBBuilder instance as any operation will modify its properties.
 */
public class DBBuilder {

    private Map<Class,ToDbConverter> toDbConverterByJavaType = null; 
    
    private Map<Class,ToJavaConverter> toJavaConverterByJavaType = null; 
    
    public DBBuilder(){
		// default JavaToDb Type converters
		addToDbConverter(java.util.Date.class, (javaDate) -> new Timestamp(javaDate.getTime()));
		addToDbConverter(java.time.LocalDate.class, (localDate) -> java.sql.Date.valueOf(localDate));
		addToDbConverter(java.time.LocalDateTime.class, (localDateTime) -> java.sql.Timestamp.valueOf(localDateTime));

		// default DbToJava Type converters
		addToJavaConverter(java.sql.Date.class, (sqlDate) -> sqlDate.toLocalDate());
		addToJavaConverter(java.sql.Timestamp.class, (sqlTimestamp) -> sqlTimestamp.toLocalDateTime());
    }
    
    public DB build(DataSource dataSource){
        DB db = new DB(dataSource);
        
        db.setToDbConverter(toDbConverterByJavaType);
        db.setToJavaConverter(toJavaConverterByJavaType);
        db.init();
        return db;
    }
    
    
    public <T> DBBuilder addToJavaConverter(Class<T> javaType, ToJavaConverter<T> dbToJavaConverter){
        if (toJavaConverterByJavaType == null){
            toJavaConverterByJavaType = new HashMap<Class, ToJavaConverter>();
        }
        toJavaConverterByJavaType.put(javaType,dbToJavaConverter);
        return this;
    }    
    
    public <T> DBBuilder addToDbConverter(Class<T> javaType, ToDbConverter<T> javaToDbConverter){
        if (toDbConverterByJavaType == null){
            toDbConverterByJavaType = new HashMap<Class, ToDbConverter>();
        }
        toDbConverterByJavaType.put(javaType,javaToDbConverter);
        return this;
    }
}
