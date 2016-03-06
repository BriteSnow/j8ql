/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

/**
 * Mutable Query to build a immutable DB object. Make sure to not share a DBBuilder instance as any operation will modify its properties.
 */
public class DBBuilder {
	static private final ZoneId UTC = ZoneId.of("UTC");

    private Map<Class,ToDbConverter> toDbConverterByJavaType = null; 
    
    private Map<Class,ToJavaConverter> toJavaConverterByJavaType = null; 
    
    public DBBuilder(){
		// --------- Default toDbConverters --------- //
		addToDbConverter(java.util.Date.class, (javaDate, ctx) -> new Timestamp(javaDate.getTime()));
		addToDbConverter(java.time.LocalDate.class, (localDate, ctx) -> java.sql.Date.valueOf(localDate));
		addToDbConverter(java.time.LocalDateTime.class, (localDateTime, ctx) -> java.sql.Timestamp.valueOf
				(localDateTime));

		// ZonedDateTime are automatically changed to UTC timestamps
        addToDbConverter(java.time.ZonedDateTime.class,
                (zonedDateTime, ctx) -> {
					ZonedDateTime utcVal = zonedDateTime.withZoneSameInstant(UTC);
					return (zonedDateTime != null) ? new Timestamp(zonedDateTime
							.toInstant().getEpochSecond() * 1000L) : null;
				});
		// --------- /Default toDbConverters --------- //

		// --------- Default toJavaConverters --------- //
		addToJavaConverter(java.sql.Date.class, (sqlDate, ctx) -> sqlDate.toLocalDate());
		//
        addToJavaConverter(java.sql.Timestamp.class, (timestamp, ctx) -> {
			// if the sqlType is timestamp with timezone, then, returned a ZonedDateTime
			if ("timestamptz".equals(ctx.getColumnSqlTypeName())){
				return ZonedDateTime.ofInstant(timestamp.toInstant(),
						ZoneId.of("UTC"));
			}
			// otherwise, return a localDateTime
			else{
				return timestamp.toLocalDateTime();
			}

		});
		// --------- /Default toJavaConverters --------- //

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
