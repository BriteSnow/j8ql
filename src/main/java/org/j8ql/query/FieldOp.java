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
	private final Splitter splitter =  Splitter.on(';').trimResults().omitEmptyStrings();
	private final String defaultOperator = "=";

	public final String key;
	public final String operator;
	public final String name;
	public final String valueFunction;

	public FieldOp(String key){
		this.key = key;
		// Not sure if doing
		if (key.indexOf(';') != -1){
			List<String> nameOperator = splitter.splitToList(key);
			name = nameOperator.get(0);
			if (nameOperator.size() > 1){
				operator = nameOperator.get(1);
			}else{
				operator = defaultOperator;
			}
			if (nameOperator.size() > 2) {
				valueFunction = nameOperator.get(2);
			}else{
				valueFunction = null;
			}

		}else{
			name = key;
			operator = "=";
			valueFunction = null;
		}
	}


	@Override
	public String toString() {
		return name + " " + operator + ((valueFunction != null)?valueFunction:"");
	}
}
