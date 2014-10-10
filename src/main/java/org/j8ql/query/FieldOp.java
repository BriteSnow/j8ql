/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

import com.google.common.base.Splitter;

import java.util.List;

/**
* <p></p>
*/ // --------- Public Utilities Class --------- //
public class FieldOp {
	private final Splitter splitter =  Splitter.on(',').trimResults().omitEmptyStrings();

	public final String key;
	public final String operator;
	public final String name;

	public FieldOp(String key){
		this.key = key;
		if (key.indexOf(',') != -1){
			List<String> nameOperator = splitter.splitToList(key);
			name = nameOperator.get(0);
			operator = nameOperator.get(1);
		}else{
			name = key;
			operator = "=";
		}
	}

	public FieldOp(String operator, String name) {
		this.key = null;
		this.operator = operator;
		this.name = name;
	}

	@Override
	public String toString() {
		return name + " " + operator;
	}
}
